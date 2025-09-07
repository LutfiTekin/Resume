package tekin.luetfi.resume.ui

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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
    val viewModel: HomeViewModel = hiltViewModel()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Lütfi Tekin", style = MaterialTheme.typography.titleLarge) },
                scrollBehavior = scrollBehavior,
                actions = {

                     IconButton(onClick = viewModel::refresh) {
                         Icon(Icons.Rounded.Refresh, contentDescription = "Refresh")
                     }
                }
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