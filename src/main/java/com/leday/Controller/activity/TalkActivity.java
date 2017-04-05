package com.leday.Controller.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.leday.BaseActivity;
import com.leday.Common.Constant;
import com.leday.Controller.adapter.TalkAdapter;
import com.leday.Model.Talk;
import com.leday.R;
import com.leday.Util.DbHelper;
import com.leday.Util.DbUtil;
import com.leday.Util.HttpUtil;
import com.leday.Util.SDCardUtil;
import com.leday.Util.TalkHttpUtils;
import com.leday.Util.ToastUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TalkActivity extends BaseActivity {

    private EditText mInputMsg;
    private Button mSendMsg;

    private ListView mListView;
    private TalkAdapter mAdapter;
    private List<Talk> mDatas = new ArrayList<>();

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //等待接收，子线程完成数据的返回
            Talk fromMessge = (Talk) msg.obj;
            mDatas.add(fromMessge);
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(mDatas.size() - 1);
            //这里将返回的消息放入数据库
            SQLiteDatabase mDatabase = new DbHelper(TalkActivity.this, SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG).getWritableDatabase();
            mDatabase.execSQL("create table if not exists " + Constant.TABLE_TALK + "(" + Constant.COLUMN_ID + " integer primary key autoincrement,"
                    + Constant.COLUMN_MESSAGE + " text,"
                    + Constant.COLUMN_TYPE + " text,"
                    + Constant.COLUMN_TIME + " text)");
            ContentValues mValues = new ContentValues();
            mValues.put(Constant.COLUMN_MESSAGE, fromMessge.getMsg());
            mValues.put(Constant.COLUMN_TYPE, fromMessge.getType().toString());
            mValues.put(Constant.COLUMN_TIME, fromMessge.getTime());
            mDatabase.insert(Constant.TABLE_TALK, null, mValues);
            mValues.clear();
            mDatabase.close();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtil.removeTag("GET");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_talk);

        //初始化控件
        initView();
        //初始化聊天记录
        initDatas();
        // 初始化事件
        initListener();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.id_listview_msgs);
        mInputMsg = (EditText) findViewById(R.id.id_input_msg);
        mSendMsg = (Button) findViewById(R.id.id_send_msg);
    }

    private void initDatas() {
        //是否第一次进入聊天，是则欢迎，不是则聊天记录
        File file = new File(SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG);
        if (!file.exists()) {
            mDatas.add(new Talk("客官，您好，我是百事通小图灵", Talk.Type.INCOMING, new Date()));
            mAdapter = new TalkAdapter(this, mDatas);
            mListView.setAdapter(mAdapter);
            return;
        }
        SQLiteDatabase mDatabase = new DbHelper(this, SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG).getWritableDatabase();
        //数据库查询
        String isNone_table = DbUtil.queryToString(mDatabase, Constant.TABLE_SQLITE_MASTER, Constant.COLUMN_NAME, Constant.COLUMN_TABLE_NAME, Constant.TABLE_TALK);
        if (TextUtils.equals(isNone_table, Constant.NONE)) {
            mDatabase.close();
            mDatas.add(new Talk("客官，您好，我是百事通小图灵", Talk.Type.INCOMING, new Date()));
            mAdapter = new TalkAdapter(this, mDatas);
            mListView.setAdapter(mAdapter);
            return;
        }
        Talk talk;
        // 此处应该调入数据库
        Cursor mCursor = mDatabase.query(Constant.TABLE_TALK, null, null, null, null, null, null);
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                talk = new Talk();
                talk.setMsg(mCursor.getString(mCursor.getColumnIndex(Constant.COLUMN_MESSAGE)));
                if (TextUtils.equals(mCursor.getString(mCursor.getColumnIndex(Constant.COLUMN_TYPE)), "INCOMING")) {
                    talk.setType(Talk.Type.INCOMING);
                } else {
                    talk.setType(Talk.Type.OUTCOMING);
                }
                talk.setTime(mCursor.getString(mCursor.getColumnIndex(Constant.COLUMN_TIME)));
                mDatas.add(talk);
            }
            mCursor.close();
        }
        mDatabase.close();
        mAdapter = new TalkAdapter(this, mDatas);
        mListView.setAdapter(mAdapter);
        //设置为底部
//            mListView.setSelection(mListView.getBottom());
//            mListView.smoothScrollToPosition(0);//移动到首部
//            mListView.smoothScrollToPosition(mAdapter.getCount() - 1);//移动到尾部
        mListView.setSelection(mDatas.size() - 1);
    }

    private void initListener() {
        mSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String toMsg = mInputMsg.getText().toString();
                if (TextUtils.isEmpty(toMsg)) {
                    ToastUtil.showMessage(TalkActivity.this, "发送消息不能为空哦!");
                    return;
                }

                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String localdate = dateformat.format(new Date());
                Talk toMessage = new Talk();
                toMessage.setTime(localdate);
                toMessage.setType(Talk.Type.OUTCOMING);
                toMessage.setMsg(toMsg);
                // 这里将发送的数据放入数据库
                SQLiteDatabase mDatabase = new DbHelper(TalkActivity.this, SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG).getWritableDatabase();
                mDatabase.execSQL("create table if not exists " + Constant.TABLE_TALK + "(" + Constant.COLUMN_ID + " integer primary key autoincrement,"
                        + Constant.COLUMN_MESSAGE + " text,"
                        + Constant.COLUMN_TYPE + " text,"
                        + Constant.COLUMN_TIME + " text)");

                ContentValues mValues = new ContentValues();
                mValues.put(Constant.COLUMN_MESSAGE, toMessage.getMsg());
                mValues.put(Constant.COLUMN_TYPE, toMessage.getType().toString());
                mValues.put(Constant.COLUMN_TIME, toMessage.getTime());
                mDatabase.insert(Constant.TABLE_TALK, null, mValues);
                mValues.clear();
                mDatabase.close();

                //数据中加入toMessage对象，发送消息
                mDatas.add(toMessage);
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(mDatas.size() - 1);

                //发送后置空消息EditText中的消息
                mInputMsg.setText("");

                //开启子线程接收fromMessage的消息
                new Thread() {
                    public void run() {
                        Talk fromMessage = TalkHttpUtils.sendMessage(toMsg);
                        Message msg = Message.obtain();
                        msg.obj = fromMessage;
                        mHandler.sendMessage(msg);
                    }
                }.start();
            }
        });
    }

    public void close(View view) {
        finish();
    }
}