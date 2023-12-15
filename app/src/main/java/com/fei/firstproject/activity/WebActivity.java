package com.fei.firstproject.activity;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.databinding.ActivityWebBinding;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;


/**
 * Created by Fei on 2017/8/17.
 */

public class WebActivity extends BaseProjectActivity<EmptyViewModel, ActivityWebBinding> {

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
    public void initTitle() {
        appHeadView.setFlHeadLeftPadding(getResources().getDimensionPixelSize(R.dimen.size_10));
        appHeadView.setLeftStyle(AppHeadView.IMAGE);
        appHeadView.setFlHeadLeftVisible(View.VISIBLE);
        appHeadView.setLeftDrawable(R.drawable.selector_head_left_arrow);
        appHeadView.setMiddleStyle(AppHeadView.SEARCH);
        appHeadView.setMiddleSearchHint(getString(R.string.web_search_tip));
        appHeadView.setFlHeadRightVisible(View.VISIBLE);
        appHeadView.setRightStyle(AppHeadView.TEXT);
        appHeadView.setRightText(getString(R.string.go_to));
    }

    @Override
    public void initRequest() {

    }

    private void initUrl() {
        String url = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(url)) finish();
        mChildBinding.webView.loadUrl(url);
    }

    private void initListener() {
        mChildBinding.webView.setWebChromeClient(chromeClient);
        mChildBinding.webView.setWebViewClient(viewClient);
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
        mChildBinding.llWebError.setVisibility(View.GONE);
        mChildBinding.webView.setVisibility(View.VISIBLE);
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
        mChildBinding.webView.loadUrl(url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings settings = mChildBinding.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
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
            if (mChildBinding.progressBar != null) {
                mChildBinding.progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mChildBinding.progressBar.setVisibility(View.GONE);
                } else {
                    mChildBinding.progressBar.setVisibility(View.VISIBLE);

                }
            }
            super.onProgressChanged(view, newProgress);
        }
    };

    private WebViewClient viewClient = new WebViewClient() {

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mChildBinding.llWebError.setVisibility(View.GONE);
            mChildBinding.webView.setVisibility(View.VISIBLE);
            mChildBinding.progressBar.setVisibility(View.VISIBLE);
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
        mChildBinding.llWebError.setVisibility(View.VISIBLE);
        mChildBinding.webView.setVisibility(View.GONE);
    }

    void clickWebError() {
        mChildBinding.btnWebError.setOnClickListener(v -> {
            mChildBinding.webView.reload();
        });
    }

    @Override
    public void onBackPressed() {
        if (mChildBinding.webView.canGoBack()) {
            mChildBinding.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void createObserver() {

    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        initWebView();
        initListener();
        initUrl();
    }
}
