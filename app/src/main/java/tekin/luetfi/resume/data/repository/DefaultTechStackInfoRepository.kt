package tekin.luetfi.resume.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import tekin.luetfi.resume.data.remote.Api
import tekin.luetfi.resume.domain.model.TechStackInfo
import tekin.luetfi.resume.domain.repository.TechStackInfoRepository
import kotlin.collections.emptyMap
import kotlin.collections.mapOf

class DefaultTechStackInfoRepository(
    private val api: Api
) : TechStackInfoRepository {

    override val techStackInfo: Flow<Map<String, TechStackInfo>> = flow {
        emit(getTechStackInfo())
    }


    suspend fun getTechStackInfo(): Map<String, TechStackInfo> {
        val info = try {
            api.getTechStackInfo()
        } catch (e: Exception) {
            e.printStackTrace()
            mapOf()
        }
        return info.ifEmpty {
            emptyMap()
        }
    }


}