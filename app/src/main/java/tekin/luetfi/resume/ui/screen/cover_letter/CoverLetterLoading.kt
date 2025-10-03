package tekin.luetfi.resume.ui.screen.cover_letter

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay
import tekin.luetfi.resume.R
import tekin.luetfi.resume.ui.component.AnimatedConfirmationIndeterminate
import tekin.luetfi.resume.util.SynonymsDictionary.applySynonyms
import tekin.luetfi.resume.util.SynonymsDictionary.considerSynonyms
import tekin.luetfi.resume.util.SynonymsDictionary.createSynonymsList
import tekin.luetfi.resume.util.SynonymsDictionary.generatingSynonyms
import tekin.luetfi.resume.util.SynonymsDictionary.loadingSynonyms
import kotlin.random.Random

@Composable
fun CoverLetterLoading(modifier: Modifier = Modifier){
    var loadingIndex by remember { mutableIntStateOf(1) }
    LaunchedEffect(Unit) {

        while (true) { // show this screen indefinitely
            delay(5000)
            loadingIndex = Random.nextInt(21)
        }
    }



    val loading by remember {
        derivedStateOf {
            createSynonymsList(
                subList = listOf("loading"),
                list = loadingSynonyms + generatingSynonyms
            )
        }
    }
    val generating by remember(loadingIndex) {
        derivedStateOf {
            createSynonymsList(
                subList = listOf("generating"),
                list = loadingSynonyms + generatingSynonyms
            )
        }
    }
    val apply by remember {
        derivedStateOf {
            createSynonymsList(
                subList = listOf("apply"),
                list = applySynonyms + considerSynonyms
            )
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(modifier = Modifier, text = loadingText(loadingIndex))
            CircularProgressIndicator(modifier = Modifier)
            AnimatedConfirmationIndeterminate(modifier = Modifier.fillMaxWidth(), items = apply)
            AnimatedConfirmationIndeterminate(modifier = Modifier.fillMaxWidth(), items = loading, delayBetweenItems = 2200)
            AnimatedConfirmationIndeterminate(modifier = Modifier.fillMaxWidth(), items = generating)
        }
    }
}


@SuppressLint("DiscouragedApi", "LocalContextResourcesRead")
@Composable
private fun loadingText(index: Int): String {
    if (index == -1) return stringResource(R.string.generating_cover_letter_1)
    val context = LocalContext.current
    val resourceId = remember(index) {
        context.resources.getIdentifier("generating_cover_letter_$index", "string", context.packageName)
    }
    return if (resourceId != 0) {
        context.getString(resourceId)
    } else {
        "Generating cover letter…"
    }
}