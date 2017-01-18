package com.leday.Util;

/**
 * Created by Administrator on 2016/8/17
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.leday.R;
import com.leday.entity.UpdateInfo;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;

public class UpdateUtil {

    private ProgressBar mProgressBar;
    private Dialog mDownLoadDialog;

    private final String URL_SERVE = "http://www.iyuce.com/Scripts/leday.json";
    private static final int DOWNLOADING = 1;
    private static final int DOWNLOAD_FINISH = 0;

    private UpdateInfo updateInfo;
    private String mSavePath;
    private String mAlreayNew;
    private int mProgress;
    private boolean mIsCancel = false;

    private Context mcontext;

    public UpdateUtil(Context context) {
        this.mcontext = context;
    }

    public UpdateUtil(Context context, String showCheck) {
        this.mcontext = context;
        this.mAlreayNew = showCheck;
    }

    @SuppressLint("HandlerLeak")
    private Handler mGetVersionHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                String jsonString = (String) msg.obj;
                String parseString = new String(jsonString.getBytes("ISO-8859-1"), "utf-8");
                JSONObject obj;
                obj = new JSONObject(parseString);

                if (obj.getString("code").equals("0")) {
                    JSONArray data = obj.getJSONArray("data");
                    obj = data.getJSONObject(0);
                    Gson gson = new Gson();
                    updateInfo = gson.fromJson(obj.toString(), UpdateInfo.class);
                    LogUtil.e("updateInfo", "updateInfo = " + updateInfo.toString());
                }
                if (isUpdate(updateInfo.getVersion())) {
                    showNoticeDialog();
                } else {
                    if (!TextUtils.isEmpty(mAlreayNew)) {
                        ToastUtil.showMessage(mcontext, "已经是最新版本v" + updateInfo.getVersion() + "啦");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mUpdateProgressHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOADING:
                    mProgressBar.setProgress(mProgress);
                    break;
                case DOWNLOAD_FINISH:
                    mDownLoadDialog.dismiss();
                    installAPK();
                    break;
            }
        }
    };

    public void checkUpdate() {
        OkGo.get(URL_SERVE).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, okhttp3.Response response) {
                Message msg = Message.obtain();
                msg.obj = s;
                mGetVersionHandler.sendMessage(msg);
            }

            @Override
            public void onError(Call call, okhttp3.Response response, Exception e) {
                super.onError(call, response, e);
                LogUtil.i("error =  update");
            }
        });
//        StringRequest request = new StringRequest(Method.GET, URL_SERVE, new Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Message msg = Message.obtain();
//                msg.obj = response;
//                mGetVersionHandler.sendMessage(msg);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                LogUtil.i("volleyError = " + volleyError.getMessage());
//            }
//        });
//        request.setTag("updateutil");
//        MyApplication.getHttpQueue().add(request);
    }

    //boolean比较本地版本是否需要更新
    private boolean isUpdate(String server_version) {
        float serverVersion = Float.parseFloat(server_version);
        //将该数据保存如sharepreference，留用
        PreferenUtil.put(mcontext, "serverVersion", String.valueOf(serverVersion));
        String localVersion = null;
        try {
            //获取versionName作比较
            localVersion = mcontext.getPackageManager().getPackageInfo("com.leday", 0).versionName;
            //将该数据保存如sharepreference，留用
            PreferenUtil.put(mcontext, "localVersion", mcontext.getPackageManager().getPackageInfo("com.leday", 0).versionName);
            LogUtil.e("localVersion = " + localVersion + "||" + serverVersion);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return serverVersion > Float.parseFloat(localVersion);
    }

    @SuppressLint("InlinedApi")
    @SuppressWarnings("deprecation")
    private void showNoticeDialog() {     //show 弹窗供选择是否更新
        if (!NetUtil.isWifi(mcontext)) {
            ToastUtil.showMessage(mcontext, "你当前不在WIFI环境，将耗费4.3M流量下载，建议在WIFI环境下下载哦", 3000);
        }
        AlertDialog.Builder builder = new Builder(mcontext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle(updateInfo.getTitle());
        builder.setMessage(updateInfo.getMessage());
        builder.setPositiveButton("立刻更新", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
                //下载新版本后，消除欢迎页的key，再次安装则可以有引导动画
                PreferenUtil.remove(mcontext, "welcome");
            }
        });
        builder.setNegativeButton("下次再说", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint({"InflateParams", "InlinedApi"})
    private void showDownloadDialog() {     //显示下载进度
        AlertDialog.Builder builder = new Builder(mcontext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle("下载中");
        View view = LayoutInflater.from(mcontext).inflate(R.layout.dialog_progress, null);
        mProgressBar = (ProgressBar) view.findViewById(R.id.update_progress);
        builder.setView(view);
        builder.setNegativeButton("取消下载", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mIsCancel = true;
            }
        });
        mDownLoadDialog = builder.create();
        mDownLoadDialog.show();

        //下载文件
        downloadAPK();
    }

    //文件下载的操作(1.存储卡/2.输入流)
    private void downloadAPK() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        mSavePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/leday/";

                        File dir = new File(mSavePath);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }

                        HttpURLConnection conn = (HttpURLConnection) new URL(updateInfo.getApkurl()).openConnection();
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        int length = conn.getContentLength();

                        File apkFile = new File(mSavePath, updateInfo.getVersion());
                        FileOutputStream fos = new FileOutputStream(apkFile);

                        int count = 0;
                        byte[] buffer = new byte[1024];

                        while (!mIsCancel) {
                            int numread = is.read(buffer);
                            count += numread;
                            mProgress = (int) (((float) count / length) * 100);
                            // 更新进度条
                            mUpdateProgressHandler.sendEmptyMessage(DOWNLOADING);
                            // 下载完成
                            if (numread < 0) {
                                mUpdateProgressHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                                break;
                            }
                            fos.write(buffer, 0, numread);
                        }
                        fos.close();
                        is.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //安装下载好的APK
    private void installAPK() {
        File apkFile = new File(mSavePath, updateInfo.getVersion());
        if (!apkFile.exists()) {
            return;
        }
        Intent it = new Intent(Intent.ACTION_VIEW);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.parse("file://" + apkFile.toString());
        it.setDataAndType(uri, "application/vnd.android.package-archive");
        mcontext.startActivity(it);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}