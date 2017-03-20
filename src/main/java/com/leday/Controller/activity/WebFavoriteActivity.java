package com.leday.Controller.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.leday.BaseActivity;
import com.leday.R;
import com.leday.Util.LogUtil;
import com.leday.Util.PreferenUtil;

import java.util.ArrayList;

public class WebFavoriteActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    //用ID来删除相对应的数据
    private ListView mListView;
    private ArrayAdapter mAdapter;
    private ArrayList<String> mIdList = new ArrayList<>();

    private ArrayList<String> mDataList = new ArrayList<>();
    private ArrayList<String> mContentList = new ArrayList<>();
    private SQLiteDatabase mDatabase;

    @Override
    protected void onRestart() {
        mDataList.clear();
        mContentList.clear();
        mIdList.clear();
        queryDatabase();
        mAdapter.notifyDataSetChanged();
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        initView();
        queryDatabase();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.listview_activity_favoriter);
        mAdapter = new ArrayAdapter(WebFavoriteActivity.this, android.R.layout.simple_list_item_1, mDataList);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    public void queryDatabase() {
        //应该用该方法来判断是否存在某表
//        if (!tabIsExist("todaytb")) {
//            return;
//        }
        if (!PreferenUtil.contains(WebFavoriteActivity.this, "wechattb_is_exist")) {
            return;
        }
        mDatabase = openOrCreateDatabase("leday.db", MODE_PRIVATE, null);
        //数据库查询
        Cursor mCursor = mDatabase.query("wechattb", null, "_id>?", new String[]{"0"}, null, null, "_id desc");
        if (mCursor != null) {
            String local_date_title;
            while (mCursor.moveToNext()) {
                local_date_title = mCursor.getString(mCursor.getColumnIndex("title"));
                mDataList.add(local_date_title);
                mContentList.add(mCursor.getString(mCursor.getColumnIndex("url")));
                mIdList.add(mCursor.getString(mCursor.getColumnIndex("_id")));
            }
            mCursor.close();
        }
        mDatabase.close();
    }

    /**
     * 判断是否存在某表
     *
     * @param tabName
     * @return
     */
    public boolean tabIsExist(String tabName) {
        boolean result = false;
        if (tabName == null) {
            return false;
        }
        try {
            Cursor cursor;
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='" + tabName + "'";
            cursor = mDatabase.rawQuery(sql, null);
            LogUtil.e("what1? ");
            if (cursor.moveToNext()) {
                LogUtil.e("what2? ");
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }
        } catch (Exception e) {
        }
        return result;
    }

    public void close(View view) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(WebFavoriteActivity.this, WebFavoriteDetailActivity.class);
        intent.putExtra("local_title", mDataList.get(i));
        intent.putExtra("local_url", mContentList.get(i));
        intent.putExtra("local_id", mIdList.get(i));
        startActivity(intent);
    }
}