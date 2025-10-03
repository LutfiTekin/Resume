package tekin.luetfi.resume.ui.screen.cover_letter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import tekin.luetfi.resume.domain.model.AnalyzeModel
import tekin.luetfi.resume.domain.model.ChatCompletionResponse
import tekin.luetfi.resume.domain.model.Cv
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.domain.repository.JobAnalyzerRepository
import javax.inject.Inject

@HiltViewModel
class CoverLetterViewModel @Inject constructor(
    private val repository: JobAnalyzerRepository
) : ViewModel() {


    private val _state = MutableStateFlow<CoverLetterState>(CoverLetterState.Loading)

    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CoverLetterState.Loading
        )

    fun generateCoverLetter(report: MatchResponse, cv: Cv) {
        val cvJson = Json.encodeToString(Cv.serializer(), cv)
        val reportJson = Json.encodeToString(MatchResponse.serializer(), report)
        viewModelScope.launch {
            _state.emit(CoverLetterState.Loading)
            runCatching {
                repository.generateCoverLetter(
                    reportJson = reportJson,
                    cvJson = cvJson,
                    model = AnalyzeModel.GROK_4_FAST
                )
            }.onSuccess {
                _state.emit(CoverLetterState.Success(it))
            }.onFailure {
                if (it is ChatCompletionResponse.JobAnalyzeException) {
                    _state.emit(CoverLetterState.Error(it.error))
                }else {
                    _state.emit(CoverLetterState.Error(null))
                }
            }
        }

    }


}