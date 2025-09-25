package tekin.luetfi.resume.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import tekin.luetfi.resume.data.remote.Api
import tekin.luetfi.resume.domain.model.Cv
import tekin.luetfi.resume.domain.repository.CvRepository

class DefaultCvRepository(
    private val api: Api,
    private val io: CoroutineDispatcher = Dispatchers.IO
) : CvRepository {

    private val _cv = MutableStateFlow<Cv?>(null)
    override val cv = _cv.asStateFlow()

    override suspend fun load(): Cv = withContext(io) {
        _cv.value?.let { return@withContext it }
        val fresh = api.getCv()
        _cv.value = fresh
        fresh
    }

    override suspend fun refresh(): Cv = withContext(io) {
        val fresh = api.getCv()
        _cv.value = fresh
        fresh
    }
}