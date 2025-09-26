package tekin.luetfi.resume.ui.screen.analyze

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tekin.luetfi.resume.ui.component.AnimatedConfirmation

@Composable
fun AnalyzeError(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {
    var showRetryButton by remember { mutableStateOf(false) }
    val list by remember { mutableStateOf(failedMessageList) }

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
            AnimatedConfirmation(
                modifier = Modifier,
                finalText = "Failed",
                items = list
            ) {
                showRetryButton = true
            }
            Spacer(Modifier.height(16.dp))
            if (showRetryButton) {
                Button(onClick = onRetry) { Text("Try again") }
            }
        }
    }
}

val failedMessageList: List<String>
    get() {
        val list = (failedSynonyms.take(failedSynonyms.size / 2) + listOf("Failed")).shuffled()
        val padding = (failedSynonyms - list).take(failedSynonyms.size / 3)
        //Ensure that "Failed" message won't end up as first or last item
        return padding + list + padding
    }


val failedSynonyms: List<String> = listOf(
    "Aborted", "Backfired", "Blocked", "Bombed", "Botched", "Broke down",
    "Collapsed", "Crashed", "Denied", "Derailed", "Died", "Disconnected",
    "Errored", "Expired", "Faltered", "Faulted", "Fell through", "Fizzled",
    "Flopped", "Froze", "Halted", "Hung", "Interrupted", "Lapsed",
    "Malfunctioned", "Misfired", "Rejected", "Refused", "Stalled",
    "Stopped", "Timed out", "Terminated", "Thwarted", "Tripped",
    "Unable", "Unavailable", "Unresponsive", "Unsuccessful"
)