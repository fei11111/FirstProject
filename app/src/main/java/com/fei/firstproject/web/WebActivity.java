package com.fei.firstproject.web;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.activity.BaseActivity;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;

import butterknife.BindView;
import butterknife.OnClick;

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
    @BindView(R.id.ll_web_error)
    LinearLayout ll_web_error;

    private boolean isNeedClearHistory = false;

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
        appHeadView.setMiddleSearchDisable();
        initWebView();
        initListener();
        initUrl();
    }

    @Override
    public void initRequest() {

    }

    private void initUrl() {
        String url = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(url)) finish();
        webView.loadUrl(url);
    }

    private void initListener() {
        webView.setWebChromeClient(chromeClient);
        webView.setWebViewClient(viewClient);
        appHeadView.setOnLeftRightClickListener(onAppHeadViewListener);
    }

    private AppHeadView.onAppHeadViewListener onAppHeadViewListener = new AppHeadView.onAppHeadViewListener() {
        @Override
        public void onLeft(View view) {
            onBackPressed();
        }

        @Override
        public void onRight(View view) {
            String text = appHeadView.getEtSearchText();
            enterWebSite(text);
        }

        @Override
        public void onEdit(TextView v, int actionId, KeyEvent event) {
            String text = v.getText().toString();
            enterWebSite(text);
        }
    };

    private void enterWebSite(String url) {
        Utils.hideKeyBoard(this);
        ll_web_error.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        if (url.contains("www")) {
            if (!url.contains("http")) {
                url = "http://" + url;
            }
        } else {
            if (!url.contains("http")) {
                url = "http://www." + url;
            }
        }
        isNeedClearHistory = true;
        webView.loadUrl(url);
    }

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
            if (progressBar != null) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                }
            }
            super.onProgressChanged(view, newProgress);
        }
    };

    private WebViewClient viewClient = new WebViewClient() {

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            ll_web_error.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
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

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            if (isNeedClearHistory) {
                isNeedClearHistory = false;
                view.clearHistory();//清除历史记录
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return;
            }
            // 在这里显示自定义错误页
            showErrView();
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (request.isForMainFrame()) {
                showErrView();
            }
        }

    };

    private void showErrView() {
        ll_web_error.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_web_error)
    void clickWebError(View view) {
        webView.reload();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
