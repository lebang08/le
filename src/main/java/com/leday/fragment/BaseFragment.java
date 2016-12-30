package com.leday.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by Administrator on 2016/12/30.
 */
public class BaseFragment extends Fragment {

    private ProgressDialog mProgressDialog;

    public void progressShow(Context context) {
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