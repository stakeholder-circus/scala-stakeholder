package stakeholder

object Catalog {
  final case class FamilyMeta(rendererKey: String, contextKey: String, contextValue: String, tranche: String)

  val classicSix: Vector[String] = Vector(
    "code_analyzer",
    "data_processing",
    "jargon",
    "metrics",
    "network_activity",
    "system_monitoring"
  )

  val modernCore: Vector[String] = Vector(
    "agent_workflows",
    "platform_engineering",
    "observability_ai_runtime",
    "delivery_preview_ops",
    "supply_chain_security"
  )

  val aiGovernance: Vector[String] = Vector(
    "ai_inference_ops",
    "evaluation_and_guardrails",
    "knowledge_retrieval",
    "edge_client_runtime",
    "identity_and_trust",
    "aibom_provenance",
    "agent_boundary_security",
    "embedded_agentic_pipeline",
    "data_governance_compliance",
    "finops_capacity"
  )

  val securityBlockchain: Vector[String] = Vector(
    "blockchain_protocol_ops",
    "cross_chain_interop",
    "proof_and_sequencer_ops"
  )

  val overlayQuantum: Vector[String] = Vector(
    "hybrid_runtime_ops",
    "capacity_cost_controller",
    "batch_execution_tuner",
    "compiler_maintainer",
    "interop_adapter_engineer",
    "preflight_capacity_planner",
    "simulator_performance_engineer"
  )

  val healthProtocol: Vector[String] = Vector(
    "fhir_profile_generator",
    "smart_launch_oauth",
    "bulk_fhir_population_ops",
    "hl7v2_feed_ops",
    "clinical_workflow_events",
    "dicomweb_imaging_ops",
    "openehr_semantic_record_ops",
    "device_telemetry_clinical",
    "emr_vendor_adapter",
    "ocpp_chargepoint_ops",
    "ocpi_roaming_ops",
    "mcp_a2a_ops",
    "streaming_bus_ops",
    "service_mesh_rpc_ops"
  )

  val allFamilies: Vector[String] = classicSix ++ modernCore ++ aiGovernance ++ securityBlockchain ++ overlayQuantum ++ healthProtocol

  private val dedicated: Map[String, FamilyMeta] = Map(
    "code_analyzer" -> FamilyMeta("classic-six.code_analyzer", "analysisFocus", "typed-module-contracts", "classic-six"),
    "data_processing" -> FamilyMeta("classic-six.data_processing", "dataWindow", "batched-stream-reconciliation", "classic-six"),
    "jargon" -> FamilyMeta("classic-six.jargon", "languagePolicy", "scala-ecosystem-glossary", "classic-six"),
    "metrics" -> FamilyMeta("classic-six.metrics", "signalBlend", "latency-error-saturation", "classic-six"),
    "network_activity" -> FamilyMeta("classic-six.network_activity", "transportMix", "http-grpc-sse", "classic-six"),
    "system_monitoring" -> FamilyMeta("classic-six.system_monitoring", "telemetryScope", "runtime-build-host", "classic-six"),
    "agent_workflows" -> FamilyMeta("modern-core.agent_workflows", "coordinationMode", "jvm-orchestration-handshake", "modern-core"),
    "platform_engineering" -> FamilyMeta("modern-core.platform_engineering", "platformSurface", "sbt-jvm-release-lane", "modern-core"),
    "observability_ai_runtime" -> FamilyMeta("modern-core.observability_ai_runtime", "runtimeSignals", "logs-metrics-provider-audit", "modern-core"),
    "delivery_preview_ops" -> FamilyMeta("modern-core.delivery_preview_ops", "deliveryGuardrail", "preview-release-checkpoints", "modern-core"),
    "supply_chain_security" -> FamilyMeta("modern-core.supply_chain_security", "supplyChainPosture", "artifact-integrity-attestation", "modern-core")
  )

  def normalizeFamily(value: String): Option[String] = {
    val normalized = value.trim.toLowerCase.replace('-', '_')
    allFamilies.find(_ == normalized)
  }

  def rendererKeyFor(family: String): String = dedicated.get(family).map(_.rendererKey).getOrElse {
    if (aiGovernance.contains(family)) "fallback.ai_governance"
    else if (securityBlockchain.contains(family)) "fallback.security_blockchain"
    else if (overlayQuantum.contains(family)) "fallback.overlay_quantum"
    else "fallback.health_protocol"
  }

  def trancheFor(family: String): String = dedicated.get(family).map(_.tranche).getOrElse(rendererKeyFor(family).replace("fallback.", "fallback-"))

  def contextFor(family: String): (String, String) = dedicated.get(family)
    .map(meta => meta.contextKey -> meta.contextValue)
    .getOrElse("fallbackFamily" -> rendererKeyFor(family).stripPrefix("fallback."))

  private def stringArray(values: Seq[String]): ujson.Arr =
    ujson.Arr(values.map(value => ujson.Str(value))* )

  def listValues: ujson.Value = ujson.Obj(
    "outputFormats" -> stringArray(Seq("text", "json")),
    "flags" -> stringArray(Seq("list-values", "focus-family", "output-format", "seed", "experimental-provider")),
    "generatorFamilies" -> ujson.Arr(allFamilies.map { family =>
      ujson.Obj(
        "id" -> family,
        "registryId" -> family.replace('_', '-'),
        "rendererKey" -> rendererKeyFor(family),
        "tranche" -> trancheFor(family)
      )
    }*),
    "classicSix" -> stringArray(classicSix.map(_.replace('_', '-'))),
    "modernCore" -> stringArray(modernCore.map(_.replace('_', '-'))),
    "fallbackFamilies" -> stringArray((aiGovernance ++ securityBlockchain ++ overlayQuantum ++ healthProtocol).map(_.replace('_', '-'))),
    "implementationMode" -> "family-focus-deterministic"
  )
}
