package tekin.luetfi.resume.domain.repository

import android.net.Uri

interface PdfRepository {

    fun saveFromDataUrl(dataUrl: String, filename: String): Uri
}
