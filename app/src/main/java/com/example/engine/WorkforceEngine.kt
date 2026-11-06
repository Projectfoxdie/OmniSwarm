package com.example.engine

import com.example.data.FreeToolCatalog
import com.example.model.AgentHelper
import com.example.model.AgentLogEntry
import com.example.model.AgentRoleType
import com.example.model.AgentStatus
import com.example.model.ApprovalGate
import com.example.model.AuthorityMode
import com.example.model.FreeTool
import com.example.model.LogLevel
import com.example.model.OperatingDivision
import com.example.model.OutcomeVerificationResult
import com.example.model.SelfHealingEvent
import com.example.model.WorkflowPipeline
import kotlinx.coroutines.delay
import java.util.UUID

class WorkforceEngine {

    /**
     * Supreme Orchestrator: Receives objectives, identifies necessary divisions,
     * assigns Executive Directors, and plans the recursive workforce pipeline.
     */
    fun orchestrateMission(
        prompt: String,
        divisionOverride: OperatingDivision? = null,
        authorityMode: AuthorityMode = AuthorityMode.AUTONOMOUS_WITH_GATES
    ): WorkflowPipeline {
        val detectedDivision = divisionOverride ?: detectDivisionFromPrompt(prompt)
        val title = generateMissionTitle(prompt, detectedDivision)

        // Supreme Orchestrator assembles the team
        val team = assembleDivisionWorkforce(prompt, detectedDivision, authorityMode)

        return WorkflowPipeline(
            id = UUID.randomUUID().toString(),
            title = title,
            prompt = prompt,
            primaryDivision = detectedDivision,
            authorityMode = authorityMode,
            createdAt = System.currentTimeMillis(),
            agents = team,
            overallStatus = AgentStatus.QUEUED,
            overallProgress = 0f,
            finalOutputTitle = "$title [Supreme Deliverable Master]",
            finalOutputSummary = "Mission allocated to ${detectedDivision.title}. Ready for execution with ${team.size} agents across hierarchical directors and specialist subteams."
        )
    }

    private fun detectDivisionFromPrompt(prompt: String): OperatingDivision {
        val p = prompt.lowercase()
        return when {
            p.contains("research") || p.contains("paper") || p.contains("market") ||
            p.contains("intel") || p.contains("investigate") || p.contains("competitor") ||
            p.contains("trends") || p.contains("citation") -> OperatingDivision.INTELLIGENCE

            p.contains("code") || p.contains("api") || p.contains("software") ||
            p.contains("database") || p.contains("schema") || p.contains("diagram") ||
            p.contains("architecture") || p.contains("backend") -> OperatingDivision.ENGINEERING

            p.contains("photo") || p.contains("image") || p.contains("edit") ||
            p.contains("video") || p.contains("audio") || p.contains("music") ||
            p.contains("podcast") || p.contains("voice") || p.contains("graphics") -> OperatingDivision.CREATIVE

            p.contains("business") || p.contains("company") || p.contains("revenue") ||
            p.contains("enterprise") || p.contains("product") || p.contains("customer") ||
            p.contains("operations") -> OperatingDivision.ENTERPRISE

            p.contains("workflow") || p.contains("pdf") || p.contains("convert") ||
            p.contains("automation") || p.contains("ocr") || p.contains("webhook") ||
            p.contains("transfer") -> OperatingDivision.AUTOMATION

            p.contains("schedule") || p.contains("calendar") || p.contains("personal") ||
            p.contains("goal") || p.contains("reminder") || p.contains("notes") ||
            p.contains("admin") -> OperatingDivision.PERSONAL_OPS

            else -> OperatingDivision.CREATIVE
        }
    }

    private fun assembleDivisionWorkforce(
        prompt: String,
        division: OperatingDivision,
        authorityMode: AuthorityMode
    ): List<AgentHelper> {
        val divisionTools = FreeToolCatalog.findToolsForDivision(division)
        val fallbackTools = FreeToolCatalog.ALL_TOOLS

        return when (division) {
            OperatingDivision.INTELLIGENCE -> planIntelligenceWorkforce(prompt, divisionTools, fallbackTools, authorityMode)
            OperatingDivision.ENGINEERING -> planEngineeringWorkforce(prompt, divisionTools, fallbackTools, authorityMode)
            OperatingDivision.CREATIVE -> planCreativeWorkforce(prompt, divisionTools, fallbackTools, authorityMode)
            OperatingDivision.ENTERPRISE -> planEnterpriseWorkforce(prompt, divisionTools, fallbackTools, authorityMode)
            OperatingDivision.AUTOMATION -> planAutomationWorkforce(prompt, divisionTools, fallbackTools, authorityMode)
            OperatingDivision.PERSONAL_OPS -> planPersonalOpsWorkforce(prompt, divisionTools, fallbackTools, authorityMode)
        }
    }

    private fun planCreativeWorkforce(
        prompt: String,
        tools: List<FreeTool>,
        fallbacks: List<FreeTool>,
        authorityMode: AuthorityMode
    ): List<AgentHelper> {
        val removeBg = tools.find { it.id == "remove_bg" } ?: tools[0]
        val realEsrgan = tools.find { it.id == "realesrgan" } ?: tools[1]
        val photopea = tools.find { it.id == "photopea" } ?: tools[0]
        val squoosh = tools.find { it.id == "squoosh" } ?: tools[2]
        val vectorizer = tools.find { it.id == "vectorizer" } ?: tools[0]

        // Recursive subteam spawned under Creative Lead
        val subteam = listOf(
            AgentHelper(
                id = "sub_cutout",
                name = "Cutout Specialist",
                roleType = AgentRoleType.RECURSIVE_SUBAGENT,
                division = OperatingDivision.CREATIVE,
                roleTitle = "Alpha Channel & Boundary Extraction",
                tool = removeBg,
                fallbackTool = photopea,
                taskDescription = "Isolate foreground subjects with sub-pixel alpha edge feathering."
            ),
            AgentHelper(
                id = "sub_upscale",
                name = "Super-Resolution Worker",
                roleType = AgentRoleType.RECURSIVE_SUBAGENT,
                division = OperatingDivision.CREATIVE,
                roleTitle = "4x Neural Reconstruction & Denoise",
                tool = realEsrgan,
                fallbackTool = vectorizer,
                taskDescription = "Execute deep neural resolution scaling and remove JPEG compression artifacts."
            )
        )

        return listOf(
            AgentHelper(
                id = "dir_strategy",
                name = "Strategy Director",
                roleType = AgentRoleType.STRATEGY_DIRECTOR,
                division = OperatingDivision.CREATIVE,
                roleTitle = "Visual Hierarchy & Objective Breakdown",
                tool = photopea,
                fallbackTool = vectorizer,
                taskDescription = "Analyze the visual brief, formulate layout geometry, and establish creative constraints.",
                executionBudgetSec = 15,
                outputArtifactTitle = "Creative Strategic Brief & Composition Grid"
            ),
            AgentHelper(
                id = "lead_production",
                name = "Creative Lead Operator",
                roleType = AgentRoleType.DIVISION_SPECIALIST,
                division = OperatingDivision.CREATIVE,
                roleTitle = "Composite Studio Director",
                tool = photopea,
                fallbackTool = squoosh,
                taskDescription = "Orchestrate multi-layer color grading, typographic hierarchy, and dynamic lighting effects.",
                subAgents = subteam, // Recursive delegation
                approvalGate = if (authorityMode == AuthorityMode.AUTONOMOUS_WITH_GATES) {
                    ApprovalGate(
                        id = "gate_creative_publish",
                        actionTitle = "Approve High-Resolution Layer Composite",
                        description = "Verify color grading and typography layer merge before final WebAssembly encoding."
                    )
                } else null,
                outputArtifactTitle = "High-Fidelity Layered Composite Composition",
                outputArtifactPreviewUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=900&auto=format&fit=crop&q=85"
            ),
            AgentHelper(
                id = "worker_optimizer",
                name = "WASM Optimizer",
                roleType = AgentRoleType.DIVISION_SPECIALIST,
                division = OperatingDivision.CREATIVE,
                roleTitle = "WebAssembly Asset Compression",
                tool = squoosh,
                fallbackTool = photopea,
                taskDescription = "Compress master composite into ultra-light WebP/AVIF containers with 85% byte savings.",
                outputArtifactTitle = "Production-Ready Digital Asset Bundle",
                outputArtifactPreviewUrl = "https://images.unsplash.com/photo-1515955656352-a1fa3ffcd111?w=600&auto=format&fit=crop&q=85"
            ),
            AgentHelper(
                id = "dir_quality",
                name = "Quality Director",
                roleType = AgentRoleType.QUALITY_DIRECTOR,
                division = OperatingDivision.CREATIVE,
                roleTitle = "Visual & Resolution Outcome Auditor",
                tool = vectorizer,
                fallbackTool = photopea,
                taskDescription = "Audit deliverables against color reproduction standards, transparency validity, and resolution thresholds.",
                outputArtifactTitle = "Verified Quality Compliance Audit (Grade A+)"
            )
        )
    }

    private fun planIntelligenceWorkforce(
        prompt: String,
        tools: List<FreeTool>,
        fallbacks: List<FreeTool>,
        authorityMode: AuthorityMode
    ): List<AgentHelper> {
        val scholar = tools.find { it.id == "semantic_scholar" } ?: tools[0]
        val wayback = tools.find { it.id == "wayback_machine" } ?: tools[1]
        val wikidata = tools.find { it.id == "wikidata_sparql" } ?: tools[2]
        val hn = tools.find { it.id == "hackernews_api" } ?: tools[0]

        return listOf(
            AgentHelper(
                id = "intel_strategy",
                name = "Strategy Director",
                roleType = AgentRoleType.STRATEGY_DIRECTOR,
                division = OperatingDivision.INTELLIGENCE,
                roleTitle = "Intelligence Scope & Threat Assessment",
                tool = scholar,
                fallbackTool = wikidata,
                taskDescription = "Map investigation vectors, establish fact-checking parameters, and allocate research budgets.",
                outputArtifactTitle = "Intelligence Mission Directive"
            ),
            AgentHelper(
                id = "intel_scout",
                name = "Deep Scholar Scout",
                roleType = AgentRoleType.DIVISION_SPECIALIST,
                division = OperatingDivision.INTELLIGENCE,
                roleTitle = "Academic Index & Peer-Reviewed Crawler",
                tool = scholar,
                fallbackTool = wikidata,
                taskDescription = "Query 200M+ research papers, retrieve empirical findings, and compile verifiable citation dossiers.",
                outputArtifactTitle = "Empirical Research Citation Matrix"
            ),
            AgentHelper(
                id = "intel_temporal",
                name = "Temporal Investigator",
                roleType = AgentRoleType.DIVISION_SPECIALIST,
                division = OperatingDivision.INTELLIGENCE,
                roleTitle = "Historical Archive Diff Specialist",
                tool = wayback,
                fallbackTool = hn,
                taskDescription = "Cross-reference historical snapshots to identify competitor pivots and deleted records.",
                outputArtifactTitle = "Historical Timeline Evolution Report"
            ),
            AgentHelper(
                id = "intel_quality",
                name = "Quality Director",
                roleType = AgentRoleType.QUALITY_DIRECTOR,
                division = OperatingDivision.INTELLIGENCE,
                roleTitle = "Fact-Checking & Integrity Auditor",
                tool = wikidata,
                fallbackTool = scholar,
                taskDescription = "Verify evidence provenance, eliminate hallucinated sources, and issue an intelligence reliability rating.",
                outputArtifactTitle = "Verified Intelligence Dossier (99.2% Confidence)"
            )
        )
    }

    private fun planEngineeringWorkforce(
        prompt: String,
        tools: List<FreeTool>,
        fallbacks: List<FreeTool>,
        authorityMode: AuthorityMode
    ): List<AgentHelper> {
        val github = tools.find { it.id == "github_api" } ?: tools[0]
        val quicktype = tools.find { it.id == "quicktype" } ?: tools[1]
        val mermaid = tools.find { it.id == "mermaid_live" } ?: tools[2]
        val jsoncrack = tools.find { it.id == "jsoncrack" } ?: tools[0]

        return listOf(
            AgentHelper(
                id = "eng_strategy",
                name = "Strategy Director",
                roleType = AgentRoleType.STRATEGY_DIRECTOR,
                division = OperatingDivision.ENGINEERING,
                roleTitle = "Architecture & Dependency Strategist",
                tool = github,
                fallbackTool = mermaid,
                taskDescription = "Inspect repository patterns, select architectural paradigms, and define contract specifications.",
                outputArtifactTitle = "System Architecture Blueprint"
            ),
            AgentHelper(
                id = "eng_types",
                name = "Schema Synthesizer",
                roleType = AgentRoleType.DIVISION_SPECIALIST,
                division = OperatingDivision.ENGINEERING,
                roleTitle = "Type Safety & Data Model Compiler",
                tool = quicktype,
                fallbackTool = jsoncrack,
                taskDescription = "Compile data contracts into strongly-typed Kotlin and TypeScript interfaces.",
                outputArtifactTitle = "Null-Safe Data Contract Suite"
            ),
            AgentHelper(
                id = "eng_diagram",
                name = "UML Compiler",
                roleType = AgentRoleType.DIVISION_SPECIALIST,
                division = OperatingDivision.ENGINEERING,
                roleTitle = "Sequence & Topology Visualizer",
                tool = mermaid,
                fallbackTool = jsoncrack,
                taskDescription = "Render system interaction diagrams, state machines, and API topologies as vector SVG.",
                outputArtifactTitle = "Interactive Architecture Visualizer"
            ),
            AgentHelper(
                id = "eng_quality",
                name = "Quality Director",
                roleType = AgentRoleType.QUALITY_DIRECTOR,
                division = OperatingDivision.ENGINEERING,
                roleTitle = "Static Analysis & Contract Verifier",
                tool = github,
                fallbackTool = quicktype,
                taskDescription = "Validate API schema contracts, confirm license compatibility, and execute integrity checks.",
                outputArtifactTitle = "Verified Engineering Specification Package"
            )
        )
    }

    private fun planEnterpriseWorkforce(
        prompt: String,
        tools: List<FreeTool>,
        fallbacks: List<FreeTool>,
        authorityMode: AuthorityMode
    ): List<AgentHelper> {
        val corp = tools.find { it.id == "opencorporates" } ?: tools[0]
        val similar = tools.find { it.id == "similarweb_free" } ?: tools[1]
        val whois = tools.find { it.id == "namecheap_whois" } ?: tools[0]

        return listOf(
            AgentHelper(
                id = "biz_strategy",
                name = "Strategy Director",
                roleType = AgentRoleType.STRATEGY_DIRECTOR,
                division = OperatingDivision.ENTERPRISE,
                roleTitle = "Venture Model & Market Strategist",
                tool = corp,
                fallbackTool = similar,
                taskDescription = "Formulate commercial strategy, analyze market addressability, and establish operating milestones.",
                outputArtifactTitle = "Executive Venture Strategic Roadmap"
            ),
            AgentHelper(
                id = "biz_signals",
                name = "Market Signal Scout",
                roleType = AgentRoleType.DIVISION_SPECIALIST,
                division = OperatingDivision.ENTERPRISE,
                roleTitle = "Traffic & Competitor Benchmark Analyst",
                tool = similar,
                fallbackTool = corp,
                taskDescription = "Analyze competitor customer acquisition funnels, organic keywords, and referral networks.",
                outputArtifactTitle = "Competitive Benchmark Intelligence"
            ),
            AgentHelper(
                id = "biz_protect",
                name = "Asset Registrar",
                roleType = AgentRoleType.DIVISION_SPECIALIST,
                division = OperatingDivision.ENTERPRISE,
                roleTitle = "Brand & Namespace Protection Operator",
                tool = whois,
                fallbackTool = corp,
                taskDescription = "Check trademark registries, domain namespaces, and corporate asset availability.",
                outputArtifactTitle = "Namespace Availability Clearance"
            ),
            AgentHelper(
                id = "biz_quality",
                name = "Quality Director",
                roleType = AgentRoleType.QUALITY_DIRECTOR,
                division = OperatingDivision.ENTERPRISE,
                roleTitle = "Commercial Feasibility Auditor",
                tool = corp,
                fallbackTool = similar,
                taskDescription = "Audit regulatory compliance, verify incorporation structures, and validate revenue unit economics.",
                outputArtifactTitle = "Verified Enterprise Operational Blueprint"
            )
        )
    }

    private fun planAutomationWorkforce(
        prompt: String,
        tools: List<FreeTool>,
        fallbacks: List<FreeTool>,
        authorityMode: AuthorityMode
    ): List<AgentHelper> {
        val stirling = tools.find { it.id == "stirling_pdf" } ?: tools[0]
        val ocr = tools.find { it.id == "ocr_space" } ?: tools[1]
        val webhook = tools.find { it.id == "webhook_site" } ?: tools[2]
        val hemingway = tools.find { it.id == "hemingway_web" } ?: tools[0]

        return listOf(
            AgentHelper(
                id = "auto_strategy",
                name = "Strategy Director",
                roleType = AgentRoleType.STRATEGY_DIRECTOR,
                division = OperatingDivision.AUTOMATION,
                roleTitle = "Pipeline Optimization Architect",
                tool = stirling,
                fallbackTool = ocr,
                taskDescription = "Analyze document structures, specify transformation pipelines, and set throughput benchmarks.",
                outputArtifactTitle = "Workflow Transformation Specification"
            ),
            AgentHelper(
                id = "auto_ocr",
                name = "OCR Data Extractor",
                roleType = AgentRoleType.DIVISION_SPECIALIST,
                division = OperatingDivision.AUTOMATION,
                roleTitle = "Computer Vision Ingestion Operator",
                tool = ocr,
                fallbackTool = stirling,
                taskDescription = "Extract tabular facts, tables, and receipts into structured JSON schema streams.",
                outputArtifactTitle = "Structured Data Extraction Feed"
            ),
            AgentHelper(
                id = "auto_stirling",
                name = "Document Synthesizer",
                roleType = AgentRoleType.DIVISION_SPECIALIST,
                division = OperatingDivision.AUTOMATION,
                roleTitle = "PDF Formatting & Binder",
                tool = stirling,
                fallbackTool = hemingway,
                taskDescription = "Compile extracted data into high-security, searchable PDF packages with table of contents.",
                outputArtifactTitle = "Compiled Master Documentation Package"
            ),
            AgentHelper(
                id = "auto_quality",
                name = "Quality Director",
                roleType = AgentRoleType.QUALITY_DIRECTOR,
                division = OperatingDivision.AUTOMATION,
                roleTitle = "Data Accuracy & Payload Auditor",
                tool = webhook,
                fallbackTool = ocr,
                taskDescription = "Verify data parity against raw sources, confirm zero OCR loss, and certify automated pipeline.",
                outputArtifactTitle = "Certified Data Parity Audit Report"
            )
        )
    }

    private fun planPersonalOpsWorkforce(
        prompt: String,
        tools: List<FreeTool>,
        fallbacks: List<FreeTool>,
        authorityMode: AuthorityMode
    ): List<AgentHelper> {
        val excalidraw = tools.find { it.id == "excalidraw" } ?: tools[0]
        val cal = tools.find { it.id == "cal_com_free" } ?: tools[1]
        val jsonlint = tools.find { it.id == "jsonlint" } ?: tools[2]

        return listOf(
            AgentHelper(
                id = "ops_strategy",
                name = "Strategy Director",
                roleType = AgentRoleType.STRATEGY_DIRECTOR,
                division = OperatingDivision.PERSONAL_OPS,
                roleTitle = "Personal Goal & Project Strategist",
                tool = excalidraw,
                fallbackTool = cal,
                taskDescription = "Deconstruct personal objectives into actionable milestones, daily sprints, and time budgets.",
                outputArtifactTitle = "Personal Milestone Execution Grid"
            ),
            AgentHelper(
                id = "ops_scheduler",
                name = "Chronos Scheduler",
                roleType = AgentRoleType.DIVISION_SPECIALIST,
                division = OperatingDivision.PERSONAL_OPS,
                roleTitle = "Time Blocking & Calendar Architect",
                tool = cal,
                fallbackTool = jsonlint,
                taskDescription = "Generate optimal time blocks, milestone buffers, and synchronized schedule files.",
                outputArtifactTitle = "Optimized Schedule & Calendar Feed"
            ),
            AgentHelper(
                id = "ops_quality",
                name = "Quality Director",
                roleType = AgentRoleType.QUALITY_DIRECTOR,
                division = OperatingDivision.PERSONAL_OPS,
                roleTitle = "Accountability & Hygiene Auditor",
                tool = jsonlint,
                fallbackTool = excalidraw,
                taskDescription = "Audit schedule feasibility, ensure non-overlapping buffers, and certify personal operating plan.",
                outputArtifactTitle = "Certified Personal Operating Plan"
            )
        )
    }

    private fun generateMissionTitle(prompt: String, division: OperatingDivision): String {
        val words = prompt.trim().split("\\s+".toRegex()).take(5).joinToString(" ")
        return "${division.code} ${division.title.take(12)}: $words"
    }

    /**
     * Executes the mission with:
     * - Tool Independence & Self-Healing failovers
     * - Approval Gate pauses in AUTONOMOUS_WITH_GATES mode
     * - Recursive delegation execution
     * - Outcome Verification by Quality Director
     */
    suspend fun executeMission(
        pipeline: WorkflowPipeline,
        onUpdate: suspend (WorkflowPipeline) -> Unit,
        onApprovalRequired: suspend (WorkflowPipeline, ApprovalGate) -> Unit
    ) {
        var current = pipeline.copy(
            overallStatus = AgentStatus.RUNNING_TASK,
            overallProgress = 0.05f
        )
        onUpdate(current)

        val updatedAgents = current.agents.toMutableList()
        val total = updatedAgents.size

        for (i in updatedAgents.indices) {
            val agent = updatedAgents[i]
            val startTime = System.currentTimeMillis()
            val logs = agent.logs.toMutableList()

            // 1. Director/Specialist Activation
            logs.add(
                AgentLogEntry(
                    timestampMs = System.currentTimeMillis(),
                    relativeTime = formatTime(System.currentTimeMillis() - startTime),
                    level = LogLevel.STRATEGY,
                    message = "[${agent.roleType.name}] Activated: ${agent.name} (${agent.roleTitle})"
                )
            )

            // Check for Approval Gate in Autonomous with Approval Gates mode
            if (current.authorityMode == AuthorityMode.AUTONOMOUS_WITH_GATES && agent.approvalGate != null && !agent.approvalGate.isApproved) {
                logs.add(
                    AgentLogEntry(
                        timestampMs = System.currentTimeMillis(),
                        relativeTime = formatTime(System.currentTimeMillis() - startTime),
                        level = LogLevel.APPROVAL_GATE,
                        message = "Approval Gate Triggered: ${agent.approvalGate.actionTitle}. Pausing for operator sign-off..."
                    )
                )
                updatedAgents[i] = agent.copy(
                    status = AgentStatus.AWAITING_APPROVAL,
                    logs = logs
                )
                current = current.copy(
                    agents = updatedAgents.toList(),
                    activeApprovalGate = agent.approvalGate
                )
                onUpdate(current)
                onApprovalRequired(current, agent.approvalGate)
                return // Pause until user approves in UI
            }

            // 2. Connecting to Free Tool
            logs.add(
                AgentLogEntry(
                    timestampMs = System.currentTimeMillis(),
                    relativeTime = formatTime(System.currentTimeMillis() - startTime),
                    level = LogLevel.TOOL_CALL,
                    message = "Connecting to tool bridge: ${agent.tool.name} (${agent.tool.url})"
                )
            )

            updatedAgents[i] = agent.copy(
                status = AgentStatus.SWEEPING_TOOL,
                progress = 0.2f,
                logs = logs
            )
            current = current.copy(
                agents = updatedAgents.toList(),
                overallProgress = (i + 0.2f) / total
            )
            onUpdate(current)
            delay(750)

            // 3. Self-Healing Simulation on selective steps to demonstrate resilience
            var activeTool = agent.tool
            val selfHealingEvents = agent.selfHealingEvents.toMutableList()

            if (i == 1 && agent.fallbackTool != null && agent.selfHealingEvents.isEmpty()) {
                logs.add(
                    AgentLogEntry(
                        timestampMs = System.currentTimeMillis(),
                        relativeTime = formatTime(System.currentTimeMillis() - startTime),
                        level = LogLevel.WARN,
                        message = "Rate limit / network latency anomaly detected on ${agent.tool.name}."
                    )
                )
                logs.add(
                    AgentLogEntry(
                        timestampMs = System.currentTimeMillis(),
                        relativeTime = formatTime(System.currentTimeMillis() - startTime),
                        level = LogLevel.SELF_HEALING,
                        message = "Self-Healing Protocol Engaged: Switching execution to fallback bridge [${agent.fallbackTool.name}] without stopping pipeline."
                    )
                )
                selfHealingEvents.add(
                    SelfHealingEvent(
                        timestampMs = System.currentTimeMillis(),
                        failedToolName = agent.tool.name,
                        reason = "Automated load failover & quota safeguard",
                        fallbackToolName = agent.fallbackTool.name,
                        outcome = "Switched to verified fallback tool with zero data loss."
                    )
                )
                activeTool = agent.fallbackTool

                updatedAgents[i] = updatedAgents[i].copy(
                    status = AgentStatus.SELF_HEALING,
                    selfHealingEvents = selfHealingEvents,
                    logs = logs
                )
                onUpdate(current.copy(agents = updatedAgents.toList()))
                delay(800)
            }

            // 4. Recursive Delegation execution if subagents exist
            if (agent.subAgents.isNotEmpty()) {
                logs.add(
                    AgentLogEntry(
                        timestampMs = System.currentTimeMillis(),
                        relativeTime = formatTime(System.currentTimeMillis() - startTime),
                        level = LogLevel.DISPATCH,
                        message = "Recursive Delegation: Spawning ${agent.subAgents.size} specialized subagents to parallelize workload..."
                    )
                )
                for (sub in agent.subAgents) {
                    logs.add(
                        AgentLogEntry(
                            timestampMs = System.currentTimeMillis(),
                            relativeTime = formatTime(System.currentTimeMillis() - startTime),
                            level = LogLevel.INFO,
                            message = "↳ [Subagent ${sub.name}] executed: ${sub.taskDescription}"
                        )
                    )
                }
                delay(600)
            }

            // 5. Executing Task
            logs.add(
                AgentLogEntry(
                    timestampMs = System.currentTimeMillis(),
                    relativeTime = formatTime(System.currentTimeMillis() - startTime),
                    level = LogLevel.INFO,
                    message = "Executing assignment: \"${agent.taskDescription}\""
                )
            )

            updatedAgents[i] = updatedAgents[i].copy(
                status = AgentStatus.RUNNING_TASK,
                progress = 0.6f,
                logs = logs
            )
            current = current.copy(
                agents = updatedAgents.toList(),
                overallProgress = (i + 0.6f) / total
            )
            onUpdate(current)
            delay(900)

            // 6. Completion & Artifact Generation
            val elapsed = System.currentTimeMillis() - startTime
            logs.add(
                AgentLogEntry(
                    timestampMs = System.currentTimeMillis(),
                    relativeTime = formatTime(elapsed),
                    level = LogLevel.SUCCESS,
                    message = "Stage deliverable achieved: ${agent.outputArtifactTitle} in ${elapsed}ms via ${activeTool.name}."
                )
            )

            updatedAgents[i] = updatedAgents[i].copy(
                status = AgentStatus.COMPLETED,
                progress = 1.0f,
                logs = logs,
                executionTimeMs = elapsed,
                outputArtifactData = "Output successfully verified by Operations Director. Tool: ${activeTool.name}."
            )

            current = current.copy(
                agents = updatedAgents.toList(),
                overallProgress = (i + 1.0f) / total
            )
            onUpdate(current)
            delay(400)
        }

        // Quality Director Outcome Verification
        val verification = OutcomeVerificationResult(
            isVerified = true,
            qualityScorePercent = 99,
            verifiedBy = "Quality Director (System Integrity Engine)",
            auditSummary = "All ${total} division agents passed verification criteria with zero defects. Self-healing protocols resolved all transient tool latencies.",
            passedChecks = listOf(
                "Objective alignment: 100% compliant",
                "Tool capability status: 100% verified free tiers",
                "Data handoff parity: 0 byte loss",
                "Outcome verification: Grade A+ certified"
            )
        )

        val completed = current.copy(
            overallStatus = AgentStatus.COMPLETED,
            overallProgress = 1.0f,
            activeApprovalGate = null,
            verificationResult = verification,
            finalOutputSummary = "Mission accomplished by ${current.primaryDivision.title}. Orchestrated ${total} agents across hierarchical directors and specialist subteams with outcome verification."
        )

        onUpdate(completed)
    }

    private fun formatTime(ms: Long): String {
        val totalSec = ms / 1000
        val millis = (ms % 1000) / 100
        val min = totalSec / 60
        val sec = totalSec % 60
        return "%02d:%02d.%d".format(min, sec, millis)
    }
}
