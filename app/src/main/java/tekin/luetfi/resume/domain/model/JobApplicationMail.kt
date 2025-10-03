package tekin.luetfi.resume.domain.model

import android.net.Uri
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JobApplicationMail(
    val email: String? = null,
    val subject: String,
    val content: String,
    @Transient val pdfUri: Uri? = null
)