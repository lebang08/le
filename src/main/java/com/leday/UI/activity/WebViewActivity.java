package com.leday.UI.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.leday.BaseActivity;
import com.leday.R;
import com.leday.Util.PreferenUtil;

public class WebViewActivity extends BaseActivity implements View.OnClickListener {

    private WebView mWebView;

    private String local_title, local_url;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                this.finish();
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        initView();
        initEvent();
    }

    private void initView() {
        Intent intent = getIntent();
        local_url = intent.getStringExtra("localurl");
        local_title = intent.getStringExtra("localtitle");

        TextView mLike = (TextView) findViewById(R.id.txt_webview_like);
        mWebView = (WebView) findViewById(R.id.webview_activity);
        TextView mTitle = (TextView) findViewById(R.id.txt_webview_title);
        mTitle.setText(local_title);

        mLike.setOnClickListener(this);
    }

    private void initEvent() {
        mWebView.loadUrl(local_url);
        //JS交互
        mWebView.getSettings().setJavaScriptEnabled(true);
        //支持放缩
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return false;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });
    }

    public void close(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_webview_like:
                //建一张表保存文章
                SQLiteDatabase mDatabase = openOrCreateDatabase("leday.db", MODE_PRIVATE, null);
                mDatabase.execSQL("create table if not exists wechattb(_id integer primary key autoincrement,title text not null,url text not null)");
                ContentValues mValues = new ContentValues();
                mValues.put("title", local_title);
                mValues.put("url", local_url);
                mDatabase.insert("wechattb", null, mValues);
                mValues.clear();
                mDatabase.close();
                Snackbar.make(view, "收藏成功 : " + local_title, Snackbar.LENGTH_SHORT).show();
                //权宜之计，做个标识给FavoriteActivity用
                PreferenUtil.put(WebViewActivity.this, "wechattb_is_exist", "actually_not");
                break;
        }
    }
}