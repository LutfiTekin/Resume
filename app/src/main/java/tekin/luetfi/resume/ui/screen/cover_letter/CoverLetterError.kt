package tekin.luetfi.resume.ui.screen.cover_letter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tekin.luetfi.resume.R
import tekin.luetfi.resume.domain.model.MatchError
import tekin.luetfi.resume.ui.component.AnimatedConfirmation
import tekin.luetfi.resume.util.getFailedMessageList

@Composable
fun CoverLetterError(
    modifier: Modifier = Modifier,
    error: MatchError?,
    onRetry: () -> Unit
) {
    var showRetryButton by remember { mutableStateOf(false) }
    val errorLabel: String = error?.type ?: "Failed"
    val list by remember { mutableStateOf(getFailedMessageList(errorLabel)) }

    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = error?.error?.message ?: stringResource(R.string.cover_letter_error_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = error?.error?.details ?: stringResource(R.string.error_description),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            AnimatedConfirmation(
                modifier = Modifier,
                finalText = errorLabel,
                items = list
            ) {
                showRetryButton = true
            }
            Spacer(Modifier.height(16.dp))
            if (showRetryButton) {
                Button(onClick = onRetry) { Text(stringResource(R.string.retry_button)) }
            }
        }
    }
}