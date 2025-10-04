package tekin.luetfi.resume.domain.repository

import kotlinx.coroutines.flow.Flow
import tekin.luetfi.resume.domain.model.AnalyzeModel

interface ModelsRepository {

    val models: Flow<List<AnalyzeModel>>


}