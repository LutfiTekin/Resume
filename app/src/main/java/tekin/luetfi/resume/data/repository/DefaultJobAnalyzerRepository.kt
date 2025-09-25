package tekin.luetfi.resume.data.repository

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tekin.luetfi.resume.data.local.JobReportDao
import tekin.luetfi.resume.data.local.toEntity
import tekin.luetfi.resume.data.remote.Api
import tekin.luetfi.resume.data.remote.OpenRouterAiApi
import tekin.luetfi.resume.domain.model.AnalyzeModel
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.domain.prompt.CVAnalyzePrompt
import tekin.luetfi.resume.domain.prompt.CVAnalyzePrompt.buildOpenRouterRequest
import tekin.luetfi.resume.domain.repository.JobAnalyzerRepository

class DefaultJobAnalyzerRepository(
    private val api: Api,
    private val openRouterAiApi: OpenRouterAiApi,
    private val db: JobReportDao,
    private val io: CoroutineDispatcher = Dispatchers.IO,
    private val moshi: Moshi
): JobAnalyzerRepository {

    override suspend fun analyzeJob(
        jobDescription: String,
        cvJson: String,
        model: AnalyzeModel
    ): MatchResponse = withContext(io){

        val systemPrompt = try {
            api.getSystemPrompt().use {
                it.string().trim()
            }
        } catch (e: Exception) {
            CVAnalyzePrompt.SYSTEM
        }

        val completionResponse = openRouterAiApi.matchJob(
            body = buildOpenRouterRequest(
                systemPrompt = systemPrompt,
                jobDescription = jobDescription,
                cvJson = cvJson,
                model = model.id
            )
        )

        return@withContext completionResponse.matchResponseOrThrow(moshi)
    }


    override suspend fun saveJobReport(report: MatchResponse) = withContext(io){
        db.saveReport(report.toEntity())
    }

    override suspend fun getJobReports(): List<MatchResponse> = withContext(io){
        return@withContext db.loadReports().map { it.result }
    }

    override suspend fun getJobReport(id: String): MatchResponse? = withContext(io){
        return@withContext db.loadReport(id)?.result
    }

    override suspend fun deleteReport(report: MatchResponse) = withContext(io){
        db.deleteReport(report.toEntity())
    }
}