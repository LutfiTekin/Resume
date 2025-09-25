package tekin.luetfi.resume.data.repository

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tekin.luetfi.resume.Result
import tekin.luetfi.resume.data.remote.OpenRouterAiApi
import tekin.luetfi.resume.domain.model.ChatCompletionResponse
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.domain.prompt.CVAnalyzePrompt.buildOpenRouterRequest
import tekin.luetfi.resume.domain.repository.JobAnalyzerRepository

class DefaultJobAnalyzerRepository(
    private val api: OpenRouterAiApi,
    private val io: CoroutineDispatcher = Dispatchers.IO,
    private val moshi: Moshi
): JobAnalyzerRepository {

    override suspend fun analyzeJob(
        jobDescription: String,
        cvJson: String
    ): MatchResponse = withContext(io){
        val completionResponse = api.matchJob(
            body = buildOpenRouterRequest(
                jobDescription = jobDescription,
                cvJson = cvJson
            )
        )

        return@withContext completionResponse.matchResponseOrThrow(moshi)

    }
}