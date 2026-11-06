package com.example.engine.resolver

import com.example.model.FreeTool
import com.example.model.RuntimeHealthStatus

object CompatibleFallbackResolver {

    /**
     * Selects a truly compatible fallback tool that satisfies the required operation
     * and capabilities of the failed primary tool.
     */
    fun resolveCompatibleFallback(
        failingTool: FreeTool,
        requiredOperation: String,
        candidatePool: List<FreeTool>
    ): FreeTool? {
        val candidates = candidatePool.filter { it.id != failingTool.id }

        // Find best match by scoring capability compatibility
        val scored = candidates.map { candidate ->
            var score = 0

            // 1. Exact match on supportedOperations
            if (candidate.supportedOperations.any { it.equals(requiredOperation, ignoreCase = true) }) {
                score += 15
            }

            // 2. Overlap on generic capabilities
            val failingCapsLower = failingTool.capabilities.map { it.lowercase() }
            val candidateCapsLower = candidate.capabilities.map { it.lowercase() }
            val commonCaps = failingCapsLower.intersect(candidateCapsLower.toSet())
            score += commonCaps.size * 4

            // 3. Same operating division affinity
            if (candidate.division == failingTool.division) {
                score += 3
            }

            // 4. Runtime health preference
            if (candidate.healthStatus == RuntimeHealthStatus.HEALTHY_ONLINE) {
                score += 4
            }

            candidate to score
        }

        // Must have at least one capability match or operation match (score >= 4)
        return scored
            .filter { it.second >= 4 }
            .maxByOrNull { it.second }
            ?.first
    }
}
