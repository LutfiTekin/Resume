package tekin.luetfi.resume.ui.component

import android.webkit.JavascriptInterface
import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import tekin.luetfi.resume.domain.usecase.SavePdfFromDataUrl

class PdfJsBridge(
    private val savePdf: SavePdfFromDataUrl,
    private val onSaved: (android.net.Uri) -> Unit
) {
    @JavascriptInterface
    fun onPdfReady(dataUrl: String) {
        val uri = savePdf(dataUrl, "resume.pdf")
        onSaved(uri)
    }
}

@SuppressLint("SetJavaScriptEnabled")
fun WebView.setupForPdf(
    savePdf: SavePdfFromDataUrl,
    onSaved: (android.net.Uri) -> Unit
) {
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.useWideViewPort = true
    settings.loadWithOverviewMode = true

    addJavascriptInterface(
        PdfJsBridge(savePdf, onSaved),
        "Android"
    )

    webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            if (!url.contains("pdf")) {
                view.loadUrl("$url${if ('?' in url) "&" else "?"}pdf")
            }
        }
    }
}
