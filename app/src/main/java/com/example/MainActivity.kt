package com.example

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AgentStatus
import com.example.model.DisplayMode
import com.example.ui.NavigationTab
import com.example.ui.WorkflowViewModel
import com.example.ui.components.AgentDrawer
import com.example.ui.components.DeliverablesSheet
import com.example.ui.components.FeedbackDialog
import com.example.ui.components.InAppToolWorkspaceDialog
import com.example.ui.components.OmniSwarmTopBar
import com.example.ui.components.SettingsDialog
import com.example.ui.components.StandaloneAppDialog
import com.example.ui.components.SweepRadarDialog
import com.example.ui.screens.CompletedResultScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ToolCatalogScreen
import com.example.ui.screens.WorkflowExecutionScreen
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.MintSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TechBackground
import com.example.ui.theme.TechSurface
import com.example.ui.theme.TechSurfaceElevated
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                OmniSwarmApp()
            }
        }
    }
}

@Composable
fun OmniSwarmApp(viewModel: WorkflowViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val activity = context as? Activity
    val window = activity?.window

    // Full screen immersive mode handler
    LaunchedEffect(uiState.isFullscreen) {
        window?.let { win ->
            val insetsController = WindowCompat.getInsetsController(win, win.decorView)
            if (uiState.isFullscreen) {
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
                insetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    LaunchedEffect(uiState.infoMessage) {
        uiState.infoMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearInfoMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = TechBackground,
        contentWindowInsets = if (uiState.isFullscreen) {
            WindowInsets(0, 0, 0, 0)
        } else {
            WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            OmniSwarmTopBar(
                displayMode = uiState.displayMode,
                isFullscreen = uiState.isFullscreen,
                activeDivision = uiState.selectedDivision,
                feedbackCount = uiState.submittedFeedbackList.size,
                onToggleDisplayMode = { viewModel.toggleDisplayMode() },
                onToggleFullscreen = { viewModel.toggleFullscreen() },
                onOpenFeedback = { viewModel.setShowFeedbackDialog(true) },
                onOpenSettings = { viewModel.setShowSettingsDialog(true) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = TechSurface,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = uiState.currentTab == NavigationTab.HOME,
                    onClick = { viewModel.navigateTo(NavigationTab.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Mission") },
                    label = { Text("Mission", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00383D),
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_home")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == NavigationTab.GRAPH || uiState.currentTab == NavigationTab.RESULT,
                    onClick = { viewModel.navigateTo(NavigationTab.GRAPH) },
                    icon = {
                        val isRunning = uiState.isExecuting
                        if (isRunning) {
                            BadgedBox(badge = { Badge(containerColor = MintSecondary) }) {
                                Icon(Icons.Default.AccountTree, contentDescription = "Workflow")
                            }
                        } else {
                            Icon(Icons.Default.AccountTree, contentDescription = "Workflow")
                        }
                    },
                    label = { Text("Workflow", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00383D),
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_graph")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == NavigationTab.TOOLS,
                    onClick = { viewModel.navigateTo(NavigationTab.TOOLS) },
                    icon = { Icon(Icons.Default.Hub, contentDescription = "Tools") },
                    label = { Text("Tools", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00383D),
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_tools")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == NavigationTab.HISTORY,
                    onClick = { viewModel.navigateTo(NavigationTab.HISTORY) },
                    icon = { Icon(Icons.Default.History, contentDescription = "Projects") },
                    label = { Text("Projects", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00383D),
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_history")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = if (uiState.displayMode == DisplayMode.DESKTOP) 1600.dp else 680.dp)
            ) {
                when (uiState.currentTab) {
                    NavigationTab.HOME -> {
                        HomeScreen(
                            uiState = uiState,
                            onDivisionSelected = { viewModel.selectDivision(it) },
                            onAuthorityModeSelected = { viewModel.setAuthorityMode(it) },
                            onDeployWorkforce = { prompt, division, mode -> viewModel.deployWorkforce(prompt, division, mode) },
                            onOpenSavedWorkflow = { viewModel.loadSavedWorkflow(it) },
                            onNavigateTo = { viewModel.navigateTo(it) },
                            onOpenStandaloneInfo = { viewModel.setShowStandaloneInfoDialog(true) }
                        )
                    }

                    NavigationTab.GRAPH -> {
                        WorkflowExecutionScreen(
                            pipeline = uiState.activePipeline,
                            selectedAgent = uiState.selectedAgent,
                            isExecuting = uiState.isExecuting,
                            onAgentSelected = { viewModel.selectAgent(it) },
                            onBackToHome = { viewModel.navigateTo(NavigationTab.HOME) },
                            onRunPipeline = { viewModel.runCurrentMission() },
                            onShowDeliverables = { viewModel.navigateTo(NavigationTab.RESULT) },
                            onApproveGate = { viewModel.approveCurrentGate() }
                        )
                    }

                    NavigationTab.RESULT -> {
                        CompletedResultScreen(
                            pipeline = uiState.activePipeline,
                            onBack = { viewModel.navigateTo(NavigationTab.GRAPH) },
                            onViewWorkflowGraph = { viewModel.navigateTo(NavigationTab.GRAPH) },
                            onStartNewMission = { viewModel.navigateTo(NavigationTab.HOME) },
                            onOpenInAppWorkspace = { viewModel.openInAppWorkspace(it) }
                        )
                    }

                    NavigationTab.TOOLS -> {
                        ToolCatalogScreen(
                            tools = uiState.toolCatalog,
                            searchQuery = uiState.toolSearchQuery,
                            onSearchQueryChanged = { viewModel.searchTools(it) },
                            onOpenInAppWorkspace = { viewModel.openInAppWorkspace(it) }
                        )
                    }

                    NavigationTab.HISTORY -> {
                        HistoryScreen(
                            savedWorkflows = uiState.savedWorkflows,
                            onOpenWorkflow = { viewModel.loadSavedWorkflow(it) },
                            onOpenResult = { viewModel.openResultView(it) },
                            onDeleteWorkflow = { viewModel.deleteSavedWorkflow(it) },
                            onStartNewMission = { viewModel.navigateTo(NavigationTab.HOME) }
                        )
                    }
                }

                // Floating quick exit button in Full Screen mode
                if (uiState.isFullscreen) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = TechSurfaceElevated.copy(alpha = 0.92f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .clickable { viewModel.toggleFullscreen() }
                            .testTag("floating_exit_fullscreen_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FullscreenExit,
                                contentDescription = "Exit Full Screen",
                                tint = CyanPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Exit Full Screen",
                                color = CyanPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Radar dialog when sweeping network
                if (uiState.isSweepingDialogVisible) {
                    SweepRadarDialog(
                        statusText = uiState.sweepingProgressText
                    )
                }

                // Inspection Drawer for selected agent
                uiState.selectedAgent?.let { agent ->
                    AgentDrawer(
                        agent = agent,
                        onDismiss = { viewModel.selectAgent(null) },
                        onOpenInAppWorkspace = { viewModel.openInAppWorkspace(it) }
                    )
                }

                // In-App Interactive Web Workspace Dialog
                uiState.activeWorkspaceTool?.let { tool ->
                    InAppToolWorkspaceDialog(
                        tool = tool,
                        onDismiss = { viewModel.openInAppWorkspace(null) }
                    )
                }

                // Standalone APK and Download Info Dialog
                if (uiState.showStandaloneInfoDialog) {
                    StandaloneAppDialog(
                        onDismiss = { viewModel.setShowStandaloneInfoDialog(false) }
                    )
                }

                // Settings Dialog
                if (uiState.showSettingsDialog) {
                    SettingsDialog(
                        settings = uiState.settings,
                        onUpdateSettings = { viewModel.updateSettings(it) },
                        onOpenFeedback = {
                            viewModel.setShowSettingsDialog(false)
                            viewModel.setShowFeedbackDialog(true)
                        },
                        onOpenStandaloneExport = {
                            viewModel.setShowSettingsDialog(false)
                            viewModel.setShowStandaloneInfoDialog(true)
                        },
                        onDismiss = { viewModel.setShowSettingsDialog(false) }
                    )
                }

                // Feedback Dialog
                if (uiState.showFeedbackDialog) {
                    FeedbackDialog(
                        history = uiState.submittedFeedbackList,
                        onSubmitFeedback = { category, rating, title, message, includeDiagnostics ->
                            viewModel.submitFeedback(category, rating, title, message, includeDiagnostics)
                        },
                        onDismiss = { viewModel.setShowFeedbackDialog(false) }
                    )
                }

                // Deliverables Sheet
                if (uiState.showOutputSheet && uiState.activePipeline != null) {
                    DeliverablesSheet(
                        pipeline = uiState.activePipeline!!,
                        onDismiss = { viewModel.setShowOutputSheet(false) }
                    )
                }
            }
        }
    }
}
