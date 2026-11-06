# OmniSwarm Master Technical Blueprint: Universal AI Operating System

OmniSwarm is an autonomous multi-division AI operating system for Android that identifies user objectives, formulates cross-division execution strategies, sweeps and connects free tools and public APIs, recursively delegates tasks to specialized agent hierarchies, and guarantees completion through automated outcome verification.

---

### Governing Law of OmniSwarm

> [!IMPORTANT]
> **Core Principle**: *No agent gets authority merely because it is intelligent. Authority comes strictly from assigned permissions, and completion requires verifiable results.*

---

### Confirmed Governing Architecture

| Architectural Dimension | Confirmed Approach | Rationale |
| :--- | :--- | :--- |
| **Platform Scope** | Universal AI Operating System | Transcends simple scripts into an autonomous workforce platform. |
| **Execution Model** | Autonomous with Approval Gates | Routine steps execute independently; consequential steps pause for human approval. |
| **Initial Platform** | Android First (Native Jetpack Compose) | Standalone, offline-first mobile command deck with embedded Web workspaces. |
| **Expansion Path** | Web & Desktop | Portable architecture with decoupled state and modular contracts. |
| **Intelligence Engine** | Model-Agnostic / Interchangeable | Orchestration core owns state; Gemini/models provide modular reasoning. |
| **Workforce Model** | Hierarchical Recursive Delegation | Supreme Orchestrator -> 3 Executive Directors -> Division Leads -> Subteams. |
| **Tool Layer** | Tool Independence with Failover | Zero vendor lock-in; swappable free tools with automatic failover. |
| **Reliability** | Self-Healing & Verification | Latency/error detection triggers fallbacks; Quality Director audits final output. |
| **Persistence** | Local Room Database Storage | 100% on-device SQLite persistence for projects, logs, and blueprints. |

---

### 1. Complete System Architecture & Operating Divisions

```
┌────────────────────────────────────────────────────────────────────────┐
│                   OmniSwarm Universal OS UI (Android)                  │
│  ┌───────────────────────┐  ┌───────────────────────────────────────┐  │
│  │ Operating Divisions   │  │ Authority Selector                    │  │
│  │ (01 - 06 Selector)    │  │ (Autonomous w/ Gates | Directed | Max)│  │
│  └───────────┬───────────┘  └───────────────────┬───────────────────┘  │
│              ▼                                  ▼                      │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │      Hierarchical Visual Graph Canvas (Topological Layers)       │  │
│  │      [Supreme Orchestrator]                                      │  │
│  │         ├── Strategy Director (Milestones & Budgets)             │  │
│  │         ├── Operations Director (Routing & Tool Match)           │  │
│  │         └── Quality Director (Verification & Auditing)           │  │
│  │               │                                                  │  │
│  │         [Division Specialist Lead]                               │  │
│  │            ├── Recursive Subagent A (Cutout / OCR)               │  │
│  │            └── Recursive Subagent B (Upscale / Schema)           │  │
│  └──────────────────────────────────┬───────────────────────────────┘  │
│                                     │                                  │
│  ┌──────────────────────────────────▼───────────────────────────────┐  │
│  │ In-App Embedded Workspace Dialog (WASM / Web Tool Direct Access) │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────┬───────────────────────────────────┘
                                     │
                                     ▼
┌────────────────────────────────────────────────────────────────────────┐
│                      Workforce Orchestration Engine                    │
│  ┌────────────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ Tool Sweeper Directory │  │ Approval Engine │  │ Self-Healing Hub│  │
│  │ 34+ Free Web Bridges   │  │ Sign-Off Gates  │  │ Failover Switch │  │
│  └──────────┬─────────────┘  └────────┬────────┘  └────────┬────────┘  │
└─────────────┼─────────────────────────┼────────────────────┼───────────┘
              │                         │                    │
              ▼                         ▼                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        Data & Verification Layer                       │
│  ┌─────────────────────────────────┐  ┌─────────────────────────────┐  │
│  │ Quality Director Audit Engine   │  │ Room Database Persistence   │  │
│  │ (Checks & Compliance Scoring)   │  │ (Pipelines, Logs, Recipes)  │  │
│  └─────────────────────────────────┘  └─────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────────┘
```

#### The Six Operating Divisions

1. **01 — Intelligence Division**: Research, market surveillance, academic paper crawls, historical web diffs, trend tracking, and strategic analysis. *(Semantic Scholar, Wayback Machine, Wikidata SPARQL, HackerNews Stream)*
2. **02 — Engineering Division**: Software architecture, null-safe data contracts, REST/GraphQL testing, UML diagrams, and CI inspection. *(GitHub API, QuickType, Mermaid Live, JSON Crack, Hoppscotch)*
3. **03 — Creative Division**: Multi-layer photo retouching, neural super-resolution, vectorization, voice narration, DAW audio mastering, and WASM compression. *(Photopea, Real-ESRGAN, Remove.bg, Squoosh, Vectorizer, TTSMaker, Audacity/AudioMass)*
4. **04 — Enterprise Division**: Business models, corporate registry audits, competitive traffic benchmarking, namespace protection, and commercial ops. *(OpenCorporates, SimilarWeb Signals, WHOIS/DNS Inspector)*
5. **05 — Automation Division**: Document OCR, PDF merging/splitting, payload webhook listeners, clarity grading, and batch format transformations. *(Stirling-PDF, OCR.space, Webhook.site, Hemingway App)*
6. **06 — Personal Operations Division**: Milestone roadmaps, time-blocking schedules, calendar feeds, spatial planning, and data hygiene. *(Excalidraw, Cal.com Free, JSONLint)*

---

### 2. Organizational Hierarchy & Delegation Protocols

Every agent operates under strict organizational boundaries:

- **Supreme Orchestrator**: Ingests mission objective, identifies primary and supporting divisions, initializes the session, and governs the operational loop.
- **Executive Directors**:
  - **Strategy Director**: Deconstructs objective into sequential milestones, allocates token/time budgets, and plans the dependency graph.
  - **Operations Director**: Connects tools, monitors execution latencies, routes data between agents, and enforces approval gates.
  - **Quality Director**: Inspects all intermediate and final artifacts against predefined acceptance criteria, calculating a verifiable Quality Score before completion.
- **Division Leads & Specialists**: Recruited agents with assigned domain competencies and primary/fallback tool allocations.
- **Recursive Subteams**: When a subtask requires multiple atomic transformations (e.g. background removal followed by 4x super-resolution), the lead spawns targeted subagents to parallelize or pipeline the work.

---

### 3. Tool Discovery & Execution Framework

#### Tool Capability Status Lifecycle

Every free program and API in the sweeper catalog has an explicit capability lifecycle:
1. `DISCOVERED`: Indexed in the network sweeper directory.
2. `TESTED`: Schema and endpoint connectivity validated.
3. `CONNECTED`: Credentials or public bridge verified.
4. `EXECUTABLE`: Ready for automated or interactive invocation.
5. `VERIFIED`: Proven track record of zero-defect outputs.

#### Tool Independence & Dynamic Self-Healing

If an active tool encounters network latency, rate limits, or parse errors:
- The agent immediately logs a `SELF_HEALING` event.
- Operations Director reroutes the payload to the agent's pre-assigned `fallbackTool` (e.g., if Remove.bg encounters a rate limit, the task seamlessly fails over to Photopea layer masking).
- Pipeline continues with zero data loss or operator interruption.

---

### 4. Permission & Approval System

#### Authority Modes

1. **Autonomous with Approval Gates (Default & Recommended)**: Routine operations execute automatically; consequential actions (file writes, exports, external API triggers) pause with an interactive approval card.
2. **Human-Directed**: Proposes each division action and pauses for explicit manual confirmation.
3. **Maximum Autonomy**: End-to-end hands-free execution across all tools with automated self-healing.

---

### 5. Database Schema & Data Models

- **`saved_workflows` Table (Room DB)**:
  - `id: String` (PK)
  - `title: String`, `prompt: String`, `divisionCode: String`, `authorityMode: String`
  - `createdAt: Long`, `overallStatus: String`
  - `agentsJson: String` (Includes recursive subagents, self-healing events, and tool states)
  - `verificationJson: String` (Audit checks, quality score, verified timestamp)
  - `finalOutputSummary: String`, `finalArtifactUrlsJson: String`

---

### 6. Phased Implementation Tasks with Acceptance Criteria

- [x] **Phase 1: Division & Hierarchy Models**: Implement 6 Operating Divisions, 3 Executive Directors, recursive subagents, and capability statuses.
- [x] **Phase 2: Expanded Free Tool Catalog**: Catalog 34+ verified tools across all 6 divisions with verified capability badges.
- [x] **Phase 3: Supreme Orchestration Core**: Implement recursive delegation, self-healing failovers, and Quality Director audit checks.
- [x] **Phase 4: Hierarchical Graph & Approval UI**: Visual canvas showing 3 tiers of hierarchy, approval gate banners, and live self-healing alerts.
- [x] **Phase 5: In-App Embedded Workspace**: Integrated full-screen WebView with navigation controls for interacting with free tools directly inside OmniSwarm.
- [x] **Phase 6: Standalone Packaging & Downloads**: Self-exporting APK utility, downloads persistence, and offline Room DB execution.
