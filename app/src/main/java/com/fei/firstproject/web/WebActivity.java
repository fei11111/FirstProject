package com.fei.firstproject.web;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.fei.firstproject.R;
import com.fei.firstproject.activity.BaseActivity;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;

import butterknife.BindView;

/**
 * Created by Fei on 2017/8/17.
 */

public class WebActivity extends BaseActivity {

    @BindView(R.id.appHeadView)
    AppHeadView appHeadView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.webView)
    WebView webView;

    @Override
    public void requestPermissionsBeforeInit() {

    }

    @Override
    public void permissionsDeniedCallBack(int requestCode) {

    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {

    }

    @Override
    public void permissionDialogDismiss(int requestCode) {

    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_web;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initWebView();
        initListener();
        initUrl();
    }

    private void initUrl() {
        String url = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(url)) finish();
        webView.loadUrl(url);
    }

    private void initListener() {
        webView.setWebChromeClient(chromeClient);
        webView.setWebViewClient(viewClient);
        appHeadView.setOnLeftRightClickListener(onLeftRightClickListener);
    }

    private AppHeadView.OnLeftRightClickListener onLeftRightClickListener = new AppHeadView.OnLeftRightClickListener() {
        @Override
        public void onLeft(View view) {
            onBackPressed();
        }

        @Override
        public void onRight(View view) {
            String text = appHeadView.getEtSearchText();
            if (text.contains("www")) {
                if (!text.contains("http")) {
                    text = "http://" + text;
                }
            } else {
                if (!text.contains("http")) {
                    text = "http://www." + text;
                }
            }
            webView.clearHistory();
            webView.clearCache(true);
            webView.clearFormData();
            webView.loadUrl(text);
        }
    };

    private void initWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(settings.LOAD_NO_CACHE);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    private WebChromeClient chromeClient = new WebChromeClient() {
        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 JsResult result) {
            // return super.onJsAlert(view, url, message, result);
            Utils.showToast(WebActivity.this, message);
            result.confirm();
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
            if (newProgress == 100) {
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.VISIBLE);

            }
            super.onProgressChanged(view, newProgress);
        }
    };

    private WebViewClient viewClient = new WebViewClient() {

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            appHeadView.setEtSearchText(url);
            super.onPageStarted(view, url, favicon);
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
