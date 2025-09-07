package tekin.luetfi.resume.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Cv(
    val name: String = "",
    @SerialName("openToOpportunities") val openToOpportunities: String? = null,
    @SerialName("careerStart") val careerStart: String? = null,   // "YYYY-MM"
    val contact: Contact = Contact(""),
    val summary: String = "",
    val experience: List<ExperienceItem> = emptyList(),
    val languages: Map<String, String> = emptyMap(),              // "english" to "Fluent"
    @SerialName("techStack") val techStack: Map<String, List<String>> = emptyMap()
)

@Serializable
data class Contact(
    val email: String, val linkedin: String? = null, val github: String? = null
)

@Serializable
data class ExperienceItem(
    val title: String,
    val type: String,
    val company: String,
    val location: String,
    val period: String,                         // e.g. "2025-05 to now"
    val project: String? = null,
    val stack: List<String> = emptyList(),
    val notes: List<String> = emptyList()
)