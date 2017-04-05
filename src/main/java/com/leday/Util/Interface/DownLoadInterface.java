package com.leday.Util.Interface;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LeBANG on 2017/4/5
 */
public interface DownLoadInterface {

    void onSuccess(File file, Call call, Response response);

    void onProgress(long currentSize, long totalSize, float progress, long networkSpeed);

}