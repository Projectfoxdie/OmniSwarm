package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FreeToolCatalog
import com.example.engine.WorkforceEngine
import com.example.model.AgentRoleType
import com.example.model.AuthorityMode
import com.example.model.OperatingDivision
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
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
}
