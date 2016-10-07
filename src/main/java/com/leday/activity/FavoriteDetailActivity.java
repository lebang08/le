package com.leday.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.leday.R;
import com.leday.Util.LogUtil;

public class FavoriteDetailActivity extends BaseActivity implements View.OnClickListener {

    private String local_content, local_title, local_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        initView();
    }

    private void initView() {
        local_content = getIntent().getStringExtra("local_content");
        local_title = getIntent().getStringExtra("local_date_title");
        local_id = getIntent().getStringExtra("local_id");
        LogUtil.e("num = " + local_id + "---" + local_title);

        ViewFlipper mViewFlipper = (ViewFlipper) findViewById(R.id.viewflipper_activity_today);
        mViewFlipper.setVisibility(View.GONE);
        TextView mTitle = (TextView) findViewById(R.id.txt_Today_title);
        TextView mContent = (TextView) findViewById(R.id.content_activity_today);
        TextView mUnLike = (TextView) findViewById(R.id.txt_Today_like);
        ImageView mBack = (ImageView) findViewById(R.id.img_today_back);

        mTitle.setText(local_title);
        mContent.setText(local_content);
        mUnLike.setText("取消收藏");

        mUnLike.setOnClickListener(this);
        mBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_Today_like:
                Snackbar.make(view, "取消成功", Snackbar.LENGTH_SHORT).show();
                String local_delete = "DELETE FROM todaytb WHERE _id = '" + local_id + "'";
                SQLiteDatabase mDatabase = openOrCreateDatabase("leday.db", MODE_PRIVATE, null);
                mDatabase.execSQL(local_delete);
                mDatabase.close();
                break;
            default:
                finish();
                break;
        }
    }
}