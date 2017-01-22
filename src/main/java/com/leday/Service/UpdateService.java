package com.leday.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.leday.Util.LogUtil;

public class UpdateService extends Service {

    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i("service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i("service onStartCommand");
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
//                        HttpURLConnection conn = (HttpURLConnection) new URL(mUrl).openConnection();
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
//                            mUpdateProgressHandler.sendEmptyMessage(DOWNLOADING);
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
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i("service onDestroy");
    }
}