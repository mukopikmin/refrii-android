package com.refrii.client.views.activities

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.refrii.client.R
import kotterknife.bindView

class WebViewActivity : AppCompatActivity() {

    private val webView: WebView by bindView(R.id.webView)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_web_view)

        val intent = intent
        val url = intent.getStringExtra("url")

        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
    }
}
