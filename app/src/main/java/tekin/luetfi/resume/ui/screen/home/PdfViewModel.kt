package tekin.luetfi.resume.ui.screen.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import tekin.luetfi.resume.domain.usecase.SavePdfFromDataUrl
import javax.inject.Inject

@HiltViewModel
class PdfViewModel @Inject constructor(
    val savePdf: SavePdfFromDataUrl
) : ViewModel() {

    private val _lastPdfUri = MutableStateFlow<Uri?>(null)
    val lastPdfUri: StateFlow<Uri?> = _lastPdfUri

    fun onPdfSaved(uri: Uri) {
        _lastPdfUri.value = uri
    }
}
