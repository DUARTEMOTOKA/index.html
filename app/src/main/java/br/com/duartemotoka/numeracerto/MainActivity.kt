package br.com.duartemotoka.numeracerto

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100

        private const val WEBSITE_URL =
            "https://duartemotoka.github.io/index.html/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)

        setContentView(webView)

        configurarWebView()

        solicitarCamera()

        webView.loadUrl(WEBSITE_URL)
    }

    private fun configurarWebView() {

        val settings = webView.settings

        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true

        settings.allowFileAccess = false
        settings.allowContentAccess = true

        settings.javaScriptCanOpenWindowsAutomatically = true

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {

                val url = request.url.toString()

                return !url.startsWith(
                    "https://duartemotoka.github.io/"
                )
            }
        }

        webView.webChromeClient = object : WebChromeClient() {

            override fun onPermissionRequest(
                request: PermissionRequest
            ) {

                runOnUiThread {

                    if (
                        ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        request.grant(
                            arrayOf(
                                PermissionRequest.RESOURCE_VIDEO_CAPTURE
                            )
                        )

                    } else {

                        request.deny()
                    }
                }
            }
        }
    }

    private fun solicitarCamera() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    override fun onBackPressed() {

        if (webView.canGoBack()) {

            webView.goBack()

        } else {

            super.onBackPressed()
        }
    }

    override fun onDestroy() {

        webView.destroy()

        super.onDestroy()
    }
}
