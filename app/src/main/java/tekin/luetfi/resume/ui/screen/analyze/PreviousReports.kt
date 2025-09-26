@file:OptIn(ExperimentalMaterial3Api::class)
package tekin.luetfi.resume.ui.screen.analyze


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import tekin.luetfi.resume.domain.model.MatchResponse

@Composable
fun PreviousReports(
    modifier: Modifier,
    previousReports: List<MatchResponse>
) {
    val viewModel: AnalyzeJobViewModel = hiltViewModel()

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
            modifier = Modifier.fillMaxWidth()
                .heightIn(min = 0.dp, max = 400.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(
                items = previousReports,
                key = { it.job.title + it.job.company } // matches JobReportEntity id
            ) { report ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            // Load the report into the screen state
                            viewModel.loadReport(report.job.title + report.job.company)
                        }
                ) {
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
                    HorizontalDivider()
                }
            }
        }
    }
}
