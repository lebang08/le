package com.leday.Controller.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.leday.Util.SDCardUtil;

public class WebFavoriteDetailActivity extends BaseActivity implements View.OnClickListener {

    //TODO 其实可以有更丰富的内容，作者,等等。。。
    private WebView mWebView;
    private Wechat mWechat = new Wechat();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webviewdetail);

        initView();
        initEvent();
    }

    private void initView() {
        mWechat = (Wechat) getIntent().getSerializableExtra("local_wechat");
        ImageView mBack = (ImageView) findViewById(R.id.img_webviewdetail_back);
        TextView mTitle = (TextView) findViewById(R.id.txt_webviewdetail_title);
        TextView mUrl = (TextView) findViewById(R.id.txt_webviewdetail_url);
        TextView mUnLike = (TextView) findViewById(R.id.txt_webviewdetail_like);
        mWebView = (WebView) findViewById(R.id.webviewdetail_activity);

        mTitle.setText(mWebView.getTitle());
        mUrl.setText("长按复制文章地址->" + mWechat.getUrl());

        mUnLike.setOnClickListener(this);
        mBack.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_webviewdetail_like:
                Snackbar.make(view, "取消收藏成功", Snackbar.LENGTH_SHORT).show();
                String local_delete = "DELETE FROM " + Constant.TABLE_WECHAT + " WHERE " + Constant.COLUMN_ID + " = \"" + mWechat.getId() + "\"";
                SQLiteDatabase mDatabase = new DbHelper(this, SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG).getWritableDatabase();
                mDatabase.execSQL(local_delete);
                mDatabase.close();
                break;
            default:
                finish();
                break;
        }
    }
}