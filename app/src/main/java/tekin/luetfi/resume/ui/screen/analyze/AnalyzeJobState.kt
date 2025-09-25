package tekin.luetfi.resume.ui.screen.analyze

import tekin.luetfi.resume.domain.model.MatchResponse

sealed class AnalyzeJobState() {
    object Start : AnalyzeJobState()
    data class Loading(val message: String) : AnalyzeJobState()
    data class ReportReady(val report: MatchResponse, val online: Boolean) : AnalyzeJobState()
    data class Error(val message: String) : AnalyzeJobState()

}