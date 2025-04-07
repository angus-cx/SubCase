package ano.subcase

import android.webkit.WebView
import androidx.compose.runtime.mutableStateOf
import ano.subcase.util.ConfigStore

object CaseStatus {
    var isAllowLan = mutableStateOf(ConfigStore.isAllowLan)
    var isWifi = mutableStateOf(false)
    var lanIP = mutableStateOf("")

    var isServiceRunning = mutableStateOf(false)

    var showUpdateDialog = mutableStateOf(false)

    var webView = WebView(caseApp).apply {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        loadUrl("http://127.0.0.1:8080/subs?api=http://127.0.0.1:8081")
    }
}