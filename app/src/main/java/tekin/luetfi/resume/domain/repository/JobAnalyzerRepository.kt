package tekin.luetfi.resume.domain.repository

import tekin.luetfi.resume.domain.model.AnalyzeModel
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.domain.model.WordAssociationResponse

interface JobAnalyzerRepository {

    //Network operation
    suspend fun analyzeJob(jobDescription: String, cvJson: String, model: AnalyzeModel): MatchResponse
    suspend fun summarizeJob(summary: String?, model: AnalyzeModel): WordAssociationResponse?

    //Local operations
    suspend fun saveJobReport(report: MatchResponse)
    suspend fun getJobReports(): List<MatchResponse>
    suspend fun getJobReport(id: String): MatchResponse?
    suspend fun deleteReport(report: MatchResponse)
}