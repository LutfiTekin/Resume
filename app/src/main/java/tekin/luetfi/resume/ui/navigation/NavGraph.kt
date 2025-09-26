package tekin.luetfi.resume.ui.navigation

import kotlinx.serialization.Serializable



import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import tekin.luetfi.resume.ui.screen.analyze.AnalyzeScreen
import tekin.luetfi.resume.ui.screen.home.HomeScreen
import tekin.luetfi.resume.ui.screen.home.HomeViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Any = HomeRoute // typed start
) {
    val vm: HomeViewModel = hiltViewModel()
    val uiState by vm.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {


        // Home
        composable<HomeRoute> {


            HomeScreen(
                modifier = modifier,
                uiState = uiState
            ){
                vm.refresh()
            }
        }

        composable<JobAnalyzerRoute> {
            uiState.resume?.let { cv ->
                AnalyzeScreen(
                    modifier = modifier,
                    cv = cv
                )
            }
        }


        /*


        // Experience detail: typed arg
        composable<ExperienceDetailRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<ExperienceDetailRoute>()
            // Pass args.company to your detail screen
            ExperienceScreen(
                onBack = { navController.popBackStack() },
                initialCompany = args.company
            )
        }*/
    }
}

/**
 * Routes
 */

@Serializable
object HomeRoute

@Serializable
object JobAnalyzerRoute

@Serializable
data class ExperienceDetailRoute(val company: String)