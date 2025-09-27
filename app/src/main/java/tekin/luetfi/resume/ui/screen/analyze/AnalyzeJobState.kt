package tekin.luetfi.resume.ui.screen.analyze

import tekin.luetfi.resume.domain.model.AnalyzeModel
import tekin.luetfi.resume.domain.model.FinalRecommendation
import tekin.luetfi.resume.domain.model.MatchError
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.domain.model.Verdict

sealed class AnalyzeJobState() {
    object Start : AnalyzeJobState()
    //should contain whole report or reports, keep track of models, if multiple models are used failed ones should be shown in loading screen
    data class Loading(
        val message: String,
        val verdict: Verdict? = null,
        val modelResults: List<ModelResult> = emptyList()) : AnalyzeJobState(){
            val sortedModelResults: List<ModelResult>
                get() = modelResults.sortedBy { it.model.displayName }
        }
    //should keep containing one report
    data class ReportReady(val report: MatchResponse, val online: Boolean) : AnalyzeJobState()
    data class Error(val matchError: MatchError?) : AnalyzeJobState()

}

data class ModelResult(
    val model: AnalyzeModel,
    val status: ModelStatus,
    val report: MatchResponse? = null,
    val error: MatchError? = null
)

sealed class ModelStatus {
    object Loading : ModelStatus()
    object Completed : ModelStatus()
    object Failed : ModelStatus()
}