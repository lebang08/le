package com.leday.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.leday.Interface.RecyclerItemClickListener;
import com.leday.R;
import com.leday.Util.PreferenUtil;
import com.leday.Util.ToastUtil;
import com.leday.adapter.NoteRecyclerViewAdapter;
import com.leday.entity.Note;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/26.
 */
public class NoteActivity extends BaseActivity {

    private TextView mTitle;
    private SwipeRefreshLayout mSwipeRefresh;

    private RecyclerView mRecyclerView;
    private NoteRecyclerViewAdapter mAdapter;
    private ArrayList<Note> mList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutmanager;

    private SQLiteDatabase mDatabase;

    @Override
    protected void onRestart() {
        super.onRestart();
        mList.clear();
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
        mTitle = (TextView) findViewById(R.id.txt_note_title);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_activity_note);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_activity_note);
        mLinearLayoutmanager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutmanager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        //RecyclerView设置Item事件
        doRecyclerViewItem();

        initData();

        mAdapter = new NoteRecyclerViewAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);
//        setFooterView(mRecyclerView);

        //swipe设置刷新时需要做的事
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFruits();
            }
        });
    }

    private void refreshFruits() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(800);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //必须在主线程操作
                            mList.clear();
                            initData();
                            mAdapter.notifyDataSetChanged();
                            mSwipeRefresh.setRefreshing(false);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (!PreferenUtil.contains(NoteActivity.this, "notetb_is_exist")) {
            mTitle.setText("还没有便签，新建一个吧");
            return;
        }
        mDatabase = openOrCreateDatabase("leday.db", MODE_PRIVATE, null);
        //数据库查询
        Cursor mCursor = mDatabase.query("notetb", null, "_id>?", new String[]{"0"}, null, null, "date desc");
        if (mCursor != null) {
            Note note;
            while (mCursor.moveToNext()) {
                note = new Note();
                note.setTime(mCursor.getString(mCursor.getColumnIndex("date")));
                note.setTitle(mCursor.getString(mCursor.getColumnIndex("title")));
                note.setContent(mCursor.getString(mCursor.getColumnIndex("content")));
                mList.add(note);
            }
            mCursor.close();
        }
        mDatabase.close();
    }

    /**
     * 修改便签风格
     *
     * @param view
     */
    public void doChange(View view) {
        //TODO 做上拉加载的判断方式,自定义一个View，继承RecyclerView，重写滑动监听，外加接口.类似FillingMissView
        int lastVisibleItemPosition = (mLinearLayoutmanager).findLastVisibleItemPosition();
        if (lastVisibleItemPosition >= (mList.size() - 1)) {
            ToastUtil.showMessage(this, "到最后啦，做上拉加载吧");
        }

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
     *
     * @param view
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
                Intent intent = new Intent(NoteActivity.this, NoteDetailActivity.class);
                intent.putExtra("local_date", mList.get(position).getTime());
                intent.putExtra("local_title", mList.get(position).getTitle());
                intent.putExtra("local_content", mList.get(position).getContent());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                new AlertDialog.Builder(NoteActivity.this)
                        .setTitle("该操作将删除这条便签")
                        .setMessage("确认删除吗?")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase = openOrCreateDatabase("leday.db", MODE_PRIVATE, null);
                                String local_delete = "DELETE FROM notetb WHERE date = '" + mList.get(position).getTime() + "'";
                                mDatabase.execSQL(local_delete);
                                mDatabase.close();

                                //带动画的效果则不能用notyfyDataSetChanged()
                                //要用notifyItemInserted(position)与notifyItemRemoved(position)
                                //有bug(源于同时可以删除多个数据)
                                mList.remove(position);
                                mAdapter.notifyItemRemoved(position);
                                //无bug
//                                //旧数据清除
//                                mList.clear();
//                                //新数据填充
//                                initData();
//                                //适配器 set change
//                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        }));
    }

    public void setFooterView(RecyclerView recyclerview) {
        View footer = LayoutInflater.from(this).inflate(R.layout.item_footer_view, recyclerview, false);
        mAdapter.setFooterView(footer);
    }
}