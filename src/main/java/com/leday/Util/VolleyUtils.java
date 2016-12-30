package com.leday.Util;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.leday.Interface.VolleyInterface;
import com.leday.application.MyApplication;

/**
 * Created by Administrator on 2016/12/30.
 */
public class VolleyUtils {

    private Context mContext;
    private VolleyInterface mInterface;

    public VolleyUtils(Context context, VolleyInterface volleyinterface) {
        this.mContext = context;
        this.mInterface = volleyinterface;
    }

    public void GetRequest(String url, String tag) {
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                ToastUtil.show(mContext, "请求成功", Toast.LENGTH_SHORT);
                LogUtil.i("S= " + s);
                mInterface.onSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(mContext, "请求失败", Toast.LENGTH_SHORT);
                mInterface.onFail(volleyError);
            }
        });
        mStringRequest.setTag(tag);
        MyApplication.getHttpQueue().add(mStringRequest);
    }

    public static void PostRequest() {

    }
}