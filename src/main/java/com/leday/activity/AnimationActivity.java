package com.leday.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.leday.R;
import com.leday.Util.PreferenUtil;

import java.util.ArrayList;
import java.util.List;

public class AnimationActivity extends BaseActivity {

    private ViewPager mViewpager;
    private List<View> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_animation_welcome);

        //判断是否存在该Key，决定是否需要引导动画
        if (PreferenUtil.contains(this, "welcome")) {
            startActivity(new Intent(this, TabActivity.class));
            finish();
        } else {
            initView();
        }
    }

    private void initView() {
        mViewpager = (ViewPager) findViewById(R.id.viewpager_activity_animation);

        initPager();
    }

    private void initPager() {
        LayoutInflater mInflate = getLayoutInflater().from(this);

        //数据View
        View view1 = mInflate.inflate(R.layout.animation_welcome_a, null);
        View view2 = mInflate.inflate(R.layout.animation_welcome_b, null);
        mList.add(view1);
        mList.add(view2);
        //御用适配器PagerAdapter
        PagerAdapter mAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return mList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(mList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mList.get(position));
                return mList.get(position);
            }
        };
        mViewpager.setAdapter(mAdapter);
    }

    //按钮，点击事件(XML中直接作了监听)
    public void animationWelcome(View view) {
        startActivity(new Intent(this, TabActivity.class));
        PreferenUtil.put(this, "welcome", 0);
        finish();
    }
}