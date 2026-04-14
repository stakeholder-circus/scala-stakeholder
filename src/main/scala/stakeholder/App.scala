package stakeholder

import scala.util.Try

object App {
  final case class RunResult(exitCode: Int, stdout: String = "", stderr: String = "")
  final case class Options(
      focusFamily: Option[String] = None,
      seed: String = "default-seed",
      outputFormat: String = "text",
      listValues: Boolean = false,
      experimentalProvider: Option[String] = None
  )

  def main(args: Array[String]): Unit = {
    val result = run(args.toList)
    if (result.stdout.nonEmpty) print(result.stdout)
    if (result.stderr.nonEmpty) System.err.print(result.stderr)
    sys.exit(result.exitCode)
  }

  def run(args: List[String]): RunResult = {
    parseArgs(args) match {
      case Left(error) => RunResult(2, stderr = s"$error\n")
      case Right(options) if options.experimentalProvider.nonEmpty =>
        RunResult(2, stderr = s"experimental provider '${options.experimentalProvider.get}' is not enabled in the deterministic first tranche\n")
      case Right(options) if options.listValues =>
        RunResult(0, stdout = ujson.write(Catalog.listValues, indent = 2) + "\n")
      case Right(options) =>
        options.focusFamily match {
          case None => RunResult(2, stderr = "focus-family is required and must be a known generator family\n")
          case Some(family) =>
            val payload = focusPayload(family, options.seed, options.outputFormat)
            if (options.outputFormat == "json") RunResult(0, stdout = ujson.write(payload, indent = 2) + "\n")
            else RunResult(0, stdout = textPayload(payload).mkString("\n") + "\n")
        }
    }
  }

  def focusPayload(family: String, seed: String, outputFormat: String): ujson.Obj = {
    val normalized = Catalog.normalizeFamily(family).getOrElse(throw new IllegalArgumentException(s"invalid family: $family"))
    val (contextKey, contextValue) = Catalog.contextFor(normalized)
    val hash = java.lang.Integer.toUnsignedLong(scala.util.hashing.MurmurHash3.stringHash(s"$seed::$normalized"))
    val seconds = hash % 86400
    val hour = seconds / 3600
    val minute = (seconds % 3600) / 60
    val second = seconds % 60
    ujson.Obj(
      "eventType" -> "stakeholder.generator.output",
      "sequence" -> (1000 + (hash % 9000)).toInt,
      "family" -> normalized,
      "message" -> s"Deterministic scala tranche for $normalized",
      "timestamp" -> f"2026-01-01T$hour%02d:$minute%02d:$second%02dZ",
      "context" -> ujson.Obj(
        "rendererKey" -> Catalog.rendererKeyFor(normalized),
        contextKey -> contextValue,
        "seedFingerprint" -> s"${normalized.replace('_', '-')}-${java.lang.Long.toHexString(hash)}",
        "tranche" -> Catalog.trancheFor(normalized),
        "scalaProfile" -> "next-20-deterministic-foundation"
      ),
      "generationProvenance" -> ujson.Obj(
        "sourceRepo" -> "scala-stakeholder",
        "baseline" -> "next20-family-focus",
        "experimental" -> false,
        "adapterType" -> "static-catalog",
        "promptVersion" -> ujson.Null
      ),
      "outputFormat" -> outputFormat
    )
  }

  def textPayload(payload: ujson.Obj): List[String] = {
    val context = payload("context").obj
    List(
      s"family: ${payload("family").str}",
      s"renderer: ${context("rendererKey").str}",
      s"tranche: ${context("tranche").str}",
      s"sequence: ${payload("sequence").num.toInt}",
      s"timestamp: ${payload("timestamp").str}",
      s"message: ${payload("message").str}"
    )
  }

  private def parseArgs(args: List[String]): Either[String, Options] = {
    def loop(rest: List[String], options: Options): Either[String, Options] = rest match {
      case Nil => Right(options)
      case "--list-values" :: tail => loop(tail, options.copy(listValues = true))
      case "--focus-family" :: value :: tail =>
        Catalog.normalizeFamily(value) match {
          case Some(family) => loop(tail, options.copy(focusFamily = Some(family)))
          case None         => Left(s"invalid --focus-family: $value")
        }
      case "--focus-family" :: Nil => Left("missing value for --focus-family")
      case "--seed" :: value :: tail => loop(tail, options.copy(seed = value))
      case "--seed" :: Nil => Left("missing value for --seed")
      case "--output-format" :: value :: tail if Set("text", "json").contains(value) =>
        loop(tail, options.copy(outputFormat = value))
      case "--output-format" :: value :: _ => Left(s"invalid --output-format: $value")
      case "--output-format" :: Nil => Left("missing value for --output-format")
      case "--experimental-provider" :: value :: tail => loop(tail, options.copy(experimentalProvider = Some(value)))
      case "--experimental-provider" :: Nil => Left("missing value for --experimental-provider")
      case unknown :: _ if unknown.startsWith("--experimental-") => Left("experimental flags require --experimental-provider")
      case unknown :: _ => Left(s"unknown argument: $unknown")
    }

    loop(args, Options())
  }
}
