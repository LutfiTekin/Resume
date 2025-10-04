package tekin.luetfi.resume.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import tekin.luetfi.resume.data.remote.Api
import tekin.luetfi.resume.domain.model.AnalyzeModel
import tekin.luetfi.resume.domain.repository.ModelsRepository
import kotlin.collections.ifEmpty

class DefaultModelsRepository(
    private val api: Api,
    repositoryScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
): ModelsRepository {


    override val models: StateFlow<List<AnalyzeModel>> = flow {
        emit(getAvailableModels())
    }.stateIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = listOf(AnalyzeModel.default)
    )



    suspend fun getAvailableModels(): List<AnalyzeModel> {
        val models = try {
            api.getAvailableModels()
        } catch (e: Exception) {
            e.printStackTrace()
            listOf(AnalyzeModel.default)
        }
        return models.ifEmpty {
            listOf(AnalyzeModel.default)
        }
    }

}