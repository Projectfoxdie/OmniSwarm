package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AgentHelper
import com.example.model.AgentRoleType
import com.example.model.AgentStatus
import com.example.model.ApprovalGate
import com.example.model.WorkflowPipeline
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.MintSecondary
import com.example.ui.theme.TechBackground
import com.example.ui.theme.TechOutline
import com.example.ui.theme.TechSurface
import com.example.ui.theme.TechSurfaceElevated
import com.example.ui.theme.TechSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun WorkflowGraphCanvas(
    pipeline: WorkflowPipeline,
    selectedAgent: AgentHelper?,
    onAgentSelected: (AgentHelper) -> Unit,
    onApproveGate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_anim")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_pulse"
    )

    val energyOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "energy_flow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TechBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(top = 18.dp, bottom = 100.dp)
        ) {
            // Supreme Orchestrator Objective Summary Card
            item {
                SupremeOrchestratorNode(pipeline = pipeline)
            }

            // Active Approval Gate Banner if waiting for user review
            pipeline.activeApprovalGate?.let { gate ->
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    ApprovalGateCard(gate = gate, onApprove = onApproveGate)
                }
            }

            // Steps & Agents in Execution Sequence
            itemsIndexed(pipeline.agents) { index, agent ->
                // Connecting line between steps
                ConnectingBusWire(
                    status = agent.status,
                    energyOffset = energyOffset
                )

                // Main Agent Step Card
                HierarchicalAgentNodeCard(
                    stepNumber = index + 1,
                    agent = agent,
                    isSelected = selectedAgent?.id == agent.id,
                    pulseAlpha = pulseAlpha,
                    onClick = { onAgentSelected(agent) }
                )

                // Sub-tasks / Recursive Subagents
                if (agent.subAgents.isNotEmpty()) {
                    Column(modifier = Modifier.padding(start = 28.dp, top = 8.dp)) {
                        agent.subAgents.forEachIndexed { subIndex, sub ->
                            RecursiveSubagentCard(
                                stepNumber = index + 1,
                                subIndex = subIndex + 1,
                                subagent = sub,
                                onClick = { onAgentSelected(sub) }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }

            // Final Quality Outcome Verification Node
            item {
                ConnectingBusWire(
                    status = pipeline.overallStatus,
                    energyOffset = energyOffset
                )

                QualityOutcomeNode(
                    title = pipeline.finalOutputTitle,
                    isCompleted = pipeline.overallStatus == AgentStatus.COMPLETED,
                    qualityScore = pipeline.verificationResult.qualityScorePercent
                )
            }
        }
    }
}

@Composable
private fun SupremeOrchestratorNode(pipeline: WorkflowPipeline) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("supreme_orchestrator_node"),
        colors = CardDefaults.cardColors(containerColor = TechSurface),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.linearGradient(listOf(CyanPrimary.copy(alpha = 0.6f), TechOutline))
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(CyanPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Objective",
                    tint = CyanPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Orchestrated Mission",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MintSecondary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = pipeline.primaryDivision.title,
                            fontSize = 10.sp,
                            color = MintSecondary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = pipeline.prompt,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun ApprovalGateCard(
    gate: ApprovalGate,
    onApprove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("approval_gate_banner"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF261D0C)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, AmberAccent)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AmberAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Approval Gate",
                        tint = AmberAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Operator Review Required",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberAccent
                    )
                    Text(
                        text = "Your approval is needed before executing this stage",
                        fontSize = 11.sp,
                        color = Color(0xFFFFE0B2)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = gate.actionTitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = gate.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onApprove,
                colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("approve_action_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Approve",
                    tint = Color(0xFF331D00),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Approve & Continue",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF331D00)
                )
            }
        }
    }
}

@Composable
private fun ConnectingBusWire(
    status: AgentStatus,
    energyOffset: Float
) {
    val wireColor = when (status) {
        AgentStatus.COMPLETED -> MintSecondary
        AgentStatus.RUNNING_TASK -> CyanPrimary
        AgentStatus.SWEEPING_TOOL -> AmberAccent
        AgentStatus.SELF_HEALING -> AmberAccent
        AgentStatus.AWAITING_APPROVAL -> AmberAccent
        AgentStatus.FAILED -> MaterialTheme.colorScheme.error
        AgentStatus.QUEUED -> TechOutline
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(26.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f

            drawLine(
                color = wireColor.copy(alpha = 0.35f),
                start = Offset(centerX, 0f),
                end = Offset(centerX, size.height),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            if (status == AgentStatus.RUNNING_TASK || status == AgentStatus.SWEEPING_TOOL || status == AgentStatus.SELF_HEALING) {
                val particleY = size.height * energyOffset
                drawCircle(
                    color = wireColor,
                    radius = 3.5.dp.toPx(),
                    center = Offset(centerX, particleY)
                )
            }
        }
    }
}

@Composable
private fun HierarchicalAgentNodeCard(
    stepNumber: Int,
    agent: AgentHelper,
    isSelected: Boolean,
    pulseAlpha: Float,
    onClick: () -> Unit
) {
    val accentColor = when (agent.status) {
        AgentStatus.COMPLETED -> MintSecondary
        AgentStatus.RUNNING_TASK -> CyanPrimary
        AgentStatus.SWEEPING_TOOL -> AmberAccent
        AgentStatus.SELF_HEALING -> AmberAccent
        AgentStatus.AWAITING_APPROVAL -> AmberAccent
        AgentStatus.FAILED -> MaterialTheme.colorScheme.error
        AgentStatus.QUEUED -> TextMuted
    }

    val cardBorder = if (agent.status == AgentStatus.RUNNING_TASK || isSelected) {
        androidx.compose.foundation.BorderStroke(
            1.5.dp,
            accentColor.copy(alpha = if (isSelected) 1f else pulseAlpha)
        )
    } else {
        androidx.compose.foundation.BorderStroke(1.dp, TechOutline)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("agent_node_${agent.id}"),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) TechSurfaceElevated else TechSurface),
        shape = RoundedCornerShape(16.dp),
        border = cardBorder
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            // Header Row: Step Number, Role Badge, Friendly Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = CyanPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Step $stepNumber",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = agent.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                FriendlyStatusPill(status = agent.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = agent.roleTitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )

            // Self-Healing Notification
            if (agent.selfHealingEvents.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = AmberAccent.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Self Healing",
                            tint = AmberAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Self-Healing: Automatically transitioned to fallback tool",
                            fontSize = 11.sp,
                            color = AmberAccent,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tool Chip Row
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = TechSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Tool",
                        tint = CyanPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = agent.tool.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        color = MintSecondary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = agent.tool.freeTierType,
                            fontSize = 10.sp,
                            color = MintSecondary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = agent.taskDescription,
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            // Progress bar if in-progress
            if (agent.status == AgentStatus.RUNNING_TASK || agent.status == AgentStatus.SWEEPING_TOOL || agent.status == AgentStatus.SELF_HEALING) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { agent.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = accentColor,
                    trackColor = TechSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RecursiveSubagentCard(
    stepNumber: Int,
    subIndex: Int,
    subagent: AgentHelper,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = TechSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, TechOutline)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SubdirectoryArrowRight,
                contentDescription = "Delegated sub-task",
                tint = CyanPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subagent.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "Tool: ${subagent.tool.name}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Delegated",
                tint = MintSecondary,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Composable
private fun FriendlyStatusPill(status: AgentStatus) {
    val (label, color, icon) = when (status) {
        AgentStatus.QUEUED -> Triple("Queued", TextMuted, Icons.Default.HourglassTop)
        AgentStatus.SWEEPING_TOOL -> Triple("Connecting...", AmberAccent, Icons.Default.Radar)
        AgentStatus.AWAITING_APPROVAL -> Triple("Review Needed", AmberAccent, Icons.Default.Security)
        AgentStatus.RUNNING_TASK -> Triple("Working...", CyanPrimary, Icons.Default.PlayArrow)
        AgentStatus.SELF_HEALING -> Triple("Recovering...", AmberAccent, Icons.Default.Warning)
        AgentStatus.COMPLETED -> Triple("Done", MintSecondary, Icons.Default.CheckCircle)
        AgentStatus.FAILED -> Triple("Failed", MaterialTheme.colorScheme.error, Icons.Default.Warning)
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
private fun QualityOutcomeNode(
    title: String,
    isCompleted: Boolean,
    qualityScore: Int
) {
    val cardBorder = if (isCompleted) {
        androidx.compose.foundation.BorderStroke(1.dp, MintSecondary.copy(alpha = 0.6f))
    } else {
        androidx.compose.foundation.BorderStroke(1.dp, TechOutline)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .testTag("quality_outcome_node"),
        colors = CardDefaults.cardColors(containerColor = if (isCompleted) Color(0xFF0F261E) else TechSurface),
        shape = RoundedCornerShape(18.dp),
        border = cardBorder
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) MintSecondary.copy(alpha = 0.2f) else TechSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.Verified else Icons.Default.HourglassTop,
                    contentDescription = "Outcome Verification",
                    tint = if (isCompleted) MintSecondary else TextMuted,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isCompleted) "Verified by Quality Director" else "Verification Pending",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) MintSecondary else TextSecondary
                    )
                    if (isCompleted) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MintSecondary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "$qualityScore% Match",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MintSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = title.ifEmpty { "Final results will be compiled and certified here once all steps finish." },
                    fontSize = 13.sp,
                    color = if (isCompleted) TextPrimary else TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
