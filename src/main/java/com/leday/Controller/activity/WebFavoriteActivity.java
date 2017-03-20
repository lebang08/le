package com.leday.Controller.activity;

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
import com.leday.Model.Wechat;
import com.leday.R;
import com.leday.Util.DbHelper;
import com.leday.Util.DbUtil;
import com.leday.Util.SDCardUtil;
import com.leday.Util.ToastUtil;

import java.io.File;
import java.util.ArrayList;

public class WebFavoriteActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    //用ID来删除相对应的数据
    private ListView mListView;
    private ArrayAdapter mAdapter;

    private ArrayList<Wechat> mWechatList = new ArrayList<>();

    @Override
    protected void onRestart() {
        mWechatList.clear();
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
        ArrayList<String> mTitleList = new ArrayList();
        for (int i = 0; i < mWechatList.size(); i++) {
            mTitleList.add(mWechatList.get(i).getTitle());
        }
        //适配器
        mAdapter = new ArrayAdapter(WebFavoriteActivity.this, android.R.layout.simple_list_item_1, mTitleList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    public void queryDatabase() {
        File file = new File(SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG);
        if (!file.exists()) {
            ToastUtil.show(this, "您的收藏夹空空如也", Toast.LENGTH_SHORT);
            return;
        }
        SQLiteDatabase mDatabase = new DbHelper(this, SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG).getWritableDatabase();
        //数据库查询
        String isNone_table = DbUtil.queryToString(mDatabase, Constant.TABLE_SQLITE_MASTER, Constant.COLUMN_NAME, Constant.COLUMN_TABLE_NAME, Constant.TABLE_WECHAT);
        if (TextUtils.equals(isNone_table, Constant.NONE)) {
            mDatabase.close();
            ToastUtil.show(this, "您的微信收藏夹空空如也", Toast.LENGTH_SHORT);
            return;
        }
        Cursor mCursor = mDatabase.query(Constant.TABLE_WECHAT, null, null, null, null, null, "_id desc");
        if (mCursor != null) {
            Wechat mWechat;
            while (mCursor.moveToNext()) {
                mWechat = new Wechat();
                mWechat.setTitle(mCursor.getString(mCursor.getColumnIndex(Constant.COLUMN_TITLE)));
                mWechat.setUrl(mCursor.getString(mCursor.getColumnIndex(Constant.COLUMN_URL)));
                mWechat.setId(mCursor.getString(mCursor.getColumnIndex(Constant.COLUMN_ID)));
                mWechatList.add(mWechat);
            }
            mCursor.close();
        }
        mDatabase.close();
    }

    public void close(View view) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(WebFavoriteActivity.this, WebFavoriteDetailActivity.class);
        intent.putExtra("local_wechat", mWechatList.get(i));
        startActivity(intent);
    }
}