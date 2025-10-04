package tekin.luetfi.resume.ui.screen.analyze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import tekin.luetfi.resume.domain.model.AnalyzeModel
import tekin.luetfi.resume.domain.model.ChatCompletionResponse
import tekin.luetfi.resume.domain.model.Cv
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.domain.model.verdict
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


    private fun analyze(jobDescription: String, cv: Cv, model: AnalyzeModel) {
        analyzingJob?.cancel()
        analyzingJob = viewModelScope.launch {
            _state.emit(AnalyzeJobState.Loading("Analyzing the job description..."))
            val json = Json.encodeToString(Cv.serializer(), cv)
            runCatching {
                repository.analyzeJob(jobDescription, json, model)
            }.onSuccess {
                val summary = repository.summarizeJob(it.fitAnalysis.summary, model)
                _state.emit(AnalyzeJobState.Loading("Final Verdict", it.verdict.copy(summary = summary)))
                userActionChannel.receive()
                _state.emit(AnalyzeJobState.ReportReady(it.attachModel(model), online = true))
            }.onFailure {
                if (it is ChatCompletionResponse.JobAnalyzeException) {
                    _state.emit(AnalyzeJobState.Error(it.error))
                }else {
                    _state.emit(AnalyzeJobState.Error(null))
                }
            }
        }
    }


    fun analyze(jobDescription: String, cv: Cv, models: List<AnalyzeModel>) {
        if (models.size == 1) {
            // Use existing single model flow
            analyze(jobDescription, cv, models.first())
            return
        }

        analyzingJob?.cancel()
        analyzingJob = viewModelScope.launch {
            val json = Json.encodeToString(Cv.serializer(), cv)

            // Initialize loading state with all models
            val initialResults = models.map { model ->
                ModelResult(model, ModelStatus.Loading)
            }
            _state.emit(AnalyzeJobState.Loading(
                message = "Analyzing job description...",
                modelResults = initialResults))

            // Launch concurrent analysis for each model
            val jobs = models.map { model ->
                async {
                    runCatching {
                        repository.analyzeJob(jobDescription, json, model)
                    }.fold(
                        onSuccess = { response ->
                            ModelResult(model, ModelStatus.Completed, response.attachModel(model))
                        },
                        onFailure = { error ->
                            val matchError = if (error is ChatCompletionResponse.JobAnalyzeException) {
                                error.error
                            } else null
                            ModelResult(model, ModelStatus.Failed, error = matchError)
                        }
                    )
                }
            }

            // Collect results as they complete
            val completedResults = initialResults.toMutableList()
            jobs.forEach { job ->
                val result = job.await()
                completedResults.removeIf { it.model == result.model }
                completedResults.add(result)

                _state.update { jobState ->
                    if (jobState is AnalyzeJobState.Loading) {
                        jobState.copy(modelResults = completedResults.toList())
                    } else {
                        jobState
                    }
                }

            }

        }
    }

    fun loadReport(report: MatchResponse){
        viewModelScope.launch {
            _state.emit(AnalyzeJobState.ReportReady(report, online = true))
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
                _state.emit(AnalyzeJobState.Error(null))
            }
        }
    }

    fun saveReport(report: MatchResponse) {
        viewModelScope.launch {
            runCatching {
                repository.saveJobReport(report)
                //save and reload current page with the saved report
                _state.emit(AnalyzeJobState.ReportReady(report, online = false))
            }
        }
    }

    fun deleteReport(report: MatchResponse) {
        viewModelScope.launch {
            runCatching {
                repository.deleteReport(report)
                //reload page with current report
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