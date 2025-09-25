package tekin.luetfi.resume.ui.screen.analyze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import tekin.luetfi.resume.domain.model.AnalyzeModel
import tekin.luetfi.resume.domain.model.Cv
import tekin.luetfi.resume.domain.repository.JobAnalyzerRepository
import javax.inject.Inject

@HiltViewModel
class AnalyzeJobViewModel @Inject constructor(
    private val repository: JobAnalyzerRepository
): ViewModel() {

    private val _state = MutableStateFlow<AnalyzeJobState>(AnalyzeJobState.Start)

    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AnalyzeJobState.Start
        )

    private var analyzingJob: Job? = null


    fun analyze(jobDescription: String, cv: Cv, model: AnalyzeModel) {
        analyzingJob?.cancel()
        analyzingJob = viewModelScope.launch {
            _state.emit(AnalyzeJobState.Loading)
            val json = Json.encodeToString(Cv.serializer(), cv)
            runCatching {
                repository.analyzeJob(jobDescription, json, model)
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