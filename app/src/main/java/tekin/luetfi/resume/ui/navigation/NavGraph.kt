package tekin.luetfi.resume.ui.navigation

import androidx.compose.material3.SnackbarHostState
import kotlinx.serialization.Serializable



import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.domain.model.MatchResponseNavType
import tekin.luetfi.resume.ui.screen.analyze.AnalyzeScreen
import tekin.luetfi.resume.ui.screen.cover_letter.CoverLetterScreen
import tekin.luetfi.resume.ui.screen.home.HomeScreen
import tekin.luetfi.resume.ui.screen.home.CvViewModel
import kotlin.reflect.typeOf

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Any = HomeRoute, // typed start
) {
    val vm: CvViewModel = hiltViewModel()
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
                ){
                    navController.navigate(CoverLetterRoute(it))
                }
            }
        }

        composable<CoverLetterRoute>(
            typeMap = mapOf(typeOf<MatchResponse>() to MatchResponseNavType)
        ) {

            val data = it.toRoute<CoverLetterRoute>()

            uiState.resume?.let { cv ->
                CoverLetterScreen(
                    modifier = modifier,
                    cv = cv,
                    report = data.report
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
data class CoverLetterRoute(val report: MatchResponse)


@Serializable
data class ExperienceDetailRoute(val company: String)