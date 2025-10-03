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
import tekin.luetfi.resume.ui.screen.analyze.AnalyzeJobViewModel
import tekin.luetfi.resume.ui.screen.analyze.AnalyzeScreen
import tekin.luetfi.resume.ui.screen.cover_letter.CoverLetterScreen
import tekin.luetfi.resume.ui.screen.home.HomeScreen
import tekin.luetfi.resume.ui.screen.home.CvViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Any = HomeRoute // typed start
) {
    val vm: CvViewModel = hiltViewModel()
    val uiState by vm.uiState.collectAsState()
    val analyzeJobViewModel: AnalyzeJobViewModel = hiltViewModel()


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
                    cv = cv,
                    viewModel = analyzeJobViewModel
                ){
                    navController.navigate(CoverLetterRoute)
                }
            }
        }

        composable<CoverLetterRoute> {
            uiState.resume?.let { cv ->
                CoverLetterScreen(
                    modifier = modifier,
                    cv = cv,
                    analyzeJobViewModel = analyzeJobViewModel
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
object CoverLetterRoute


@Serializable
data class ExperienceDetailRoute(val company: String)