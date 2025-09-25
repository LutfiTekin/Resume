package tekin.luetfi.resume.ui.screen.analyze

import tekin.luetfi.resume.domain.model.MatchResponse

sealed class AnalyzeJobState() {
    object Start : AnalyzeJobState()
    object Loading : AnalyzeJobState()
    data class ReportReady(val result: MatchResponse) : AnalyzeJobState()
    data class Error(val message: String) : AnalyzeJobState()

}