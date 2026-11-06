package com.example.engine.adapter

import com.example.model.FreeTool
import com.example.model.RuntimeHealthStatus
import com.example.model.ToolExecutionResponse

/**
 * Contract for genuine execution adapters that invoke external APIs,
 * local compilation engines, or SDK bridges.
 */
interface ToolExecutionAdapter {
    val adapterId: String

    /**
     * Executes the specific tool assignment with real parameters and produces verifiable output.
     */
    suspend fun execute(
        tool: FreeTool,
        prompt: String,
        taskDescription: String,
        parameters: Map<String, Any> = emptyMap()
    ): ToolExecutionResponse

    /**
     * Performs a runtime health check / ping to determine actual availability.
     */
    suspend fun healthCheck(tool: FreeTool): RuntimeHealthStatus
}
