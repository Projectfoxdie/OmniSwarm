package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FreeTool
import com.example.model.OperatingDivision
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ToolCatalogScreen(
    tools: List<FreeTool>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onOpenInAppWorkspace: (FreeTool) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedDivisionFilter by remember { mutableStateOf<OperatingDivision?>(null) }

    val filteredTools = tools.filter { tool ->
        selectedDivisionFilter == null || tool.division == selectedDivisionFilter
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TechBackground)
            .padding(horizontal = 18.dp)
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        // Friendly Header
        Text(
            text = "Free Tool Catalog",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Explore 24+ free web applications, public APIs, and open tools connected to OmniSwarm.",
            fontSize = 13.sp,
            color = TextSecondary,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("tool_search_input"),
            placeholder = { Text("Search tools, capabilities, or file formats...", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = CyanPrimary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = TextSecondary
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = TechSurfaceElevated,
                unfocusedContainerColor = TechSurfaceElevated,
                focusedBorderColor = CyanPrimary,
                unfocusedBorderColor = TechOutline,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Division Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                val isSelected = selectedDivisionFilter == null
                Surface(
                    color = if (isSelected) CyanPrimary.copy(alpha = 0.2f) else TechSurfaceElevated,
                    shape = RoundedCornerShape(10.dp),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary) else androidx.compose.foundation.BorderStroke(1.dp, TechOutline),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { selectedDivisionFilter = null }
                ) {
                    Text(
                        text = "All Tools (${tools.size})",
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) CyanPrimary else TextPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            items(OperatingDivision.values()) { division ->
                val isSelected = selectedDivisionFilter == division
                val divColor = Color(division.tagColor)
                Surface(
                    color = if (isSelected) divColor.copy(alpha = 0.2f) else TechSurfaceElevated,
                    shape = RoundedCornerShape(10.dp),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, divColor) else androidx.compose.foundation.BorderStroke(1.dp, TechOutline),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { selectedDivisionFilter = division }
                ) {
                    Text(
                        text = division.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) divColor else TextSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredTools.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "No results",
                        tint = TextMuted,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No tools match \"$searchQuery\"",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try searching for photo, code, research, audio, or pdf",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = {
                            onSearchQueryChanged("")
                            selectedDivisionFilter = null
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text(text = "Reset Filters", color = CyanPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        } else {
            // Tools List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTools) { tool ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tool_card_${tool.id}"),
                        colors = CardDefaults.cardColors(containerColor = TechSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TechOutline)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tool.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${tool.division.title} • ${tool.category}",
                                        fontSize = 12.sp,
                                        color = CyanPrimary
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Surface(
                                        color = Color(tool.integrationType.badgeColor).copy(alpha = 0.18f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = tool.integrationType.label,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(tool.integrationType.badgeColor),
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(Color(tool.healthStatus.color), CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = tool.healthStatus.label,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(tool.healthStatus.color)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = tool.description,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Execution Engine Specs & Policy
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(TechSurfaceElevated, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Adapter: ${tool.adapterId}",
                                    fontSize = 10.sp,
                                    color = CyanPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = tool.rateLimitPolicy,
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Capabilities chips
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                tool.capabilities.forEach { cap ->
                                    Surface(
                                        color = TechSurfaceVariant,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = cap,
                                            fontSize = 11.sp,
                                            color = Color(0xFFD4DFEE),
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Actions: Open in App & Visit Website
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onOpenInAppWorkspace(tool) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = "Open In-App Workspace",
                                        tint = Color(0xFF00383D),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Open in App",
                                        fontSize = 12.sp,
                                        color = Color(0xFF00383D),
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tool.url))
                                        context.startActivity(intent)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = "Visit Website",
                                        tint = CyanPrimary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Visit Site",
                                        fontSize = 12.sp,
                                        color = CyanPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
