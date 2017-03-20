package com.leday.Controller.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.leday.BaseActivity;
import com.leday.Common.Constant;
import com.leday.R;
import com.leday.Util.DbHelper;
import com.leday.Util.DbUtil;
import com.leday.Util.GlideImageLoader;
import com.leday.Util.PreferenUtil;
import com.leday.Util.SDCardUtil;
import com.leday.Model.Today;
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

    private Today mToday = new Today();
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
        mToday = (Today) intent.getSerializableExtra("local_today");

        mContent = (TextView) findViewById(R.id.content_activity_today);
        TextView mTitle = (TextView) findViewById(R.id.txt_Today_title);
        TextView mLike = (TextView) findViewById(R.id.txt_Today_like);
        ImageView mImgBack = (ImageView) findViewById(R.id.img_today_back);
        banner = (Banner) findViewById(R.id.banner_activity_today);

        mLike.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mTitle.setText(mToday.getTitle());
    }

    private void getJson() {
        progressdialogShow(this);
        OkGo.get(URL_TODAY + mToday.getE_id()).tag("todayactivity")
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
                mValues.put("date", mToday.getDate());
                mValues.put("title", mToday.getTitle());
                mValues.put("content", local_content);
                mDatabase.insert("todaytb", null, mValues);
                mValues.clear();
                mDatabase.close();

                /**新的数据库*/
                SQLiteDatabase database_new = new DbHelper(this, SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG).getWritableDatabase();
                String sql_create_ = "CREATE TABLE IF NOT EXISTS " + Constant.TABLE_TODAY + "("
                        + Constant.COLUMN_ID + " integer PRIMARY KEY AUTOINCREMENT,"
                        + Constant.COLUMN_DATE + " text,"
                        + Constant.COLUMN_TITLE + " text, "
                        + Constant.COLUMN_CONTENT + " text)";
                database_new.execSQL(sql_create_);

                String sql_select = "SELECT * FROM " + Constant.TABLE_TODAY + " WHERE " + Constant.COLUMN_DATE + " =? AND " + Constant.COLUMN_TITLE + " =?";
                String isNone = DbUtil.cursorToNotNullString(database_new.rawQuery(sql_select, new String[]{mToday.getDate(), mToday.getTitle()}));
                if (TextUtils.equals(isNone, Constant.NONE)) {
                    String sql_insert = "INSERT INTO " + Constant.TABLE_TODAY + "("
                            + Constant.COLUMN_DATE + ","
                            + Constant.COLUMN_TITLE + ","
                            + Constant.COLUMN_CONTENT + ")VALUES(\""
                            + mToday.getDate() + "\",\""
                            + mToday.getTitle() + "\",\""
                            + local_content + "\");";
                    database_new.execSQL(sql_insert);
                    Snackbar.make(view, "收藏成功!", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(view, "已经收藏啦，可以前往收藏夹查看", Snackbar.LENGTH_SHORT).show();
                }
                database_new.close();
                //权宜之计，做个标识给FavoriteActivity用
                PreferenUtil.put(TodayActivity.this, "todaytb_is_exist", "actually_not");
                break;
            case R.id.img_today_back:
                finish();
                break;
        }
    }
}