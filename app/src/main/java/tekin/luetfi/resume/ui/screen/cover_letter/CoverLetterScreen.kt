package tekin.luetfi.resume.ui.screen.cover_letter

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tekin.luetfi.resume.domain.model.Cv
import tekin.luetfi.resume.domain.model.JobApplicationMail
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.util.sendEmailWithAttachment

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


