package com.leday.UI.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.leday.BaseActivity;
import com.leday.R;
import com.leday.Util.GlideImageLoader;
import com.leday.Util.PreferenUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;

public class TodayActivity extends BaseActivity implements View.OnClickListener {

    private Banner banner;
    private TextView mContent;

    private String local_id, local_title, local_date;
    private static final String URL_TODAY = "http://v.juhe.cn/todayOnhistory/queryDetail.php?key=776cbc23ec84837a647a7714a0f06bff&e_id=";

    private String local_content;
    //图片数组
    private ArrayList<String> imgList = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        OkGo.getInstance().cancelTag("todayactivity");
        banner.stopAutoPlay();
    }

    @Override
    protected void onStart() {
        super.onStart();
        banner.startAutoPlay();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        initView();
        getJson();
    }

    private void initView() {
        Intent intent = getIntent();
        local_id = intent.getStringExtra("local_id");
        local_title = intent.getStringExtra("local_title");
        local_date = intent.getStringExtra("local_date");

        mContent = (TextView) findViewById(R.id.content_activity_today);
        TextView mTitle = (TextView) findViewById(R.id.txt_Today_title);
        TextView mLike = (TextView) findViewById(R.id.txt_Today_like);
        ImageView mImgBack = (ImageView) findViewById(R.id.img_today_back);
        banner = (Banner) findViewById(R.id.banner_activity_today);

        mLike.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mTitle.setText(local_title);
    }

    private void getJson() {
        progressdialogShow(this);
        OkGo.get(URL_TODAY + local_id).tag("todayactivity")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        Dosuccess(s);
                        progressCancel();
                    }
                });
    }

    private void Dosuccess(String response) {
        JSONObject obj;
        JSONArray arr;
        try {
            obj = new JSONObject(response);
            arr = obj.getJSONArray("result");
            obj = arr.getJSONObject(0);
            local_content = obj.getString("content");
            mContent.setText(local_content);
            arr = obj.getJSONArray("picUrl");
            if (arr.length() == 0) {
                banner.setVisibility(View.GONE);
            }
            for (int i = 0; i < arr.length(); i++) {
                obj = arr.getJSONObject(i);
                String imgurl = obj.getString("url");
                imgList.add(imgurl);
            }
            //设置图片加载器
            banner.setImageLoader(new GlideImageLoader());
            //设置图片集合
            banner.setImages(imgList);
            banner.setDelayTime(3000);
            banner.setIndicatorGravity(BannerConfig.RIGHT);
            banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
            banner.setBannerAnimation(Transformer.ZoomOutSlide);
            //banner设置方法全部调用完毕时最后调用
            banner.start();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_Today_like:
                //建一张表保存文章
                SQLiteDatabase mDatabase = openOrCreateDatabase("leday.db", MODE_PRIVATE, null);
                mDatabase.execSQL("create table if not exists todaytb(_id integer primary key autoincrement,date text not null,title text not null,content text not null)");
                ContentValues mValues = new ContentValues();
                mValues.put("date", local_date);
                mValues.put("title", local_title);
                mValues.put("content", local_content);
                mDatabase.insert("todaytb", null, mValues);
                mValues.clear();
                mDatabase.close();
                Snackbar.make(view, "收藏成功" + local_content, Snackbar.LENGTH_SHORT).show();
                //权宜之计，做个标识给FavoriteActivity用
                PreferenUtil.put(TodayActivity.this, "todaytb_is_exist", "actually_not");
                break;
            case R.id.img_today_back:
                finish();
                break;
        }
    }
}