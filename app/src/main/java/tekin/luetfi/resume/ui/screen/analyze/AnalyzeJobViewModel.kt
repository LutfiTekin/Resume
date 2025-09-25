package tekin.luetfi.resume.ui.screen.analyze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import tekin.luetfi.resume.domain.model.Cv
import tekin.luetfi.resume.domain.repository.JobAnalyzerRepository
import javax.inject.Inject

@HiltViewModel
class AnalyzeJobViewModel @Inject constructor(
    private val repository: JobAnalyzerRepository
): ViewModel() {

    private val _state = MutableStateFlow<AnalyzeJobState>(AnalyzeJobState.Start)

    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AnalyzeJobState.Start)


    fun analyze(jobDescription: String, cv: Cv) {
        val json = Json.encodeToString(Cv.serializer(), cv)
        viewModelScope.launch {
            _state.emit(AnalyzeJobState.Loading)
            runCatching {
                repository.analyzeJob(jobDescription, json)
            }.onSuccess {
                _state.emit(AnalyzeJobState.ReportReady(it))
            }.onFailure {
                _state.emit(AnalyzeJobState.Error(""))
            }

        }
    }

    fun reset(){
        viewModelScope.launch {
            _state.emit(AnalyzeJobState.Start)
        }
    }

}