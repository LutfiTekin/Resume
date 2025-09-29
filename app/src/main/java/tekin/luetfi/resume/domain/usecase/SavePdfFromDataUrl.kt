package tekin.luetfi.resume.domain.usecase

import android.net.Uri
import tekin.luetfi.resume.domain.repository.PdfRepository
import javax.inject.Inject

class SavePdfFromDataUrl @Inject constructor(
    private val repo: PdfRepository
) {
    operator fun invoke(dataUrl: String, filename: String): Uri =
        repo.saveFromDataUrl(dataUrl, filename)
}
