package tekin.luetfi.resume.ui.screen.analyze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tekin.luetfi.resume.domain.model.AnalyzeModel
import tekin.luetfi.resume.domain.repository.ModelsRepository
import javax.inject.Inject

@HiltViewModel
class AvailableModelsViewModel @Inject constructor(
    repository: ModelsRepository
) : ViewModel() {

    val models = repository.models

}