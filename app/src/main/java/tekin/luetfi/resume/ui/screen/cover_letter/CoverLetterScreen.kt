package tekin.luetfi.resume.ui.screen.cover_letter

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import tekin.luetfi.resume.R
import tekin.luetfi.resume.domain.model.Cv
import tekin.luetfi.resume.domain.model.JobApplicationMail
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.ui.component.AnimatedConfirmationIndeterminate
import tekin.luetfi.resume.util.SynonymsDictionary.applySynonyms
import tekin.luetfi.resume.util.SynonymsDictionary.considerSynonyms
import tekin.luetfi.resume.util.SynonymsDictionary.createSynonymsList
import tekin.luetfi.resume.util.SynonymsDictionary.generatingSynonyms
import tekin.luetfi.resume.util.SynonymsDictionary.loadingSynonyms
import tekin.luetfi.resume.util.sendEmailWithAttachment
import kotlin.random.Random

@Composable
fun CoverLetterScreen(modifier: Modifier, cv: Cv, report: MatchResponse){

    val coverLetterViewModel: CoverLetterViewModel = hiltViewModel()
    val state by coverLetterViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(report){
        coverLetterViewModel.generateCoverLetter(report, cv)
    }

    when (state) {
        is CoverLetterState.Error -> CoverLetterLoading()
        CoverLetterState.Loading -> CoverLetterLoading(modifier = modifier.fillMaxSize())
        is CoverLetterState.Success -> CoverLetter(modifier = modifier.fillMaxSize(), mail = (state as CoverLetterState.Success).mail)
    }


}

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


@Composable
fun CoverLetter(
    modifier: Modifier = Modifier,
    mail: JobApplicationMail
) {
    val context = LocalContext.current
    val pdfViewModel: PdfViewModel = hiltViewModel()
    var subject by remember { mutableStateOf(mail.subject) }
    var content by remember { mutableStateOf(mail.content) }
    val downloadedPdf: Uri? by pdfViewModel.downloadedPdf.collectAsStateWithLifecycle(null)
    var cvFileAttached by remember { mutableStateOf(downloadedPdf != null) }

    if (cvFileAttached && downloadedPdf == null){
        //Download pdf file if user requested
        CvWebViewScreen(
            modifier = Modifier.fillMaxSize(),
            viewModel = pdfViewModel)
        return
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Subject TextField
        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("Subject") },
            placeholder = { Text("Enter email subject") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors()
        )

        // Content TextField
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
            placeholder = {  },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = OutlinedTextFieldDefaults.colors()
        )

        Row(
            modifier = Modifier.clickable { cvFileAttached = !cvFileAttached },
            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = cvFileAttached, onCheckedChange = { cvFileAttached = it })
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Attach CV File",
                style = MaterialTheme.typography.bodyMedium)
        }

        // Send Button
        Button(
            onClick = {
                sendEmailWithAttachment(context, mail.copy(subject = subject, content = content, pdfUri = downloadedPdf))
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = mail.subject.isNotBlank() && mail.content.isNotBlank()
        ) {
            Text("Send via Default Email App")
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