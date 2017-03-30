package com.leday.Controller.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.leday.BaseActivity;
import com.leday.Common.Constant;
import com.leday.Model.Today;
import com.leday.R;
import com.leday.Util.DbHelper;
import com.leday.Util.SDCardUtil;
import com.youth.banner.Banner;

public class TodayFavoriteDetailActivity extends BaseActivity implements View.OnClickListener {

    private Today mToday = new Today();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        initView();
    }

    private void initView() {
        mToday = (Today) getIntent().getSerializableExtra("local_today_bean");

        Banner mBanner = (Banner) findViewById(R.id.banner_activity_today);
        mBanner.setVisibility(View.GONE);
        TextView mTitle = (TextView) findViewById(R.id.txt_Today_title);
        TextView mContent = (TextView) findViewById(R.id.content_activity_today);
        ImageView mUnLike = (ImageView) findViewById(R.id.img_today_like);
        ImageView mBack = (ImageView) findViewById(R.id.img_today_back);

        mTitle.setText(mToday.getDate() + "\r" + mToday.getTitle());
        mContent.setText(mToday.getContent());
        mUnLike.setBackgroundResource(R.mipmap.icon_star_cancel);
        mUnLike.setOnClickListener(this);
        mBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_today_like:
                Snackbar.make(view, "取消收藏成功", Snackbar.LENGTH_SHORT).show();
                String sql_delete = "DELETE FROM " + Constant.TABLE_TODAY + " WHERE " + Constant.COLUMN_ID + " = " + mToday.getE_id();
                SQLiteDatabase mDatabase = new DbHelper(this, SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG).getWritableDatabase();
                mDatabase.execSQL(sql_delete);
                mDatabase.close();
                break;
            default:
                finish();
                break;
        }
    }
}