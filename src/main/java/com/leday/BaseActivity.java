package com.leday;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.leday.Common.Constant;
import com.leday.Util.LogUtil;
import com.leday.Util.NetUtil;
import com.leday.Util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends AppCompatActivity {

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


    /**
     * 判断是否拥有权限
     */
    public boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    /**
     * 请求权限
     */
    protected void requestPermission(int code, String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, code);
        ToastUtil.showMessage(this, "如果拒绝存储授权,会导致应用无法正常使用");
    }

    /**
     * 请求权限的回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.CODE_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ToastUtil.showMessage(this, "现在您拥有了存储权限");
                    doStorePermission();
                } else {
                    ToastUtil.showMessage(this, "您拒绝存储授权,会导致应用无法正常使用，可以在系统设置中重新开启权限");
                }
                break;

        }
    }

    //子类重写后实现具体调用相机的业务逻辑
    public void doStorePermission() {
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