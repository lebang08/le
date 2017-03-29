package com.leday.Controller.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leday.BaseActivity;
import com.leday.Common.Constant;
import com.leday.Controller.Service.UpdateService;
import com.leday.Controller.fragment.FragmentMine;
import com.leday.Controller.fragment.FragmentStar;
import com.leday.Controller.fragment.FragmentToday;
import com.leday.Controller.fragment.FragmentWechat;
import com.leday.Model.Note;
import com.leday.Model.Talk;
import com.leday.Model.Today;
import com.leday.Model.Wechat;
import com.leday.R;
import com.leday.Util.DbHelper;
import com.leday.Util.DbUtil;
import com.leday.Util.NetUtil;
import com.leday.Util.PreferenUtil;
import com.leday.Util.SDCardUtil;
import com.leday.Util.StringUtil;
import com.leday.Util.ToastUtil;
import com.leday.Util.UpdateUtil;

import java.util.ArrayList;
import java.util.List;

public class MainTabActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private List<Fragment> mFragmentList;

    private TextView mTxt_a, mTxt_b, mTxt_d, mTxt_e;
    private ImageView mImg_a, mImg_b, mImg_d, mImg_e;

    //用于检测双击退出程序
    private boolean isFirst = true;
    private long lastTime;

    @Override
    public void onBackPressed() {
        if (isFirst) {
            ToastUtil.showMessage(this, "再按一次退出程序");
            lastTime = System.currentTimeMillis();
            isFirst = false;
        } else {
            if ((System.currentTimeMillis() - lastTime) < 2000) {
                this.finish();
                Intent intent = new Intent(this, UpdateService.class);
                this.stopService(intent);
            } else {
                ToastUtil.showMessage(this, "再按一次退出程序");
                lastTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

//        检测自动更新
        Intent intent = new Intent(this, UpdateService.class);
        startService(intent);
        new UpdateUtil(this).checkUpdate();

        initTab();
        initView();

        //检查网络
        if (!NetUtil.isConnected(MainTabActivity.this)) {
            ToastUtil.showMessage(MainTabActivity.this, "亲，你没有连接网络哦");
        }
    }

    private void initView() {
        LinearLayout mLinearLayout_a = (LinearLayout) findViewById(R.id.linearlayout_tab_a);
        LinearLayout mLinearLayout_b = (LinearLayout) findViewById(R.id.linearlayout_tab_b);
        LinearLayout mLinearLayout_d = (LinearLayout) findViewById(R.id.linearlayout_tab_d);
        LinearLayout mLinearLayout_e = (LinearLayout) findViewById(R.id.linearlayout_tab_e);
        mTxt_a = (TextView) findViewById(R.id.txt_tab_a);
        mTxt_b = (TextView) findViewById(R.id.txt_tab_b);
        mTxt_d = (TextView) findViewById(R.id.txt_tab_d);
        mTxt_e = (TextView) findViewById(R.id.txt_tab_e);
        mImg_a = (ImageView) findViewById(R.id.icon_tab_a);
        mImg_b = (ImageView) findViewById(R.id.icon_tab_b);
        mImg_d = (ImageView) findViewById(R.id.icon_tab_d);
        mImg_e = (ImageView) findViewById(R.id.icon_tab_e);

        mLinearLayout_a.setOnClickListener(this);
        mLinearLayout_b.setOnClickListener(this);
        mLinearLayout_d.setOnClickListener(this);
        mLinearLayout_e.setOnClickListener(this);
        setSelect(0);
    }

    //初始化Tab，三步走，控件、数据源、适配器
    private void initTab() {
        //控件
        mViewPager = (ViewPager) findViewById(R.id.viewpager_activity_tab);
        //数据源
        mFragmentList = new ArrayList<>();
        Fragment fragment_a = new FragmentToday();
        Fragment fragment_b = new FragmentStar();
        Fragment fragment_c = new FragmentWechat();
        Fragment fragment_d = new FragmentMine();
        mFragmentList.add(fragment_a);
        mFragmentList.add(fragment_b);
        mFragmentList.add(fragment_c);
        mFragmentList.add(fragment_d);
        //适配器
        FragmentPagerAdapter mFragmentAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        };
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setOnPageChangeListener(this);

        //提示转移数据库
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            doStorePermission();
        } else {
            //没权限，进行权限请求
            requestPermission(Constant.CODE_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void doStorePermission() {
        super.doStorePermission();
        try {
            if (getPackageManager().getPackageInfo("com.leday", 0).versionCode < 31) {
                if (!PreferenUtil.get(this, Constant.IsTransfer, "").equals(Constant.IsTransfer)) {
                    new AlertDialog.Builder(this)
                            .setTitle("亲，收藏夹优化啦")
                            .setMessage("部分亲反馈后，现在修改了收藏夹的位置和结构啦，迁移后您可以方便的在手机保留/迁移您的收藏夹、便签等数据哦,拒绝迁移可能导致数据丢失")
                            .setPositiveButton("现在迁移", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    transferDatabase(true);
                                }
                            }).setNegativeButton("下次提醒", null).show();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    //迁移表用
    private ArrayList<Today> mTodayList = new ArrayList<>();
    private Today mmToday;
    private ArrayList<Wechat> mWechatList = new ArrayList<>();
    private Wechat mWechat;
    private ArrayList<Note> mNoteList = new ArrayList<>();
    private Note mNote;
    private ArrayList<Talk> mTalkList = new ArrayList<>();
    private Talk mTalk;

    private void transferDatabase(boolean need_transfer) {
        //TODO 拿出自己的数据库,通过PreferenceUtil或者文件是否存在判断是否需要转移
        if (!need_transfer) {
            return;
        }
        ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("更新题库数据，请稍候");
        mProgressDialog.setMessage("Waiting...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        //取出历史上的今天
        SQLiteDatabase database_orgina = new DbHelper(this, "leday.db").getWritableDatabase();
        String isNone_today_table = DbUtil.queryToString(database_orgina, Constant.TABLE_SQLITE_MASTER, Constant.COLUMN_NAME, Constant.COLUMN_TABLE_NAME, "todaytb");
        if (!TextUtils.equals(isNone_today_table, Constant.NONE)) {
            Cursor cursor_today = database_orgina.query("todaytb", null, null, null, null, null, null);
            database_orgina.beginTransaction();
            if (cursor_today != null) {
                while (cursor_today.moveToNext()) {
                    mmToday = new Today();
                    mmToday.setE_id(cursor_today.getString(cursor_today.getColumnIndex(Constant.COLUMN_ID)));
                    mmToday.setDate(cursor_today.getString(cursor_today.getColumnIndex(Constant.COLUMN_DATE)));
                    mmToday.setTitle(cursor_today.getString(cursor_today.getColumnIndex(Constant.COLUMN_TITLE)));
                    mmToday.setContent(cursor_today.getString(cursor_today.getColumnIndex(Constant.COLUMN_CONTENT)));
                    mTodayList.add(mmToday);
                }
                cursor_today.close();
            }
            database_orgina.setTransactionSuccessful();
            database_orgina.endTransaction();
        }
        //取出微信微选
        String isNone_wechat_table = DbUtil.queryToString(database_orgina, Constant.TABLE_SQLITE_MASTER, Constant.COLUMN_NAME, Constant.COLUMN_TABLE_NAME, "wechattb");
        if (!TextUtils.equals(isNone_wechat_table, Constant.NONE)) {
            Cursor cursor = database_orgina.query("wechattb", null, null, null, null, null, null);
            database_orgina.beginTransaction();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    mWechat = new Wechat();
                    mWechat.setId(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_ID)));
                    mWechat.setTitle(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_TITLE)));
                    mWechat.setUrl(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_URL)));
                    mWechatList.add(mWechat);
                }
                cursor.close();
            }
            //批量操作成功,关闭事务
            database_orgina.setTransactionSuccessful();
            database_orgina.endTransaction();
        }
        //取出便签
        String isNone_note_table = DbUtil.queryToString(database_orgina, Constant.TABLE_SQLITE_MASTER, Constant.COLUMN_NAME, Constant.COLUMN_TABLE_NAME, "notetb");
        if (!TextUtils.equals(isNone_note_table, Constant.NONE)) {
            Cursor cursor_note = database_orgina.query("notetb", null, null, null, null, null, null);
            database_orgina.beginTransaction();
            if (cursor_note != null) {
                while (cursor_note.moveToNext()) {
                    mNote = new Note();
                    mNote.setTime(cursor_note.getString(cursor_note.getColumnIndex(Constant.COLUMN_DATE)));
                    mNote.setTitle(cursor_note.getString(cursor_note.getColumnIndex(Constant.COLUMN_TITLE)));
                    mNote.setContent(cursor_note.getString(cursor_note.getColumnIndex(Constant.COLUMN_CONTENT)));
                    mNoteList.add(mNote);
                }
                cursor_note.close();
            }
            database_orgina.setTransactionSuccessful();
            database_orgina.endTransaction();
        }
        //取出图灵对话
        String isNone_talk_table = DbUtil.queryToString(database_orgina, Constant.TABLE_SQLITE_MASTER, Constant.COLUMN_NAME, Constant.COLUMN_TABLE_NAME, "talktb");
        if (!TextUtils.equals(isNone_talk_table, Constant.NONE)) {
            Cursor cursor_note = database_orgina.query("talktb", null, null, null, null, null, null);
            database_orgina.beginTransaction();
            if (cursor_note != null) {
                while (cursor_note.moveToNext()) {
                    mTalk = new Talk();
                    mTalk.setMsg(cursor_note.getString(cursor_note.getColumnIndex(Constant.COLUMN_MESSAGE)));
                    //取一个String 暂做过渡
                    mTalk.setName(cursor_note.getString(cursor_note.getColumnIndex(Constant.COLUMN_TYPE)));
                    mTalk.setTime(cursor_note.getString(cursor_note.getColumnIndex(Constant.COLUMN_TIME)));
                    mTalkList.add(mTalk);
                }
                cursor_note.close();
            }
            database_orgina.setTransactionSuccessful();
            database_orgina.endTransaction();
        }
        database_orgina.close();

        SQLiteDatabase database_new = new DbHelper(this, SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG).getWritableDatabase();
        //迁移历史上的今天
        if (!TextUtils.equals(isNone_today_table, Constant.NONE)) {
            String sql_create_today = "CREATE TABLE IF NOT EXISTS " + Constant.TABLE_TODAY + "("
                    + Constant.COLUMN_ID + " integer PRIMARY KEY AUTOINCREMENT,"
                    + Constant.COLUMN_DATE + " text,"
                    + Constant.COLUMN_TITLE + " text, "
                    + Constant.COLUMN_CONTENT + " text)";
            database_new.execSQL(sql_create_today);
            for (int i = 0; i < mTodayList.size(); i++) {
                String sql_insert_today = "INSERT INTO " + Constant.TABLE_TODAY + "("
                        + Constant.COLUMN_DATE + ","
                        + Constant.COLUMN_TITLE + ","
                        + Constant.COLUMN_CONTENT + ")VALUES(\""
                        + mTodayList.get(i).getDate() + "\",\""
                        + StringUtil.transferString(mTodayList.get(i).getTitle()) + "\",\""
                        + StringUtil.transferString(mTodayList.get(i).getContent()) + "\");";
                database_new.execSQL(sql_insert_today);
            }
        }
        //迁移微信微选
        if (!TextUtils.equals(isNone_wechat_table, Constant.NONE)) {
            String sql_create_wechat = "CREATE TABLE IF NOT EXISTS " + Constant.TABLE_WECHAT + "("
                    + Constant.COLUMN_ID + " integer PRIMARY KEY AUTOINCREMENT,"
                    + Constant.COLUMN_TITLE + " text, "
                    + Constant.COLUMN_URL + " text)";
            database_new.execSQL(sql_create_wechat);
            for (int i = 0; i < mWechatList.size(); i++) {
                String sql_insert_wechat = "INSERT INTO " + Constant.TABLE_WECHAT + "("
                        + Constant.COLUMN_TITLE + ","
                        + Constant.COLUMN_URL + ")VALUES(\""
                        + StringUtil.transferString(mWechatList.get(i).getTitle()) + "\",\""
                        + StringUtil.transferString(mWechatList.get(i).getUrl()) + "\");";
                database_new.execSQL(sql_insert_wechat);
            }
        }
        //迁移便签
        if (!TextUtils.equals(isNone_note_table, Constant.NONE)) {
            String sql_create_note = "CREATE TABLE IF NOT EXISTS "
                    + Constant.TABLE_NOTE + "("
                    + Constant.COLUMN_ID + " integer primary key autoincrement,"
                    + Constant.COLUMN_DATE + " date,"
                    + Constant.COLUMN_TITLE + " text,"
                    + Constant.COLUMN_CONTENT + " text)";
            database_new.execSQL(sql_create_note);
            for (int i = 0; i < mNoteList.size(); i++) {
                String sql_insert_note = "INSERT INTO " + Constant.TABLE_NOTE + "("
                        + Constant.COLUMN_DATE + ","
                        + Constant.COLUMN_TITLE + ","
                        + Constant.COLUMN_CONTENT + ")VALUES(\""
                        + StringUtil.transferString(mNoteList.get(i).getTime()) + "\",\""
                        + StringUtil.transferString(mNoteList.get(i).getTitle()) + "\",\""
                        + StringUtil.transferString(mNoteList.get(i).getContent()) + "\");";
                database_new.execSQL(sql_insert_note);
            }
        }
        //迁移图灵机器人
        if (!TextUtils.equals(isNone_talk_table, Constant.NONE)) {
            String sql_create_talk = "CREATE TABLE IF NOT EXISTS "
                    + Constant.TABLE_TALK + "("
                    + Constant.COLUMN_ID + " integer primary key autoincrement,"
                    + Constant.COLUMN_MESSAGE + " text,"
                    + Constant.COLUMN_TYPE + " text,"
                    + Constant.COLUMN_TIME + " text)";
            database_new.execSQL(sql_create_talk);
            for (int i = 0; i < mTalkList.size(); i++) {
                String sql_insert_talk = "INSERT INTO " + Constant.TABLE_TALK + "("
                        + Constant.COLUMN_MESSAGE + ","
                        + Constant.COLUMN_TYPE + ","
                        + Constant.COLUMN_TIME + ")VALUES(\""
                        + StringUtil.transferString(mTalkList.get(i).getMsg()) + "\",\""
                        + StringUtil.transferString(mTalkList.get(i).getName()) + "\",\""
                        + StringUtil.transferString(mTalkList.get(i).getTime()) + "\");";
                database_new.execSQL(sql_insert_talk);
            }
        }
        database_new.close();
        mProgressDialog.cancel();
        PreferenUtil.put(this, Constant.IsTransfer, Constant.IsTransfer);
        ToastUtil.show(this, "恭喜您,迁移完成啦", Toast.LENGTH_SHORT);
    }

    //        页卡点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearlayout_tab_a:
                setSelect(0);
                break;
            case R.id.linearlayout_tab_b:
                setSelect(1);
                break;
            case R.id.linearlayout_tab_d:
                setSelect(2);
                break;
            case R.id.linearlayout_tab_e:
                setSelect(3);
                break;
        }
    }

    //页卡滑动事件
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        setSelect(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    //    选项事件，1.viewpager切换   2.Title切换
    private void setSelect(int i) {
        mViewPager.setCurrentItem(i);
        setTabTitle(i);
    }

    //   Title切换事件
    private void setTabTitle(int i) {
        reSetTextColor();
        reSetIcon();
        switch (i) {
            case 0:
                mTxt_a.setTextColor(Color.parseColor("#ffffff"));
                mImg_a.setImageResource(R.drawable.icon_tabone_light);
                break;
            case 1:
                mTxt_b.setTextColor(Color.parseColor("#ffffff"));
                mImg_b.setImageResource(R.drawable.icon_tabtwo_light);
                break;
            case 2:
                mTxt_d.setTextColor(Color.parseColor("#ffffff"));
                mImg_d.setImageResource(R.drawable.icon_tabthree_light);
                break;
            case 3:
                mTxt_e.setTextColor(Color.parseColor("#ffffff"));
                mImg_e.setImageResource(R.drawable.icon_tabfour_light);
                break;
        }
    }

    //重设图片为未选中时
    private void reSetIcon() {
        mImg_a.setImageResource(R.drawable.icon_tabone);
        mImg_b.setImageResource(R.drawable.icon_tabtwo);
        mImg_d.setImageResource(R.drawable.icon_tabthree);
        mImg_e.setImageResource(R.drawable.icon_tabfour);
    }

    //重设Title中字体颜色为默认
    private void reSetTextColor() {
        mTxt_a.setTextColor(Color.parseColor("#707070"));
        mTxt_b.setTextColor(Color.parseColor("#707070"));
        mTxt_d.setTextColor(Color.parseColor("#707070"));
        mTxt_e.setTextColor(Color.parseColor("#707070"));
    }
}