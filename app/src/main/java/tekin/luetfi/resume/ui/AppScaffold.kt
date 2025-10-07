@file:Suppress("KotlinConstantConditions", "SimplifyBooleanWithConstants")

package tekin.luetfi.resume.ui

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.mapNotNull
import tekin.luetfi.resume.BuildConfig
import tekin.luetfi.resume.R
import tekin.luetfi.resume.domain.model.Cv
import tekin.luetfi.resume.util.openToOpportunities
import tekin.luetfi.resume.ui.navigation.AppNavHost
import tekin.luetfi.resume.ui.navigation.HomeRoute
import tekin.luetfi.resume.ui.navigation.JobAnalyzerRoute
import tekin.luetfi.resume.ui.screen.cover_letter.CvWebViewScreen
import tekin.luetfi.resume.ui.screen.cover_letter.PdfViewModel
import tekin.luetfi.resume.ui.screen.home.CvViewModel
import tekin.luetfi.resume.ui.theme.AnalyzeIcon
import tekin.luetfi.resume.util.sharePdf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val snackbarHost = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val vm: CvViewModel = hiltViewModel()
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val cv by vm.uiState.mapNotNull { it.resume }.collectAsStateWithLifecycle(Cv())
    var shareRequested by remember { mutableStateOf(false) }
    //keep a reference to last downloaded pdf file
    //so future requests in the same session won't start unnecessary requests
    var downloadedPdf by remember { mutableStateOf<Uri?>(null) }

    if (shareRequested) {
        CvWebViewScreen(
            modifier = Modifier.fillMaxSize(),
            uri = downloadedPdf
        ) {
            downloadedPdf = it
            sharePdf(context, it)
            shareRequested = false
        }
        if (downloadedPdf == null)
            return
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                modifier = Modifier.clickable {
                    navController.navigate(HomeRoute)
                },
                title = {
                    Column {
                        Text(
                            text = cv.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = cv.openToOpportunities(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    if (BuildConfig.IS_MOCK.not() && uiState.resume != null) {
                        IconButton(onClick = {
                            navController.navigate(JobAnalyzerRoute)
                        }) {
                            Icon(
                                imageVector = AnalyzeIcon,
                                contentDescription = stringResource(R.string.analyze),
                                tint = Color.Unspecified
                            )
                        }
                    }
                    IconButton(onClick = {
                        shareRequested = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.share),
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHost) }
    ) { inner ->
        CompositionLocalProvider(LocalSnackbarHostState provides snackbarHost) {
            AppNavHost(
                modifier = Modifier
                    .padding(inner)
                    .consumeWindowInsets(inner), // avoid double padding with IME/system bars
                navController = navController,
                startDestination = HomeRoute
            )
        }
    }
}

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No Snackbar Host State")
}