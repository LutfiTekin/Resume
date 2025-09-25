@file:OptIn(ExperimentalMaterial3Api::class)

package tekin.luetfi.resume.ui.screen.analyze

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.hilt.navigation.compose.hiltViewModel
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

    when (val state = analyzeJobState) {
        is AnalyzeJobState.Error -> AnalyzeError(
            modifier = modifier.fillMaxSize(),
            message = state.message.ifBlank { "Something went wrong" },
            onRetry = {
                viewModel.reset()
            }
        )
        AnalyzeJobState.Loading -> AnalyzeLoading(modifier.fillMaxSize())
        is AnalyzeJobState.ReportReady -> AnalyzeReport(
            modifier = modifier.fillMaxSize(),
            report = state.report
        )
        AnalyzeJobState.Start -> {
            AnalyzeStart(
                modifier = modifier
            ) { jobDesc ->
                viewModel.analyze(jobDesc, cv)
            }
        }
    }
}

@Composable
private fun AnalyzeLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("Analyzing the job description...")
        }
    }
}

@Composable
private fun AnalyzeError(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Could not analyze",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(message, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry) { Text("Try again") }
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    CvTheme { AnalyzeLoading(Modifier.fillMaxSize()) }
}






