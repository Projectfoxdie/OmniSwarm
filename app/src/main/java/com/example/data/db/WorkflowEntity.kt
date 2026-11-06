package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_workflows")
data class WorkflowEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val prompt: String,
    val domainName: String,
    val createdAt: Long,
    val overallStatus: String,
    val agentsJson: String,
    val finalOutputTitle: String,
    val finalOutputSummary: String,
    val finalArtifactUrlsJson: String
)
