package tekin.luetfi.resume.util


import android.content.Context
import android.content.Intent
import android.net.Uri
import tekin.luetfi.resume.domain.model.JobApplicationMail

fun openPdf(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, PDF_MIME_TYPE)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Open CV PDF"))
}

fun sharePdf(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = PDF_MIME_TYPE
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share CV PDF"))
}

fun sendEmailWithAttachment(
    context: Context,
    mailData: JobApplicationMail
) {
    val emailIntent = Intent(Intent.ACTION_SEND).apply {


        // Set email recipient if provided
        mailData.email?.let {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(it))
        }

        // Set subject and body
        putExtra(Intent.EXTRA_SUBJECT, mailData.subject)
        putExtra(Intent.EXTRA_TEXT, mailData.content)

        mailData.pdfUri?.let {
            type = PDF_MIME_TYPE
            // Attach the PDF file
            putExtra(Intent.EXTRA_STREAM, it)
        }

        // Grant read permission to the email app
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
}