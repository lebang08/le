package com.leday.Controller.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.leday.BaseActivity;
import com.leday.Common.Constant;
import com.leday.Model.Wechat;
import com.leday.R;
import com.leday.Util.DbHelper;
import com.leday.Util.DbUtil;
import com.leday.Util.SDCardUtil;

public class WebViewActivity extends BaseActivity implements View.OnClickListener {

    private WebView mWebView;
    private Wechat mWechat = new Wechat();

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
        mWechat = (Wechat) getIntent().getSerializableExtra("local_wechat_web");
        ImageView mLike = (ImageView) findViewById(R.id.img_webview_like);
        mWebView = (WebView) findViewById(R.id.webview_activity);
        TextView mTitle = (TextView) findViewById(R.id.txt_webview_title);
        mTitle.setText(mWechat.getTitle());

        mLike.setOnClickListener(this);
    }

    private void initEvent() {
        mWebView.loadUrl(mWechat.getUrl());
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
            case R.id.img_webview_like:
                SQLiteDatabase mDatabase = new DbHelper(this, SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG).getWritableDatabase();
                String sql_create = "CREATE TABLE IF NOT EXISTS " + Constant.TABLE_WECHAT + "("
                        + Constant.COLUMN_ID + " integer PRIMARY KEY AUTOINCREMENT,"
                        + Constant.COLUMN_TITLE + " text, "
                        + Constant.COLUMN_URL + " text)";
                mDatabase.execSQL(sql_create);

                String sql_select = "SELECT * FROM " + Constant.TABLE_WECHAT + " WHERE " + Constant.COLUMN_URL + " =? AND " + Constant.COLUMN_TITLE + " =?";
                String isNone = DbUtil.cursorToNotNullString(mDatabase.rawQuery(sql_select, new String[]{mWechat.getUrl(), mWechat.getTitle()}));
                if (TextUtils.equals(isNone, Constant.NONE)) {
                    ContentValues mValues = new ContentValues();
                    mValues.put(Constant.COLUMN_TITLE, mWechat.getTitle());
                    mValues.put(Constant.COLUMN_URL, mWechat.getUrl());
                    long count = mDatabase.insert(Constant.TABLE_WECHAT, null, mValues);
                    mValues.clear();
                    if (count > 0) {
                        Snackbar.make(view, "收藏成功!", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(view, "收藏失败!", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(view, "已经收藏啦，可以前往收藏夹查看", Snackbar.LENGTH_SHORT).show();
                }
                mDatabase.close();
                break;
        }
    }
}