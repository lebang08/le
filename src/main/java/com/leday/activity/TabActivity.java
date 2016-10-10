package com.leday.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leday.R;
import com.leday.Util.NetUtil;
import com.leday.Util.ToastUtil;
import com.leday.Util.UpdateUtil;
import com.leday.fragment.FragmentA;
import com.leday.fragment.FragmentB;
import com.leday.fragment.FragmentC;
import com.leday.fragment.FragmentD;

import java.util.ArrayList;
import java.util.List;

public class TabActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

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

        //检测自动更新
        new UpdateUtil(this).checkUpdate();
        initTab();
        initView();

        //检查网络
        if (!NetUtil.isConnected(TabActivity.this)) {
            ToastUtil.showMessage(TabActivity.this, "亲，你没有连接网络哦");
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
        Fragment fragment_a = new FragmentA();
        Fragment fragment_b = new FragmentB();
        Fragment fragment_c = new FragmentC();
        Fragment fragment_d = new FragmentD();
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