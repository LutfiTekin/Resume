package tekin.luetfi.resume.ui.screen.cover_letter

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import kotlinx.coroutines.flow.map
import tekin.luetfi.resume.R
import tekin.luetfi.resume.domain.model.Cv
import tekin.luetfi.resume.domain.model.JobApplicationMail
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.ui.component.AnimatedConfirmationIndeterminate
import tekin.luetfi.resume.ui.screen.analyze.AnalyzeJobState
import tekin.luetfi.resume.ui.screen.analyze.AnalyzeJobViewModel
import tekin.luetfi.resume.util.SynonymsDictionary.applySynonyms
import tekin.luetfi.resume.util.SynonymsDictionary.considerSynonyms
import tekin.luetfi.resume.util.SynonymsDictionary.createSynonymsList
import tekin.luetfi.resume.util.SynonymsDictionary.generatingSynonyms
import tekin.luetfi.resume.util.SynonymsDictionary.loadingSynonyms
import kotlin.random.Random

@Composable
fun CoverLetterScreen(modifier: Modifier, cv: Cv, analyzeJobViewModel: AnalyzeJobViewModel){

    val report: MatchResponse? by analyzeJobViewModel.state
        .map {
            if (it is AnalyzeJobState.ReportReady){
                it.report
            }else null

        }.collectAsStateWithLifecycle(null)
    val coverLetterViewModel: CoverLetterViewModel = hiltViewModel()
    val state by coverLetterViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(report){
        report?.let {
            coverLetterViewModel.generateCoverLetter(it, cv)
        }
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
            AnimatedConfirmationIndeterminate(modifier = Modifier.fillMaxWidth(), items = loading)
            AnimatedConfirmationIndeterminate(modifier = Modifier.fillMaxWidth(), items = generating)
        }
    }
}


@Composable
fun CoverLetter(
    modifier: Modifier = Modifier,
    mail: JobApplicationMail,
    onSendClick: () -> Unit = {}
) {
    var subject by remember { mutableStateOf(mail.subject) }
    var content by remember { mutableStateOf(mail.content) }


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
                .height(300.dp),
            maxLines = 15,
            minLines = 10,
            colors = OutlinedTextFieldDefaults.colors()
        )

        // Send Button
        Button(
            onClick = onSendClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = mail.subject.isNotBlank() && mail.content.isNotBlank()
        ) {
            Text("Send")
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