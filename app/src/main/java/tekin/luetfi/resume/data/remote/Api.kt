package tekin.luetfi.resume.data.remote

import retrofit2.http.GET
import tekin.luetfi.resume.domain.model.Cv

interface Api {


    @GET("cv.json")
    suspend fun getCv(): Cv

}