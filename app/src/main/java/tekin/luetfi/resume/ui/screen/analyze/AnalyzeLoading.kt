package tekin.luetfi.resume.ui.screen.analyze

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import tekin.luetfi.resume.R
import tekin.luetfi.resume.domain.model.FinalRecommendation
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.domain.model.Verdict
import tekin.luetfi.resume.ui.component.AnimatedConfirmation
import tekin.luetfi.resume.ui.component.AnimatedConfirmationIndeterminate
import tekin.luetfi.resume.ui.component.phoneticMap
import tekin.luetfi.resume.ui.screen.home.HomeViewModel
import kotlin.random.Random

const val FINAL_VERDICT = -1

//TODO support multiple models

@Composable
fun AnalyzeLoading(
    modifier: Modifier = Modifier,
    verdict: Verdict? = null,
    onResume: () -> Unit = {}
) {
    var showButton by remember { mutableStateOf(false) }
    var decisionNodes by remember { mutableIntStateOf(1) }
    var loadingIndex by remember { mutableIntStateOf(1) }
    val viewModel: HomeViewModel = hiltViewModel()
    val cv by viewModel.uiState.map { it.resume }.collectAsState(null)

    LaunchedEffect(verdict) {
        if (verdict != null) {
            loadingIndex = FINAL_VERDICT
        }
        while (verdict == null) { // operation is still ongoing
            delay(5000)
            decisionNodes++
            loadingIndex = Random.nextInt(21)
            if (decisionNodes > 5) decisionNodes = 2
        }
    }

    // Fill the screen so the center is truly screen center
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        val (progress, msg, belowGroup, actionBtn) = createRefs()

        // Center anchor
        Text(
            text = loadingText(loadingIndex),
            modifier = Modifier.constrainAs(msg) {
                centerTo(parent)
            }
        )

        if (verdict == null) {
            // Show a spinner above the centered message
            CircularProgressIndicator(
                modifier = Modifier.constrainAs(progress) {
                    bottom.linkTo(msg.top, margin = 12.dp)
                    start.linkTo(msg.start)
                    end.linkTo(msg.end)
                }
            )

            // The animated indeterminate rows live below the message
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.constrainAs(belowGroup) {
                    top.linkTo(msg.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                }
            ) {
                repeat(decisionNodes) { index ->
                    val isEven = index % 2 == 0
                    val list by remember(index) {
                        derivedStateOf {
                            if (Random.nextBoolean()) {
                                lists.entries
                                    .flatMap { it.value }
                            } else {
                                (cv?.techStack?.values?.flatten()
                                    ?: emptyList()) + techStackSectionSynonyms.take(10)
                            }
                                .shuffled()
                                .take(if (isEven) 40 else 15)
                        }
                    }
                    AnimatedConfirmationIndeterminate(
                        modifier = Modifier.fillMaxWidth(),
                        items = list
                    )
                }
            }
        } else {
            // Confirmation sequence directly below the message
            val list = remember(verdict) { createSynonymsList(verdict.finalRecommendation) }
            val techKeywords by remember(verdict) { derivedStateOf {
                (verdict.techKeywords + techStackSectionSynonyms.take(5)).shuffled()
            } }
            val workModes by remember(verdict) { derivedStateOf {
                (allWorkModeSynonyms + listOf(verdict.workMode)).shuffled()
            }}
            val languages by remember(verdict) {
                derivedStateOf {
                    val verdictLanguages = verdict.languages.toSet()
                    val otherLanguages = (spokenLanguagesInTech + verdict.languages).toSet() - verdictLanguages
                    val shuffledOthers = otherLanguages.shuffled()

                    val result = shuffledOthers.toMutableList()

                    // Insert verdict languages in middle positions only
                    val availablePositions = (2 until result.size - 2).toList()
                    val insertPositions = availablePositions.shuffled().take(verdictLanguages.size)

                    verdictLanguages.forEachIndexed { index, lang ->
                        if (index < insertPositions.size) {
                            result.add(insertPositions[index], lang)
                        }
                    }

                    result
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.constrainAs(belowGroup) {
                    top.linkTo(msg.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = androidx.constraintlayout.compose.Dimension.wrapContent
                }
            ) {
                //Verdict node
                AnimatedConfirmation(
                    modifier = Modifier.fillMaxWidth(),
                    finalText = verdict.finalRecommendation.name,
                    items = list
                ) {
                    showButton = true
                }
                //Tech Keywords
                AnimatedConfirmationIndeterminate(
                    modifier = Modifier.fillMaxWidth(),
                    items = techKeywords
                )

                //Work Setting
                AnimatedConfirmation(
                    modifier = Modifier.fillMaxWidth(),
                    finalText = verdict.workMode,
                    items = workModes
                )

                if (verdict.languages.isNotEmpty()){
                    AnimatedConfirmation(
                        modifier = Modifier.fillMaxWidth(),
                        finalText = verdict.languages.first(),
                        items = languages
                    )
                }
            }

            // Action button below the confirmation area
            Button(
                enabled = showButton,
                onClick = onResume,
                modifier = Modifier
                    .constrainAs(actionBtn) {
                        top.linkTo(belowGroup.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .alpha(if (showButton) 1f else 0f)
            ) {
                Text("Show Full Report")
            }
        }
    }
}

//Multi-Model
@Composable
fun AnalyzeLoading(
    modifier: Modifier = Modifier,
    modelResults: List<ModelResult>,
    onShowReport: (MatchResponse) -> Unit = {}
) {

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.analyzing_multi_model),
                    modifier = Modifier
                )
            }
            items(modelResults) { modelResult ->
                ModelResultItem(
                    modelResult = modelResult,
                    onShowReport = onShowReport
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ModelResultItem(
    modelResult: ModelResult,
    onShowReport: (MatchResponse) -> Unit
) {
    var showButton by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            when (modelResult.status) {
                ModelStatus.Completed -> {
                    Text(
                        text = modelResult.model.displayName + " says:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                ModelStatus.Failed -> {
                    Text(
                        text = modelResult.model.displayName + ": " + (modelResult.error?.error?.message
                            ?: "An Error Occurred"),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                ModelStatus.Loading -> {
                    Text(
                        text = modelResult.model.displayName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            // Show report button for completed models
            if (modelResult.status == ModelStatus.Completed && modelResult.report != null && showButton) {
                Text(
                    modifier = Modifier.clickable {
                        onShowReport(modelResult.report)
                    },
                    text = "Show Full Report",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        }

        when (modelResult.status) {
            ModelStatus.Loading -> {
                AnimatedConfirmationIndeterminate(
                    modifier = Modifier.fillMaxWidth(),
                    items = lists.entries.flatMap { it.value }.shuffled().take(20)
                )
            }

            ModelStatus.Completed -> {
                modelResult.report?.finalRecommendation?.let {
                    AnimatedConfirmation(modifier = Modifier, finalRecommendation = it) {
                        showButton = true
                    }

                }
            }

            ModelStatus.Failed -> {
                val errorLabel: String = modelResult.error?.type ?: "Failed"
                val list by remember { mutableStateOf(getFailedMessageList(errorLabel)) }
                AnimatedConfirmation(
                    modifier = Modifier,
                    finalText = errorLabel,
                    items = list
                )
            }
        }
    }
}


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

@SuppressLint("DiscouragedApi", "LocalContextResourcesRead")
@Composable
fun loadingText(index: Int): String {
    if (index == -1) return stringResource(R.string.analyzing_text_21)
    val context = LocalContext.current
    val resourceId = remember(index) {
        context.resources.getIdentifier("analyzing_text_$index", "string", context.packageName)
    }
    return if (resourceId != 0) {
        context.getString(resourceId)
    } else {
        "Analyzing the job description…"
    }
}


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



val lists = mapOf(
    FinalRecommendation.APPLY to applySynonyms,
    FinalRecommendation.CONSIDER to considerSynonyms,
    FinalRecommendation.SKIP to skipSynonyms
)