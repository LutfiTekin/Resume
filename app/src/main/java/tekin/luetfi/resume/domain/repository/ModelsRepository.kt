package tekin.luetfi.resume.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import tekin.luetfi.resume.domain.model.AnalyzeModel

interface ModelsRepository {

    val models: StateFlow<List<AnalyzeModel>>


}