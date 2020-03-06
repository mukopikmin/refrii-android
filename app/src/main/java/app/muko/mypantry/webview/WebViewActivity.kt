package app.muko.mypantry.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import app.muko.mypantry.R
import butterknife.BindView
import butterknife.ButterKnife

class WebViewActivity : AppCompatActivity() {

    @BindView(R.id.webView)
    lateinit var mWebView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_web_view)
        ButterKnife.bind(this)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onStart() {
        super.onStart()

        val intent = intent
        val url = intent.getStringExtra("url")

        mWebView.webViewClient = WebViewClient()
        mWebView.settings.javaScriptEnabled = true
        mWebView.loadUrl(url)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        var result = true

        when (id) {
            android.R.id.home -> finish()
            else -> result = super.onOptionsItemSelected(item)
        }

        return result
    }
}
