import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "scala-stakeholder",
    version := "0.1.0",
    scalaVersion := "3.3.4",
    Compile / mainClass := Some("stakeholder.App"),
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "ujson" % "4.1.0",
      "org.scalameta" %% "munit" % "1.0.0" % Test
    )
  )
