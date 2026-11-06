package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AuthorityMode
import com.example.model.OperatingDivision
import com.example.model.WorkflowPipeline
import com.example.ui.NavigationTab
import com.example.ui.WorkflowUiState
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.MintSecondary
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TechBackground
import com.example.ui.theme.TechOutline
import com.example.ui.theme.TechSurface
import com.example.ui.theme.TechSurfaceElevated
import com.example.ui.theme.TechSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class ShowcaseAbility(
    val title: String,
    val division: OperatingDivision,
    val badge: String,
    val description: String,
    val prompt: String,
    val tools: List<String>
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    uiState: WorkflowUiState,
    onDivisionSelected: (OperatingDivision) -> Unit,
    onAuthorityModeSelected: (AuthorityMode) -> Unit,
    onDeployWorkforce: (String, OperatingDivision, AuthorityMode) -> Unit,
    onOpenSavedWorkflow: (WorkflowPipeline) -> Unit,
    onNavigateTo: (NavigationTab) -> Unit,
    onOpenStandaloneInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var promptInput by remember {
        mutableStateOf("Product photoshoot studio: Isolate sneaker foreground, 4x super-resolution, cyber neon color curves in Photopea, and optimize WebP bundle.")
    }

    var showAdvancedSettings by remember { mutableStateOf(false) }

    val revolvingAbilities = remember {
        listOf(
            ShowcaseAbility(
                title = "🎨 Cyberpunk Sneaker Studio",
                division = OperatingDivision.CREATIVE,
                badge = "Visual Media & Image Gen",
                description = "Isolates sneaker foreground with Remove.bg, executes 4x Real-ESRGAN super-resolution, grades neon color curves in Photopea, and optimizes WebP payload.",
                prompt = "Product photoshoot studio: Isolate sneaker foreground, 4x super-resolution, cyber neon color curves in Photopea, and optimize WebP bundle.",
                tools = listOf("Remove.bg", "Real-ESRGAN", "Photopea", "Squoosh")
            ),
            ShowcaseAbility(
                title = "🔬 Autonomous arXiv Investigator",
                division = OperatingDivision.INTELLIGENCE,
                badge = "Literature & Citation Matrix",
                description = "Queries Semantic Scholar & arXiv for multi-agent reinforcement learning papers, extracts empirical citation deltas, and drafts a markdown briefing.",
                prompt = "Comprehensive research review: Search arXiv and Semantic Scholar for agentic AI papers, synthesize citation diffs, and save markdown report.",
                tools = listOf("Semantic Scholar", "arXiv API", "Wayback Machine")
            ),
            ShowcaseAbility(
                title = "💻 Microservice Architecture Blueprint",
                division = OperatingDivision.ENGINEERING,
                badge = "Code & Schema Synthesis",
                description = "Crawls GitHub API patterns, compiles type-safe QuickType models, scaffolds endpoints, and draws interactive Mermaid architecture diagrams.",
                prompt = "Full-stack app blueprint: Synthesize GitHub repositories, generate QuickType data contracts, and draw Mermaid architecture diagrams.",
                tools = listOf("GitHub API", "QuickType", "Mermaid.js")
            ),
            ShowcaseAbility(
                title = "📈 Competitor Market Intelligence",
                division = OperatingDivision.ENTERPRISE,
                badge = "Corporate Filings & Traffic",
                description = "Audits corporate filings via OpenCorporates, benchmarks SimilarWeb traffic rankings, and cross-references historical WHOIS registration changes.",
                prompt = "Competitor audit: Scan corporate registries with OpenCorporates, estimate traffic with SimilarWeb, and check WHOIS history.",
                tools = listOf("OpenCorporates", "SimilarWeb", "WHOIS Lookup")
            ),
            ShowcaseAbility(
                title = "⚡ Batch Document & Invoice OCR",
                division = OperatingDivision.AUTOMATION,
                badge = "Document Processing",
                description = "Extracts tabular data from invoice scans with OCR.Space, validates JSON schema with Webhook.site, and binds signed PDFs in Stirling-PDF.",
                prompt = "Batch document extraction: Run OCR on invoice scans with OCR.Space, validate with Webhook.site, and merge with Stirling-PDF.",
                tools = listOf("OCR.Space", "Webhook.site", "Stirling-PDF")
            ),
            ShowcaseAbility(
                title = "📅 Executive Chronos & Goal Planner",
                division = OperatingDivision.PERSONAL_OPS,
                badge = "Schedule Optimization",
                description = "Calculates high-focus productivity blocks with Cal.com, maps milestone roadmaps on Excalidraw, and cleans recurring task queues.",
                prompt = "Time-block organizer: Build weekly calendar allocations with Cal.com and sketch executive milestones on Excalidraw whiteboard.",
                tools = listOf("Cal.com", "Excalidraw", "OpenTodo")
            ),
            ShowcaseAbility(
                title = "🎵 Podcast Vocal Master & RSS Suite",
                division = OperatingDivision.CREATIVE,
                badge = "Audio Engineering",
                description = "Removes ambient hum and normalizes audio levels with Auphonic, generates automated chapter markers, and formats podcast RSS metadata.",
                prompt = "Podcast audio studio: Clean background noise with Auphonic, generate chapter markers, and export podcast RSS feed metadata.",
                tools = listOf("Auphonic", "Descript Free", "ID3 Tag Engine")
            ),
            ShowcaseAbility(
                title = "🚀 SaaS Launch Strategy & SEO Matrix",
                division = OperatingDivision.ENTERPRISE,
                badge = "Product Launch Kit",
                description = "Drafts high-converting landing page copy, audits competitor pricing tiers, and compiles ProductHunt launch kits with SEO sitemaps.",
                prompt = "SaaS launch orchestrator: Generate landing page copy, compile SEO robots/sitemaps, audit competitor pricing matrices, and prepare ProductHunt launch kit.",
                tools = listOf("ProductHunt API", "OpenCorporates", "QuickType")
            )
        )
    }

    var revolvingIndex by remember { mutableIntStateOf(0) }
    var isAutoRevolving by remember { mutableStateOf(true) }

    LaunchedEffect(isAutoRevolving) {
        if (isAutoRevolving) {
            while (true) {
                delay(4500)
                revolvingIndex = (revolvingIndex + 1) % revolvingAbilities.size
            }
        }
    }

    val quickPrompts = listOf(
        "🎨 Photo Studio" to ("Product photoshoot studio: Isolate sneaker foreground, 4x super-resolution, cyber neon color curves in Photopea, and optimize WebP bundle." to OperatingDivision.CREATIVE),
        "🔬 Deep Research" to ("Comprehensive research review: Search arXiv and Semantic Scholar for agentic AI papers, synthesize citation diffs, and save markdown report." to OperatingDivision.INTELLIGENCE),
        "💻 App Architecture" to ("Full-stack app blueprint: Synthesize GitHub repositories, generate QuickType data contracts, and draw Mermaid architecture diagrams." to OperatingDivision.ENGINEERING),
        "📈 Competitor Intel" to ("Competitor audit: Scan corporate registries with OpenCorporates, estimate traffic with SimilarWeb, and check WHOIS history." to OperatingDivision.ENTERPRISE),
        "⚡ Document Pipeline" to ("Batch document extraction: Run OCR on invoice scans with OCR.Space, validate with Webhook.site, and merge with Stirling-PDF." to OperatingDivision.AUTOMATION),
        "📅 Smart Schedule" to ("Time-block organizer: Build weekly calendar allocations with Cal.com and sketch executive milestones on Excalidraw whiteboard." to OperatingDivision.PERSONAL_OPS)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TechBackground)
            .padding(horizontal = 18.dp),
        contentPadding = PaddingValues(top = 18.dp, bottom = 96.dp)
    ) {
        // Welcoming Header Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MintSecondary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Autonomous AI Workforce",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MintSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "OmniSwarm",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Download APK pill
                    Surface(
                        color = MintSecondary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MintSecondary.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onOpenStandaloneInfo() }
                            .testTag("standalone_apk_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download APK",
                                tint = MintSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "APK",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MintSecondary
                            )
                        }
                    }

                    // Tools button
                    Surface(
                        color = TechSurfaceElevated,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TechOutline),
                        modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onNavigateTo(NavigationTab.TOOLS) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hub,
                                contentDescription = "Tool Catalog",
                                tint = CyanPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Tools",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CyanPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Hero Card: Objective Input & Category Picker
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mission_prompt_card"),
                colors = CardDefaults.cardColors(containerColor = TechSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.linearGradient(listOf(CyanPrimary.copy(alpha = 0.5f), TechOutline))
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "What would you like to achieve?",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "OmniSwarm breaks down your objective, recruits specialized agents, and uses free online tools to deliver verified results.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Revolving Abilities Showcase Section
                    Surface(
                        color = TechSurfaceElevated,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TechOutline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("revolving_abilities_showcase")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Header with controls
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = CyanPrimary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "REVOLVING SHOWCASE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanPrimary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = CyanPrimary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "${revolvingIndex + 1}/${revolvingAbilities.size}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyanPrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // Controls: Prev, Pause/Play, Next, Shuffle
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            revolvingIndex = if (revolvingIndex > 0) revolvingIndex - 1 else revolvingAbilities.size - 1
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Previous ability",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { isAutoRevolving = !isAutoRevolving },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isAutoRevolving) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = if (isAutoRevolving) "Pause revolving" else "Resume revolving",
                                            tint = if (isAutoRevolving) MintSecondary else TextSecondary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            revolvingIndex = (revolvingIndex + 1) % revolvingAbilities.size
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Next ability",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            var next = (0 until revolvingAbilities.size).random()
                                            if (next == revolvingIndex && revolvingAbilities.size > 1) {
                                                next = (revolvingIndex + 1) % revolvingAbilities.size
                                            }
                                            revolvingIndex = next
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shuffle,
                                            contentDescription = "Shuffle ability",
                                            tint = AmberAccent,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Animated rotating ability details
                            AnimatedContent(
                                targetState = revolvingAbilities[revolvingIndex],
                                transitionSpec = {
                                    (fadeIn() + slideInHorizontally { width -> width / 3 }).togetherWith(
                                        fadeOut() + slideOutHorizontally { width -> -width / 3 }
                                    )
                                },
                                label = "RevolvingAbilityAnimation"
                            ) { ability ->
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = ability.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Surface(
                                            color = Color(ability.division.tagColor).copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = ability.badge,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(ability.division.tagColor),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = ability.description,
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        lineHeight = 16.sp
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Tools row & Try button
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 8.dp)
                                        ) {
                                            items(ability.tools) { tool ->
                                                Surface(
                                                    color = TechSurface,
                                                    shape = RoundedCornerShape(6.dp),
                                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, TechOutline)
                                                ) {
                                                    Text(
                                                        text = tool,
                                                        fontSize = 10.sp,
                                                        color = MintSecondary,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                promptInput = ability.prompt
                                                onDivisionSelected(ability.division)
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = CyanPrimary
                                            ),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text(
                                                text = "Try Objective ⚡",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF00383D)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quick jump mini pills across abilities
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(revolvingAbilities.indices.toList()) { idx ->
                                    val ability = revolvingAbilities[idx]
                                    val isSelected = revolvingIndex == idx
                                    Surface(
                                        color = if (isSelected) CyanPrimary.copy(alpha = 0.22f) else TechSurface,
                                        shape = RoundedCornerShape(8.dp),
                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary) else androidx.compose.foundation.BorderStroke(0.5.dp, TechOutline),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                revolvingIndex = idx
                                                promptInput = ability.prompt
                                                onDivisionSelected(ability.division)
                                            }
                                    ) {
                                        Text(
                                            text = ability.title.substringBefore(" "),
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) CyanPrimary else TextSecondary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Main Prompt Text Area
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(105.dp)
                            .testTag("mission_prompt_input"),
                        placeholder = {
                            Text(
                                text = "Describe what you need built, researched, edited, or automated...",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = TechSurfaceElevated,
                            unfocusedContainerColor = TechSurfaceElevated,
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = TechOutline,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Specialized Divisions Grid/Chips
                    Text(
                        text = "Specialized Team Division:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(OperatingDivision.values()) { division ->
                            val isSelected = division == uiState.selectedDivision
                            val divIcon = getDivisionIcon(division)
                            val accentColor = Color(division.tagColor)

                            Surface(
                                color = if (isSelected) accentColor.copy(alpha = 0.18f) else TechSurfaceElevated,
                                shape = RoundedCornerShape(12.dp),
                                border = if (isSelected) {
                                    androidx.compose.foundation.BorderStroke(1.5.dp, accentColor)
                                } else {
                                    androidx.compose.foundation.BorderStroke(1.dp, TechOutline)
                                },
                                modifier = Modifier
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onDivisionSelected(division) }
                                    .testTag("division_pill_${division.name}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = divIcon,
                                        contentDescription = division.title,
                                        tint = if (isSelected) accentColor else TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = division.title,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) TextPrimary else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = uiState.selectedDivision.description,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Execution Mode Row (Collapsible / Clean)
                    Surface(
                        color = TechSurfaceElevated,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TechOutline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showAdvancedSettings = !showAdvancedSettings }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Review Mode",
                                    tint = AmberAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Review Mode: ${getFriendlyAuthorityLabel(uiState.authorityMode)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = getFriendlyAuthoritySub(uiState.authorityMode),
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Change Mode",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    AnimatedVisibility(visible = showAdvancedSettings) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            AuthorityMode.values().forEach { mode ->
                                val isSelected = mode == uiState.authorityMode
                                Surface(
                                    color = if (isSelected) AmberAccent.copy(alpha = 0.15f) else TechSurfaceElevated,
                                    shape = RoundedCornerShape(10.dp),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, AmberAccent) else null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 48.dp)
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { onAuthorityModeSelected(mode) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Security,
                                            contentDescription = mode.label,
                                            tint = if (isSelected) AmberAccent else TextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = getFriendlyAuthorityLabel(mode),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isSelected) TextPrimary else TextSecondary
                                            )
                                            Text(
                                                text = getFriendlyAuthoritySub(mode),
                                                fontSize = 11.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Launch Button
                    Button(
                        onClick = {
                            onDeployWorkforce(promptInput, uiState.selectedDivision, uiState.authorityMode)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("deploy_workforce_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = "Deploy",
                            tint = Color(0xFF00383D),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Start ${uiState.selectedDivision.title} Workforce",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00383D)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Section: Ready-to-Run Starter Blueprints
        item {
            Text(
                text = "Popular Ready-to-Run Blueprints",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap any recipe to auto-populate the workforce configuration.",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        val blueprints = listOf(
            Triple(
                "🎨 Creative: Photo Retouch & Poster Studio",
                "Isolates sneaker foreground with Remove.bg, applies 4x Real-ESRGAN super-resolution, and grades neon color curves in Photopea.",
                OperatingDivision.CREATIVE
            ),
            Triple(
                "🔬 Research: Academic Citation Deep-Dive",
                "Queries Semantic Scholar and arXiv for neural agents, synthesizes citation diffs, and compiles a clean markdown report.",
                OperatingDivision.INTELLIGENCE
            ),
            Triple(
                "💻 Code: System Architecture & Schemas",
                "Crawls GitHub patterns, compiles type-safe QuickType models, and generates interactive Mermaid diagrams.",
                OperatingDivision.ENGINEERING
            ),
            Triple(
                "📈 Business: Competitor Market Intelligence",
                "Scans corporate filings via OpenCorporates, estimates web traffic via SimilarWeb, and checks WHOIS domain histories.",
                OperatingDivision.ENTERPRISE
            ),
            Triple(
                "⚡ Automation: Batch PDF & OCR Processing",
                "Extracts structured table records with OCR.Space, validates payloads via Webhook.site, and binds PDFs with Stirling-PDF.",
                OperatingDivision.AUTOMATION
            ),
            Triple(
                "📅 Daily Ops: Smart Weekly Time-Blocking",
                "Calculates optimal productivity blocks with Cal.com, sketches project goals on Excalidraw, and organizes task lists.",
                OperatingDivision.PERSONAL_OPS
            )
        )

        items(blueprints) { (title, desc, div) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        promptInput = desc
                        onDivisionSelected(div)
                    }
                    .testTag("preset_blueprint_${div.name}"),
                colors = CardDefaults.cardColors(containerColor = TechSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TechOutline)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(div.tagColor).copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getDivisionIcon(div),
                            contentDescription = title,
                            tint = Color(div.tagColor),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = desc,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Use this blueprint",
                        tint = CyanPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Section: Recent Projects
        if (uiState.savedWorkflows.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Recent Projects",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${uiState.savedWorkflows.size} completed missions stored",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = "View All →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyanPrimary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onNavigateTo(NavigationTab.HISTORY) }
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(uiState.savedWorkflows.take(3)) { workflow ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onOpenSavedWorkflow(workflow) }
                        .testTag("recent_workflow_${workflow.id}"),
                    colors = CardDefaults.cardColors(containerColor = TechSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TechOutline)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MintSecondary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Done",
                                tint = MintSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = workflow.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${workflow.agents.size} agents • ${workflow.primaryDivision.title} • Verified",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Open",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun getDivisionIcon(division: OperatingDivision): ImageVector {
    return when (division) {
        OperatingDivision.CREATIVE -> Icons.Default.Palette
        OperatingDivision.INTELLIGENCE -> Icons.Default.Science
        OperatingDivision.ENGINEERING -> Icons.Default.Code
        OperatingDivision.ENTERPRISE -> Icons.Default.BusinessCenter
        OperatingDivision.AUTOMATION -> Icons.Default.AutoAwesome
        OperatingDivision.PERSONAL_OPS -> Icons.Default.EventNote
    }
}

private fun getFriendlyAuthorityLabel(mode: AuthorityMode): String {
    return when (mode) {
        AuthorityMode.AUTONOMOUS_WITH_GATES -> "Review Key Steps (Recommended)"
        AuthorityMode.MAXIMUM_AUTONOMY -> "Fully Automated"
        AuthorityMode.HUMAN_DIRECTED -> "Step-by-Step Approval"
    }
}

private fun getFriendlyAuthoritySub(mode: AuthorityMode): String {
    return when (mode) {
        AuthorityMode.AUTONOMOUS_WITH_GATES -> "Pauses at major milestones for your review"
        AuthorityMode.MAXIMUM_AUTONOMY -> "Executes all steps continuously without interruption"
        AuthorityMode.HUMAN_DIRECTED -> "Requires confirmation before running each agent tool"
    }
}
