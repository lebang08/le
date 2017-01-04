package com.leday.Util;
/**
 * Time:2017/01/04
 * By LeBang
 */

import android.app.Activity;

import com.leday.Interface.OkHttpInterface;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpUtils {

    public static void OkHttpGet(final Activity activity, String url, final OkHttpInterface okHttpInterface) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showMessage(activity, "网络请求失败，请重试");
                        okHttpInterface.onFail(call);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        okHttpInterface.onSuccess(response);
                    }
                });
            }
        });
    }

    public static void OkHttpPost() {

    }
}