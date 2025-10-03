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
import tekin.luetfi.resume.domain.model.ChatMessage
import tekin.luetfi.resume.domain.model.ChatRequest
import tekin.luetfi.resume.domain.model.JobApplicationMail
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.domain.model.ResponseFormat
import tekin.luetfi.resume.domain.model.WordAssociationResponse
import tekin.luetfi.resume.domain.prompt.CVAnalyzePrompt
import tekin.luetfi.resume.domain.prompt.CVAnalyzePrompt.buildOpenRouterRequest
import tekin.luetfi.resume.domain.repository.JobAnalyzerRepository

class DefaultJobAnalyzerRepository(
    private val api: Api,
    private val openRouterAiApi: OpenRouterAiApi,
    private val db: JobReportDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val moshi: Moshi
) : JobAnalyzerRepository {

    override suspend fun analyzeJob(
        jobDescription: String,
        cvJson: String,
        model: AnalyzeModel
    ): MatchResponse = withContext(dispatcher) {


        val systemPrompt = try {
            api.getSystemPrompt().use {
                it.string().trim()
            }
        } catch (e: Exception) {
            CVAnalyzePrompt.SYSTEM
        }

        val completionResponse = openRouterAiApi.getChatCompletion(
            body = buildOpenRouterRequest(
                systemPrompt = systemPrompt,
                jobDescription = jobDescription,
                cvJson = cvJson,
                model = model.id
            )
        )

        return@withContext completionResponse.matchResponseOrThrow(moshi)
    }

    override suspend fun summarizeJob(
        summary: String?,
        model: AnalyzeModel
    ): WordAssociationResponse? = withContext(dispatcher) {
        if (summary == null)
            return@withContext null
        val prompt = try {
            api.getSummaryPrompt().use {
                it.string().trim()
            }
        } catch (e: Exception) {
            return@withContext null
        }

        val messages = listOf(
            ChatMessage(role = "system", content = prompt),
            ChatMessage(role = "user", content = summary)
        )

        val request = ChatRequest(
            messages = messages,
            responseFormat = ResponseFormat("json_object"),
            model = model.id
        )

        val completionResponse = openRouterAiApi.getChatCompletion(request)

        return@withContext completionResponse.matchResponseOrNull(moshi)
    }

    override suspend fun generateCoverLetter(
        reportJson: String,
        cvJson: String,
        model: AnalyzeModel
    ): JobApplicationMail = withContext(dispatcher) {
        val coverLetterSystemPrompt = try {
            api.getCoverLetterPrompt().use {
                it.string().trim()
            }
        } catch (e: Exception) {
            throw Exception("Failed to load cover letter prompt")
        }

        val coverLetterInput = """
            MatchReport Json:
            $reportJson
            
            Cv Json:
            $cvJson
        """.trimIndent()

        val messages = listOf(
            ChatMessage(role = "system", content = coverLetterSystemPrompt),
            ChatMessage(role = "user", content = coverLetterInput)
        )

        val request = ChatRequest(
            messages = messages,
            responseFormat = ResponseFormat("json_object"),
            model = model.id
        )

        val completionResponse = openRouterAiApi.getChatCompletion(request)

        return@withContext completionResponse.matchResponseOrNull(moshi) ?: throw Exception("Failed to generate cover letter")
    }

    override suspend fun saveJobReport(report: MatchResponse) = withContext(dispatcher) {
        db.saveReport(report.toEntity())
    }

    override suspend fun getJobReports(): List<MatchResponse> = withContext(dispatcher) {
        return@withContext db.loadReports().map { it.result }
    }

    override suspend fun getJobReport(id: String): MatchResponse? = withContext(dispatcher) {
        return@withContext db.loadReport(id)?.result
    }

    override suspend fun deleteReport(report: MatchResponse) = withContext(dispatcher) {
        db.deleteReport(report.toEntity())
    }
}