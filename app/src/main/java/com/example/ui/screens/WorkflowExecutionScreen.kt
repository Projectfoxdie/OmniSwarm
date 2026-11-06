package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AgentHelper
import com.example.model.AgentStatus
import com.example.model.WorkflowPipeline
import com.example.ui.components.WorkflowGraphCanvas
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.MintSecondary
import com.example.ui.theme.TechBackground
import com.example.ui.theme.TechOutline
import com.example.ui.theme.TechSurface
import com.example.ui.theme.TechSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun WorkflowExecutionScreen(
    pipeline: WorkflowPipeline?,
    selectedAgent: AgentHelper?,
    isExecuting: Boolean,
    onAgentSelected: (AgentHelper) -> Unit,
    onBackToHome: () -> Unit,
    onRunPipeline: () -> Unit,
    onShowDeliverables: () -> Unit,
    onApproveGate: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBackToHome()
    }

    if (pipeline == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(TechBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(TechSurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = "No Workflow",
                        tint = CyanPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Active Workforce",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Create or select a mission from the home screen to see its step-by-step progress here.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = onBackToHome,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = "Go to Mission Setup",
                        color = Color(0xFF00383D),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TechBackground)
    ) {
        // Top Mission Navigation Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = TechSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, TechOutline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackToHome,
                    modifier = Modifier.testTag("back_to_home_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home",
                        tint = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pipeline.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${pipeline.agents.size} Steps • ${pipeline.primaryDivision.title}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                // If finished, show View Output button
                if (pipeline.overallStatus == AgentStatus.COMPLETED) {
                    Button(
                        onClick = onShowDeliverables,
                        colors = ButtonDefaults.buttonColors(containerColor = MintSecondary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("view_deliverables_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "View Output",
                            tint = Color(0xFF003822),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Results",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF003822)
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onRunPipeline,
                        enabled = !isExecuting,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("relaunch_pipeline_button")
                    ) {
                        Icon(
                            imageVector = if (isExecuting) Icons.Default.Replay else Icons.Default.PlayArrow,
                            contentDescription = "Run",
                            tint = CyanPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isExecuting) "Running" else "Run",
                            fontSize = 12.sp,
                            color = CyanPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Live Interactive Graph Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            WorkflowGraphCanvas(
                pipeline = pipeline,
                selectedAgent = selectedAgent,
                onAgentSelected = onAgentSelected,
                onApproveGate = onApproveGate
            )
        }

        // Bottom Progress & Action Dock
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = TechSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, TechOutline)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val activeAgent = pipeline.agents.find {
                    it.status == AgentStatus.RUNNING_TASK || it.status == AgentStatus.SWEEPING_TOOL
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isExecuting) CyanPrimary else MintSecondary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isExecuting && activeAgent != null) {
                                "Working on: ${activeAgent.name}"
                            } else if (pipeline.overallStatus == AgentStatus.COMPLETED) {
                                "All steps verified and completed!"
                            } else {
                                "Ready to execute"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isExecuting) CyanPrimary else MintSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = "${(pipeline.overallProgress * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { pipeline.overallProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (pipeline.overallStatus == AgentStatus.COMPLETED) MintSecondary else CyanPrimary,
                    trackColor = TechBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (selectedAgent != null) {
                        OutlinedButton(
                            onClick = { onAgentSelected(selectedAgent) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("inspect_selected_agent_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Inspect",
                                tint = CyanPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Inspect ${selectedAgent.name}",
                                fontSize = 12.sp,
                                color = CyanPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (pipeline.overallStatus == AgentStatus.COMPLETED) {
                        Button(
                            onClick = onShowDeliverables,
                            colors = ButtonDefaults.buttonColors(containerColor = MintSecondary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("open_deliverables_bottom_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "View Deliverables",
                                tint = Color(0xFF003822),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "View Deliverables",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF003822)
                            )
                        }
                    }
                }
            }
        }
    }
}
