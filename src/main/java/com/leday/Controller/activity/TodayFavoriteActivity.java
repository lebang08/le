package com.leday.Controller.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.leday.BaseActivity;
import com.leday.Common.Constant;
import com.leday.Model.Today;
import com.leday.R;
import com.leday.Util.DbHelper;
import com.leday.Util.DbUtil;
import com.leday.Util.SDCardUtil;
import com.leday.Util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TodayFavoriteActivity extends BaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView mListView;
    private ArrayAdapter mAdapter;

    private List<Today> mTodayList = new ArrayList<>();
    private ArrayList<String> mDataList = new ArrayList<>();

    @Override
    protected void onRestart() {
        mDataList.clear();
        mTodayList.clear();
        queryDatabase();
        mAdapter.notifyDataSetChanged();
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        initView();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.listview_activity_favoriter);
        //数据
        queryDatabase();
        //适配器
        mAdapter = new ArrayAdapter(TodayFavoriteActivity.this, android.R.layout.simple_list_item_1, mDataList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
    }

    public void queryDatabase() {
        File file = new File(SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG);
        if (!file.exists()) {
            ToastUtil.show(this, "您的收藏夹空空如也", Toast.LENGTH_SHORT);
            return;
        }
        SQLiteDatabase mDatabase = new DbHelper(this, SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG).getWritableDatabase();
        //数据库查询
        String isNone_table = DbUtil.queryToString(mDatabase, Constant.TABLE_SQLITE_MASTER, Constant.COLUMN_NAME, Constant.COLUMN_TABLE_NAME, Constant.TABLE_TODAY);
        if (TextUtils.equals(isNone_table, Constant.NONE)) {
            mDatabase.close();
            ToastUtil.show(this, "您的今时今往收藏夹空空如也", Toast.LENGTH_SHORT);
            return;
        }
        Cursor mCursor = mDatabase.query(Constant.TABLE_TODAY, null, null, null, null, null, "_id desc");
        if (mCursor != null) {
            Today today;
            while (mCursor.moveToNext()) {
                today = new Today();
                today.setTitle(mCursor.getString(mCursor.getColumnIndex(Constant.COLUMN_TITLE)));
                today.setContent(mCursor.getString(mCursor.getColumnIndex(Constant.COLUMN_CONTENT)));
                today.setDate(mCursor.getString(mCursor.getColumnIndex(Constant.COLUMN_DATE)));
                today.setE_id(mCursor.getString(mCursor.getColumnIndex(Constant.COLUMN_ID)));
                mTodayList.add(today);
            }
            mCursor.close();
        }
        mDatabase.close();

        //将数据中的时间和标题拼接后，单独做成一个数组，用于展示
        for (int i = 0; i < mTodayList.size(); i++) {
            mDataList.add(mTodayList.get(i).getDate() + ":\r" + mTodayList.get(i).getTitle());
        }
    }

    public void close(View view) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(TodayFavoriteActivity.this, TodayFavoriteDetailActivity.class);
        intent.putExtra("local_today_bean", mTodayList.get(i));
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(this)
                .setTitle("该操作将删除这条便签")
                .setMessage("确认删除吗?")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase mDatabase = new DbHelper(TodayFavoriteActivity.this, SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG).getWritableDatabase();
                        String local_delete = "DELETE FROM " + Constant.TABLE_TODAY + " WHERE " + Constant.COLUMN_ID + " = \"" + mTodayList.get(position).getE_id() + "\"";
                        mDatabase.execSQL(local_delete);
                        mDatabase.close();

                        //刷新列表
                        mTodayList.clear();
                        mDataList.clear();
                        queryDatabase();
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
        return true;
    }
}