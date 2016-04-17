package com.aavens.mindloft.ui.webpage;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.aavens.mindloft.R;

import java.net.URL;

public class WebPageActivity extends AppCompatActivity {
    public static final String URL_EXTRA = "com.aavens.mindloft.ui.webpage.URL_EXTRA";

    private WebView webView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_page);
        initToolbar();
        initWebView();
        loadUrl();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.reload:
                webView.reload();
                break;
            case R.id.in_browser:
                openLinkInBrowser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.default_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.link_title);
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.web_view);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                updateTitle();
            }
        });
    }

    private void updateTitle() {
        getSupportActionBar().setTitle(webView.getTitle());
    }

    private void loadUrl() {
        url = getIntent().getStringExtra(URL_EXTRA);
        if (!url.startsWith("http://") || !url.startsWith("https://")) {
            url = "http://" + url;
        }
        webView.loadUrl(url);
    }

    private void openLinkInBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
