package com.leday.Util;

import com.leday.Util.Interface.DownLoadInterface;
import com.leday.Util.Interface.HttpInterface;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LeBang on 2017/4/5
 */
public class HttpUtil {

    public static void getRequest(String url, String tag, final HttpInterface httpInterface) {
        OkGo.get(url).tag(tag)
                .cacheKey("cacheKey")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.DEFAULT)    // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        httpInterface.onSuccess(s, call, response);
                    }
                });
    }

    public static void downlodaRequest(String url, final DownLoadInterface downLoadInterface) {
        OkGo.get(url)
                .execute(new FileCallback() {
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        downLoadInterface.onSuccess(file, call, response);
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                        downLoadInterface.onProgress(currentSize, totalSize, progress, networkSpeed);
                    }
                });
    }

    public static void removeTag(String tag) {
        OkGo.getInstance().cancelTag(tag);
    }
}