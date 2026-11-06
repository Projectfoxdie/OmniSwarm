package com.example.engine.adapter

import com.example.data.FreeToolCatalog
import com.example.model.FreeTool
import com.example.model.RuntimeHealthStatus
import com.example.model.ToolExecutionResponse

/**
 * Contract for genuine execution adapters that invoke external APIs,
 * local compilation engines, or SDK bridges.
 *
 * Implements ExecutionAdapter to provide functional execution, status monitoring, and artifact validation.
 */
interface ToolExecutionAdapter : ExecutionAdapter {
    override val adapterId: String
    override val implementationClass: String
        get() = this::class.java.name

    /**
     * Executes the requested tool operation by string name or prompt.
     */
    override suspend fun execute(operation: String): ToolExecutionResponse {
        val tool = FreeToolCatalog.findById(adapterId) ?: FreeToolCatalog.allTools.first()
        return execute(tool = tool, prompt = operation, taskDescription = operation, parameters = emptyMap())
    }

    /**
     * Executes the specific tool assignment with real parameters and produces verifiable output.
     */
    override suspend fun execute(
        tool: FreeTool,
        prompt: String,
        taskDescription: String,
        parameters: Map<String, Any>
    ): ToolExecutionResponse

    /**
     * Performs a runtime health check / ping to determine actual availability.
     */
    override suspend fun healthCheck(tool: FreeTool): RuntimeHealthStatus

    /**
     * Bridges monitorStatus(tool) to healthCheck.
     */
    override suspend fun monitorStatus(tool: FreeTool): RuntimeHealthStatus = healthCheck(tool)

    /**
     * Default adapter status monitor.
     */
    override suspend fun monitorStatus(): RuntimeHealthStatus {
        val tool = FreeToolCatalog.findById(adapterId) ?: FreeToolCatalog.allTools.first()
        return healthCheck(tool)
    }

    /**
     * Validates artifact cryptographic digest.
     */
    override fun validateArtifact(hash: String): Boolean {
        return hash.isNotBlank() && hash.length == 64 && hash.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
    }
}
