package tekin.luetfi.resume.domain.model

data class JobApplicationMail(
    val email: String? = null,
    val subject: String,
    val content: String
)