@file:OptIn(ExperimentalMaterial3Api::class)

package tekin.luetfi.resume.ui.screen.analyze

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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

    val state by viewModel.state.collectAsStateWithLifecycle(AnalyzeJobState.Start)

    when (state) {
        is AnalyzeJobState.Error -> TODO()
        AnalyzeJobState.Loading -> TODO()
        is AnalyzeJobState.ReportReady -> TODO()
        AnalyzeJobState.Start -> {
            AnalyzeStart(
                modifier = modifier
            ){
                viewModel.analyze(it, cv)
            }
        }
    }



}


@Composable
fun AnalyzeStart(
    modifier: Modifier = Modifier,
    onAnalyze: (String) -> Unit = {}
) {
    var jobDescription by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = jobDescription,
            onValueChange = { jobDescription = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            placeholder = { },
            minLines = 5,
            maxLines = 8,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                enabled = jobDescription.isNotBlank(),
                onClick = {
                    onAnalyze(jobDescription)
                }
            ) {
                Text("Analyze")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun HomePreview() {
    CvTheme {
        AnalyzeStart(
        )
    }
}





