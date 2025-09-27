@file:OptIn(ExperimentalMaterial3Api::class)

package tekin.luetfi.resume.ui.screen.analyze

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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

        is AnalyzeJobState.Loading -> {
            if (state.modelResults.isNotEmpty()){
                AnalyzeLoading(
                    modifier = modifier.fillMaxSize(),
                    modelResults = state.sortedModelResults){
                    viewModel.loadReport(it)
                }
            }else {
                AnalyzeLoading(
                    modifier = modifier.fillMaxSize(),
                    verdict = state.verdict){
                    viewModel.onUserContinue()
                }
            }
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
            ) { jobDesc, models ->
                viewModel.analyze(
                    jobDescription = jobDesc,
                    cv = cv,
                    models = models)
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    CvTheme { AnalyzeLoading(Modifier.fillMaxSize()) }
}






