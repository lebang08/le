package com.leday.Util;

/**
 * Created by Administrator on 2016/8/17
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leday.Model.UpdateInfo;
import com.leday.R;
import com.leday.Util.Interface.DownLoadInterface;
import com.leday.Util.Interface.HttpInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

public class UpdateUtil {

    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    private ProgressBar mProgressBar;
    private TextView mProgressTxt;
    private Dialog mDownLoadDialog;

    private final String URL_SERVE = "http://lebang08.github.io/update/leday.json";
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
    private Handler mUpdateProgressHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOADING:
                    mProgressBar.setProgress(mProgress);
                    mProgressTxt.setText((Integer) msg.obj + "%");

                    //通知栏
                    builder.setContentText(msg.obj + "%");
//                    builder.setTicker(msg.obj + "%");
                    notificationManager.notify(0, builder.build());
                    break;
                case DOWNLOAD_FINISH:
                    mDownLoadDialog.dismiss();
//                    installAPK();
                    break;
            }
        }
    };

    public void checkUpdate() {
        HttpUtil.getRequest(URL_SERVE, null, new HttpInterface() {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                doSuccess(result);
                LogUtil.i("service do update");
            }
        });
    }

    private void doSuccess(String s) {
        try {
//                String parseString = new String(jsonString.getBytes("ISO-8859-1"), "utf-8");
            JSONObject obj;
            obj = new JSONObject(s);
            if (obj.getString("code").equals("0")) {
                JSONArray data = obj.getJSONArray("data");
                obj = data.getJSONObject(0);
//                Gson gson = new Gson();
//                updateInfo = gson.fromJson(obj.toString(), UpdateInfo.class);
                updateInfo = new UpdateInfo();
                updateInfo.setTitle(obj.getString("title"));
                updateInfo.setMessage(obj.getString("message"));
                updateInfo.setVersion(obj.getString("version"));
                updateInfo.setApkurl(obj.getString("apkurl"));
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
        mProgressTxt = (TextView) view.findViewById(R.id.update_text);
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

        //创建通知栏
        createNotification();
        //下载文件
        downloadAPK();
    }

    /**
     * 系统通知栏设置
     */
    private void createNotification() {
        notificationManager = (NotificationManager) mcontext.getSystemService(mcontext.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(mcontext);

        // action when clicked
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("host://anotheractivity"));
        builder.setSmallIcon(R.mipmap.img_threepoint)
                .setContentTitle("开始下载")
                .setContentText("下载进度")
                .setTicker("下载进度")
                .setContentIntent(PendingIntent.getActivity(mcontext, 0, intent, 0));
        notificationManager.notify(0, builder.build());
    }

    //文件下载的操作(1.存储卡/2.输入流)
    private void downloadAPK() {
        HttpUtil.downlodaRequest(updateInfo.getApkurl(), new DownLoadInterface() {
            @Override
            public void onSuccess(File file, Call call, Response response) {
                mSavePath = file.toString();
                mDownLoadDialog.dismiss();
                installAPK(file);
            }

            @Override
            public void onProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                mProgress = (int) (((float) currentSize / totalSize) * 100);
                // 更新进度条
                Message msg = new Message();
                msg.what = DOWNLOADING;
                msg.obj = mProgress;
                mUpdateProgressHandler.sendMessageDelayed(msg, 500);
                // 下载完成
                if (mProgress >= 100) {
                    mUpdateProgressHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                }
                LogUtil.i("progress = " + progress + "====" + currentSize + "/" + totalSize + "||networkSpeed = " + networkSpeed);

            }
        });
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                        mSavePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/leday/";
//
//                        File dir = new File(mSavePath);
//                        if (!dir.exists()) {
//                            dir.mkdir();
//                        }
//
//                        HttpURLConnection conn = (HttpURLConnection) new URL(updateInfo.getApkurl()).openConnection();
//                        conn.connect();
//                        InputStream is = conn.getInputStream();
//                        int length = conn.getContentLength();
//
//                        File apkFile = new File(mSavePath, updateInfo.getVersion());
//                        FileOutputStream fos = new FileOutputStream(apkFile);
//
//                        int count = 0;
//                        byte[] buffer = new byte[1024];
//
//                        while (!mIsCancel) {
//                            int numread = is.read(buffer);
//                            count += numread;
//                            mProgress = (int) (((float) count / length) * 100);
//                            // 更新进度条
//                            Message msg = new Message();
//                            msg.what = DOWNLOADING;
//                            msg.obj = mProgress;
//                            mUpdateProgressHandler.sendMessage(msg);
//                            // 下载完成
//                            if (numread < 0) {
//                                mUpdateProgressHandler.sendEmptyMessage(DOWNLOAD_FINISH);
//                                break;
//                            }
//                            fos.write(buffer, 0, numread);
//                        }
//                        fos.close();
//                        is.close();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }

    //安装下载好的APK
    private void installAPK(File file) {
//        File apkFile = new File(mSavePath, updateInfo.getVersion());
//        if (!apkFile.exists()) {
//            return;
//        }
        Intent it = new Intent(Intent.ACTION_VIEW);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.parse("file://" + file.toString());
        it.setDataAndType(uri, "application/vnd.android.package-archive");
        mcontext.startActivity(it);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}