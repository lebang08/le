package com.leday.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.leday.R;
import com.leday.Util.PreferenUtil;
import com.leday.application.MyApplication;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TodayActivity extends BaseActivity implements View.OnClickListener {

    private TextView mContent;
    private ViewFlipper mViewFlipper;

    private String local_id, local_title, local_date;
    private static final String URL_TODAY = "http://v.juhe.cn/todayOnhistory/queryDetail.php?key=776cbc23ec84837a647a7714a0f06bff&e_id=";
    private String local_content;

    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.getHttpQueue().cancelAll("todayactivity");
    }

    @Override
    protected void onStart() {
        super.onStart();

        getJson();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        initView();
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
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewflipper_activity_today);

        mLike.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mTitle.setText(local_title);
//        mViewFlipper.setAutoStart(true);
        mViewFlipper.startFlipping();
    }

    private void getJson() {
        progressdialogShow(this);
        StringRequest todayactivityrequest = new StringRequest(Request.Method.GET, URL_TODAY + local_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Dosuccess(response);
                progressCancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressCancel();
            }
        });
        todayactivityrequest.setTag("todayactivity");
        MyApplication.getHttpQueue().add(todayactivityrequest);
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
                mViewFlipper.setVisibility(View.GONE);
            }
            for (int i = 0; i < arr.length(); i++) {
                obj = arr.getJSONObject(i);
                String imgurl = obj.getString("url");
                ImageView mImgview = new ImageView(TodayActivity.this);
                Picasso.with(TodayActivity.this).load(imgurl).into(mImgview);
                mViewFlipper.addView(mImgview);
            }
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