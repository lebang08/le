package com.leday.Interface;

import com.android.volley.VolleyError;

/**
 * Created by Administrator on 2016/12/30.
 */
public interface VolleyInterface {

    void onSuccess(String response);

    void onFail(VolleyError error);
}