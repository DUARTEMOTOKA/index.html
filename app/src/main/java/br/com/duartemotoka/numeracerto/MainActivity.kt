package br.com.duartemotoka.numeracerto

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.PermissionRequest
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : Activity() {

    private lateinit var webView: WebView

    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val FILE_CHOOSER_CODE = 200

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

        settings.allowFileAccess = true
        settings.allowContentAccess = true

        settings.javaScriptCanOpenWindowsAutomatically = true

        webView.webViewClient = WebViewClient()

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

            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {

                this@MainActivity.filePathCallback?.onReceiveValue(null)

                this@MainActivity.filePathCallback = filePathCallback

                val intent = Intent(Intent.ACTION_GET_CONTENT)

                intent.addCategory(Intent.CATEGORY_OPENABLE)

                intent.type = "application/pdf"

                startActivityForResult(
                    Intent.createChooser(
                        intent,
                        "Escolher PDF"
                    ),
                    FILE_CHOOSER_CODE
                )

                return true
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

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (requestCode == FILE_CHOOSER_CODE) {

            val result =
                if (
                    resultCode == RESULT_OK &&
                    data?.data != null
                ) {
                    arrayOf(data.data!!)
                } else {
                    null
                }

            filePathCallback?.onReceiveValue(result)

            filePathCallback = null
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

        filePathCallback?.onReceiveValue(null)

        filePathCallback = null

        webView.destroy()

        super.onDestroy()
    }
}
