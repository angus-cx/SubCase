package ano.subcase.ui

import android.webkit.WebSettings
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState


@Preview
@Composable
fun CaseWebView() {
    val state = rememberWebViewState("http://127.0.0.1:8080/subs?api=http://127.0.0.1:8081")

    WebView(
        modifier = Modifier
            .fillMaxSize(),
        state = state,
        onCreated = { view ->
            view.clearCache(true)
            view.settings.run {
                javaScriptEnabled = true
                domStorageEnabled = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        },
        captureBackPresses = true,
    )
}
