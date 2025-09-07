package tekin.luetfi.resume.ui.screen.home

import tekin.luetfi.resume.domain.model.Cv

data class HomeUiState(
    val isLoading: Boolean = true,
    val resume: Cv? = null,
    val error: String? = null
)