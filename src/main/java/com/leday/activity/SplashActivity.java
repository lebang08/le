package com.leday.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.leday.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity implements View.OnClickListener {

    private TextView mTitle, mTxtPass;
    private ImageView mImg;
    private Timer mTimer = new Timer();
    private final int DO_COUNT = 0;
    private int count;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == DO_COUNT) {
                mTitle.setText("倒计时" + msg.obj + "秒");
                if ((int) msg.obj == 0) {
                    startActivity(new Intent(SplashActivity.this, TabActivity.class));
                    SplashActivity.this.finish();
                    mTimer.cancel();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        mTimer.cancel();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
        count = 5;
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                count = count - 1;
                msg.what = DO_COUNT;
                msg.obj = count;
                mHandler.sendMessage(msg);
            }
        }, 100, 1000);
    }

    private void initView() {
        mImg = (ImageView) findViewById(R.id.img_activity_splash);
        mTitle = (TextView) findViewById(R.id.txt_splash_count);
        mTxtPass = (TextView) findViewById(R.id.txt_splash_pass);

        mTxtPass.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mTimer.cancel();
        startActivity(new Intent(SplashActivity.this, TabActivity.class));
    }

}