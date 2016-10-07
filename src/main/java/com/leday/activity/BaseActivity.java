package com.leday.activity;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {

//    private boolean isFirst = true;
//    private long lastTime;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

//    @Override
//    public void onBackPressed() {
//        if (isFirst) {
//            ToastUtil.showMessage(this, "再按一次退出程序");
//            lastTime = System.currentTimeMillis();
//            isFirst = false;
//        } else {
//            if ((System.currentTimeMillis() - lastTime) < 2000) {
//                this.finish();
//            } else {
//                ToastUtil.showMessage(this, "再按一次退出程序");
//                lastTime = System.currentTimeMillis();
//            }
//        }
//    }
}