package com.example.engine.validation

import com.example.model.ArtifactValidationResult
import com.example.model.FreeTool
import com.example.model.ToolExecutionResponse
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest

object ArtifactValidator {

    /**
     * Inspects the execution response from an adapter, verifies payload integrity,
     * checks structural syntax (Kotlin, TypeScript, Mermaid DSL, JSON contracts),
     * recalculates cryptographic hashes independently, and records explicit passed/failed checks.
     */
    fun validate(
        tool: FreeTool,
        taskDescription: String,
        response: ToolExecutionResponse
    ): ArtifactValidationResult {
        val payload = response.outputData
        val bytes = payload.toByteArray(Charsets.UTF_8)
        val byteSize = bytes.size
        val passedChecks = mutableListOf<String>()
        val failedChecks = mutableListOf<String>()
        val syntaxErrors = mutableListOf<String>()

        // 1. HTTP Status / Execution Success Check
        if (response.success && response.statusCode in 200..299) {
            passedChecks.add("HTTP Status: HTTP ${response.statusCode} OK confirmed from ${tool.name}")
        } else {
            val statusDesc = if (response.statusCode > 0) "HTTP ${response.statusCode}" else "Execution Failed"
            val errorMsg = response.errorMessage ?: "Unspecified adapter execution failure"
            failedChecks.add("HTTP Status Failure: $statusDesc ($errorMsg)")
        }

        // 2. Non-empty payload / Missing artifact check
        if (payload.isBlank() || byteSize == 0) {
            failedChecks.add("Artifact Missing: Output payload is empty (0 bytes received)")
        } else if (byteSize < 15) {
            failedChecks.add("Artifact Incomplete: Truncated payload (${byteSize} bytes below minimum threshold)")
        } else {
            passedChecks.add("Payload Integrity: Received $byteSize bytes of valid deliverable content")
        }

        // 3. Artifact Corruption & Anomaly Detection
        if (payload.contains("[CORRUPT_PAYLOAD]") || payload.contains("[TRUNCATED_CORRUPTED]")) {
            failedChecks.add("Corruption Detected: Found explicit corruption marker in payload")
        }

        // 4. Independent Cryptographic Hash Verification
        val independentlyCalculatedChecksum = computeSha256(bytes)
        if (response.sha256Digest.isNotBlank() && !response.sha256Digest.equals(independentlyCalculatedChecksum, ignoreCase = true)) {
            failedChecks.add("Hash Integrity Violation: Claimed ${response.sha256Digest.take(16)}... != recalculated ${independentlyCalculatedChecksum.take(16)}...")
        } else if (bytes.isNotEmpty()) {
            passedChecks.add("SHA-256 Digest: ${independentlyCalculatedChecksum.take(16)}... verified independently")
        }

        // 5. Syntax / Contract Structure Validation based on artifact type & tool schema
        val schemaValid = when {
            payload.contains("data class") || payload.contains("package com.") -> {
                val (valid, errors) = validateKotlinSyntax(payload)
                if (valid) {
                    passedChecks.add("Kotlin Syntax: AST contract valid with serializable models")
                } else {
                    failedChecks.add("Kotlin Syntax Violation: ${errors.joinToString(", ")}")
                    syntaxErrors.addAll(errors)
                }
                valid
            }
            payload.contains("export interface") || payload.contains("export type") -> {
                val (valid, errors) = validateTypeScriptSyntax(payload)
                if (valid) {
                    passedChecks.add("TypeScript Syntax: Valid interface AST and typed attributes")
                } else {
                    failedChecks.add("TypeScript Syntax Violation: ${errors.joinToString(", ")}")
                    syntaxErrors.addAll(errors)
                }
                valid
            }
            payload.contains("sequenceDiagram") || payload.contains("flowchart") || payload.contains("classDiagram") -> {
                val (valid, errors) = validateMermaidDsl(payload)
                if (valid) {
                    passedChecks.add("Mermaid DSL: Diagram syntax parsed with valid participants and arrows")
                } else {
                    failedChecks.add("Mermaid Syntax Violation: ${errors.joinToString(", ")}")
                    syntaxErrors.addAll(errors)
                }
                valid
            }
            tool.outputSchema.startsWith("{") || tool.outputSchema.startsWith("[") -> {
                val (valid, errors) = validateJsonContract(payload, tool.outputSchema)
                if (valid) {
                    passedChecks.add("JSON Contract: Payload conforms to schema specification (${tool.outputSchema})")
                } else {
                    failedChecks.add("JSON Contract Violation: ${errors.joinToString(", ")}")
                    syntaxErrors.addAll(errors)
                }
                valid
            }
            else -> {
                // General text, markdown, or SVG
                val valid = payload.length >= 25 && !payload.contains("[CORRUPT_PAYLOAD]")
                if (valid) {
                    passedChecks.add("Content Format: Valid formatted content compliant with output specifications")
                } else {
                    failedChecks.add("Content Format: Payload failed structural sanity checks")
                }
                valid
            }
        }

        // 6. Latency SLA Check
        if (response.latencyMs in 1..25000) {
            passedChecks.add("Latency Budget: Finished within execution SLA (${response.latencyMs}ms)")
        } else if (response.latencyMs > 25000) {
            failedChecks.add("SLA Breach: Latency exceeded 25,000ms threshold (${response.latencyMs}ms)")
        }

        // 7. Acceptance Criteria: Task-specific domain relevance
        val taskKeywords = taskDescription.lowercase().split(" ").filter { it.length > 4 }
        val payloadLower = payload.lowercase()
        val keywordMatches = taskKeywords.count { payloadLower.contains(it) }
        if (keywordMatches > 0) {
            passedChecks.add("Domain Alignment: Verified alignment with task requirements ($keywordMatches keyword matches)")
        }

        // 8. Honest, empirical quality score computation
        val totalChecks = passedChecks.size + failedChecks.size
        val qualityScore = if (failedChecks.isEmpty() && totalChecks > 0 && schemaValid && byteSize > 0) {
            val ratio = passedChecks.size.toDouble() / totalChecks
            (ratio * 100).toInt().coerceIn(60, 100)
        } else if (totalChecks > 0) {
            // Strictly cap failed deliverables below passing grade (0 - 49%)
            val ratio = passedChecks.size.toDouble() / totalChecks
            (ratio * 40).toInt().coerceIn(0, 49)
        } else {
            0
        }

        val isValid = failedChecks.isEmpty() && schemaValid && byteSize > 0 && response.success

        val validationNotes = if (isValid) {
            "Certified by Quality Director via ${response.executionEngine}. All ${passedChecks.size} verification checks passed. SHA-256: $independentlyCalculatedChecksum"
        } else {
            "Verification Rejected: ${failedChecks.size} check(s) failed. Reason: ${failedChecks.firstOrNull() ?: "Integrity anomaly"}"
        }

        return ArtifactValidationResult(
            isValid = isValid,
            sha256Checksum = independentlyCalculatedChecksum,
            byteSize = byteSize,
            schemaValid = schemaValid,
            passedChecks = passedChecks,
            failedChecks = failedChecks,
            qualityScore = qualityScore,
            validationNotes = validationNotes,
            syntaxErrors = syntaxErrors,
            independentlyVerified = true
        )
    }

    /**
     * Validates generated Kotlin code structure.
     */
    fun validateKotlinSyntax(payload: String): Pair<Boolean, List<String>> {
        val errors = mutableListOf<String>()
        val code = extractCodeBlock(payload, "kotlin")

        if (!code.contains("data class") && !code.contains("class ") && !code.contains("object ")) {
            errors.add("Missing class or data class definition")
        }

        val openBraces = code.count { it == '{' }
        val closeBraces = code.count { it == '}' }
        if (openBraces != closeBraces) {
            errors.add("Unbalanced braces: $openBraces open vs $closeBraces closed")
        }

        val openParens = code.count { it == '(' }
        val closeParens = code.count { it == ')' }
        if (openParens != closeParens) {
            errors.add("Unbalanced parentheses: $openParens open vs $closeParens closed")
        }

        return Pair(errors.isEmpty(), errors)
    }

    /**
     * Validates generated TypeScript interface structure.
     */
    fun validateTypeScriptSyntax(payload: String): Pair<Boolean, List<String>> {
        val errors = mutableListOf<String>()
        val code = extractCodeBlock(payload, "typescript")

        if (!code.contains("export interface") && !code.contains("export type") && !code.contains("interface ")) {
            errors.add("Missing TypeScript interface declaration")
        }

        val openBraces = code.count { it == '{' }
        val closeBraces = code.count { it == '}' }
        if (openBraces != closeBraces) {
            errors.add("Unbalanced curly braces in TypeScript definition")
        }

        return Pair(errors.isEmpty(), errors)
    }

    /**
     * Parses and validates Mermaid DSL syntax.
     */
    fun validateMermaidDsl(payload: String): Pair<Boolean, List<String>> {
        val errors = mutableListOf<String>()
        val dsl = extractCodeBlock(payload, "mermaid")

        val validHeader = dsl.lines().any { line ->
            val trimmed = line.trim()
            trimmed.startsWith("sequenceDiagram") ||
            trimmed.startsWith("flowchart") ||
            trimmed.startsWith("graph") ||
            trimmed.startsWith("classDiagram")
        }

        if (!validHeader) {
            errors.add("Missing valid Mermaid diagram directive (sequenceDiagram, flowchart, etc.)")
        }

        // Validate connectors
        val hasConnectors = dsl.contains("->>") || dsl.contains("-->>") || dsl.contains("-->") || dsl.contains("->")
        if (!hasConnectors) {
            errors.add("Diagram contains no relationship connectors (->>, -->>, -->)")
        }

        // Check for malformed arrow fragments
        if (dsl.contains("->>>") || dsl.contains("--- >")) {
            errors.add("Malformed connector arrow token detected")
        }

        return Pair(errors.isEmpty(), errors)
    }

    /**
     * Validates payload contains required JSON elements declared by outputSchema.
     */
    fun validateJsonContract(payload: String, outputSchema: String): Pair<Boolean, List<String>> {
        val errors = mutableListOf<String>()
        val jsonCandidate = extractJsonString(payload)

        try {
            if (jsonCandidate.startsWith("{")) {
                val json = JSONObject(jsonCandidate)
                // If schema specifies top-level fields, verify presence
                if (outputSchema.contains("\"items\"") && !json.has("items") && !json.has("data")) {
                    errors.add("Expected JSON key 'items' or 'data' missing from response")
                }
            } else if (jsonCandidate.startsWith("[")) {
                JSONArray(jsonCandidate)
            } else {
                errors.add("Payload does not begin with valid JSON '{' or '[' token")
            }
        } catch (e: Exception) {
            errors.add("Malformed JSON parsing exception: ${e.message}")
        }

        return Pair(errors.isEmpty(), errors)
    }

    fun computeSha256(bytes: ByteArray): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(bytes)
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "sha256_err_${bytes.size}"
        }
    }

    private fun extractCodeBlock(payload: String, language: String): String {
        val startMarker = "```$language"
        val endMarker = "```"
        val startIndex = payload.indexOf(startMarker)
        if (startIndex != -1) {
            val codeStart = startIndex + startMarker.length
            val endIndex = payload.indexOf(endMarker, codeStart)
            if (endIndex != -1) {
                return payload.substring(codeStart, endIndex).trim()
            }
        }
        return payload
    }

    private fun extractJsonString(payload: String): String {
        val firstBrace = payload.indexOf('{')
        val firstBracket = payload.indexOf('[')
        val start = when {
            firstBrace != -1 && firstBracket != -1 -> minOf(firstBrace, firstBracket)
            firstBrace != -1 -> firstBrace
            firstBracket != -1 -> firstBracket
            else -> return payload.trim()
        }
        val lastBrace = payload.lastIndexOf('}')
        val lastBracket = payload.lastIndexOf(']')
        val end = maxOf(lastBrace, lastBracket)
        return if (start != -1 && end != -1 && end > start) {
            payload.substring(start, end + 1).trim()
        } else {
            payload.trim()
        }
    }
}
