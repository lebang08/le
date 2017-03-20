package com.leday.UI.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.leday.BaseActivity;
import com.leday.Common.Constant;
import com.leday.R;
import com.leday.Util.DbHelper;
import com.leday.Util.SDCardUtil;
import com.leday.entity.Today;
import com.youth.banner.Banner;

public class FavoriteDetailActivity extends BaseActivity implements View.OnClickListener {

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
        TextView mUnLike = (TextView) findViewById(R.id.txt_Today_like);
        ImageView mBack = (ImageView) findViewById(R.id.img_today_back);

        mTitle.setText(mToday.getDate() + "\r" + mToday.getTitle());
        mContent.setText(mToday.getContent());
        mUnLike.setText("取消收藏");

        mUnLike.setOnClickListener(this);
        mBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_Today_like:
                Snackbar.make(view, "取消成功", Snackbar.LENGTH_SHORT).show();
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