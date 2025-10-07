package tekin.luetfi.resume.domain.model

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@Serializable
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
    @param:Json(name = "score_1_to_5") val score1to5: Int,
    val model: AnalyzeModel? = null
){
    //Keep track of used model
    fun attachModel(model: AnalyzeModel): MatchResponse {
        return copy(model = model)
    }

    //fallback to default model
    val usedModel: AnalyzeModel
        get() = model ?: AnalyzeModel.default
}

@Serializable
@JsonClass(generateAdapter = true)
data class JobInfo(
    val title: String,
    val company: String,
    val location: String,
    @param:Json(name = "extracted_email") val extractedEmail: String?,
    @param:Json(name = "work_mode") val workMode: WorkMode,
    @param:Json(name = "seniority_label") val seniorityLabel: String,
    @param:Json(name = "language_requirements") val languageRequirements: List<String>,
    @param:Json(name = "tech_keywords") val techKeywords: List<String>
)

@Serializable
@JsonClass(generateAdapter = true)
data class ExtractedRequirements(
    @param:Json(name = "must_haves") val mustHaves: List<String>,
    @param:Json(name = "nice_to_haves") val niceToHaves: List<String>,
    @param:Json(name = "years_experience_min") val yearsExperienceMin: Int?,
    @param:Json(name = "years_experience_max") val yearsExperienceMax: Int?
)

@Serializable
@JsonClass(generateAdapter = true)
data class CvAtAGlance(
    val name: String,
    @param:Json(name = "career_start") val careerStart: String,
    val location: String,
    val languages: List<String>,
    @param:Json(name = "key_tech") val keyTech: List<String>
)

@Serializable
@JsonClass(generateAdapter = true)
data class FitAnalysis(
    val matched: List<String>,
    val gaps: List<String>,
    val uncertain: List<String>,
    val summary: String? = null
)

@Serializable
@JsonClass(generateAdapter = true)
data class LocationFit(
    @param:Json(name = "cv_location") val cvLocation: String,
    @param:Json(name = "commute_within_2h_feasible") val commuteWithin2hFeasible: Boolean?, // may be null if unknown
    val notes: String
)

@Serializable
@JsonClass(generateAdapter = true)
data class LanguageFit(
    @param:Json(name = "required_languages") val requiredLanguages: List<String>,
    @param:Json(name = "cv_languages") val cvLanguages: List<String>,
    @param:Json(name = "missing_or_insufficient") val missingOrInsufficient: List<String>
)

@Serializable
@JsonClass(generateAdapter = true)
data class ResumeActions(
    val add: List<String>,
    val remove: List<String>,
    @param:Json(name = "rewrite_or_quantify") val rewriteOrQuantify: List<String>,
    @param:Json(name = "keywords_to_include") val keywordsToInclude: List<String>,
    @param:Json(name = "tailored_summary") val tailoredSummary: String
)


@Serializable
@JsonClass(generateAdapter = false)
enum class WorkMode {
    @Json(name = "remote") REMOTE,
    @Json(name = "hybrid") HYBRID,
    @Json(name = "onsite") ONSITE
}

@Serializable
@JsonClass(generateAdapter = false)
enum class FinalRecommendation {
    @Json(name = "APPLY") APPLY,
    @Json(name = "CONSIDER") CONSIDER,
    @Json(name = "SKIP") SKIP
}

@Serializable
@JsonClass(generateAdapter = true)
data class MatchError(
    val error: ErrorInfo
){
    val type: String
        get() {
            return error.type.name.replace("_", " ")
        }
}

@Serializable
@JsonClass(generateAdapter = true)
data class ErrorInfo(
    val type: ErrorType,
    val message: String,
    val details: String
)

@Serializable
@JsonClass(generateAdapter = false)
enum class ErrorType {
    @Json(name = "validation_error") VALIDATION_ERROR,
    @Json(name = "processing_error") PROCESSING_ERROR,
    @Json(name = "input_error") INPUT_ERROR
}

@Serializable
@JsonClass(generateAdapter = true)
data class WordAssociationResponse(
    @param:Json(name = "original_phrase") val originalPhrase: String,
    @param:Json(name = "word_associations") val wordAssociations: Map<String, List<String>>,
    @param:Json(name = "alternative_phrases") val alternativePhrases: List<String>
)


val MatchResponseNavType = object : NavType<MatchResponse>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): MatchResponse? {
        return bundle.getString(key)?.let {
            kotlinx.serialization.json.Json.decodeFromString<MatchResponse>(it)
        }
    }

    override fun put(bundle: Bundle, key: String, value: MatchResponse) {
        bundle.putString(key, kotlinx.serialization.json.Json.encodeToString(value))
    }

    override fun parseValue(value: String): MatchResponse {
        return kotlinx.serialization.json.Json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: MatchResponse): String {
        return Uri.encode(kotlinx.serialization.json.Json.encodeToString(value))
    }
}