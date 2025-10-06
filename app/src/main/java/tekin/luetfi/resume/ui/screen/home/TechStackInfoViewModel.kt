package tekin.luetfi.resume.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import tekin.luetfi.resume.domain.model.TechStackInfo
import tekin.luetfi.resume.domain.repository.TechStackInfoRepository
import javax.inject.Inject

@HiltViewModel
class TechStackInfoViewModel @Inject constructor(
    repository: TechStackInfoRepository
) : ViewModel() {

    val techStackInfo: StateFlow<Map<String, TechStackInfo>> =
        repository.techStackInfo
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyMap()
            )

    fun getTechStackInfo(tech: String): TechStackInfo? {
        return techStackInfo.value[tech]
    }
}