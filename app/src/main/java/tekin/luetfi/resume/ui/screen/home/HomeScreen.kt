package tekin.luetfi.resume.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tekin.luetfi.resume.ui.theme.CvTheme


@Composable
fun HomeScreen(
    modifier: Modifier,
    uiState: HomeUiState,
    onNavigateExperience: () -> Unit = {},
    onNavigateTech: () -> Unit = {}
) {
    when {
        uiState.isLoading -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${uiState.error}")
            }
        }
        uiState.resume != null -> {
            Home(
                modifier = modifier,
                summary = uiState.resume.summary,
                email = uiState.resume.contact.email,
                linkedin = uiState.resume.contact.linkedin,
                github = uiState.resume.contact.github,
                openToOpportunities = uiState.resume.openToOpportunities,
                onNavigateExperience = onNavigateExperience,
                onNavigateTech = onNavigateTech
            )
        }
    }
}


@Composable
fun Home(
    modifier: Modifier = Modifier,
    summary: String,
    email: String,
    linkedin: String?,
    github: String?,
    openToOpportunities: String?,
    onNavigateExperience: () -> Unit = {},
    onNavigateTech: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = summary, style = MaterialTheme.typography.bodyMedium)
        Text(text = "📧 $email", style = MaterialTheme.typography.bodySmall)
        linkedin?.let { Text(text = "🔗 LinkedIn: $it", style = MaterialTheme.typography.bodySmall) }
        github?.let { Text(text = "💻 GitHub: $it", style = MaterialTheme.typography.bodySmall) }

        openToOpportunities?.let {
            Text(
                text = when (it) {
                    "actively_looking" -> "Actively looking for new opportunities"
                    "passive" -> "Open to interesting offers"
                    "not_interested" -> "Not currently seeking new roles"
                    else -> ""
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onNavigateExperience) { Text("Experience") }
            Button(onClick = onNavigateTech) { Text("Tech Stack") }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomePreview() {
    CvTheme {
        Home(
            summary = "Seasoned Android Developer with over eight years of experience. Kotlin, Jetpack Compose, Firebase.",
            email = "contact@lutfitek.in",
            linkedin = "https://linkedin.com/in/lutfitekin",
            github = "https://github.com/LutfiTekin",
            openToOpportunities = "actively_looking"
        )
    }
}





