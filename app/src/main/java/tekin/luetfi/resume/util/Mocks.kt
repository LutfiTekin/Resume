package tekin.luetfi.resume.util

import tekin.luetfi.resume.domain.model.Contact
import tekin.luetfi.resume.domain.model.Cv

object Mocks {
    val cv = Cv(
        name = "Lütfi Tekin",
        openToOpportunities = "actively_looking",
        careerStart = "2017-02",
        contact = Contact(
            email = "contact@lutfitek.in",
            linkedin = "https://linkedin.com/in/lutfitekin",
            github = "https://github.com/LutfiTekin"
        ),
        summary = "Seasoned Android Developer with over eight years of experience. Kotlin, Jetpack Compose, Firebase.",
        experience = emptyList(),
        languages = mapOf("english" to "Fluent", "german" to "Intermediate"),
        techStack = emptyMap()
    )
}