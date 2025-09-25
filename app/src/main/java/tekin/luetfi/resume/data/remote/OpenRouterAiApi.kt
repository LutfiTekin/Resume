package tekin.luetfi.resume.data.remote

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import tekin.luetfi.resume.domain.model.ChatCompletionResponse
import tekin.luetfi.resume.domain.model.ChatRequest

interface OpenRouterAiApi {

    @POST("chat/completions")
    @Headers("Content-Type: application/json")
    suspend fun matchJob(
        @Body body: ChatRequest
    ): ChatCompletionResponse
}