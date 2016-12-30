package com.leday.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.leday.Util.ActivityManager;
import com.leday.Util.LogUtil;
import com.leday.Util.NetUtil;
import com.leday.Util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {

    private ProgressDialog mProgressDialog;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //1.打印类名
        LogUtil.i(this.getClass().getSimpleName());
        //2.判断是否有网络
        if (!NetUtil.isConnected(this)) {
            ToastUtil.showMessage(this, "网络连接不可用，请检查网络");
        }
        //3.加入Activity管理栈
        ActivityManager.getAppManager().addActivity(this);
        //4.是否沉浸式状态栏
    }

    public void progressdialogShow(Context context) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            initProgressDialog();
            return;
        }
        initProgressDialog();

    }

    /**
     * 配置progressDialog
     */
    private void initProgressDialog() {
        mProgressDialog.setTitle("加载中，请稍候");
        mProgressDialog.setMessage("loading...");
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    public void progressCancel() {
        if (mProgressDialog == null) {
            return;
        }
        mProgressDialog.cancel();
    }

}