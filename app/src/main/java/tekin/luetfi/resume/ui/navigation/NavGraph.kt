package tekin.luetfi.resume.ui.navigation

import kotlinx.serialization.Serializable



import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import tekin.luetfi.resume.ui.screen.home.HomeScreen
import tekin.luetfi.resume.ui.screen.home.HomeViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Any = HomeRoute // typed start
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Home
        composable<HomeRoute> {
            val vm: HomeViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsState()

            HomeScreen(
                modifier = modifier,
                uiState = uiState,
                onNavigateExperience = { navController.navigate(ExperienceRoute) },
                onNavigateTech = { navController.navigate(TechStackRoute) }
            )
        }

        composable<ExperienceRoute> {

        }

        /*
        // Experience list
        composable<ExperienceRoute> {
            ExperienceScreen(
                onBack = { navController.popBackStack() },
                onDetailClick = { company ->
                    navController.navigate(ExperienceDetailRoute(company))
                }
            )
        }

        // Tech stack
        composable<TechStackRoute> {
            TechStackScreen(
                onBack = { navController.popBackStack() }
            )
        }

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
object ExperienceRoute

@Serializable
object TechStackRoute


@Serializable
data class ExperienceDetailRoute(val company: String)