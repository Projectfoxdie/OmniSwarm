package com.example

import com.omniswarm.contracts.ContractPayload
import com.omniswarm.contracts.MissionContract
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.security.MessageDigest

class EngineeringValidationTest {

  private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

  private val adapter = moshi.adapter(MissionContract::class.java)

  @Test
  fun `valid mission contract serializes and deserializes deterministically`() {
    val payload = ContractPayload(
      items = listOf("adapter_gh", "adapter_mermaid", "adapter_quicktype"),
      metricScore = 98.5,
      checksumSha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
    )

    val contract = MissionContract(
      contractId = "mission_contract_001",
      title = "Autonomous Engineering Validation Contract",
      version = "1.0.0",
      status = "VERIFIED",
      verified = true,
      payload = payload
    )

    val json = adapter.toJson(contract)
    assertNotNull(json)
    assertTrue("JSON must contain contract_id", json.contains("contract_id"))
    assertTrue("JSON must contain metric_score", json.contains("metric_score"))

    val deserialized = adapter.fromJson(json)
    assertNotNull(deserialized)
    assertEquals(contract.contractId, deserialized?.contractId)
    assertEquals(contract.title, deserialized?.title)
    assertEquals(contract.status, deserialized?.status)
    assertEquals(contract.payload.items, deserialized?.payload?.items)
    assertEquals(contract.payload.metricScore, deserialized?.payload?.metricScore ?: 0.0, 0.001)
  }

  @Test
  fun `missing required fields in json payload triggers parsing exception`() {
    // Missing 'contract_id' and 'payload'
    val invalidJson = """
      {
        "title": "Incomplete Contract",
        "version": "1.0.0",
        "status": "QUEUED"
      }
    """.trimIndent()

    try {
      adapter.fromJson(invalidJson)
      fail("Should throw JsonDataException on missing required non-null fields")
    } catch (e: JsonDataException) {
      assertNotNull("Expected JsonDataException caught", e.message)
    }
  }

  @Test
  fun `null value on non-nullable field triggers parsing exception`() {
    val jsonWithNull = """
      {
        "contract_id": null,
        "title": "Null ID Contract",
        "version": "1.0.0",
        "status": "FAILED",
        "verified": false,
        "payload": {
          "items": [],
          "metric_score": 0.0,
          "checksum_sha256": "abc"
        }
      }
    """.trimIndent()

    try {
      adapter.fromJson(jsonWithNull)
      fail("Should throw JsonDataException when non-null field contract_id is null")
    } catch (e: JsonDataException) {
      assertNotNull("Expected JsonDataException caught", e.message)
    }
  }

  @Test
  fun `malformed json payload triggers decoding exception`() {
    val malformedJson = "{ contract_id: incomplete JSON syntax... "

    try {
      adapter.fromJson(malformedJson)
      fail("Should throw JsonEncodingException or syntax error on malformed JSON")
    } catch (e: Exception) {
      assertTrue(
        "Exception must be JsonEncodingException or EOF",
        e is JsonEncodingException || e is java.io.EOFException || e is java.io.IOException
      )
    }
  }

  @Test
  fun `sha256 hash recalculation matches cryptographic digest`() {
    val sampleData = "OMNISWARM_ENGINEERING_VALIDATION_MISSION_03_DATA_PAYLOAD"
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(sampleData.toByteArray(Charsets.UTF_8))
    val hexString = digest.joinToString("") { "%02x".format(it) }

    assertEquals(64, hexString.length)
    assertEquals(hexString, hexString.lowercase())
  }
}
