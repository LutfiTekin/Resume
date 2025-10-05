@file:OptIn(ExperimentalMaterial3Api::class)
package tekin.luetfi.resume.ui.screen.analyze


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tekin.luetfi.resume.R
import tekin.luetfi.resume.domain.model.FinalRecommendation
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.util.isLargeScreen

@Composable
fun PreviousReports(
    modifier: Modifier,
    onReportSelected: (MatchResponse) -> Unit = {},
) {
    val viewModel: AnalyzeJobViewModel = hiltViewModel()
    val previousReports by viewModel.previousReports.collectAsStateWithLifecycle(emptyList())

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Previous reports",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))

        if (previousReports.isEmpty()) {
            Text(
                text = "No saved reports yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 0.dp, max = 400.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(
                items = previousReports,
                key = { it.job.title + it.job.company } // matches JobReportEntity id
            ) { report ->
                ReportListItem(report, onReportSelected)
            }
        }
    }
}

@Composable
private fun ReportListItem(
    report: MatchResponse,
    onReportSelected: (MatchResponse) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                // Load the report into the screen state
                onReportSelected(report)
            }
    ) {
        if (isLargeScreen()){
            ListItem(
                headlineContent = { Text(report.job.title) },
                //key, high-level piece of metadata
                overlineContent = {
                    //seniority level and recommendation as key indicators
                    Text(
                        text = "${report.job.seniorityLabel.uppercase()} • ${report.finalRecommendation.name.uppercase()}",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                // supportingContent for a multi-line, detailed overview
                supportingContent = {
                    val matched = report.fitAnalysis.matched.size
                    val gaps = report.fitAnalysis.gaps.size
                    val locNote = report.locationFit.notes
                    val languageIssues = report.languageFit.missingOrInsufficient
                    Column {
                        // Company and Location are the primary supporting details
                        Text(
                            text = "${report.job.company} • ${report.job.location}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = buildString {
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
                            },
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        // Add secondary, scannable details like Work Mode and Tech Keywords
                        Text(
                            text = "Mode: ${report.job.workMode.name.lowercase().replaceFirstChar { it.titlecase() }} • Tech: ${report.job.techKeywords.take(2).joinToString(", ")}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                // leadingContent for a prominent visual indicator
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Score ${report.score1to5} out of 5",
                        tint = when (report.finalRecommendation) {
                            FinalRecommendation.APPLY -> MaterialTheme.colorScheme.primaryContainer
                            FinalRecommendation.CONSIDER -> MaterialTheme.colorScheme.tertiaryContainer
                            FinalRecommendation.SKIP -> MaterialTheme.colorScheme.errorContainer
                        }
                    )
                },
                // trailingContent for the meta-information and navigation affordance
                trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Display the score itself as meta-text
                        Text(
                            text = "${report.score1to5}/5",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        // Keep the navigation arrow
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }
            )
        }else {
            ListItem(
                headlineContent = { Text(report.job.title) },
                supportingContent = {
                    Text(
                        text = report.job.company,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
            )
        }
    }
}
