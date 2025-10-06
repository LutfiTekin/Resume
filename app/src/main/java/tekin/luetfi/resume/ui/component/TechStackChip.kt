package tekin.luetfi.resume.ui.component

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.launch
import tekin.luetfi.resume.ui.LocalSnackbarHostState
import tekin.luetfi.resume.ui.screen.home.TechStackInfoViewModel
import tekin.luetfi.resume.ui.theme.CvTheme

@Composable
fun TechStackChip(tech: String, onClick: () -> Unit = {}) {

    AssistChip(
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            labelColor = MaterialTheme.colorScheme.onSecondary
        ),
        onClick = onClick,
        label = {
            Text(
                text = tech
            )
        })
}

@Composable
fun rememberTechStackClickHandler(
    viewModel: TechStackInfoViewModel = hiltViewModel()
): (String) -> Unit {
    val snackbarHostState = LocalSnackbarHostState.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()


    return remember(viewModel, snackbarHostState, uriHandler) {
        { tech ->
            scope.launch {
                val techStackInfo = viewModel.getTechStackInfo(tech) ?: return@launch
                snackbarHostState.currentSnackbarData?.dismiss()
                val result = snackbarHostState.showSnackbar(
                    message = techStackInfo.description,
                    actionLabel = "MORE INFO",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                if (result == SnackbarResult.ActionPerformed) {
                    //TODO open inside app
                    uriHandler.openUri(techStackInfo.url)
                }
            }
        }
    }
}

@Preview
@Composable
fun TechStackChipPreview(){
    CvTheme {
        TechStackChip("Kotlin")
    }
}