package tekin.luetfi.resume.ui.component
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import tekin.luetfi.resume.domain.model.Cv

@Composable
fun ContactSection(cv: Cv) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = cv.summary,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = cv.contact.email,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.clickable {
                uriHandler.openUri("mailto:${cv.contact.email}")
            }
        )
        cv.contact.linkedin?.let {
            Text(
                text = "LinkedIn: $it",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.clickable {
                    uriHandler.openUri(it)
                }
            )
        }
        cv.contact.github?.let {
            Text(
                text = "GitHub: $it",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.clickable {
                    uriHandler.openUri(it)
                }
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
