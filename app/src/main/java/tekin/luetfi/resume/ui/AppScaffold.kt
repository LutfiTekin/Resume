package tekin.luetfi.resume.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import tekin.luetfi.resume.domain.model.Cv
import tekin.luetfi.resume.openToOpportunities
import tekin.luetfi.resume.ui.navigation.AppNavHost
import tekin.luetfi.resume.ui.navigation.HomeRoute
import tekin.luetfi.resume.ui.screen.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    navController: NavHostController = rememberNavController()
) {
    val snackbarHost = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val vm: HomeViewModel = hiltViewModel()
    val cv by vm.uiState.mapNotNull { it.resume }.collectAsStateWithLifecycle(Cv())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Lütfi Tekin"
                        )
                        Text(
                            text = cv.openToOpportunities(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                     },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHost) }
    ) { inner ->
        AppNavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier
                .padding(inner)
                .consumeWindowInsets(inner) // avoid double padding with IME/system bars
        )
    }
}