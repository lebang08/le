package com.leday.activity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.leday.R;
import com.leday.Util.PreferenUtil;
import com.leday.Util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/10/26.
 */
public class NoteDetailActivity extends BaseActivity {

    private EditText mTitle, mContent;
    private Button mBtnTime;
    private String local_content, local_title, local_date, local_style;

    private SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notedetail);

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

        mTitle.setText(local_title);
        mContent.setText(local_content);
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
     *
     * @param view
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
     *
     * @param view
     */
    public void doAsk(View view) {
        startActivity(new Intent(this, TalkActivity.class));
    }

    public void doBack(View view) {
        finish();
    }

    /**
     * 提交保存标签
     *
     * @param view
     */
    public void doSubmit(View view) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        local_content = mContent.getText().toString();
        if (TextUtils.isEmpty(local_content)) {
            Snackbar.make(view, "便签内容不能为空", Snackbar.LENGTH_SHORT).show();
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
        local_date = mBtnTime.getText().toString().equals("修改时间") ? sdf.format(new Date()) : mBtnTime.getText().toString();
        mDatabase = openOrCreateDatabase("leday.db", MODE_PRIVATE, null);
        mDatabase.execSQL("create table if not exists notetb(_id integer primary key autoincrement,date date,title text,content text not null)");
        ContentValues mValues = new ContentValues();
        mValues.put("date", local_date);
        mValues.put("title", local_title);
        mValues.put("content", local_content);
        mDatabase.insert("notetb", null, mValues);
        mValues.clear();
        mDatabase.close();
        PreferenUtil.put(NoteDetailActivity.this, "notetb_is_exist", "actually_not");
        ToastUtil.showMessage(this,"保存便签成功");
    }
}
