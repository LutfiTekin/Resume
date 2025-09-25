package tekin.luetfi.resume.data.repository

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tekin.luetfi.resume.data.remote.Api
import tekin.luetfi.resume.data.remote.OpenRouterAiApi
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.domain.prompt.CVAnalyzePrompt.buildOpenRouterRequest
import tekin.luetfi.resume.domain.repository.JobAnalyzerRepository

class DefaultJobAnalyzerRepository(
    private val api: Api,
    private val openRouterAiApi: OpenRouterAiApi,
    private val io: CoroutineDispatcher = Dispatchers.IO,
    private val moshi: Moshi
): JobAnalyzerRepository {

    override suspend fun analyzeJob(
        jobDescription: String,
        cvJson: String
    ): MatchResponse = withContext(io){

        val systemPrompt = api.getSystemPrompt().use {
            it.string().trim()
        }

        val completionResponse = openRouterAiApi.matchJob(
            body = buildOpenRouterRequest(
                systemPrompt = systemPrompt,
                jobDescription = jobDescription,
                cvJson = cvJson
            )
        )

        return@withContext completionResponse.matchResponseOrThrow(moshi)

    }
}