package tekin.luetfi.resume.ui.screen.cover_letter

import android.net.Uri
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.delay
import tekin.luetfi.resume.BuildConfig
import tekin.luetfi.resume.ui.component.AnimatedConfirmation
import tekin.luetfi.resume.ui.component.setupForPdf
import tekin.luetfi.resume.util.SynonymsDictionary
import tekin.luetfi.resume.util.SynonymsDictionary.createSynonymsList
import tekin.luetfi.resume.util.SynonymsDictionary.loadingSynonyms

@Composable
fun CvWebViewScreen(
    modifier: Modifier = Modifier,
    url: String = BuildConfig.CV_BASE_URL,
    uri: Uri? = null,
    viewModel: PdfViewModel = hiltViewModel(),
    onPdfReady: (Uri) -> Unit = {}
) {
    var pdfUri by remember(uri) { mutableStateOf(uri) }

    LaunchedEffect(pdfUri) {
        pdfUri?.let {
            //wait for loading animation to settle
            if (uri == null) { //first time download
                delay(3000)
            }
            onPdfReady(it)
        }
    }
    //Downloaded pdf uri is supplied do not draw any UI
    if (pdfUri != null && uri != null)
        return
    if (pdfUri == null) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                WebView(context).apply {
                    // setupForPdf needs the use case and a callback
                    setupForPdf(savePdf = viewModel.savePdf) { uri: Uri ->
                        viewModel.onPdfSaved(uri)
                        //openPdf(context, uri) // or call sharePdf(context, uri)
                        pdfUri = uri
                    }
                    loadUrl(url) // setupForPdf will append ?pdf automatically
                }
            }
        )
    }
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val associations = if (pdfUri != null){
            mapOf(
                SynonymsDictionary.generatedSynonyms.random() to SynonymsDictionary.generatedSynonyms.shuffled(),
                "PDF File" to SynonymsDictionary.pdfFileSynonyms.shuffled()
            )
        }else {
            mapOf(
                loadingSynonyms.random() to loadingSynonyms.shuffled(),
                SynonymsDictionary.generatingSynonyms.random() to SynonymsDictionary.generatedSynonyms.shuffled(),
                SynonymsDictionary.pdfFileSynonyms.random() to SynonymsDictionary.pdfFileSynonyms.shuffled()
            )
        }


        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column {
                associations.forEach { (targetWord, list) ->
                    val list by remember(targetWord) {
                        derivedStateOf {
                            createSynonymsList(listOf(targetWord), list + listOf(targetWord))
                        }
                    }
                    AnimatedConfirmation(
                        modifier = Modifier.fillMaxWidth(),
                        finalText = targetWord,
                        items = list
                    )
                }

            }
        }
    }
}
