package com.refrii.client

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val webView = findViewById(R.id.webView) as WebView
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true

        val intent = intent
        val url = intent.getStringExtra("url")
        webView.loadUrl(url)
    }
}
