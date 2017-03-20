package com.leday.UI.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.leday.BaseActivity;
import com.leday.Common.Constant;
import com.leday.R;
import com.leday.Util.DbHelper;
import com.leday.Util.PreferenUtil;
import com.leday.Util.SDCardUtil;
import com.leday.entity.Today;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends BaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    //用ID来删除相对应的数据
    private ListView mListView;
    private ArrayAdapter mAdapter;

    private List<Today> mList = new ArrayList<>();
    private ArrayList<String> mDataList = new ArrayList<>();
    private SQLiteDatabase mDatabase;


    @Override
    protected void onRestart() {
        mDataList.clear();
        mList.clear();
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
        mAdapter = new ArrayAdapter(FavoriteActivity.this, android.R.layout.simple_list_item_1, mDataList);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
    }

    public void queryDatabase() {
        if (!PreferenUtil.contains(FavoriteActivity.this, "todaytb_is_exist")) {
            return;
        }
//        mDatabase = openOrCreateDatabase("leday.db", MODE_PRIVATE, null);
        mDatabase = new DbHelper(this, SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG).getWritableDatabase();
        //数据库查询
        Cursor mCursor = mDatabase.query(Constant.TABLE_TODAY, null, null, null, null, null, "_id desc");
        if (mCursor != null) {
            Today today;
            while (mCursor.moveToNext()) {
                today = new Today();
                today.setTitle(mCursor.getString(mCursor.getColumnIndex(Constant.COLUMN_TITLE)));
                today.setContent(mCursor.getString(mCursor.getColumnIndex(Constant.COLUMN_CONTENT)));
                today.setDate(mCursor.getString(mCursor.getColumnIndex(Constant.COLUMN_DATE)));
                today.setE_id(mCursor.getString(mCursor.getColumnIndex(Constant.COLUMN_ID)));
                mList.add(today);
            }
            mCursor.close();
        }
        mDatabase.close();

        //将数据中的时间和标题拼接后，单独做成一个数组，用于展示
        for (int i = 0; i < mList.size(); i++) {
            mDataList.add(mList.get(i).getDate() + ":\r" + mList.get(i).getTitle());
        }
    }

    public void close(View view) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(FavoriteActivity.this, FavoriteDetailActivity.class);
        intent.putExtra("local_today_bean", mList.get(i));
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
                        mDatabase = new DbHelper(FavoriteActivity.this, SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG).getWritableDatabase();
                        String local_delete = "DELETE FROM " + Constant.TABLE_TODAY + " WHERE " + Constant.COLUMN_ID + " = \"" + mList.get(position).getE_id() + "\"";
                        mDatabase.execSQL(local_delete);
                        mDatabase.close();

                        //刷新列表
                        mList.clear();
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