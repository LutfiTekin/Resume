package tekin.luetfi.resume.util

import tekin.luetfi.resume.domain.model.FinalRecommendation
import tekin.luetfi.resume.ui.screen.analyze.lists
import kotlin.collections.plus

object SynonymsDictionary {

    val applySynonyms: List<String> = listOf(
        "Activate", "Adopt", "Administer", "Advance", "Allocate", "Appropriate",
        "Assemble", "Assign", "Bestow", "Bring to bear", "Build",
        "Carry out", "Commit", "Configure", "Dedicate", "Deploy", "Develop",
        "Devote", "Direct", "Drive", "Embrace", "Employ", "Enact", "Enforce",
        "Engage", "Engineer", "Establish", "Execute", "Exercise", "Exert",
        "Facilitate", "File", "Fulfill", "Function", "Govern", "Guide",
        "Harness", "Implement", "Incorporate", "Initiate", "Institute",
        "Install", "Integrate", "Introduce", "Launch", "Leverage", "Lodge",
        "Mobilize", "Offer", "Operate", "Orchestrate", "Organize", "Perform",
        "Pilot", "Pioneer", "Practice", "Present", "Propose", "Pursue",
        "Put forward", "Put into action", "Recruit", "Register", "Reinforce",
        "Render", "Roll out", "Submit", "Spearhead", "Steer", "Tender",
        "Use", "Utilize", "Wield"
    )

    val considerSynonyms: List<String> = listOf(
        "Analyze", "Appraise", "Ascertain", "Assess", "Audit", "Balance",
        "Bear in mind", "Benchmark", "Brood on", "Calculate", "Check",
        "Compare", "Contemplate", "Critique", "Debate", "Decode",
        "Decipher", "Deem", "Deliberate", "Determine", "Diagnose",
        "Dissect", "Discern", "Esteem", "Estimate", "Evaluate",
        "Examine", "Explore", "Factor in", "Forecast", "Gauge",
        "Inspect", "Interpret", "Investigate", "Judge", "Keep in view",
        "Look at", "Measure", "Meditate", "Monitor", "Mull over",
        "Muse", "Ponder", "Prioritize", "Probe", "Quantify", "Rank",
        "Rate", "Reckon", "Reconcile", "Reflect", "Regard", "Research",
        "Review", "Revolve", "Scrutinize", "Study", "Survey",
        "Take into account", "Test", "Think over", "Turn over",
        "Verify", "View", "Weigh"
    )

    val skipSynonyms: List<String> = listOf(
        "Abandon", "Abstain", "Avert", "Avoid", "Bar", "Brush aside",
        "Bypass", "Cease", "Circumvent", "Cut", "Decline", "Defer",
        "Delete", "Discard", "Disregard", "Dismiss", "Dodge", "Drop",
        "Eliminate", "Eschew", "Evade", "Exclude", "Forgo", "Forfeit",
        "Forego", "Gloss over", "Halt", "Ignore", "Jump", "Jump over",
        "Leave out", "Leapfrog", "Let slide", "Miss", "Neglect",
        "Not pursue", "Omit", "Overlook", "Overpass", "Pass",
        "Pass over", "Preclude", "Reject", "Relinquish", "Remove",
        "Renounce", "Refrain", "Set aside", "Shun", "Sidestep",
        "Skim", "Steer clear of", "Suspend", "Waive"
    )

    val techStackSectionSynonyms: List<String> = listOf(
        // From LANGUAGES
        "Programming Languages", "Coding Languages", "Development Languages",
        "Script Languages", "Software Languages", "Code",

        // From DEVOPS
        "Development Operations", "CI/CD", "Deployment", "Infrastructure",
        "Automation", "Pipeline Tools", "Operations",

        // From ANDROID
        "Mobile Development", "Mobile Platform", "Mobile Technologies",
        "Native Development", "Mobile Framework", "App Development",

        // From BACKEND
        "Server-side", "Backend Technologies", "Server Technologies",
        "API Development", "Server Development", "Backend Services",

        // From TOOLS
        "Development Tools", "Software Tools", "Utilities",
        "Build Tools", "Development Utilities", "Productivity Tools",

        // From DESIGN
        "UI/UX", "Design Tools", "Visual Design", "Interface Design",
        "Creative Tools", "Design Software",

        // From FIREBASE
        "Cloud Services", "Backend Services", "Cloud Platform",
        "BaaS", "Cloud Infrastructure", "Server Services"
    )

    val allWorkModeSynonyms: List<String> = listOf(
        // Remote synonyms
        "Telecommuting", "Work From Home", "WFH", "Telework",
        "Distributed Work", "Virtual Work", "Mobile Work", "Home-based",
        "Fully Remote", "Remote First", "Work From Anywhere", "Digital Nomad",
        "Location Independent", "Off-site", "Cloud-based Work",

        // Hybrid synonyms
        "Mixed Work", "Flexible Work", "Blended Work",
        "Part Remote", "Flex Work", "Variable Location", "Split Schedule",
        "Office Optional", "Location Flexible", "Partially Remote",
        "Combined Work", "Multi-location", "Alternating Work", "Strategic Flexibility",

        // Onsite synonyms
        "In-office", "Office-based", "Physical Workplace",
        "Traditional Work", "Centralized Work", "In-person Work",
        "Campus Work", "Workplace Present", "Fixed Location", "Office Bound",
        "Colocated Work", "Premises Work", "Site-based", "Facility Work"
    )

    val spokenLanguagesInTech: List<String> = listOf(
        // Tier 1 - Universal Languages
        "English",          // Global tech lingua franca

        // Tier 2 - Major Tech Hub Languages
        "Mandarin Chinese", // China's massive tech sector
        "Hindi",            // India's IT industry
        "Spanish",          // Latin America, Spain tech growth

        // Tier 3 - Regional Tech Languages
        "Russian",          // Eastern Europe, ex-Soviet states
        "German",           // DACH region tech hubs
        "Japanese",         // Japan's tech industry
        "French",           // France, Francophone countries
        "Portuguese",       // Brazil's growing tech sector
        "Korean",           // South Korea's tech dominance

        // Tier 4 - Emerging Tech Markets
        "Arabic",           // Middle East tech expansion
        "Dutch",            // Netherlands tech hubs
        "Italian",          // Italian tech companies
        "Swedish",          // Nordic tech innovation
        "Polish",           // Poland's IT outsourcing
        "Turkish",          // Turkey's tech growth
        "Indonesian",       // Southeast Asia's largest economy
        "Vietnamese",       // Vietnam's IT services boom
        "Ukrainian"         // Major IT outsourcing hub
    )


    val loadingSynonyms: List<String> = listOf(
        "Buffering", "Processing", "Fetching", "Retrieving", "Importing",
        "Downloading", "Uploading", "Parsing", "Reading", "Scanning",
        "Analyzing", "Extracting", "Initializing", "Preparing", "Opening",
        "Accessing", "Acquiring", "Collecting", "Gathering", "Obtaining",
        "Pulling", "Receiving", "Transferring", "Transmitting", "Syncing",
        "Rendering", "Compiling", "Decoding", "Interpreting", "Translating",
        "Converting", "Transforming", "Building", "Constructing", "Generating",
        "Creating", "Assembling", "Organizing", "Structuring", "Formatting",
        "Validating", "Verifying", "Checking", "Reviewing", "Examining",
        "In progress", "Please wait", "Stand by", "Working", "Busy"
    )

    val errorSynonyms: List<String> = listOf(
        "Failed", "Crashed", "Blocked", "Denied", "Rejected", "Corrupted",
        "Invalid", "Unsupported", "Broken", "Damaged", "Malformed", "Expired",
        "Timeout", "Disconnected", "Offline", "Unreachable", "Forbidden",
        "Unauthorized", "Not found", "Missing", "Empty", "Incomplete",
        "Interrupted", "Cancelled", "Aborted", "Terminated", "Suspended",
        "Overloaded", "Busy", "Unavailable", "Down", "Maintenance", "Locked",
        "Restricted", "Limited", "Exceeded", "Too large", "Too small",
        "Format error", "Parse error", "Read error", "Write error", "Network error",
        "Server error", "Client error", "System error", "Fatal error", "Critical error"
    )

    val failedSynonyms: List<String> = listOf(
        "Aborted", "Backfired", "Blocked", "Bombed", "Botched", "Broke down",
        "Collapsed", "Crashed", "Denied", "Derailed", "Died", "Disconnected",
        "Errored", "Expired", "Faltered", "Faulted", "Fell through", "Fizzled",
        "Flopped", "Froze", "Halted", "Hung", "Interrupted", "Lapsed",
        "Malfunctioned", "Misfired", "Rejected", "Refused", "Stalled",
        "Stopped", "Timed out", "Terminated", "Thwarted", "Tripped",
        "Unable", "Unavailable", "Unresponsive", "Unsuccessful"
    )

    val generatingSynonyms: List<String> = listOf(
        "Producing", "Creating", "Forming", "Assembling", "Constructing",
        "Building", "Fabricating", "Synthesizing", "Inventing", "Developing",
        "Composing", "Evolving", "Initiating", "Spawning", "Rendering",
        "Outputting", "Deriving", "Issuing", "Breeding", "Forging"
    )

    val pdfFileSynonyms: List<String> = listOf(
        "Document", "Report", "Digital File", "E-file", "Portable Document",
        "Paperless Document", "Electronic Report", "Digital Document",
        "Formatted File", "Printable File", "Publication", "Record",
        "Attachment", "Doc", "Booklet", "PDF File"
    )

    val generatedSynonyms: List<String> = listOf(
        "Created", "Produced", "Formed", "Constructed", "Built",
        "Assembled", "Developed", "Issued", "Compiled", "Authored",
        "Realized", "Initiated", "Spawned", "Synthesized",
        "Completed", "Drafted", "Finalized", "Output", "Rendered"
    )


    val phoneticMap = linkedMapOf(
        "A" to "Alfa", "B" to "Bravo", "C" to "Charlie", "D" to "Delta",
        "E" to "Echo", "F" to "Foxtrot", "G" to "Golf", "H" to "Hotel",
        "I" to "India", "J" to "Juliett", "K" to "Kilo", "L" to "Lima",
        "M" to "Mike", "N" to "November", "O" to "Oscar", "P" to "Papa",
        "Q" to "Quebec", "R" to "Romeo", "S" to "Sierra", "T" to "Tango",
        "U" to "Uniform", "V" to "Victor", "W" to "Whiskey", "X" to "X-ray",
        "Y" to "Yankee", "Z" to "Zulu",
        "0" to "Zero", "1" to "One", "2" to "Two", "3" to "Three",
        "4" to "Four", "5" to "Five", "6" to "Six", "7" to "Seven",
        "8" to "Eight", "9" to "Nine"
    )


    fun createSynonymsList(finalRecommendation: FinalRecommendation): List<String> {
        val mainList = (lists[finalRecommendation] ?: phoneticMap.values)

        val otherList = lists
            .asSequence()
            .filter { it.key != finalRecommendation }
            .flatMap { it.value }
            .shuffled()
            .take(10)
            .toList()
        val padding = (lists.flatMap { it.value } - otherList).shuffled().take(10)

        return padding + (mainList + otherList + listOf(finalRecommendation.name)).shuffled() + padding
    }

    fun createSynonymsList(subList: List<String>, list: List<String>): List<String> {
        val otherLanguages = (list + subList).toSet() - subList
        val shuffledOthers = otherLanguages.shuffled()

        val result = shuffledOthers.toMutableList()

        // Insert verdict languages in middle positions only
        val availablePositions = (2 until result.size - 2).toList()
        val insertPositions = availablePositions.shuffled().take(subList.size)

        subList.forEachIndexed { index, lang ->
            if (index < insertPositions.size) {
                result.add(insertPositions[index], lang)
            }
        }
        return result
    }

}