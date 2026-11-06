package com.omniswarm.contracts

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Verifiable Data Contract: MissionContract
 * Compiled by project Gradle toolchain with Moshi KSP code generation.
 */
@JsonClass(generateAdapter = true)
data class MissionContract(
    @Json(name = "contract_id") val contractId: String,
    @Json(name = "title") val title: String,
    @Json(name = "version") val version: String = "1.0.0",
    @Json(name = "status") val status: String,
    @Json(name = "verified") val verified: Boolean = true,
    @Json(name = "payload") val payload: ContractPayload
)

@JsonClass(generateAdapter = true)
data class ContractPayload(
    @Json(name = "items") val items: List<String>,
    @Json(name = "metric_score") val metricScore: Double,
    @Json(name = "checksum_sha256") val checksumSha256: String
)
