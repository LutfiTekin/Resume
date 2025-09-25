package tekin.luetfi.resume.ui.screen.analyze

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tekin.luetfi.resume.domain.model.FinalRecommendation
import tekin.luetfi.resume.domain.model.MatchResponse

@Composable
fun AnalyzeReport(
    modifier: Modifier = Modifier,
    report: MatchResponse
) {
    val scroll = rememberScrollState()
    Column(
        modifier = modifier
            .verticalScroll(scroll)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        VerdictHeader(
            recommendation = report.finalRecommendation,
            score = report.score1to5
        )

        ConciseSummary(report = report)

        // Optional: supporting sections. Keep them compact.
        SupportingFacts(report = report)
    }
}

@Composable
private fun VerdictHeader(
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
        color = bg,
        contentColor = fg,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
    report: MatchResponse
) {
    // A crisp one-paragraph summary. Keep it skimmable.
    val matched = report.fitAnalysis.matched.size
    val gaps = report.fitAnalysis.gaps.size
    val locNote = report.locationFit.notes
    val languageIssues = report.languageFit.missingOrInsufficient

    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = buildString {
                    append("${report.job.title} at ${report.job.company}: ")
                    append("match highlights $matched, gaps $gaps. ")
                    if (languageIssues.isNotEmpty()) {
                        append("Language risks: ${languageIssues.joinToString()}. ")
                    }
                    if (locNote.isNotBlank()) {
                        append("Location: $locNote")
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
private fun SupportingFacts(report: MatchResponse) {
    // Three compact cards: Job, Fit, Actions
    JobAtAGlance(report)
    FitBreakdown(report)
    ResumeTweaks(report)
}

@Composable
private fun JobAtAGlance(report: MatchResponse) {
    Card {
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
private fun FitBreakdown(report: MatchResponse) {
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Fit analysis", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            KeyValueRow("Matched", report.fitAnalysis.matched.joinToString().ifBlank { "—" })
            KeyValueRow("Gaps", report.fitAnalysis.gaps.joinToString().ifBlank { "—" })
            KeyValueRow("Uncertain", report.fitAnalysis.uncertain.joinToString().ifBlank { "—" })
        }
    }
}

@Composable
private fun ResumeTweaks(report: MatchResponse) {
    val act = report.resumeActions
    Card {
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
private fun KeyValueRow(key: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("$key:", fontWeight = FontWeight.SemiBold)
        Text(value)
    }
}