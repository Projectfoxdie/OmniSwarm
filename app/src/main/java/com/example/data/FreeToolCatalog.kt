package com.example.data

import com.example.model.FreeTool
import com.example.model.OperatingDivision
import com.example.model.ToolCapabilityStatus

object FreeToolCatalog {

    val ALL_TOOLS: List<FreeTool> = listOf(
        // 01 — Intelligence Division
        FreeTool(
            id = "semantic_scholar",
            name = "Semantic Scholar API",
            division = OperatingDivision.INTELLIGENCE,
            category = "Scientific Literature & Citations",
            url = "https://www.semanticscholar.org/product/api",
            description = "AI-powered research intelligence querying 200M+ peer-reviewed papers, citations, and evidence graphs.",
            freeTierType = "Free Academic API (100 req/min)",
            capabilities = listOf("Citation indexing", "TLDR extraction", "Evidence synthesis", "Author network"),
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "wayback_machine",
            name = "Wayback Machine API",
            division = OperatingDivision.INTELLIGENCE,
            category = "Historical Web Intelligence",
            url = "https://archive.org/help/wayback_api.php",
            description = "Internet Archive crawler API to inspect historical page diffs, competitor evolution, and deleted records.",
            freeTierType = "100% Free Public API",
            capabilities = listOf("Snapshot diffs", "Historical capture", "Temporal search", "Archive lookup"),
            openSource = true,
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "wikidata_sparql",
            name = "Wikidata Knowledge Graph",
            division = OperatingDivision.INTELLIGENCE,
            category = "Structured Knowledge Engine",
            url = "https://query.wikidata.org",
            description = "Open structured graph database for cross-referencing corporate entities, facts, and geopolitical entities.",
            freeTierType = "Open Non-Profit SPARQL",
            capabilities = listOf("Graph queries", "Entity disambiguation", "Fact verification", "JSON SPARQL"),
            openSource = true,
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "hackernews_api",
            name = "HackerNews Intelligence Feed",
            division = OperatingDivision.INTELLIGENCE,
            category = "Tech Market Signals & Sentiment",
            url = "https://github.com/HackerNews/API",
            description = "Real-time tech sentiment and market signals tracking developer trends, launch discussions, and breakthroughs.",
            freeTierType = "100% Free Firebase API",
            capabilities = listOf("Trend detection", "Sentiment tracking", "Launch signals", "Realtime stream"),
            openSource = true,
            status = ToolCapabilityStatus.VERIFIED
        ),

        // 02 — Engineering Division
        FreeTool(
            id = "github_api",
            name = "GitHub Public Open API",
            division = OperatingDivision.ENGINEERING,
            category = "Code & Repository Intelligence",
            url = "https://api.github.com",
            description = "Scans open source repositories, commit topologies, permissive licenses (MIT/Apache), and CI build status.",
            freeTierType = "Free Developer API (60 req/hr)",
            capabilities = listOf("Repo metadata", "Code inspection", "License verification", "Release tarballs"),
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "quicktype",
            name = "QuickType Schema Compiler",
            division = OperatingDivision.ENGINEERING,
            category = "Data Model & Schema Generator",
            url = "https://app.quicktype.io",
            description = "Compiles arbitrary JSON and OpenAPI payloads into null-safe Kotlin, TypeScript, Swift, and Go models.",
            freeTierType = "100% Free Open Source",
            capabilities = listOf("Kotlin data class", "TypeScript interfaces", "Type validation", "Nullability enforcement"),
            openSource = true,
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "mermaid_live",
            name = "Mermaid Live Diagram Engine",
            division = OperatingDivision.ENGINEERING,
            category = "Architecture-as-Code Compiler",
            url = "https://mermaid.live",
            description = "Compiles plain text markdown into UML class models, sequence flows, and system architecture blueprints.",
            freeTierType = "100% Free Open Source",
            capabilities = listOf("Flowchart SVG", "Sequence diagrams", "State machines", "Architecture graphs"),
            openSource = true,
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "jsoncrack",
            name = "JSON Crack Graph Visualizer",
            division = OperatingDivision.ENGINEERING,
            category = "Hierarchy & Data Visualizer",
            url = "https://jsoncrack.com",
            description = "Transforms complex relational databases and nested JSON trees into interactive 2D node graphs.",
            freeTierType = "Free Web Application",
            capabilities = listOf("Tree graph layout", "Node search", "Schema collapse", "Export SVG"),
            openSource = true,
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "hoppscotch",
            name = "Hoppscotch Web API Studio",
            division = OperatingDivision.ENGINEERING,
            category = "API Testing & Gateway Suite",
            url = "https://hoppscotch.io",
            description = "Lightweight open-source API development ecosystem for testing REST, GraphQL, and WebSocket endpoints.",
            freeTierType = "100% Free Open Source",
            capabilities = listOf("REST testing", "GraphQL query", "Header verification", "Mock responses"),
            openSource = true,
            status = ToolCapabilityStatus.VERIFIED
        ),

        // 03 — Creative Division
        FreeTool(
            id = "photopea",
            name = "Photopea Web Suite",
            division = OperatingDivision.CREATIVE,
            category = "Raster & Vector Studio",
            url = "https://www.photopea.com",
            description = "Professional online image editing workstation supporting PSD, AI, RAW, and SVG layer pipelines.",
            freeTierType = "100% Free Web App",
            capabilities = listOf("Layer masks", "Color curves", "Typography styles", "PSD automation"),
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "remove_bg",
            name = "Remove.bg / Slazzer Web",
            division = OperatingDivision.CREATIVE,
            category = "Subject Cutout & Segmentation",
            url = "https://www.remove.bg",
            description = "High-precision neural computer vision algorithm for isolating subjects and generating alpha transparency masks.",
            freeTierType = "Generous Free Web API",
            capabilities = listOf("Alpha cutout", "Edge feathering", "Hair isolation", "PNG transparency"),
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "realesrgan",
            name = "Real-ESRGAN / Waifu2x Web",
            division = OperatingDivision.CREATIVE,
            category = "Super-Resolution Neural Engine",
            url = "https://waifu2x.udp.jp",
            description = "Deep convolutional neural network to upscale images 2x-4x with noise reduction, super-resolution, and artifact removal.",
            freeTierType = "Open Source / Free Web API",
            capabilities = listOf("Upscale", "4x Upscaling", "Super-resolution", "Denoising", "Detail reconstruction"),
            openSource = true,
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "squoosh",
            name = "Squoosh by GoogleChromeLabs",
            division = OperatingDivision.CREATIVE,
            category = "WASM Compression & Format Lab",
            url = "https://squoosh.app",
            description = "Browser-based WebAssembly image optimizer converting to ultra-efficient WebP, AVIF, and MozJPEG.",
            freeTierType = "100% Free Open Source",
            capabilities = listOf("WebP/AVIF encoding", "Lossless compression", "Palette reduction", "Client-side WASM"),
            openSource = true,
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "vectorizer",
            name = "SVGViewer & Vectorizer AI",
            division = OperatingDivision.CREATIVE,
            category = "Vector Conversion Engine",
            url = "https://www.svgviewer.dev",
            description = "Converts raster bitmaps into infinite-resolution SVG vectors with geometric path smoothing.",
            freeTierType = "Free Web Utility",
            capabilities = listOf("Path tracing", "SVG sanitization", "Color clustering", "Node reduction"),
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "ttsmaker",
            name = "TTSMaker AI Voice Synthesizer",
            division = OperatingDivision.CREATIVE,
            category = "Voice Synthesis Studio",
            url = "https://ttsmaker.com",
            description = "Free speech synthesis providing natural human timbre voices in 50+ languages with full commercial license.",
            freeTierType = "Free Online Service (20k chars/wk)",
            capabilities = listOf("Speech synthesis", "Pitch modulation", "Natural pauses", "WAV export"),
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "audacity_online",
            name = "Audacity Web / AudioMass",
            division = OperatingDivision.CREATIVE,
            category = "In-Browser Audio DAW",
            url = "https://audiomass.co",
            description = "Full waveform editor with multi-track mixing, noise reduction, stereo EQ, and audio normalization.",
            freeTierType = "100% Free Open Source",
            capabilities = listOf("Vocal cleanup", "EQ modulation", "Dynamic compression", "Mastering"),
            openSource = true,
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "freesound",
            name = "Freesound Creative Commons API",
            division = OperatingDivision.CREATIVE,
            category = "Sound FX & Ambience Index",
            url = "https://freesound.org",
            description = "Collaborative index of 500,000+ Creative Commons audio stems, ambient textures, and sound design loops.",
            freeTierType = "Open Non-Profit API",
            capabilities = listOf("CC0 licensing", "Audio fingerprinting", "Spectral preview", "Stem extraction"),
            openSource = true,
            status = ToolCapabilityStatus.VERIFIED
        ),

        // 04 — Enterprise Division
        FreeTool(
            id = "opencorporates",
            name = "OpenCorporates Global Registry",
            division = OperatingDivision.ENTERPRISE,
            category = "Company Registry & Incorporation",
            url = "https://opencorporates.com",
            description = "The world's largest open database of companies, corporate officers, filings, and jurisdictional registries.",
            freeTierType = "Free Public Search API",
            capabilities = listOf("Corporate verification", "Officer search", "Jurisdiction checks", "Filing history"),
            openSource = true,
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "similarweb_free",
            name = "SimilarWeb Open Signals",
            division = OperatingDivision.ENTERPRISE,
            category = "Traffic & Competitor Benchmarks",
            url = "https://www.similarweb.com",
            description = "Analyzes digital market share, website traffic estimates, competitor referrals, and organic search keywords.",
            freeTierType = "Free Public Tier",
            capabilities = listOf("Traffic metrics", "Competitor benchmarks", "Audience overlap", "Referral channels"),
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "namecheap_whois",
            name = "WHOIS & DNS Domain Inspector",
            division = OperatingDivision.ENTERPRISE,
            category = "Brand & Asset Protection",
            url = "https://www.whois.com",
            description = "Inspects trademark conflicts, domain availability, registrar ownership, and global nameserver topologies.",
            freeTierType = "Free ICANN Query Bridge",
            capabilities = listOf("Domain lookup", "WHOIS inspection", "DNS resolution", "Trademark check"),
            status = ToolCapabilityStatus.VERIFIED
        ),

        // 05 — Automation Division
        FreeTool(
            id = "stirling_pdf",
            name = "Stirling PDF Open Engine",
            division = OperatingDivision.AUTOMATION,
            category = "Document & PDF Manipulation",
            url = "https://github.com/Stirling-Tools/Stirling-PDF",
            description = "Open source powerhouse for merging, splitting, OCR-ing, watermarking, and restructuring PDF documents.",
            freeTierType = "100% Free Open Source",
            capabilities = listOf("PDF OCR", "Docx conversion", "Page reordering", "Security metadata removal"),
            openSource = true,
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "ocr_space",
            name = "OCR.Space Cloud Engine",
            division = OperatingDivision.AUTOMATION,
            category = "Optical Character Recognition",
            url = "https://ocr.space/ocrapi",
            description = "Parses scanned receipts, documentation, invoices, and photos into structured, searchable text streams.",
            freeTierType = "Free API (25,000 calls/mo)",
            capabilities = listOf("Table parsing", "Multi-lingual OCR", "Orientation detection", "JSON bounding boxes"),
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "webhook_site",
            name = "Webhook.site Flow Inspector",
            division = OperatingDivision.AUTOMATION,
            category = "Payload & Trigger Testing",
            url = "https://webhook.site",
            description = "Instant URL endpoint generator for debugging inbound webhooks, payload schemas, and event triggers.",
            freeTierType = "100% Free Service",
            capabilities = listOf("Webhook listener", "JSON payload capture", "CORS header mock", "Event verification"),
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "hemingway_web",
            name = "Hemingway App Style Grader",
            division = OperatingDivision.AUTOMATION,
            category = "Prose Clarity & Readability",
            url = "https://hemingwayapp.com",
            description = "Automated readability grader highlighting passive voice, dense clauses, and stylistic weaknesses in deliverables.",
            freeTierType = "Free Web Application",
            capabilities = listOf("Grade level index", "Clarity score", "Passive voice flag", "Sentence breakdown"),
            status = ToolCapabilityStatus.VERIFIED
        ),

        // 06 — Personal Operations Division
        FreeTool(
            id = "excalidraw",
            name = "Excalidraw Visual Scratchpad",
            division = OperatingDivision.PERSONAL_OPS,
            category = "Whiteboard & Spatial Planning",
            url = "https://excalidraw.com",
            description = "Virtual collaborative whiteboard for mind-mapping, system sketching, and strategic timeline visualization.",
            freeTierType = "100% Free Open Source",
            capabilities = listOf("Spatial canvas", "Hand-drawn diagrams", "SVG/PNG export", "Local storage"),
            openSource = true,
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "cal_com_free",
            name = "Cal.com Open Scheduler",
            division = OperatingDivision.PERSONAL_OPS,
            category = "Calendar & Time Allocation",
            url = "https://cal.com",
            description = "Open scheduling infrastructure for booking time slots, milestone deadlines, and calendar synchronization.",
            freeTierType = "Free Individual Tier",
            capabilities = listOf("Booking links", "Timezone math", "ICS generation", "Buffer management"),
            openSource = true,
            status = ToolCapabilityStatus.VERIFIED
        ),
        FreeTool(
            id = "jsonlint",
            name = "JSONLint Data Sanitizer",
            division = OperatingDivision.PERSONAL_OPS,
            category = "Record Validation & Hygiene",
            url = "https://jsonlint.com",
            description = "Fast JSON validator and formatter for cleaning database backups, state records, and export files.",
            freeTierType = "100% Free Web Tool",
            capabilities = listOf("Syntax validation", "Formatting", "Error highlighting", "Encoding check"),
            status = ToolCapabilityStatus.VERIFIED
        )
    )

    fun findToolsForDivision(division: OperatingDivision): List<FreeTool> {
        return ALL_TOOLS.filter { it.division == division }
    }

    fun searchTools(query: String): List<FreeTool> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return ALL_TOOLS
        return ALL_TOOLS.filter {
            it.name.lowercase().contains(q) ||
            it.description.lowercase().contains(q) ||
            it.category.lowercase().contains(q) ||
            it.division.title.lowercase().contains(q) ||
            it.capabilities.any { cap -> cap.lowercase().contains(q) }
        }
    }
}
