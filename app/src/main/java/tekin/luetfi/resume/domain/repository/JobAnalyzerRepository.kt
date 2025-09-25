package tekin.luetfi.resume.domain.repository

import tekin.luetfi.resume.Result
import tekin.luetfi.resume.domain.model.AnalyzeModel
import tekin.luetfi.resume.domain.model.MatchResponse

interface JobAnalyzerRepository {

    //Network operation
    suspend fun analyzeJob(jobDescription: String, cvJson: String, model: AnalyzeModel): MatchResponse

    //Local operations
    suspend fun saveJobReport(report: MatchResponse)
    suspend fun getJobReports(): List<MatchResponse>
    suspend fun getJobReport(id: String): MatchResponse?
    suspend fun deleteReport(report: MatchResponse)
}