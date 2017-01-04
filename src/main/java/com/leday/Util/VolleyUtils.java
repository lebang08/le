package com.leday.Util;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.leday.Interface.VolleyInterface;
import com.leday.application.MyApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/30.
 */
public class VolleyUtils {

    private Context mContext;

    public VolleyUtils(Context context) {
        this.mContext = context;
    }

    /**
     * @param url
     * @param tag
     * @param volleyinterface
     */
    public void GetRequest(String url, String tag, final VolleyInterface volleyinterface) {
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                volleyinterface.onSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(mContext, "网络请求失败，请检查网络", Toast.LENGTH_SHORT);
                volleyinterface.onFail(volleyError);
            }
        });
        mStringRequest.setTag(tag);
        MyApplication.getHttpQueue().add(mStringRequest);
    }

    public void PostRequest(String url, String tag, final HashMap<String, String> map, final VolleyInterface volleyinterface) {
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                volleyinterface.onSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(mContext, "网络请求失败，请检查网络", Toast.LENGTH_SHORT);
                volleyinterface.onFail(volleyError);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return map;
            }
        };
        mStringRequest.setTag(tag);
        MyApplication.getHttpQueue().add(mStringRequest);
    }
}