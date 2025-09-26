package tekin.luetfi.resume.ui.screen.analyze

import tekin.luetfi.resume.domain.model.FinalRecommendation
import tekin.luetfi.resume.domain.model.MatchError
import tekin.luetfi.resume.domain.model.MatchResponse

sealed class AnalyzeJobState() {
    object Start : AnalyzeJobState()
    data class Loading(val message: String, val finalRecommendation: FinalRecommendation? = null) : AnalyzeJobState()
    data class ReportReady(val report: MatchResponse, val online: Boolean) : AnalyzeJobState()
    data class Error(val matchError: MatchError?) : AnalyzeJobState()

}