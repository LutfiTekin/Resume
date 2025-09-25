package tekin.luetfi.resume.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ───────────────────────────────────────────────────────────────────────────────
// Root
// ───────────────────────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class MatchResponse(
    val job: JobInfo,
    @param:Json(name = "extracted_requirements") val extractedRequirements: ExtractedRequirements,
    @param:Json(name = "cv_at_a_glance") val cvAtAGlance: CvAtAGlance,
    @param:Json(name = "fit_analysis") val fitAnalysis: FitAnalysis,
    @param:Json(name = "location_fit") val locationFit: LocationFit,
    @param:Json(name = "language_fit") val languageFit: LanguageFit,
    @param:Json(name = "resume_actions") val resumeActions: ResumeActions,
    @param:Json(name = "questions_for_recruiter") val questionsForRecruiter: List<String>,
    @param:Json(name = "risk_flags") val riskFlags: List<String>,
    @param:Json(name = "final_recommendation") val finalRecommendation: FinalRecommendation,
    @param:Json(name = "score_1_to_5") val score1to5: Int
)

// ───────────────────────────────────────────────────────────────────────────────
// Sections
// ───────────────────────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class JobInfo(
    val title: String,
    val company: String,
    val location: String,
    @param:Json(name = "work_mode") val workMode: WorkMode,
    @param:Json(name = "seniority_label") val seniorityLabel: String,
    @param:Json(name = "language_requirements") val languageRequirements: List<String>,
    @param:Json(name = "tech_keywords") val techKeywords: List<String>
)

@JsonClass(generateAdapter = true)
data class ExtractedRequirements(
    @param:Json(name = "must_haves") val mustHaves: List<String>,
    @param:Json(name = "nice_to_haves") val niceToHaves: List<String>,
    @param:Json(name = "years_experience_min") val yearsExperienceMin: Int?,
    @param:Json(name = "years_experience_max") val yearsExperienceMax: Int?
)

@JsonClass(generateAdapter = true)
data class CvAtAGlance(
    val name: String,
    @param:Json(name = "career_start") val careerStart: String,
    val location: String,
    val languages: List<String>,
    @param:Json(name = "key_tech") val keyTech: List<String>
)

@JsonClass(generateAdapter = true)
data class FitAnalysis(
    val matched: List<String>,
    val gaps: List<String>,
    val uncertain: List<String>
)

@JsonClass(generateAdapter = true)
data class LocationFit(
    @param:Json(name = "cv_location") val cvLocation: String,
    @param:Json(name = "commute_within_2h_feasible") val commuteWithin2hFeasible: Boolean?, // may be null if unknown
    val notes: String
)

@JsonClass(generateAdapter = true)
data class LanguageFit(
    @param:Json(name = "required_languages") val requiredLanguages: List<String>,
    @param:Json(name = "cv_languages") val cvLanguages: List<String>,
    @param:Json(name = "missing_or_insufficient") val missingOrInsufficient: List<String>
)

@JsonClass(generateAdapter = true)
data class ResumeActions(
    val add: List<String>,
    val remove: List<String>,
    @param:Json(name = "rewrite_or_quantify") val rewriteOrQuantify: List<String>,
    @param:Json(name = "keywords_to_include") val keywordsToInclude: List<String>,
    @param:Json(name = "tailored_summary") val tailoredSummary: String
)

// ───────────────────────────────────────────────────────────────────────────────
// Enums
// ───────────────────────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = false)
enum class WorkMode {
    @Json(name = "remote") REMOTE,
    @Json(name = "hybrid") HYBRID,
    @Json(name = "onsite") ONSITE
}

@JsonClass(generateAdapter = false)
enum class FinalRecommendation {
    @Json(name = "APPLY") APPLY,
    @Json(name = "CONSIDER") CONSIDER,
    @Json(name = "SKIP") SKIP
}
