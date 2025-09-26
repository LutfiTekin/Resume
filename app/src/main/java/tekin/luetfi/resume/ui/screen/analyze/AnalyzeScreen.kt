@file:OptIn(ExperimentalMaterial3Api::class)

package tekin.luetfi.resume.ui.screen.analyze

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tekin.luetfi.resume.domain.model.Cv
import tekin.luetfi.resume.ui.theme.CvTheme


@Composable
fun AnalyzeScreen(
    modifier: Modifier,
    cv: Cv
) {
    val viewModel: AnalyzeJobViewModel = hiltViewModel()
    val analyzeJobState by viewModel.state.collectAsStateWithLifecycle(AnalyzeJobState.Start)

    val previousReports by viewModel.previousReports.collectAsStateWithLifecycle(emptyList())

    when (val state = analyzeJobState) {
        is AnalyzeJobState.Error -> AnalyzeError(
            modifier = modifier.fillMaxSize(),
            error = state.matchError,
            onRetry = {
                viewModel.reset()
            }
        )

        is AnalyzeJobState.Loading -> AnalyzeLoading(
            modifier = modifier.fillMaxSize(),
            verdict = state.finalRecommendation){
            viewModel.onUserContinue()
        }

        is AnalyzeJobState.ReportReady -> AnalyzeReport(
            modifier = modifier.fillMaxSize(),
            online = state.online,
            report = state.report,
            onSaveReport = viewModel::saveReport,
            onDeleteReport = viewModel::deleteReport
        )

        AnalyzeJobState.Start -> {
            AnalyzeStart(
                modifier = modifier,
                previousReports = previousReports,
            ) { jobDesc, model ->
                viewModel.analyze(
                    jobDescription = jobDesc,
                    cv = cv,
                    model = model)
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    CvTheme { AnalyzeLoading(Modifier.fillMaxSize()) }
}






