package tekin.luetfi.resume.domain.repository

import tekin.luetfi.resume.Result
import tekin.luetfi.resume.domain.model.AnalyzeModel
import tekin.luetfi.resume.domain.model.MatchResponse

interface JobAnalyzerRepository {

    suspend fun analyzeJob(jobDescription: String, cvJson: String, model: AnalyzeModel): MatchResponse

}