package tekin.luetfi.resume.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tekin.luetfi.resume.domain.model.Contact
import tekin.luetfi.resume.domain.model.Cv
import tekin.luetfi.resume.ui.component.ExperienceCard
import tekin.luetfi.resume.ui.theme.CvTheme
import java.util.Locale


const val TAB_EXPERIENCE = 0
const val TAB_TECH = 1

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
                cv = uiState.resume
            )
        }
    }
}


@Composable
fun Home(
    modifier: Modifier = Modifier,
    cv: Cv
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Experience", "Tech Stack")

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
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
                    style = MaterialTheme.typography.bodySmall
                )
                cv.contact.linkedin?.let {
                    Text(
                        text = "LinkedIn: $it",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                cv.contact.github?.let {
                    Text(
                        text = "GitHub: $it",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        item {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
        }

        when (selectedTab) {
            TAB_EXPERIENCE -> {
                items(cv.experience) { experience ->
                    //List of experience
                    ExperienceCard(experience) {

                    }
                }
            }

            TAB_TECH -> {
                item {
                    cv.techStack.forEach { stack ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = stack.key.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.ENGLISH
                                ) else it.toString()
                            }, style = MaterialTheme.typography.titleMedium)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                stack.value.forEach { tech ->
                                    AssistChip(onClick = {}, label = {
                                        Text(text = tech, color = MaterialTheme.colorScheme.primary)
                                    })
                                }
                            }
                        }
                    }

                }
            }
        }


    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomePreview() {
    CvTheme {
        Home(
            cv = Cv(
                name = "Lütfi Tekin",
                openToOpportunities = "actively_looking",
                careerStart = "2017-02",
                contact = Contact(
                    email = "contact@lutfitek.in",
                    linkedin = "https://linkedin.com/in/lutfitekin",
                    github = "https://github.com/LutfiTekin"
                ),
                summary = "Seasoned Android Developer with over eight years of experience. Kotlin, Jetpack Compose, Firebase.",
                experience = emptyList(),
                languages = mapOf("english" to "Fluent", "german" to "Intermediate"),
                techStack = emptyMap()
            )
        )
    }
}





