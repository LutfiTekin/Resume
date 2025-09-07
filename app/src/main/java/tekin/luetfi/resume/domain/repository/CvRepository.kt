package tekin.luetfi.resume.domain.repository

import kotlinx.coroutines.flow.Flow
import tekin.luetfi.resume.domain.model.Cv

interface CvRepository {

    /** Emits the latest CV, cached in memory after the first load. */
    val cv: Flow<Cv?>

    /** Network-first load. Uses HTTP cache in OkHttp under the hood. */
    suspend fun load(): Cv

    /** Forces a fresh call, ignoring memory. Still benefits from OkHttp disk cache. */
    suspend fun refresh(): Cv

}