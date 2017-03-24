package com.leday.Controller.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.leday.BaseActivity;
import com.leday.Common.Constant;
import com.leday.Controller.adapter.NoteAdapter;
import com.leday.Model.Note;
import com.leday.R;
import com.leday.Util.DbHelper;
import com.leday.Util.DbUtil;
import com.leday.Util.Interface.RecyclerItemClickListener;
import com.leday.Util.PreferenUtil;
import com.leday.Util.SDCardUtil;
import com.leday.Util.ToastUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/26
 */
public class NoteActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    private TextView mTitle;

    private XRecyclerView mRecyclerView;
    private NoteAdapter mAdapter;
    private ArrayList<Note> mNoteList = new ArrayList<>();

    @Override
    protected void onRestart() {
        super.onRestart();
        mNoteList.clear();
        initData();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        initView();
    }

    private void initView() {
        initData();

        mRecyclerView = (XRecyclerView) findViewById(R.id.recycler_activity_note);
        mTitle = (TextView) findViewById(R.id.txt_note_title);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        doRecyclerViewItem();
        mAdapter = new NoteAdapter(this, mNoteList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLoadingListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        File file = new File(SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG);
        if (!file.exists()) {
            ToastUtil.show(this, "您的收藏夹空空如也", Toast.LENGTH_SHORT);
            return;
        }
        SQLiteDatabase mDatabase = new DbHelper(this, SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG).getWritableDatabase();
        //数据库查询
        String isNone_table = DbUtil.queryToString(mDatabase, Constant.TABLE_SQLITE_MASTER, Constant.COLUMN_NAME, Constant.COLUMN_TABLE_NAME, Constant.TABLE_NOTE);
        if (TextUtils.equals(isNone_table, Constant.NONE)) {
            mDatabase.close();
            ToastUtil.show(this, "您的便签夹空空如也", Toast.LENGTH_SHORT);
            return;
        }
//        mDatabase = openOrCreateDatabase("leday.db", MODE_PRIVATE, null);
        //数据库查询
        Cursor mCursor = mDatabase.query(Constant.TABLE_NOTE, null, null, null, null, null, Constant.COLUMN_DATE + " desc");
        if (mCursor != null) {
            Note note;
            while (mCursor.moveToNext()) {
                note = new Note();
                note.setTime(mCursor.getString(mCursor.getColumnIndex(Constant.COLUMN_DATE)));
                note.setTitle(mCursor.getString(mCursor.getColumnIndex(Constant.COLUMN_TITLE)));
                note.setContent(mCursor.getString(mCursor.getColumnIndex(Constant.COLUMN_CONTENT)));
                mNoteList.add(note);
            }
            mCursor.close();
        }
        mDatabase.close();
    }

    /**
     * 修改便签风格
     */
    public void doChange(View view) {
        Snackbar.make(view, "修改便签底色风格", Snackbar.LENGTH_SHORT)
                .setActionTextColor(Color.parseColor("#3f51b5"))
                .setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(NoteActivity.this)
                                .setTitle("选择便签风格")
                                .setPositiveButton("默认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PreferenUtil.put(NoteActivity.this, "note_style", "violet");
                                        startActivity(new Intent(NoteActivity.this, NoteDetailActivity.class));
                                    }
                                })
                                .setNegativeButton("经典", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PreferenUtil.put(NoteActivity.this, "note_style", "white");
                                        startActivity(new Intent(NoteActivity.this, NoteDetailActivity.class));
                                    }
                                }).show();
                    }
                }).show();
    }

    /**
     * 新建便签
     */
    public void doWrite(View view) {
        startActivity(new Intent(NoteActivity.this, NoteDetailActivity.class));
    }

    public void doBack(View view) {
        finish();
    }

    private void doRecyclerViewItem() {
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                new AlertDialog.Builder(NoteActivity.this)
                        .setTitle("该操作将删除这条便签")
                        .setMessage("确认删除吗?")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteDatabase mDatabase = new DbHelper(NoteActivity.this, SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG).getWritableDatabase();
                                String local_delete = "DELETE FROM " + Constant.TABLE_NOTE + " WHERE " + Constant.COLUMN_DATE + " = \"" + mNoteList.get(position - 1).getTime() + "\"";
                                mDatabase.execSQL(local_delete);
                                mDatabase.close();
                                //带动画的效果则不能用notyfyDataSetChanged(),要用notifyItemInserted(position)与notifyItemRemoved(position)
                                mNoteList.remove(position - 1);
                                mAdapter.notifyItemRemoved(position);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        }));
    }

    @Override
    public void onRefresh() {
        mRecyclerView.refreshComplete();
    }

    @Override
    public void onLoadMore() {
        mRecyclerView.loadMoreComplete();
    }
}