package tekin.luetfi.resume.domain.repository

import kotlinx.coroutines.flow.Flow
import tekin.luetfi.resume.domain.model.TechStackInfo

interface TechStackInfoRepository {

    val techStackInfo: Flow<Map<String, TechStackInfo>>

}