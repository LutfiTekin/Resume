package tekin.luetfi.resume.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tekin.luetfi.resume.domain.repository.CvRepository
import tekin.luetfi.resume.util.NetworkMonitor
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CvRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> =
        combine(
            _uiState
            .onStart {
                loadCv()
            }, networkMonitor.isOnline
        ) { uiState, isOnline ->
            if (isOnline.not())
                uiState.copy(error = "No internet connection")
            else {
                if (uiState.error != null)
                    loadCv()
                uiState
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())


    private fun loadCv() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)
            runCatching { repository.load() }
                .onSuccess { cv -> _uiState.value = HomeUiState(isLoading = false, resume = cv) }
                .onFailure { e ->
                    _uiState.value = HomeUiState(isLoading = false, error = e.message)
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)
            //Wait for loading UI to be updated
            delay(2000)
            runCatching { repository.refresh() }
                .onSuccess { cv -> _uiState.value = HomeUiState(isLoading = false, resume = cv) }
                .onFailure { e ->
                    _uiState.value = HomeUiState(isLoading = false, error = e.message)
                }
        }
    }
}