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
import tekin.luetfi.resume.domain.model.WordAssociationResponse
import tekin.luetfi.resume.ui.component.AnimatedConfirmation
import tekin.luetfi.resume.ui.component.AnimatedConfirmationIndeterminate
import tekin.luetfi.resume.ui.screen.home.CvViewModel
import tekin.luetfi.resume.util.SynonymsDictionary.allWorkModeSynonyms
import tekin.luetfi.resume.util.SynonymsDictionary.applySynonyms
import tekin.luetfi.resume.util.SynonymsDictionary.considerSynonyms
import tekin.luetfi.resume.util.SynonymsDictionary.createSynonymsList
import tekin.luetfi.resume.util.SynonymsDictionary.skipSynonyms
import tekin.luetfi.resume.util.SynonymsDictionary.spokenLanguagesInTech
import tekin.luetfi.resume.util.SynonymsDictionary.techStackSectionSynonyms
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
    val viewModel: CvViewModel = hiltViewModel()
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

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
    ) {
        val (topGroup, progress, msg, belowGroup, actionBtn) = createRefs()

        verdict?.summary?.let { summary ->
            SummaryColumn(
                modifier = Modifier.constrainAs(topGroup) {
                    bottom.linkTo(msg.top, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                },
                summary = summary)
        }


        // Center anchor
        Text(
            text = loadingText(loadingIndex),
            modifier = Modifier.constrainAs(msg) {
                centerTo(parent)
            }
        )

        if (verdict == null) {
            // Spinner above the centered message
            CircularProgressIndicator(
                modifier = Modifier.constrainAs(progress) {
                    bottom.linkTo(msg.top, margin = if (verdict?.summary == null) 12.dp else 8.dp)
                    start.linkTo(msg.start)
                    end.linkTo(msg.end)
                }
            )

            // Animated indeterminate rows below the message
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
                                lists.entries.flatMap { it.value }
                            } else {
                                (cv?.techStack?.values?.flatten() ?: emptyList()) +
                                        techStackSectionSynonyms.take(10)
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
            val techKeywords by remember(verdict) {
                derivedStateOf {
                    createSynonymsList(
                        subList = verdict.techKeywords,
                        list = verdict.techKeywords + techStackSectionSynonyms.take(5)
                    )
                }
            }
            val workModes by remember(verdict) {
                derivedStateOf {
                    createSynonymsList(
                        subList = listOf(verdict.workMode),
                        list = allWorkModeSynonyms + listOf(verdict.workMode)
                    )
                }
            }
            val languages by remember(verdict) {
                derivedStateOf {
                    createSynonymsList(
                        subList = verdict.languages,
                        list = spokenLanguagesInTech
                    )
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
                // Verdict node
                AnimatedConfirmation(
                    modifier = Modifier.fillMaxWidth(),
                    finalText = verdict.finalRecommendation.name,
                    items = list
                ) { showButton = true }

                if (verdict.finalRecommendation != FinalRecommendation.SKIP) {
                    // Tech Keywords
                    if (verdict.techKeywords.size > 5) {
                        AnimatedConfirmationIndeterminate(
                            modifier = Modifier.fillMaxWidth(),
                            items = techKeywords
                        )
                    }

                    // Work Setting
                    AnimatedConfirmation(
                        modifier = Modifier.fillMaxWidth(),
                        finalText = verdict.workMode,
                        items = workModes
                    )

                    // Languages
                    if (verdict.languages.isNotEmpty()) {
                        AnimatedConfirmation(
                            modifier = Modifier.fillMaxWidth(),
                            finalText = verdict.languages.first(),
                            items = languages
                        )
                    }
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

@Composable
fun SummaryColumn(modifier: Modifier = Modifier, summary: WordAssociationResponse){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ){

        summary.wordAssociations.entries.forEach { (targetWord, list) ->
            val list by remember {
                derivedStateOf {
                    createSynonymsList(listOf(targetWord), list + listOf(targetWord))
                }
            }
            AnimatedConfirmation(
                modifier = Modifier.fillMaxWidth(),
                finalText = targetWord,
                items = list
            )
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




@SuppressLint("DiscouragedApi", "LocalContextResourcesRead")
@Composable
private fun loadingText(index: Int): String {
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



val lists = mapOf(
    FinalRecommendation.APPLY to applySynonyms,
    FinalRecommendation.CONSIDER to considerSynonyms,
    FinalRecommendation.SKIP to skipSynonyms
)