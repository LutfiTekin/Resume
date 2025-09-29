package tekin.luetfi.resume.data.repository

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import tekin.luetfi.resume.domain.repository.PdfRepository
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidPdfRepository @Inject constructor(
    @param:ApplicationContext private val appContext: Context
) : PdfRepository {

    override fun saveFromDataUrl(dataUrl: String, filename: String): Uri {
        val base64Index = dataUrl.indexOf("base64,")
        require(base64Index != -1) { "Not a base64 data URL" }

        val base64 = dataUrl.substring(base64Index + "base64,".length)
        val bytes = Base64.decode(base64, Base64.DEFAULT)

        val out = File(appContext.cacheDir, filename)
        out.outputStream().use { it.write(bytes) }

        return FileProvider.getUriForFile(
            appContext,
            "${appContext.packageName}.fileprovider",
            out
        )
    }
}
