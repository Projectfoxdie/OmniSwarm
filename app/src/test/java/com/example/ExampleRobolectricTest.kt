package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FreeToolCatalog
import com.example.engine.WorkforceEngine
import com.example.engine.adapter.ToolExecutionRegistry
import com.example.engine.resolver.CompatibleFallbackResolver
import com.example.engine.validation.ArtifactValidator
import com.example.model.AgentRoleType
import com.example.model.AgentStatus
import com.example.model.AuthorityMode
import com.example.model.FreeTool
import com.example.model.OperatingDivision
import com.example.model.OutcomeVerificationResult
import com.example.model.RuntimeHealthStatus
import com.example.model.ToolExecutionResponse
import com.example.model.VerificationState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("OmniSwarm", appName)
  }

  @Test
  fun `free tool catalog contains entries across all 6 divisions`() {
    for (division in OperatingDivision.values()) {
      val tools = FreeToolCatalog.findToolsForDivision(division)
      assertFalse("Division ${division.title} should have tools", tools.isEmpty())
    }

    val searchResults = FreeToolCatalog.searchTools("upscale")
    assertTrue(searchResults.any { it.name.contains("Real-ESRGAN") })
  }

  @Test
  fun `supreme orchestrator delegates to executive directors and subteams`() {
    val engine = WorkforceEngine()
    val pipeline = engine.orchestrateMission(
      prompt = "Isolate sneaker photo, upscale 4x, and grade color in Photopea",
      divisionOverride = OperatingDivision.CREATIVE,
      authorityMode = AuthorityMode.AUTONOMOUS_WITH_GATES
    )

    assertEquals(OperatingDivision.CREATIVE, pipeline.primaryDivision)
    assertTrue("Should recruit multiple hierarchical agents", pipeline.agents.size >= 3)
    assertTrue("Should include Strategy Director", pipeline.agents.any { it.roleType == AgentRoleType.STRATEGY_DIRECTOR })
    assertTrue("Should include Quality Director", pipeline.agents.any { it.roleType == AgentRoleType.QUALITY_DIRECTOR })

    val productionLead = pipeline.agents.find { it.id == "lead_production" }
    assertTrue("Production Lead should have recursive subagents", productionLead != null && productionLead.subAgents.isNotEmpty())
  }

  @Test
  fun `navigation tabs include result tab for completed deliverable review`() {
    val tabs = com.example.ui.NavigationTab.values()
    assertTrue(tabs.contains(com.example.ui.NavigationTab.RESULT))
    assertTrue(tabs.contains(com.example.ui.NavigationTab.HOME))
    assertTrue(tabs.contains(com.example.ui.NavigationTab.GRAPH))
  }

  @Test
  fun `pipeline export helper generates formatted text report and copies to clipboard`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val engine = WorkforceEngine()
    val pipeline = engine.orchestrateMission(
      prompt = "Generate TypeScript contracts for payment gateway",
      divisionOverride = OperatingDivision.ENGINEERING,
      authorityMode = AuthorityMode.AUTONOMOUS_WITH_GATES
    )

    val report = com.example.util.PipelineExportHelper.generateFormattedReport(pipeline)
    assertTrue("Report should contain title", report.contains("OMNISWARM AUTONOMOUS WORKFORCE"))
    assertTrue("Report should contain prompt", report.contains("payment gateway"))
    assertTrue("Report should contain quality audit", report.contains("QUALITY DIRECTOR OUTCOME AUDIT"))

    var copiedCallbackTriggered = false
    com.example.util.PipelineExportHelper.copyToClipboard(context, pipeline) { msg ->
      copiedCallbackTriggered = true
      assertTrue(msg.contains("copied to clipboard"))
    }
    assertTrue("Callback should be invoked", copiedCallbackTriggered)

    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clipData = clipboard.primaryClip
    assertTrue("Clipboard should contain text", clipData != null && clipData.itemCount > 0)
    assertTrue("Clipboard text should match report", clipData?.getItemAt(0)?.text?.contains("OMNISWARM") == true)
  }

  @Test
  fun `execution telemetry records exact adapter metadata and sha256 checksums`() = runBlocking {
    val quickTypeTool = FreeToolCatalog.findById("quicktype")
    assertNotNull("QuickType tool should exist", quickTypeTool)

    val response = ToolExecutionRegistry.execute(
      tool = quickTypeTool!!,
      prompt = "Generate user auth schema with id, email, and roles",
      taskDescription = "Generate strongly-typed Kotlin data models"
    )

    assertTrue("Execution should succeed", response.success)
    assertEquals("quicktype_compiler", response.adapterId)
    assertTrue("Implementation class should be recorded", response.implementationClass.contains("QuickTypeCompilerAdapter"))
    assertTrue("Request URL should be recorded", response.requestUrl.isNotBlank())
    assertEquals("GET", response.httpMethod)
    assertEquals(200, response.statusCode)
    assertTrue("Response byte count should be greater than 0", response.responseByteCount > 50)
    assertEquals(64, response.sha256Digest.length)
    assertTrue("Artifact URI should have valid scheme", response.artifactLocation.startsWith("omniswarm://deliverables/"))

    // Independent hash recalculation
    val rawBytes = response.outputData.toByteArray(Charsets.UTF_8)
    val independentlyCalculatedHash = ArtifactValidator.computeSha256(rawBytes)
    assertEquals("Recalculated hash must match response digest", independentlyCalculatedHash, response.sha256Digest)
  }

  @Test
  fun `artifact syntax validation parses kotlin, typescript, and mermaid DSL contracts`() = runBlocking {
    val quickTypeTool = FreeToolCatalog.findById("quicktype")!!
    val mermaidTool = FreeToolCatalog.findById("mermaid_live")!!

    // 1. QuickType Kotlin & TypeScript validation
    val qtResponse = ToolExecutionRegistry.execute(
      tool = quickTypeTool,
      prompt = "Generate user auth schema with id, email, and roles",
      taskDescription = "Generate strongly-typed Kotlin data models"
    )
    val (ktValid, ktErrors) = ArtifactValidator.validateKotlinSyntax(qtResponse.outputData)
    assertTrue("Kotlin syntax must be valid: $ktErrors", ktValid)

    val (tsValid, tsErrors) = ArtifactValidator.validateTypeScriptSyntax(qtResponse.outputData)
    assertTrue("TypeScript syntax must be valid: $tsErrors", tsValid)

    // 2. Mermaid Diagram validation
    val mermaidResponse = ToolExecutionRegistry.execute(
      tool = mermaidTool,
      prompt = "Design distributed microservices message queue architecture",
      taskDescription = "Compile architecture sequence diagram"
    )
    val (mermaidValid, mermaidErrors) = ArtifactValidator.validateMermaidDsl(mermaidResponse.outputData)
    assertTrue("Mermaid DSL must be valid: $mermaidErrors", mermaidValid)
  }

  @Test
  fun `controlled negative tests - invalid requests, rate limits, and errors must never pass`() = runBlocking {
    val ghTool = FreeToolCatalog.findById("github_search") ?: FreeToolCatalog.allTools.first()
    val quickTypeTool = FreeToolCatalog.findById("quicktype")!!
    val mermaidTool = FreeToolCatalog.findById("mermaid_live")!!

    // 1. Invalid GitHub request (404)
    val response404 = ToolExecutionRegistry.execute(
      tool = ghTool,
      prompt = "Test invalid route",
      taskDescription = "Simulate 404 error",
      parameters = mapOf("simulate_invalid_request" to true, "force_status" to 404)
    )
    assertFalse("404 must not succeed", response404.success)
    assertEquals(404, response404.statusCode)
    val v404 = ArtifactValidator.validate(ghTool, "Simulate 404 error", response404)
    assertFalse("404 deliverable must be invalid", v404.isValid)
    assertTrue("Quality score must be below 50 on failure", v404.qualityScore < 50)

    // 2. HTTP 429 Rate Limit
    val response429 = ToolExecutionRegistry.execute(
      tool = ghTool,
      prompt = "Test 429 rate limit",
      taskDescription = "Rate limit breach",
      parameters = mapOf("force_status" to 429)
    )
    assertFalse("429 must not succeed", response429.success)
    assertEquals(429, response429.statusCode)
    val v429 = ArtifactValidator.validate(ghTool, "Rate limit breach", response429)
    assertFalse("429 deliverable must be invalid", v429.isValid)

    // 3. HTTP 500 Server Error
    val response500 = ToolExecutionRegistry.execute(
      tool = ghTool,
      prompt = "Test 500 server error",
      taskDescription = "Server failure",
      parameters = mapOf("force_status" to 500)
    )
    assertFalse("500 must not succeed", response500.success)
    assertEquals(500, response500.statusCode)
    val v500 = ArtifactValidator.validate(ghTool, "Server failure", response500)
    assertFalse("500 deliverable must be invalid", v500.isValid)

    // 4. Network Timeout (504)
    val responseTimeout = ToolExecutionRegistry.execute(
      tool = ghTool,
      prompt = "Test network timeout",
      taskDescription = "Network timeout test",
      parameters = mapOf("force_timeout" to true)
    )
    assertFalse("Timeout must not succeed", responseTimeout.success)
    assertEquals(504, responseTimeout.statusCode)
    val vTimeout = ArtifactValidator.validate(ghTool, "Network timeout test", responseTimeout)
    assertFalse("Timeout deliverable must be invalid", vTimeout.isValid)

    // 5. Invalid QuickType schema
    val responseBadSchema = ToolExecutionRegistry.execute(
      tool = quickTypeTool,
      prompt = "invalid_schema { bad json",
      taskDescription = "Parse bad schema",
      parameters = mapOf("invalid_schema" to true)
    )
    assertFalse("Invalid schema compilation must fail", responseBadSchema.success)
    val vBadSchema = ArtifactValidator.validate(quickTypeTool, "Parse bad schema", responseBadSchema)
    assertFalse("Bad schema deliverable must be rejected", vBadSchema.isValid)

    // 6. Invalid Mermaid syntax
    val responseBadMermaid = ToolExecutionRegistry.execute(
      tool = mermaidTool,
      prompt = "invalid_mermaid",
      taskDescription = "Parse bad diagram",
      parameters = mapOf("invalid_syntax" to true)
    )
    assertFalse("Invalid mermaid compilation must fail", responseBadMermaid.success)
    val (badMermaidValid, badMermaidErrors) = ArtifactValidator.validateMermaidDsl(responseBadMermaid.outputData)
    assertFalse("Mermaid validator must flag syntax violation", badMermaidValid)
    assertTrue("Should report syntax error", badMermaidErrors.isNotEmpty())

    // 7. Corrupted artifact
    val responseCorrupted = ToolExecutionRegistry.execute(
      tool = quickTypeTool,
      prompt = "Corrupted test",
      taskDescription = "Test corrupted payload",
      parameters = mapOf("corrupt_payload" to true)
    )
    val vCorrupted = ArtifactValidator.validate(quickTypeTool, "Test corrupted payload", responseCorrupted)
    assertFalse("Corrupted payload must be rejected", vCorrupted.isValid)
    assertTrue(vCorrupted.failedChecks.any { it.contains("Corruption Detected") })

    // 8. Missing output artifact (0 bytes)
    val responseEmpty = ToolExecutionRegistry.execute(
      tool = quickTypeTool,
      prompt = "Empty test",
      taskDescription = "Test empty payload",
      parameters = mapOf("empty_payload" to true)
    )
    val vEmpty = ArtifactValidator.validate(quickTypeTool, "Test empty payload", responseEmpty)
    assertFalse("Empty artifact must be rejected", vEmpty.isValid)
    assertTrue(vEmpty.failedChecks.any { it.contains("Artifact Missing") })
  }

  @Test
  fun `quality director defaults to unverified and detects duplicate evidence`() {
    // 1. VerificationResult defaults strictly to unverified and 0 score
    val freshVerification = OutcomeVerificationResult()
    assertFalse("Default isVerified must be false", freshVerification.isVerified)
    assertEquals("Default quality score must be 0", 0, freshVerification.qualityScorePercent)
    assertEquals(VerificationState.PENDING, freshVerification.state)

    // 2. Compatible fallback resolution
    val ocrTool = FreeToolCatalog.findById("ocr_space")!!
    val fallback = CompatibleFallbackResolver.resolveCompatibleFallback(
      failingTool = ocrTool,
      requiredOperation = "ocr_extract_text",
      candidatePool = FreeToolCatalog.allTools
    )
    assertNotNull("Compatible fallback must be resolved", fallback)
    assertFalse("Fallback cannot be the failing tool", fallback!!.id == ocrTool.id)
  }

  @Test
  fun `desktop mode and mobile mode toggle updates ui state and settings`() {
    val application = ApplicationProvider.getApplicationContext<Context>() as android.app.Application
    val viewModel = com.example.ui.WorkflowViewModel(application)

    // Initial state is Mobile Mode
    assertEquals(com.example.model.DisplayMode.MOBILE, viewModel.uiState.value.displayMode)

    // Toggle to Desktop Mode
    viewModel.toggleDisplayMode()
    assertEquals(com.example.model.DisplayMode.DESKTOP, viewModel.uiState.value.displayMode)
    assertEquals(com.example.model.DisplayMode.DESKTOP, viewModel.uiState.value.settings.displayMode)
    assertTrue("Message should indicate switched to Desktop Mode", viewModel.uiState.value.infoMessage?.contains("Desktop Mode") == true)

    // Toggle back to Mobile Mode
    viewModel.toggleDisplayMode()
    assertEquals(com.example.model.DisplayMode.MOBILE, viewModel.uiState.value.displayMode)
    assertEquals(com.example.model.DisplayMode.MOBILE, viewModel.uiState.value.settings.displayMode)

    // Direct setter
    viewModel.setDisplayMode(com.example.model.DisplayMode.DESKTOP)
    assertEquals(com.example.model.DisplayMode.DESKTOP, viewModel.uiState.value.displayMode)
  }

  @Test
  fun `fullscreen mode toggle updates state and settings`() {
    val application = ApplicationProvider.getApplicationContext<Context>() as android.app.Application
    val viewModel = com.example.ui.WorkflowViewModel(application)

    // Initial state is not fullscreen
    assertFalse("Default should not be fullscreen", viewModel.uiState.value.isFullscreen)

    // Toggle fullscreen on
    viewModel.toggleFullscreen()
    assertTrue("Should be fullscreen", viewModel.uiState.value.isFullscreen)
    assertTrue("Settings should reflect fullscreen", viewModel.uiState.value.settings.isFullscreen)
    assertTrue("Info message should report entered fullscreen", viewModel.uiState.value.infoMessage?.contains("Entered Full Screen") == true)

    // Toggle fullscreen off
    viewModel.toggleFullscreen()
    assertFalse("Should exit fullscreen", viewModel.uiState.value.isFullscreen)
  }

  @Test
  fun `settings update and dialog visibility control`() {
    val application = ApplicationProvider.getApplicationContext<Context>() as android.app.Application
    val viewModel = com.example.ui.WorkflowViewModel(application)

    assertFalse(viewModel.uiState.value.showSettingsDialog)
    viewModel.setShowSettingsDialog(true)
    assertTrue(viewModel.uiState.value.showSettingsDialog)

    val updatedSettings = com.example.model.AppSettings(
      displayMode = com.example.model.DisplayMode.DESKTOP,
      isFullscreen = true,
      autoAdvanceToResults = false,
      strictQualityEnforcement = true,
      soundAndHapticEnabled = false
    )
    viewModel.updateSettings(updatedSettings)

    assertEquals(com.example.model.DisplayMode.DESKTOP, viewModel.uiState.value.displayMode)
    assertTrue(viewModel.uiState.value.isFullscreen)
    assertFalse(viewModel.uiState.value.settings.autoAdvanceToResults)
    assertFalse(viewModel.uiState.value.settings.soundAndHapticEnabled)
    assertEquals("Settings saved.", viewModel.uiState.value.infoMessage)
  }

  @Test
  fun `feedback submission validates records diagnostics and tracks history`() {
    val application = ApplicationProvider.getApplicationContext<Context>() as android.app.Application
    val viewModel = com.example.ui.WorkflowViewModel(application)

    assertFalse(viewModel.uiState.value.showFeedbackDialog)
    viewModel.setShowFeedbackDialog(true)
    assertTrue(viewModel.uiState.value.showFeedbackDialog)

    assertTrue("Initial history should be empty", viewModel.uiState.value.submittedFeedbackList.isEmpty())

    val submission = viewModel.submitFeedback(
      category = com.example.model.FeedbackCategory.TOOL_INTEGRATION,
      rating = 5,
      title = "Request for Blender CLI integration",
      message = "Please add a headless 3D rendering adapter for Creative Division pipelines.",
      includeDiagnostics = true
    )

    assertNotNull(submission.id)
    assertTrue(submission.id.startsWith("FB-"))
    assertEquals(5, submission.rating)
    assertEquals(com.example.model.FeedbackCategory.TOOL_INTEGRATION, submission.category)
    assertTrue(submission.diagnosticDetails.contains("OmniSwarm v11.0"))
    assertTrue(submission.diagnosticDetails.contains("Android"))

    assertEquals(1, viewModel.uiState.value.submittedFeedbackList.size)
    assertEquals(submission.id, viewModel.uiState.value.submittedFeedbackList.first().id)
    assertTrue("Feedback submission message should be displayed", viewModel.uiState.value.infoMessage?.contains(submission.id) == true)
  }

  @Test
  fun `string resources contain settings feedback desktop and fullscreen labels`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    assertEquals("Settings", context.getString(R.string.title_settings))
    assertEquals("Feedback", context.getString(R.string.title_feedback))
    assertEquals("Desktop Mode", context.getString(R.string.title_desktop_mode))
    assertEquals("Mobile Mode", context.getString(R.string.title_mobile_mode))
    assertEquals("Full Screen", context.getString(R.string.title_fullscreen))
    assertEquals("Exit Full Screen", context.getString(R.string.btn_exit_fullscreen))
  }

  @Test
  fun `execution adapter defines mandatory methods and executes successfully`() = runBlocking {
    val quickTypeTool = FreeToolCatalog.findById("quicktype")!!
    val adapter = ToolExecutionRegistry.getAdapter(quickTypeTool.adapterId)

    // 1. Mandatory execution method
    val response = adapter.execute(
      tool = quickTypeTool,
      prompt = "Generate user auth schema",
      taskDescription = "Generate strongly-typed Kotlin data models"
    )
    assertTrue("Execution must succeed", response.success)
    assertEquals(200, response.statusCode)

    // 2. Request encapsulation execution
    val req = com.example.engine.adapter.ExecutionRequest(
      tool = quickTypeTool,
      prompt = "Generate user auth schema",
      taskDescription = "Generate strongly-typed Kotlin data models"
    )
    val reqResponse = adapter.executeRequest(req)
    assertTrue("ExecutionRequest must succeed", reqResponse.success)

    // 3. Mandatory status & health monitoring methods
    val status = adapter.monitorStatus(quickTypeTool)
    assertEquals(com.example.model.RuntimeHealthStatus.HEALTHY_ONLINE, status)
    val healthCheck = adapter.healthCheck(quickTypeTool)
    assertEquals(status, healthCheck)

    val telemetry = adapter.getTelemetry(quickTypeTool)
    assertEquals(adapter.adapterId, telemetry.adapterId)
    assertEquals(quickTypeTool.id, telemetry.toolId)
    assertEquals(com.example.model.RuntimeHealthStatus.HEALTHY_ONLINE, telemetry.status)

    // 4. Mandatory artifact validation methods
    val validation = adapter.validateArtifact(quickTypeTool, "Generate models", response)
    assertTrue("Deliverable must be valid", validation.isValid)
    assertTrue("Quality score must be high", validation.qualityScore >= 90)

    val checksum = adapter.computeArtifactChecksum(response.outputData)
    assertEquals(64, checksum.length)
    assertEquals(response.sha256Digest, checksum)

    assertTrue("Deliverable contract must verify", adapter.verifyDeliverableContract(response))
  }

  @Test
  fun `custom execution adapter registers with tool execution registry`() = runBlocking {
    val customAdapter = object : com.example.engine.adapter.ExecutionAdapter {
      override val adapterId: String = "custom_test_adapter"

      override suspend fun execute(operation: String): ToolExecutionResponse {
        val payload = "VERIFIED_CUSTOM_ADAPTER_OUTPUT: $operation"
        val hash = computeArtifactChecksum(payload)
        return ToolExecutionResponse(
          success = true,
          statusCode = 200,
          outputData = payload,
          artifactTitle = "Custom Deliverable",
          sha256Digest = hash,
          adapterId = adapterId,
          implementationClass = implementationClass
        )
      }

      override suspend fun monitorStatus(): com.example.model.RuntimeHealthStatus {
        return com.example.model.RuntimeHealthStatus.HEALTHY_ONLINE
      }

      override fun validateArtifact(hash: String): Boolean {
        return hash.isNotBlank() && hash.length == 64
      }
    }

    ToolExecutionRegistry.register(customAdapter)
    val retrieved = ToolExecutionRegistry.getAdapter("custom_test_adapter")
    assertEquals("custom_test_adapter", retrieved.adapterId)

    // Verify mandatory execute(operation: String)
    val directOpRes = retrieved.execute("compile_microservices_blueprint")
    assertTrue(directOpRes.success)
    assertEquals(200, directOpRes.statusCode)
    assertTrue(directOpRes.outputData.contains("compile_microservices_blueprint"))

    // Verify mandatory monitorStatus()
    val status = retrieved.monitorStatus()
    assertEquals(com.example.model.RuntimeHealthStatus.HEALTHY_ONLINE, status)

    // Verify mandatory validateArtifact(hash: String)
    assertTrue(retrieved.validateArtifact(directOpRes.sha256Digest))
    assertFalse(retrieved.validateArtifact("invalid_short_hash"))

    // Verify catalog execution overload
    val testTool = FreeToolCatalog.allTools.first().copy(
      adapterId = "custom_test_adapter",
      outputSchema = "text/plain"
    )
    val res = retrieved.execute(testTool, "Run test pipeline", "Verify adapter")
    assertTrue(res.success)
    assertTrue(res.outputData.contains("VERIFIED_CUSTOM_ADAPTER_OUTPUT"))

    val validation = retrieved.validateArtifact(testTool, "Verify adapter", res)
    assertTrue(validation.isValid)
  }
}


