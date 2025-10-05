@file:OptIn(ExperimentalMaterial3Api::class)

package tekin.luetfi.resume.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tekin.luetfi.resume.R
import tekin.luetfi.resume.domain.model.Cv
import tekin.luetfi.resume.ui.component.AnimatedConfirmationIndeterminate
import tekin.luetfi.resume.ui.component.ContactSection
import tekin.luetfi.resume.ui.component.ExperienceCard
import tekin.luetfi.resume.ui.component.TechStackChip
import tekin.luetfi.resume.ui.theme.CvTheme
import tekin.luetfi.resume.util.Mocks
import tekin.luetfi.resume.util.SynonymsDictionary.errorSynonyms
import tekin.luetfi.resume.util.SynonymsDictionary.loadingSynonyms
import tekin.luetfi.resume.util.isLargeScreen
import java.util.Locale


const val TAB_EXPERIENCE = 0
const val TAB_TECH = 1

@Composable
fun HomeScreen(
    modifier: Modifier,
    uiState: HomeUiState,
    onRefresh: () -> Unit = {}
) {

    val pullToRefreshState = rememberPullToRefreshState()
    when {
        uiState.isLoading -> {
            val list by remember { mutableStateOf(
                loadingSynonyms.shuffled().take(loadingSynonyms.size / 2)
            ) }
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally))
                    AnimatedConfirmationIndeterminate(
                        modifier = Modifier.fillMaxWidth(),
                        items = list)
                }
            }
        }

        uiState.error != null && uiState.resume == null -> {
            val list by remember { mutableStateOf(
                errorSynonyms.shuffled().take(errorSynonyms.size / 2)
            ) }
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "An error occurred",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium)
                    AnimatedConfirmationIndeterminate(
                        modifier = Modifier.fillMaxWidth(),
                        items = list,
                        delayBetweenItems = 3000
                    )
                    AnimatedConfirmationIndeterminate(
                        modifier = Modifier.fillMaxWidth(),
                        items = list,
                        delayBetweenItems = 1000
                    )
                    AnimatedConfirmationIndeterminate(
                        modifier = Modifier.fillMaxWidth(),
                        items = list,
                        delayBetweenItems = 4000
                    )
                }
            }
        }
    }
    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = onRefresh,
        state = pullToRefreshState
    ) {
        uiState.resume?.let { cv ->
            Home(
                modifier = modifier,
                cv = cv
            )
        }
    }
}


@Composable
fun Home(
    modifier: Modifier = Modifier,
    cv: Cv
) {
    if (isLargeScreen()) {
        TabletLayout(
            modifier = modifier,
            cv = cv
        )
    } else {
        PhoneLayout(
            modifier = modifier,
            cv = cv
        )
    }
}

@Composable
private fun TabletLayout(modifier: Modifier, cv: Cv) {
    Row(modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize().weight(1f),
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ContactSection(cv)
            }
            item {
                Text(text = "Tech Stack", style = MaterialTheme.typography.titleLarge)
            }
            item {
                cv.techStack.forEach { stack ->
                    TechStackColumn(stack)
                }
            }
        }

        LazyVerticalStaggeredGrid(
            modifier = Modifier.weight(1.5f).padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp,
            columns = StaggeredGridCells.Fixed(2),) {
            item(span = StaggeredGridItemSpan.FullLine){
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "Experience", style = MaterialTheme.typography.titleLarge)
            }

            items(cv.experience) { experience ->
                //List of experience
                ExperienceCard(experience) {

                }
            }
        }
    }
}

@Composable
fun PhoneLayout(modifier: Modifier, cv: Cv) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.tab_experience),
        stringResource(R.string.tab_tech_stack)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ContactSection(cv)
        }


        stickyHeader {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
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
                        TechStackColumn(stack)
                    }
                }
            }
        }


    }
}

@Composable
private fun TechStackColumn(stack: Map.Entry<String, List<String>>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = stack.key.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ENGLISH
            ) else it.toString()
        }, style = MaterialTheme.typography.titleMedium)
        CompositionLocalProvider(
            LocalMinimumInteractiveComponentSize provides 32.dp
        ) {
            FlowRow(
                modifier = Modifier.padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                stack.value.forEach { tech ->
                    TechStackChip(tech)
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
            cv = Mocks.cv
        )
    }
}





