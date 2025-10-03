package tekin.luetfi.resume.domain.repository

import tekin.luetfi.resume.domain.model.AnalyzeModel
import tekin.luetfi.resume.domain.model.Cv
import tekin.luetfi.resume.domain.model.JobApplicationMail
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.domain.model.WordAssociationResponse

interface JobAnalyzerRepository {

    //Network operations
    suspend fun analyzeJob(jobDescription: String, cvJson: String, model: AnalyzeModel): MatchResponse
    suspend fun summarizeJob(summary: String?, model: AnalyzeModel): WordAssociationResponse?
    suspend fun generateCoverLetter(reportJson: String, cvJson: String, model: AnalyzeModel): JobApplicationMail


    //Local operations
    suspend fun saveJobReport(report: MatchResponse)
    suspend fun getJobReports(): List<MatchResponse>
    suspend fun getJobReport(id: String): MatchResponse?
    suspend fun deleteReport(report: MatchResponse)
}