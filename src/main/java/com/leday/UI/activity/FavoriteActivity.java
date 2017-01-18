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
import com.leday.R;
import com.leday.Util.PreferenUtil;
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
        //应该用该方法来判断是否存在某表
//        if (!tabIsExist("todaytb")) {
//            return;
//        }
        if (!PreferenUtil.contains(FavoriteActivity.this, "todaytb_is_exist")) {
            return;
        }
        mDatabase = openOrCreateDatabase("leday.db", MODE_PRIVATE, null);
        //数据库查询
        Cursor mCursor = mDatabase.query("todaytb", null, "_id>?", new String[]{"0"}, null, null, "_id desc");
        if (mCursor != null) {
            Today today;
            while (mCursor.moveToNext()) {
                today = new Today();
                today.setTitle(mCursor.getString(mCursor.getColumnIndex("title")));
                today.setContent(mCursor.getString(mCursor.getColumnIndex("content")));
                today.setDate(mCursor.getString(mCursor.getColumnIndex("date")));
                today.setE_id(mCursor.getString(mCursor.getColumnIndex("_id")));
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
        intent.putExtra("local_date", mList.get(i).getDate());
        intent.putExtra("local_title", mList.get(i).getTitle());
        intent.putExtra("local_content", mList.get(i).getContent());
        intent.putExtra("local_id", mList.get(i).getE_id());
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
                        mDatabase = openOrCreateDatabase("leday.db", MODE_PRIVATE, null);
                        String local_delete = "DELETE FROM todaytb WHERE _id = '" + mList.get(position).getE_id() + "'";
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