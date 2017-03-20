package com.leday.Util.Interface;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/1/4.
 */
public interface OkHttpInterface {

    void onSuccess(Response response);

    void onFail(Call call);
}
