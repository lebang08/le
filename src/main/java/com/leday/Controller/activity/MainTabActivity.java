package com.leday.Controller.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leday.Common.Constant;
import com.leday.R;
import com.leday.Controller.Service.UpdateService;
import com.leday.Controller.fragment.FragmentToday;
import com.leday.Controller.fragment.FragmentStar;
import com.leday.Controller.fragment.FragmentWechat;
import com.leday.Controller.fragment.FragmentMine;
import com.leday.Util.DbHelper;
import com.leday.Util.NetUtil;
import com.leday.Util.SDCardUtil;
import com.leday.Util.StringUtil;
import com.leday.Util.ToastUtil;
import com.leday.Util.UpdateUtil;
import com.leday.Model.Today;

import java.util.ArrayList;
import java.util.List;

public class MainTabActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

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

        //转移数据库
        new AlertDialog.Builder(this)
                .setTitle("亲，收藏夹优化啦")
                .setMessage("部分亲反馈后，现在修改了收藏夹的位置和结构啦，迁移后您可以方便的在本地保留/迁移您的收藏夹数据库哦,拒绝迁移可能导致数据丢失")
                .setPositiveButton("现在迁移", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        transferDatabase(true);
                    }
                }).setNegativeButton("不再提醒", null)
                .show();
    }

    private void transferDatabase(boolean need_transfer) {
        //TODO 拿出自己的数据库,通过PreferenceUtil或者文件是否存在判断是否需要转移
        if (!need_transfer) {
            return;
        }
        ArrayList<Today> mTransferList = new ArrayList<>();
        Today mmToday;
        SQLiteDatabase database_orgina = new DbHelper(this, "leday.db").getWritableDatabase();
        Cursor cursor = database_orgina.query("todaytb", null, null, null, null, null, null);
        database_orgina.beginTransaction();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                mmToday = new Today();
                mmToday.setE_id(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_ID)));
                mmToday.setDate(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_DATE)));
                mmToday.setTitle(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_TITLE)));
                mmToday.setContent(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_CONTENT)));
                mTransferList.add(mmToday);
            }
            cursor.close();
        }
        //批量操作成功,关闭事务
        database_orgina.setTransactionSuccessful();
        database_orgina.endTransaction();
        database_orgina.close();

        SQLiteDatabase database_new = new DbHelper(this, SDCardUtil.getSDCardPath() + Constant.DATABASE_LEBANG).getWritableDatabase();
        String sql_create = "CREATE TABLE IF NOT EXISTS " + Constant.TABLE_TODAY + "("
                + Constant.COLUMN_ID + " integer PRIMARY KEY AUTOINCREMENT,"
                + Constant.COLUMN_DATE + " text,"
                + Constant.COLUMN_TITLE + " text, "
                + Constant.COLUMN_CONTENT + " text)";
        database_new.execSQL(sql_create);
        for (int i = 0; i < mTransferList.size(); i++) {
            String sql_insert = "INSERT INTO " + Constant.TABLE_TODAY + "("
                    + Constant.COLUMN_DATE + ","
                    + Constant.COLUMN_TITLE + ","
                    + Constant.COLUMN_CONTENT + ")VALUES(\""
                    + mTransferList.get(i).getDate() + "\",\""
                    + StringUtil.transferString(mTransferList.get(i).getTitle()) + "\",\""
                    + StringUtil.transferString(mTransferList.get(i).getContent()) + "\");";
            database_new.execSQL(sql_insert);
        }
        database_new.close();
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