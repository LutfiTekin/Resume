package tekin.luetfi.resume.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import tekin.luetfi.resume.domain.model.Cv

interface Api {

    @Headers("Cache-Control: no-cache")
    @GET("cv.json")
    suspend fun getCv(): Cv

    @Headers("Cache-Control: no-cache")
    @GET("job_analysis_prompt.txt")
    suspend fun getSystemPrompt(): ResponseBody

    @Headers("Cache-Control: no-cache")
    @GET("fit_summary_prompt.txt")
    suspend fun getSummaryPrompt(): ResponseBody

    @Headers("Cache-Control: no-cache")
    @GET("job_application_email_prompt.txt")
    suspend fun getCoverLetterPrompt(): ResponseBody

}