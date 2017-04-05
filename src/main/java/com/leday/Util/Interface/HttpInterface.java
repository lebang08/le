package com.leday.Util.Interface;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LeBang on 2017/4/5
 */
public interface HttpInterface {

    void onSuccess(String result, Call call, Response response);
}
