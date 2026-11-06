package com.example.model

enum class OperatingDivision(
    val code: String,
    val title: String,
    val description: String,
    val tagColor: Long
) {
    INTELLIGENCE(
        "01",
        "Intelligence Division",
        "Research, market surveillance, competitive intel, deep investigation, and strategic planning.",
        0xFF00F0FF // Cyan
    ),
    ENGINEERING(
        "02",
        "Engineering Division",
        "Software, APIs, web apps, databases, system architecture, automation, and infrastructure.",
        0xFF7986CB // Indigo/Slate
    ),
    CREATIVE(
        "03",
        "Creative Division",
        "Graphics, media, video, audio mastering, typography, and brand content production.",
        0xFFFF5376 // Neon Coral
    ),
    ENTERPRISE(
        "04",
        "Enterprise Division",
        "Business modeling, competitor research, customer targeting, product development, and operations.",
        0xFFFFB800 // Amber
    ),
    AUTOMATION(
        "05",
        "Automation Division",
        "Document processing, format transformation, file handoffs, workflows, and repetitive pipelines.",
        0xFF00FF9D // Mint Green
    ),
    PERSONAL_OPS(
        "06",
        "Personal Operations",
        "Project tracking, schedules, personal knowledge bases, goal execution, and administration.",
        0xFFB388FF // Violet Purple
    )
}

enum class AuthorityMode(val label: String, val description: String) {
    AUTONOMOUS_WITH_GATES(
        "Autonomous with Approval Gates",
        "Handles routine operations automatically; requests explicit user approval for consequential actions."
    ),
    HUMAN_DIRECTED(
        "Human-Directed",
        "Proposes each division action and pauses for manual user sign-off."
    ),
    MAXIMUM_AUTONOMY(
        "Maximum Autonomy",
        "Full autonomous execution across tools, self-healing retries, and automated verification."
    )
}

enum class ToolCapabilityStatus {
    DISCOVERED,
    TESTED,
    CONNECTED,
    EXECUTABLE,
    VERIFIED
}

enum class IntegrationType(val label: String, val badgeColor: Long) {
    REST_API("REST API", 0xFF00F0FF),
    LOCAL_ENGINE("Local Engine", 0xFF00FF9D),
    WEBVIEW_BRIDGE("WebView Bridge", 0xFFFFB800),
    SDK("Native SDK", 0xFFB388FF)
}

enum class RuntimeHealthStatus(val label: String, val color: Long) {
    HEALTHY_ONLINE("Online & Verified", 0xFF00FF9D),
    DEGRADED_LATENCY("Degraded Latency", 0xFFFFB800),
    OFFLINE("Offline", 0xFFFF5376),
    UNCHECKED("Unchecked", 0xFF8A99A8)
}

enum class AgentRoleType {
    SUPREME_ORCHESTRATOR,
    STRATEGY_DIRECTOR,
    OPERATIONS_DIRECTOR,
    QUALITY_DIRECTOR,
    DIVISION_SPECIALIST,
    RECURSIVE_SUBAGENT
}

enum class AgentStatus {
    QUEUED,
    SWEEPING_TOOL,
    AWAITING_APPROVAL,
    RUNNING_TASK,
    SELF_HEALING,
    COMPLETED,
    FAILED
}

enum class LogLevel {
    INFO,
    STRATEGY,
    TOOL_CALL,
    DISPATCH,
    APPROVAL_GATE,
    SELF_HEALING,
    SUCCESS,
    WARN
}

data class FreeTool(
    val id: String,
    val name: String,
    val division: OperatingDivision,
    val category: String,
    val url: String,
    val description: String,
    val freeTierType: String,
    val capabilities: List<String>,
    val openSource: Boolean = false,
    val status: ToolCapabilityStatus = ToolCapabilityStatus.VERIFIED,
    val integrationType: IntegrationType = IntegrationType.REST_API,
    val adapterId: String = "default_adapter",
    val supportedOperations: List<String> = emptyList(),
    val authenticationRequired: Boolean = false,
    val rateLimitPolicy: String = "Free Anonymous Tier",
    val inputSchema: String = "{}",
    val outputSchema: String = "{}",
    val healthStatus: RuntimeHealthStatus = RuntimeHealthStatus.HEALTHY_ONLINE,
    val requiredCapabilities: List<String> = emptyList()
)

data class ToolExecutionResponse(
    val success: Boolean,
    val statusCode: Int = 200,
    val latencyMs: Long = 0L,
    val outputData: String = "",
    val artifactType: String = "text/plain",
    val artifactTitle: String = "",
    val artifactPreviewUrl: String? = null,
    val rawResponseHeaders: Map<String, String> = emptyMap(),
    val errorMessage: String? = null,
    val isFallback: Boolean = false,
    val executionEngine: String = "OkHttp Engine",
    val verifiedChecksum: String = ""
)

data class ArtifactValidationResult(
    val isValid: Boolean,
    val sha256Checksum: String,
    val byteSize: Int,
    val schemaValid: Boolean,
    val passedChecks: List<String>,
    val qualityScore: Int,
    val validationNotes: String
)

data class AgentLogEntry(
    val timestampMs: Long,
    val relativeTime: String,
    val level: LogLevel,
    val message: String
)

data class SelfHealingEvent(
    val timestampMs: Long,
    val failedToolName: String,
    val reason: String,
    val fallbackToolName: String,
    val outcome: String
)

data class OutcomeVerificationResult(
    val isVerified: Boolean = false,
    val qualityScorePercent: Int = 100,
    val verifiedBy: String = "Quality Director",
    val auditSummary: String = "",
    val passedChecks: List<String> = emptyList(),
    val validationResults: List<ArtifactValidationResult> = emptyList(),
    val totalArtifactsValidated: Int = 0,
    val averageChecksum: String = ""
)

data class ApprovalGate(
    val id: String,
    val actionTitle: String,
    val description: String,
    val isApproved: Boolean = false,
    val requiredReason: String = "Consequential tool action or file write"
)

data class AgentHelper(
    val id: String,
    val name: String,
    val roleType: AgentRoleType,
    val division: OperatingDivision,
    val roleTitle: String,
    val tool: FreeTool,
    val fallbackTool: FreeTool? = null,
    val taskDescription: String,
    val executionBudgetSec: Int = 30,
    val status: AgentStatus = AgentStatus.QUEUED,
    val progress: Float = 0f,
    val logs: List<AgentLogEntry> = emptyList(),
    val subAgents: List<AgentHelper> = emptyList(), // Recursive delegation
    val approvalGate: ApprovalGate? = null,
    val selfHealingEvents: List<SelfHealingEvent> = emptyList(),
    val inputArtifact: String? = null,
    val outputArtifactTitle: String = "",
    val outputArtifactPreviewUrl: String? = null,
    val outputArtifactData: String = "",
    val executionTimeMs: Long = 0L,
    val executionResponse: ToolExecutionResponse? = null,
    val validationResult: ArtifactValidationResult? = null
)

data class WorkflowPipeline(
    val id: String,
    val title: String,
    val prompt: String,
    val primaryDivision: OperatingDivision,
    val authorityMode: AuthorityMode = AuthorityMode.AUTONOMOUS_WITH_GATES,
    val createdAt: Long = System.currentTimeMillis(),
    val agents: List<AgentHelper> = emptyList(),
    val overallStatus: AgentStatus = AgentStatus.QUEUED,
    val overallProgress: Float = 0f,
    val activeApprovalGate: ApprovalGate? = null,
    val verificationResult: OutcomeVerificationResult = OutcomeVerificationResult(),
    val finalOutputTitle: String = "",
    val finalOutputSummary: String = "",
    val finalArtifactUrls: List<String> = emptyList(),
    val executionLog: List<AgentLogEntry> = emptyList()
)
