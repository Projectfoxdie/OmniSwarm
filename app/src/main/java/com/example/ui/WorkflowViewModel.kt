package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FreeToolCatalog
import com.example.data.WorkflowRepository
import com.example.engine.WorkforceEngine
import com.example.model.AgentHelper
import com.example.model.AgentStatus
import com.example.model.ApprovalGate
import com.example.model.AuthorityMode
import com.example.model.FreeTool
import com.example.model.OperatingDivision
import com.example.model.WorkflowPipeline
import com.example.util.ApkExportHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.example.model.AppSettings
import com.example.model.DisplayMode
import com.example.model.FeedbackCategory
import com.example.model.FeedbackSubmission

enum class NavigationTab {
    HOME,
    GRAPH,
    RESULT,
    TOOLS,
    HISTORY
}

data class WorkflowUiState(
    val currentTab: NavigationTab = NavigationTab.HOME,
    val selectedDivision: OperatingDivision = OperatingDivision.CREATIVE,
    val authorityMode: AuthorityMode = AuthorityMode.AUTONOMOUS_WITH_GATES,
    val activePipeline: WorkflowPipeline? = null,
    val selectedAgent: AgentHelper? = null,
    val activeWorkspaceTool: FreeTool? = null,
    val isSweepingDialogVisible: Boolean = false,
    val sweepingProgressText: String = "",
    val savedWorkflows: List<WorkflowPipeline> = emptyList(),
    val toolCatalog: List<FreeTool> = FreeToolCatalog.ALL_TOOLS,
    val toolSearchQuery: String = "",
    val isExecuting: Boolean = false,
    val showOutputSheet: Boolean = false,
    val showStandaloneInfoDialog: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val showFeedbackDialog: Boolean = false,
    val displayMode: DisplayMode = DisplayMode.MOBILE,
    val isFullscreen: Boolean = false,
    val settings: AppSettings = AppSettings(),
    val submittedFeedbackList: List<FeedbackSubmission> = emptyList(),
    val infoMessage: String? = null
)

class WorkflowViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkflowRepository(application)
    private val engine = WorkforceEngine()

    private val _uiState = MutableStateFlow(WorkflowUiState())
    val uiState: StateFlow<WorkflowUiState> = _uiState.asStateFlow()

    private var executionJob: Job? = null

    init {
        // Collect saved workflows from local Room persistence
        viewModelScope.launch {
            repository.allWorkflows.collect { workflows ->
                _uiState.update { it.copy(savedWorkflows = workflows) }
            }
        }
    }

    fun selectDivision(division: OperatingDivision) {
        _uiState.update { it.copy(selectedDivision = division) }
    }

    fun setAuthorityMode(mode: AuthorityMode) {
        _uiState.update { it.copy(authorityMode = mode) }
    }

    fun navigateTo(tab: NavigationTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun selectAgent(agent: AgentHelper?) {
        _uiState.update { it.copy(selectedAgent = agent) }
    }

    fun openInAppWorkspace(tool: FreeTool?) {
        _uiState.update { it.copy(activeWorkspaceTool = tool) }
    }

    fun setShowOutputSheet(show: Boolean) {
        _uiState.update { it.copy(showOutputSheet = show) }
    }

    fun setShowStandaloneInfoDialog(show: Boolean) {
        _uiState.update { it.copy(showStandaloneInfoDialog = show) }
    }

    fun clearInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }

    fun setDisplayMode(mode: DisplayMode) {
        _uiState.update {
            it.copy(
                displayMode = mode,
                settings = it.settings.copy(displayMode = mode),
                infoMessage = "Switched to ${mode.label}"
            )
        }
    }

    fun toggleDisplayMode() {
        val next = if (_uiState.value.displayMode == DisplayMode.MOBILE) DisplayMode.DESKTOP else DisplayMode.MOBILE
        setDisplayMode(next)
    }

    fun setFullscreen(fullscreen: Boolean) {
        _uiState.update {
            it.copy(
                isFullscreen = fullscreen,
                settings = it.settings.copy(isFullscreen = fullscreen),
                infoMessage = if (fullscreen) "Entered Full Screen mode" else "Exited Full Screen mode"
            )
        }
    }

    fun toggleFullscreen() {
        setFullscreen(!_uiState.value.isFullscreen)
    }

    fun setShowSettingsDialog(show: Boolean) {
        _uiState.update { it.copy(showSettingsDialog = show) }
    }

    fun setShowFeedbackDialog(show: Boolean) {
        _uiState.update { it.copy(showFeedbackDialog = show) }
    }

    fun updateSettings(newSettings: AppSettings) {
        _uiState.update {
            it.copy(
                settings = newSettings,
                displayMode = newSettings.displayMode,
                isFullscreen = newSettings.isFullscreen,
                infoMessage = "Settings saved."
            )
        }
    }

    fun submitFeedback(
        category: FeedbackCategory,
        rating: Int,
        title: String,
        message: String,
        includeDiagnostics: Boolean = true
    ): FeedbackSubmission {
        val diagnostics = if (includeDiagnostics) {
            "OS: Android API ${android.os.Build.VERSION.SDK_INT} | Device: ${android.os.Build.MODEL} | OmniSwarm v11.0 | Division: ${_uiState.value.selectedDivision.name} | Viewport: ${_uiState.value.displayMode.name}"
        } else ""

        val submission = FeedbackSubmission(
            category = category,
            rating = rating,
            title = title.ifBlank { "OmniSwarm Feedback" },
            message = message,
            includeDiagnostics = includeDiagnostics,
            diagnosticDetails = diagnostics
        )

        _uiState.update { state ->
            state.copy(
                submittedFeedbackList = listOf(submission) + state.submittedFeedbackList,
                infoMessage = "Thank you! Feedback #${submission.id} logged to QA registry."
            )
        }
        return submission
    }


    fun searchTools(query: String) {
        val filtered = FreeToolCatalog.searchTools(query)
        _uiState.update {
            it.copy(
                toolSearchQuery = query,
                toolCatalog = filtered
            )
        }
    }

    /**
     * Supreme Orchestrator: Sweeps resources, delegates across divisions,
     * recruits Executive Directors and specialist subagents.
     */
    fun deployWorkforce(
        prompt: String,
        division: OperatingDivision = _uiState.value.selectedDivision,
        authorityMode: AuthorityMode = _uiState.value.authorityMode
    ) {
        if (prompt.isBlank()) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSweepingDialogVisible = true,
                    sweepingProgressText = "Supreme Orchestrator: Allocating mission to ${division.title}..."
                )
            }
            delay(500)
            _uiState.update {
                it.copy(sweepingProgressText = "Executive Directors establishing milestone budgets & tool contracts...")
            }
            delay(600)

            val pipeline = engine.orchestrateMission(prompt, division, authorityMode)

            _uiState.update {
                it.copy(
                    sweepingProgressText = "Recruited ${pipeline.agents.size} hierarchical agents. Initializing graph...",
                    activePipeline = pipeline
                )
            }
            delay(400)

            _uiState.update {
                it.copy(
                    isSweepingDialogVisible = false,
                    currentTab = NavigationTab.GRAPH
                )
            }

            runCurrentMission()
        }
    }

    fun runCurrentMission() {
        val current = _uiState.value.activePipeline ?: return
        if (_uiState.value.isExecuting) return

        executionJob?.cancel()
        executionJob = viewModelScope.launch {
            _uiState.update { it.copy(isExecuting = true) }

            engine.executeMission(
                pipeline = current,
                onUpdate = { updated ->
                    _uiState.update { state ->
                        val updatedSelected = if (state.selectedAgent != null) {
                            updated.agents.find { it.id == state.selectedAgent.id } ?: state.selectedAgent
                        } else null

                        state.copy(
                            activePipeline = updated,
                            selectedAgent = updatedSelected
                        )
                    }

                    if (updated.overallStatus == AgentStatus.COMPLETED) {
                        repository.saveWorkflow(updated)
                        _uiState.update {
                            it.copy(
                                isExecuting = false,
                                currentTab = NavigationTab.RESULT,
                                showOutputSheet = false,
                                infoMessage = "Quality Director verified outcome! Deliverables ready for review."
                            )
                        }
                    }
                },
                onApprovalRequired = { pausedPipeline, gate ->
                    _uiState.update { state ->
                        state.copy(
                            isExecuting = false,
                            activePipeline = pausedPipeline,
                            infoMessage = "Approval Required: ${gate.actionTitle}"
                        )
                    }
                }
            )
        }
    }

    /**
     * Operator signs off on an active approval gate in Autonomous with Approval Gates mode.
     */
    fun approveCurrentGate() {
        val current = _uiState.value.activePipeline ?: return
        val gate = current.activeApprovalGate ?: return

        val updatedAgents = current.agents.map { agent ->
            if (agent.approvalGate?.id == gate.id) {
                agent.copy(
                    approvalGate = agent.approvalGate.copy(isApproved = true),
                    status = AgentStatus.RUNNING_TASK
                )
            } else agent
        }

        val updatedPipeline = current.copy(
            activeApprovalGate = null,
            agents = updatedAgents
        )

        _uiState.update {
            it.copy(
                activePipeline = updatedPipeline,
                infoMessage = "Action Approved: Resuming autonomous execution..."
            )
        }

        // Resume execution
        runCurrentMission()
    }

    fun exportSelfApk() {
        viewModelScope.launch {
            val result = ApkExportHelper.exportApkToPublicDownloads(getApplication())
            result.onSuccess { msg ->
                _uiState.update { it.copy(infoMessage = msg) }
            }.onFailure { err ->
                _uiState.update { it.copy(infoMessage = "APK Export: ${err.message}") }
            }
        }
    }

    fun openResultView(pipeline: WorkflowPipeline) {
        _uiState.update {
            it.copy(
                activePipeline = pipeline,
                currentTab = NavigationTab.RESULT
            )
        }
    }

    fun loadSavedWorkflow(pipeline: WorkflowPipeline) {
        _uiState.update {
            it.copy(
                activePipeline = pipeline,
                currentTab = NavigationTab.GRAPH,
                selectedAgent = pipeline.agents.firstOrNull()
            )
        }
    }

    fun deleteSavedWorkflow(id: String) {
        viewModelScope.launch {
            repository.deleteWorkflow(id)
            _uiState.update { it.copy(infoMessage = "Mission archive entry removed.") }
        }
    }
}
