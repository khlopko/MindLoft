package com.aavens.mindloft.ui.webpage

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient

import com.aavens.mindloft.R

class WebPageActivity: AppCompatActivity() {

    private var webView: WebView? = null
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_page)
        initToolbar()
        initWebView()
        loadUrl()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.web_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> onBackPressed()
            R.id.reload -> webView?.reload()
            R.id.in_browser -> openLinkInBrowser()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar() {
        val toolbar = findViewById(R.id.default_toolbar) as Toolbar?
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setTitle(R.string.link_title)
    }

    private fun initWebView() {
        webView = findViewById(R.id.web_view) as WebView?
        val settings = webView?.settings
        settings?.javaScriptEnabled = true
        webView?.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                updateTitle()
            }
        })
    }

    private fun updateTitle() {
        supportActionBar?.title = webView?.title
    }

    private fun loadUrl() {
        url = intent.getStringExtra(URL_EXTRA)
        webView?.loadUrl(url)
    }

    private fun openLinkInBrowser() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    companion object {
        val URL_EXTRA = "com.aavens.mindloft.ui.webpage.URL_EXTRA"
    }
}
