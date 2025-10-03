package tekin.luetfi.resume.util


import android.content.Context
import android.content.Intent
import android.net.Uri
import tekin.luetfi.resume.domain.model.JobApplicationMail
import androidx.core.net.toUri
import tekin.luetfi.resume.R

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

private fun sendEmail(
    context: Context,
    mailData: JobApplicationMail
) {
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        // This ensures only email apps handle the intent
        data = "mailto:".toUri()

        // Set email recipient if provided
        mailData.email?.let {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(it))
        }

        // Set subject and body
        putExtra(Intent.EXTRA_SUBJECT, mailData.subject)
        putExtra(Intent.EXTRA_TEXT, mailData.content)


    }

    context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.send_email)))
}

fun sendEmailWithAttachment(
    context: Context,
    mailData: JobApplicationMail
) {
    if (mailData.pdfUri == null) {
        sendEmail(context, mailData)
        return
    }
    val emailIntent = Intent(Intent.ACTION_SEND).apply {
        // Use message/rfc822 to filter for email apps
        type = MESSAGE_MIME_TYPE

        // Set email recipient if provided
        mailData.email?.let {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(it))
        }

        // Set subject and body
        putExtra(Intent.EXTRA_SUBJECT, mailData.subject)
        putExtra(Intent.EXTRA_TEXT, mailData.content)

        // Attach the PDF
        putExtra(Intent.EXTRA_STREAM, mailData.pdfUri)

        // Grant read permission to the email app
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.send_email)))
}