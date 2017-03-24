package com.leday.Controller.activity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.leday.Common.Constant;
import com.leday.R;
import com.leday.Util.DbHelper;
import com.leday.Util.PreferenUtil;
import com.leday.Util.SDCardUtil;
import com.leday.Util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LeBang on 2016/10/26
 */
public class NoteDetailActivity extends AppCompatActivity {

    private EditText mTitle, mContent;
    private Button mBtnTime;
    private String local_content, local_title, local_date, local_style;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notedetail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity_notedetail);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_white_back);
        }

        initView();
    }

    private void initView() {
        local_style = (String) PreferenUtil.get(this, "note_style", "violet");
        local_title = getIntent().getStringExtra("local_title");
        local_date = getIntent().getStringExtra("local_date");
        local_content = getIntent().getStringExtra("local_content");
        mTitle = (EditText) findViewById(R.id.edt_notedetial_title);
        mContent = (EditText) findViewById(R.id.edt_notedetial_content);
        mBtnTime = (Button) findViewById(R.id.btn_notedetial_time);

        if (!TextUtils.isEmpty(local_title)) {
            mTitle.setText(local_title);
            mContent.setText(local_content);
        }
        //设置便签风格
        if (local_style.equals("violet")) {
            PreferenUtil.put(this, "note_style", "violet");
            mContent.setBackgroundColor(Color.parseColor("#3f5199"));
            mContent.setTextColor(Color.parseColor("#ffffff"));
            mContent.setHintTextColor(Color.parseColor("#ffffff"));
        } else if (local_style.equals("white")) {
            PreferenUtil.put(this, "note_style", "white");
            mContent.setBackgroundColor(Color.parseColor("#ffffff"));
            mContent.setTextColor(Color.parseColor("#000000"));
            mContent.setHintTextColor(Color.parseColor("#000000"));
        }
    }

    /**
     * 时间选择器
     */
    public void doTimePick(View view) {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mBtnTime.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            }
        }, 2018, 8, 8).show();
    }

    /**
     * 跳往图灵
     */
    public void doAsk(View view) {
        startActivity(new Intent(this, TalkActivity.class));
    }

    public void doBack(View view) {
        finish();
    }

    /**
     * 提交保存标签
     */
    public void doSubmit() {
        local_content = mContent.getText().toString();
        if (TextUtils.isEmpty(local_content)) {
            ToastUtil.showMessage(this, "便签内容不能为空");
            return;
        }
        local_title = mTitle.getText().toString();
        if (TextUtils.isEmpty(local_title)) {
            if (local_content.length() < 20) {
                local_title = local_content;
            } else {
                local_title = local_content.substring(0, 20) + "...";
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        local_date = mBtnTime.getText().toString().equals("修改时间") ? sdf.format(new Date()) : mBtnTime.getText().toString();
        SQLiteDatabase mDatabase = new DbHelper(this, SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG).getWritableDatabase();
        mDatabase.execSQL("create table if not exists "
                + Constant.TABLE_NOTE + "("
                + Constant.COLUMN_ID + " integer primary key autoincrement,"
                + Constant.COLUMN_DATE + " date,"
                + Constant.COLUMN_TITLE + " text,"
                + Constant.COLUMN_CONTENT + " text)");
        ContentValues mValues = new ContentValues();
        mValues.put(Constant.COLUMN_DATE, local_date);
        mValues.put(Constant.COLUMN_TITLE, local_title);
        mValues.put(Constant.COLUMN_CONTENT, local_content);
        mDatabase.insert(Constant.TABLE_NOTE, null, mValues);
        mValues.clear();
        mDatabase.close();
        ToastUtil.showMessage(this, "保存便签成功");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.submit:
                doSubmit();
                break;
        }
        return true;
    }
}
