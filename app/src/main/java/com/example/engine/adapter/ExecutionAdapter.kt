package com.example.engine.adapter

import com.example.engine.validation.ArtifactValidator
import com.example.model.ArtifactValidationResult
import com.example.model.FreeTool
import com.example.model.RuntimeHealthStatus
import com.example.model.ToolExecutionResponse

/**
 * ExecutionAdapter
 *
 * Defines the core functional integration interface for OmniSwarm's autonomous execution layer,
 * transitioning from static catalog entries to active, verifiable tool execution.
 *
 * Mandatory Methods:
 * 1. execute(operation: String) - Invokes the operation and produces verifiable telemetry & deliverables.
 * 2. monitorStatus() - Queries live runtime availability, health status, and latency.
 * 3. validateArtifact(hash: String) - Validates deliverables against declared cryptographic digests.
 */
interface ExecutionAdapter {

    /**
     * Unique identifier for this adapter implementation (e.g., "github_rest", "quicktype_compiler").
     */
    val adapterId: String

    /**
     * Fully qualified class name of the adapter handling the execution.
     */
    val implementationClass: String
        get() = this::class.java.name

    // =========================================================================
    // 1. TOOL EXECUTION METHODS
    // =========================================================================

    /**
     * Mandatory method: Executes the requested tool operation by directive/operation name.
     *
     * @param operation The operation identifier or prompt directive to execute.
     * @return Verifiable ToolExecutionResponse with latency, status code, digests, and outputs.
     */
    suspend fun execute(operation: String): ToolExecutionResponse

    /**
     * Executes the specific tool assignment with rich parameters and produces verifiable output.
     *
     * @param tool The target catalog tool definition.
     * @param prompt The original user objective or mission prompt.
     * @param taskDescription Specific milestone or task instructions.
     * @param parameters Execution arguments and configuration.
     * @return Verifiable ToolExecutionResponse.
     */
    suspend fun execute(
        tool: FreeTool,
        prompt: String,
        taskDescription: String,
        parameters: Map<String, Any> = emptyMap()
    ): ToolExecutionResponse = execute(operation = "$prompt: $taskDescription")

    /**
     * Executes a tool assignment using an encapsulated ExecutionRequest.
     */
    suspend fun executeRequest(request: ExecutionRequest): ToolExecutionResponse {
        return execute(
            tool = request.tool,
            prompt = request.prompt,
            taskDescription = request.taskDescription,
            parameters = request.parameters
        )
    }

    // =========================================================================
    // 2. STATUS & HEALTH MONITORING METHODS
    // =========================================================================

    /**
     * Mandatory method: Actively monitors and queries the runtime health of this adapter.
     *
     * @return RuntimeHealthStatus (HEALTHY_ONLINE, DEGRADED_LATENCY, or OFFLINE).
     */
    suspend fun monitorStatus(): RuntimeHealthStatus

    /**
     * Monitors status for a specific catalog tool instance.
     */
    suspend fun monitorStatus(tool: FreeTool): RuntimeHealthStatus = monitorStatus()

    /**
     * Health check alias for status monitoring.
     */
    suspend fun healthCheck(tool: FreeTool): RuntimeHealthStatus = monitorStatus(tool)

    /**
     * Evaluates comprehensive execution telemetry for real-time workforce monitoring.
     */
    suspend fun getTelemetry(tool: FreeTool): ExecutionTelemetry {
        val start = System.currentTimeMillis()
        val status = monitorStatus(tool)
        val latency = System.currentTimeMillis() - start
        return ExecutionTelemetry(
            adapterId = adapterId,
            toolId = tool.id,
            toolName = tool.name,
            status = status,
            latencyMs = latency
        )
    }

    // =========================================================================
    // 3. ARTIFACT VALIDATION METHODS
    // =========================================================================

    /**
     * Mandatory method: Validates an artifact hash against expected cryptographic digest format and rules.
     *
     * @param hash SHA-256 cryptographic digest string.
     * @return True if the hash is non-blank, exactly 64 hexadecimal characters, and valid.
     */
    fun validateArtifact(hash: String): Boolean {
        if (hash.isBlank() || hash.length != 64) return false
        return hash.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
    }

    /**
     * Independently validates the deliverable produced by this adapter against declared contracts,
     * schemas, and expected outputs.
     *
     * @param tool The tool that generated the artifact.
     * @param taskDescription The assigned task.
     * @param response The execution response containing output payload and digests.
     * @return Detailed ArtifactValidationResult with passed/failed checks and quality score.
     */
    fun validateArtifact(
        tool: FreeTool,
        taskDescription: String,
        response: ToolExecutionResponse
    ): ArtifactValidationResult {
        return ArtifactValidator.validate(tool, taskDescription, response)
    }

    /**
     * Calculates the cryptographic SHA-256 digest of artifact payload data.
     */
    fun computeArtifactChecksum(outputData: String): String {
        return ArtifactValidator.computeSha256(outputData.toByteArray(Charsets.UTF_8))
    }

    /**
     * Verifies that the produced output satisfies minimum length, non-empty criteria,
     * and valid cryptographic hash verification.
     */
    fun verifyDeliverableContract(response: ToolExecutionResponse): Boolean {
        if (!response.success || response.statusCode !in 200..299) return false
        if (response.outputData.isBlank() || response.outputData.length < 15) return false
        val recalculated = computeArtifactChecksum(response.outputData)
        val hashValid = validateArtifact(recalculated)
        val digestMatches = response.sha256Digest.isBlank() || response.sha256Digest.equals(recalculated, ignoreCase = true)
        return hashValid && digestMatches
    }
}

/**
 * Encapsulated execution request parameters.
 */
data class ExecutionRequest(
    val tool: FreeTool,
    val prompt: String,
    val taskDescription: String,
    val parameters: Map<String, Any> = emptyMap()
)

/**
 * Telemetry report for active tool monitoring.
 */
data class ExecutionTelemetry(
    val adapterId: String,
    val toolId: String,
    val toolName: String,
    val status: RuntimeHealthStatus,
    val latencyMs: Long,
    val timestampMs: Long = System.currentTimeMillis()
)
