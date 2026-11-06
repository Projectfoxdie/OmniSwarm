package com.example.data

import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.db.WorkflowEntity
import com.example.model.AgentHelper
import com.example.model.AgentLogEntry
import com.example.model.AgentRoleType
import com.example.model.AgentStatus
import com.example.model.AuthorityMode
import com.example.model.FreeTool
import com.example.model.IntegrationType
import com.example.model.LogLevel
import com.example.model.OperatingDivision
import com.example.model.OutcomeVerificationResult
import com.example.model.RuntimeHealthStatus
import com.example.model.SelfHealingEvent
import com.example.model.ToolCapabilityStatus
import com.example.model.WorkflowPipeline
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class WorkflowRepository(context: Context) {
    private val dao = AppDatabase.getInstance(context).workflowDao()

    val allWorkflows: Flow<List<WorkflowPipeline>> = dao.getAllWorkflows().map { entities ->
        entities.map { entityToModel(it) }
    }

    suspend fun getWorkflow(id: String): WorkflowPipeline? {
        val entity = dao.getWorkflowById(id) ?: return null
        return entityToModel(entity)
    }

    suspend fun saveWorkflow(pipeline: WorkflowPipeline) {
        dao.insertOrUpdate(modelToEntity(pipeline))
    }

    suspend fun deleteWorkflow(id: String) {
        dao.deleteById(id)
    }

    private fun modelToEntity(pipeline: WorkflowPipeline): WorkflowEntity {
        val agentsArray = JSONArray()
        for (agent in pipeline.agents) {
            val agentObj = JSONObject().apply {
                put("id", agent.id)
                put("name", agent.name)
                put("roleType", agent.roleType.name)
                put("division", agent.division.name)
                put("roleTitle", agent.roleTitle)
                put("taskDescription", agent.taskDescription)
                put("status", agent.status.name)
                put("progress", agent.progress.toDouble())
                put("executionBudgetSec", agent.executionBudgetSec)
                put("inputArtifact", agent.inputArtifact ?: "")
                put("outputArtifactTitle", agent.outputArtifactTitle)
                put("outputArtifactPreviewUrl", agent.outputArtifactPreviewUrl ?: "")
                put("outputArtifactData", agent.outputArtifactData)
                put("executionTimeMs", agent.executionTimeMs)

                // Tool mapping
                val toolObj = JSONObject().apply {
                    put("id", agent.tool.id)
                    put("name", agent.tool.name)
                    put("division", agent.tool.division.name)
                    put("category", agent.tool.category)
                    put("url", agent.tool.url)
                    put("description", agent.tool.description)
                    put("freeTierType", agent.tool.freeTierType)
                    put("openSource", agent.tool.openSource)
                    put("status", agent.tool.status.name)
                    put("integrationType", agent.tool.integrationType.name)
                    put("adapterId", agent.tool.adapterId)
                    put("healthStatus", agent.tool.healthStatus.name)
                    put("rateLimitPolicy", agent.tool.rateLimitPolicy)
                    put("outputSchema", agent.tool.outputSchema)
                    val capArr = JSONArray()
                    agent.tool.capabilities.forEach { capArr.put(it) }
                    put("capabilities", capArr)
                    val opsArr = JSONArray()
                    agent.tool.supportedOperations.forEach { opsArr.put(it) }
                    put("supportedOperations", opsArr)
                }
                put("tool", toolObj)

                // Fallback Tool mapping if present
                agent.fallbackTool?.let { fb ->
                    val fbObj = JSONObject().apply {
                        put("id", fb.id)
                        put("name", fb.name)
                        put("division", fb.division.name)
                        put("url", fb.url)
                        put("freeTierType", fb.freeTierType)
                    }
                    put("fallbackTool", fbObj)
                }

                // Logs
                val logsArr = JSONArray()
                for (log in agent.logs) {
                    val logObj = JSONObject().apply {
                        put("timestampMs", log.timestampMs)
                        put("relativeTime", log.relativeTime)
                        put("level", log.level.name)
                        put("message", log.message)
                    }
                    logsArr.put(logObj)
                }
                put("logs", logsArr)

                // Self Healing events
                val shArr = JSONArray()
                for (sh in agent.selfHealingEvents) {
                    val shObj = JSONObject().apply {
                        put("timestampMs", sh.timestampMs)
                        put("failedToolName", sh.failedToolName)
                        put("reason", sh.reason)
                        put("fallbackToolName", sh.fallbackToolName)
                        put("outcome", sh.outcome)
                    }
                    shArr.put(shObj)
                }
                put("selfHealingEvents", shArr)
            }
            agentsArray.put(agentObj)
        }

        val artifactsArr = JSONArray()
        pipeline.finalArtifactUrls.forEach { artifactsArr.put(it) }

        return WorkflowEntity(
            id = pipeline.id,
            title = pipeline.title,
            prompt = pipeline.prompt,
            domainName = pipeline.primaryDivision.name,
            createdAt = pipeline.createdAt,
            overallStatus = pipeline.overallStatus.name,
            agentsJson = agentsArray.toString(),
            finalOutputTitle = pipeline.finalOutputTitle,
            finalOutputSummary = pipeline.finalOutputSummary,
            finalArtifactUrlsJson = artifactsArr.toString()
        )
    }

    private fun entityToModel(entity: WorkflowEntity): WorkflowPipeline {
        val division = try {
            OperatingDivision.valueOf(entity.domainName)
        } catch (_: Exception) {
            OperatingDivision.CREATIVE
        }

        val status = try {
            AgentStatus.valueOf(entity.overallStatus)
        } catch (_: Exception) {
            AgentStatus.COMPLETED
        }

        val agents = mutableListOf<AgentHelper>()
        try {
            val agentsArray = JSONArray(entity.agentsJson)
            for (i in 0 until agentsArray.length()) {
                val agentObj = agentsArray.getJSONObject(i)
                val toolObj = agentObj.getJSONObject("tool")

                val capList = mutableListOf<String>()
                val capArr = toolObj.optJSONArray("capabilities")
                if (capArr != null) {
                    for (c in 0 until capArr.length()) {
                        capList.add(capArr.getString(c))
                    }
                }

                val opsList = mutableListOf<String>()
                val opsArr = toolObj.optJSONArray("supportedOperations")
                if (opsArr != null) {
                    for (o in 0 until opsArr.length()) {
                        opsList.add(opsArr.getString(o))
                    }
                }

                val toolDivision = try {
                    OperatingDivision.valueOf(toolObj.optString("division", division.name))
                } catch (_: Exception) {
                    division
                }

                val toolStatus = try {
                    ToolCapabilityStatus.valueOf(toolObj.optString("status", "VERIFIED"))
                } catch (_: Exception) {
                    ToolCapabilityStatus.VERIFIED
                }

                val integrationType = try {
                    IntegrationType.valueOf(toolObj.optString("integrationType", "REST_API"))
                } catch (_: Exception) {
                    IntegrationType.REST_API
                }

                val healthStatus = try {
                    RuntimeHealthStatus.valueOf(toolObj.optString("healthStatus", "HEALTHY_ONLINE"))
                } catch (_: Exception) {
                    RuntimeHealthStatus.HEALTHY_ONLINE
                }

                val freeTool = FreeTool(
                    id = toolObj.optString("id", "tool_$i"),
                    name = toolObj.optString("name", "Free Online Utility"),
                    division = toolDivision,
                    category = toolObj.optString("category", "General"),
                    url = toolObj.optString("url", "https://example.com"),
                    description = toolObj.optString("description", ""),
                    freeTierType = toolObj.optString("freeTierType", "100% Free"),
                    capabilities = capList,
                    openSource = toolObj.optBoolean("openSource", false),
                    status = toolStatus,
                    integrationType = integrationType,
                    adapterId = toolObj.optString("adapterId", "default_adapter"),
                    supportedOperations = opsList,
                    rateLimitPolicy = toolObj.optString("rateLimitPolicy", "Free Anonymous Tier"),
                    outputSchema = toolObj.optString("outputSchema", "{}"),
                    healthStatus = healthStatus
                )

                val logsList = mutableListOf<AgentLogEntry>()
                val logsArr = agentObj.optJSONArray("logs")
                if (logsArr != null) {
                    for (l in 0 until logsArr.length()) {
                        val logObj = logsArr.getJSONObject(l)
                        val level = try {
                            LogLevel.valueOf(logObj.optString("level", "INFO"))
                        } catch (_: Exception) {
                            LogLevel.INFO
                        }
                        logsList.add(
                            AgentLogEntry(
                                timestampMs = logObj.optLong("timestampMs", 0L),
                                relativeTime = logObj.optString("relativeTime", "00:00.0"),
                                level = level,
                                message = logObj.optString("message", "")
                            )
                        )
                    }
                }

                val selfHealingList = mutableListOf<SelfHealingEvent>()
                val shArr = agentObj.optJSONArray("selfHealingEvents")
                if (shArr != null) {
                    for (s in 0 until shArr.length()) {
                        val shObj = shArr.getJSONObject(s)
                        selfHealingList.add(
                            SelfHealingEvent(
                                timestampMs = shObj.optLong("timestampMs", 0L),
                                failedToolName = shObj.optString("failedToolName", ""),
                                reason = shObj.optString("reason", ""),
                                fallbackToolName = shObj.optString("fallbackToolName", ""),
                                outcome = shObj.optString("outcome", "")
                            )
                        )
                    }
                }

                val agentStatus = try {
                    AgentStatus.valueOf(agentObj.optString("status", "QUEUED"))
                } catch (_: Exception) {
                    AgentStatus.QUEUED
                }

                val roleType = try {
                    AgentRoleType.valueOf(agentObj.optString("roleType", "DIVISION_SPECIALIST"))
                } catch (_: Exception) {
                    AgentRoleType.DIVISION_SPECIALIST
                }

                agents.add(
                    AgentHelper(
                        id = agentObj.optString("id", "agent_$i"),
                        name = agentObj.optString("name", "Helper Agent"),
                        roleType = roleType,
                        division = toolDivision,
                        roleTitle = agentObj.optString("roleTitle", "Specialist"),
                        tool = freeTool,
                        taskDescription = agentObj.optString("taskDescription", ""),
                        status = agentStatus,
                        progress = agentObj.optDouble("progress", 0.0).toFloat(),
                        logs = logsList,
                        selfHealingEvents = selfHealingList,
                        inputArtifact = agentObj.optString("inputArtifact").ifEmpty { null },
                        outputArtifactTitle = agentObj.optString("outputArtifactTitle", ""),
                        outputArtifactPreviewUrl = agentObj.optString("outputArtifactPreviewUrl").ifEmpty { null },
                        outputArtifactData = agentObj.optString("outputArtifactData", ""),
                        executionTimeMs = agentObj.optLong("executionTimeMs", 0L)
                    )
                )
            }
        } catch (_: Exception) {
            // Keep agents empty on JSON parsing edge case
        }

        val artifactsList = mutableListOf<String>()
        try {
            val artArr = JSONArray(entity.finalArtifactUrlsJson)
            for (a in 0 until artArr.length()) {
                artifactsList.add(artArr.getString(a))
            }
        } catch (_: Exception) {
            // Empty artifacts
        }

        return WorkflowPipeline(
            id = entity.id,
            title = entity.title,
            prompt = entity.prompt,
            primaryDivision = division,
            authorityMode = AuthorityMode.AUTONOMOUS_WITH_GATES,
            createdAt = entity.createdAt,
            agents = agents,
            overallStatus = status,
            overallProgress = if (status == AgentStatus.COMPLETED) 1f else 0f,
            verificationResult = OutcomeVerificationResult(
                isVerified = status == AgentStatus.COMPLETED,
                qualityScorePercent = if (status == AgentStatus.COMPLETED) 99 else 0,
                auditSummary = "Archived mission record from ${division.title}."
            ),
            finalOutputTitle = entity.finalOutputTitle,
            finalOutputSummary = entity.finalOutputSummary,
            finalArtifactUrls = artifactsList
        )
    }
}
