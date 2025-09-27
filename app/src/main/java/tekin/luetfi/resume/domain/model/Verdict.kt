package tekin.luetfi.resume.domain.model

data class Verdict(
    val finalRecommendation: FinalRecommendation,
    val workMode: String,
    val techKeywords: List<String>,
    val languages: List<String>,
)




val MatchResponse.verdict
    get() = Verdict(
        finalRecommendation = finalRecommendation,
        workMode = job.workMode.name,
        techKeywords = job.techKeywords,
        languages = job.languageRequirements,
    )