package tekin.luetfi.resume.ui.screen.analyze

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tekin.luetfi.resume.R
import tekin.luetfi.resume.domain.model.FinalRecommendation
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.util.isLargeScreen

@Composable
fun AnalyzeReport(
    modifier: Modifier = Modifier,
    report: MatchResponse,
    onSaveReport: (MatchResponse) -> Unit = {},
    onDeleteReport: (MatchResponse) -> Unit = {},
    onGenerateCoverLetter: () -> Unit = {},
    onExit: () -> Unit = {},
    online: Boolean
) {

    BackHandler {
        onExit()
    }

    val scroll = rememberScrollState()
    Column(
        modifier = modifier
            .verticalScroll(scroll)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Header + Save
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Analysis",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            if (online){
                FilledTonalButton(
                    onClick = { onSaveReport(report) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save report")
                }
            }else {
                FilledTonalButton(
                    onClick = { onDeleteReport(report) }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete report")
                }
            }
        }

        if (isLargeScreen()){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                VerdictHeader(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    recommendation = report.finalRecommendation,
                    score = report.score1to5
                )

                if (report.finalRecommendation != FinalRecommendation.SKIP) {
                    CoverLetter(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        report = report,
                        onGenerateCoverLetter = onGenerateCoverLetter)
                }
            }


            ConciseSummary(
                modifier = Modifier.fillMaxWidth(),
                report = report)

            SupportingFacts(
                modifier = Modifier.fillMaxWidth(),
                report = report)
        }else {
            VerdictHeader(
                recommendation = report.finalRecommendation,
                score = report.score1to5
            )

            if (report.finalRecommendation != FinalRecommendation.SKIP) {
                CoverLetter(
                    modifier = Modifier.fillMaxWidth(),
                    report = report,
                    onGenerateCoverLetter = onGenerateCoverLetter)
            }

            ConciseSummary(
                modifier = Modifier.fillMaxWidth(),
                report = report)

            SupportingFacts(
                modifier = Modifier.fillMaxWidth(),
                report = report)
        }
    }
}

@Composable
private fun VerdictHeader(
    modifier: Modifier = Modifier,
    recommendation: FinalRecommendation,
    score: Int
) {
    val bg = when (recommendation) {
        FinalRecommendation.APPLY -> MaterialTheme.colorScheme.primaryContainer
        FinalRecommendation.CONSIDER -> MaterialTheme.colorScheme.tertiaryContainer
        FinalRecommendation.SKIP -> MaterialTheme.colorScheme.errorContainer
    }
    val fg = when (recommendation) {
        FinalRecommendation.APPLY -> MaterialTheme.colorScheme.onPrimaryContainer
        FinalRecommendation.CONSIDER -> MaterialTheme.colorScheme.onTertiaryContainer
        FinalRecommendation.SKIP -> MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        modifier = modifier,
        color = bg,
        contentColor = fg,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Verdict: ${recommendation.name}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            AssistChip(
                onClick = {},
                enabled = false,
                label = { Text("Score: $score/5") }
            )
        }
    }
}

@Composable
private fun ConciseSummary(
    modifier: Modifier,
    report: MatchResponse
) {
    // A crisp one-paragraph summary. Keep it skimmable.
    val matched = report.fitAnalysis.matched.size
    val gaps = report.fitAnalysis.gaps.size
    val locNote = report.locationFit.notes
    val languageIssues = report.languageFit.missingOrInsufficient

    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = buildString {
                    append("${report.job.title} at ${report.job.company}: ")
                    append(stringResource(R.string.match_highlights_gaps, matched, gaps))
                    if (languageIssues.isNotEmpty()) {
                        append(
                            stringResource(
                                R.string.language_risks,
                                languageIssues.joinToString()
                            ))
                    }
                    if (locNote.isNotBlank()) {
                        append(stringResource(R.string.location_note, locNote))
                    }
                }
            )
            if (report.resumeActions.tailoredSummary.isNotBlank()) {
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                Text(
                    text = report.resumeActions.tailoredSummary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SupportingFacts(modifier: Modifier, report: MatchResponse) {
    // Three compact cards: Job, Fit, Actions
    if (isLargeScreen()){
        Row(
            modifier = modifier.fillMaxWidth()
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            JobAtAGlance(modifier.weight(1f).fillMaxHeight(), report)
            FitBreakdown(modifier.weight(1f).fillMaxHeight(), report)
            ResumeTweaks(modifier.weight(1f).fillMaxHeight(), report)
        }
    }else {
        JobAtAGlance(modifier, report)
        FitBreakdown(modifier, report)
        ResumeTweaks(modifier, report)
    }
}

@Composable
private fun JobAtAGlance(modifier: Modifier, report: MatchResponse) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Job", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text("${report.job.title} • ${report.job.company}")
            Text("${report.job.location} • ${report.job.workMode.name.lowercase().replaceFirstChar { it.uppercase() }}")
            if (report.job.techKeywords.isNotEmpty()) {
                Text("Tech: ${report.job.techKeywords.joinToString()}")
            }
        }
    }
}

@Composable
private fun FitBreakdown(modifier: Modifier, report: MatchResponse) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Fit analysis", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            KeyValueRow("Matched", report.fitAnalysis.matched.joinToString().ifBlank { "—" })
            KeyValueRow("Gaps", report.fitAnalysis.gaps.joinToString().ifBlank { "—" })
            KeyValueRow("Uncertain", report.fitAnalysis.uncertain.joinToString().ifBlank { "—" })
        }
    }
}

@Composable
private fun ResumeTweaks(modifier: Modifier, report: MatchResponse) {
    val act = report.resumeActions
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Resume actions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            if (act.add.isNotEmpty()) KeyValueRow("Add", act.add.joinToString())
            if (act.rewriteOrQuantify.isNotEmpty()) KeyValueRow("Rewrite", act.rewriteOrQuantify.joinToString())
            if (act.remove.isNotEmpty()) KeyValueRow("Remove", act.remove.joinToString())
            if (act.keywordsToInclude.isNotEmpty()) KeyValueRow("Keywords", act.keywordsToInclude.joinToString())
        }
    }
}

@Composable
private fun CoverLetter(modifier: Modifier, report: MatchResponse, onGenerateCoverLetter: () -> Unit = {}) {
    Card(modifier = modifier
        .clickable{
            onGenerateCoverLetter()
        }) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Generate a cover letter", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.tertiary)
            Text("Generate a cover letter using the summary and your cv data.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.tertiary)
            report.job.extractedEmail?.let { email ->
                Text("Email extracted from job description:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
                Text(email, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}

@Composable
private fun KeyValueRow(key: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("$key:", fontWeight = FontWeight.SemiBold)
        Text(value)
    }
}