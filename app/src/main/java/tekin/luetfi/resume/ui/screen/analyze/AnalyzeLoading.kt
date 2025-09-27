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
import kotlinx.coroutines.delay
import tekin.luetfi.resume.R
import tekin.luetfi.resume.domain.model.FinalRecommendation
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.ui.component.AnimatedConfirmation
import tekin.luetfi.resume.ui.component.AnimatedConfirmationIndeterminate
import tekin.luetfi.resume.ui.component.phoneticMap
import kotlin.random.Random

const val FINAL_VERDICT = -1

//TODO support multiple models

@Composable
fun AnalyzeLoading(
    modifier: Modifier = Modifier,
    verdict: FinalRecommendation? = null,
    onResume: () -> Unit = {}
) {
    var showButton by remember { mutableStateOf(false) }
    var decisionNodes by remember { mutableIntStateOf(1) }
    var loadingIndex by remember { mutableIntStateOf(1) }

    LaunchedEffect(verdict) {
        if (verdict != null){
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
                    AnimatedConfirmationIndeterminate(
                        modifier = Modifier.fillMaxWidth(),
                        items = lists.entries
                            .flatMap { it.value }
                            .shuffled()
                            .take(if (isEven) 40 else 15)
                    )
                }
            }
        } else {
            // Confirmation sequence directly below the message
            val list = remember(verdict) { createSynonymsList(verdict) }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.constrainAs(belowGroup) {
                    top.linkTo(msg.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = androidx.constraintlayout.compose.Dimension.wrapContent
                }
            ) {
                AnimatedConfirmation(
                    modifier = Modifier,
                    finalText = verdict.name,
                    items = list
                ) {
                    showButton = true
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
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
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

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            when(modelResult.status) {
                ModelStatus.Completed -> {
                    Text(
                        text = modelResult.model.displayName + " says:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                ModelStatus.Failed -> {
                    Text(
                        text = modelResult.model.displayName + ": " + (modelResult.error?.error?.message ?: "An Error Occurred"),
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

val lists = mapOf(
    FinalRecommendation.APPLY to applySynonyms,
    FinalRecommendation.CONSIDER to considerSynonyms,
    FinalRecommendation.SKIP to skipSynonyms
)