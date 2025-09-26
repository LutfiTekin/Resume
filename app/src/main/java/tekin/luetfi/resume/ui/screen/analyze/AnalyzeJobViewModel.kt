package tekin.luetfi.resume.ui.screen.analyze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import tekin.luetfi.resume.domain.model.AnalyzeModel
import tekin.luetfi.resume.domain.model.Cv
import tekin.luetfi.resume.domain.model.FinalRecommendation
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.domain.repository.JobAnalyzerRepository
import javax.inject.Inject

@HiltViewModel
class AnalyzeJobViewModel @Inject constructor(
    private val repository: JobAnalyzerRepository
): ViewModel() {

    private val userActionChannel = Channel<Unit>(Channel.UNLIMITED)

    private val _state = MutableStateFlow<AnalyzeJobState>(AnalyzeJobState.Start)

    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AnalyzeJobState.Start
        )

    val previousReports = _state.map {
        if (it is AnalyzeJobState.Start){
            repository.getJobReports()
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var analyzingJob: Job? = null


    fun analyze(jobDescription: String, cv: Cv, model: AnalyzeModel) {
        analyzingJob?.cancel()
        analyzingJob = viewModelScope.launch {
            _state.emit(AnalyzeJobState.Loading("Analyzing the job description..."))
            val json = Json.encodeToString(Cv.serializer(), cv)
            runCatching {
                repository.analyzeJob(jobDescription, json, model)
            }.onSuccess {
                _state.emit(AnalyzeJobState.Loading("Final Verdict", it.finalRecommendation))
                userActionChannel.receive()
                _state.emit(AnalyzeJobState.ReportReady(it, online = true))
            }.onFailure {
                _state.emit(AnalyzeJobState.Error(""))
            }
        }
    }

    fun loadReport(id: String){
        viewModelScope.launch {
            _state.emit(AnalyzeJobState.Loading("Loading report from list"))
            runCatching {
                repository.getJobReport(id) ?: throw Exception("Report not found")
            }.onSuccess {
                _state.emit(AnalyzeJobState.ReportReady(it, online = false))
            }.onFailure {
                _state.emit(AnalyzeJobState.Error(""))
            }
        }
    }

    fun saveReport(report: MatchResponse) {
        viewModelScope.launch {
            runCatching {
                repository.saveJobReport(report)
                _state.emit(AnalyzeJobState.ReportReady(report, online = false))
            }
        }
    }

    fun deleteReport(report: MatchResponse) {
        viewModelScope.launch {
            runCatching {
                repository.deleteReport(report)
                _state.emit(AnalyzeJobState.ReportReady(report, online = true))
            }
        }
    }

    fun reset(){
        viewModelScope.launch {
            _state.emit(AnalyzeJobState.Start)
        }
    }

    fun onUserContinue() {
        userActionChannel.trySend(Unit)
    }

}