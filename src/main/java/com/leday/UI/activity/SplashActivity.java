package com.leday.UI.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.leday.BaseActivity;
import com.leday.R;

import java.util.Timer;

public class SplashActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitle, mTxtPass;
    private Timer mTimer = new Timer();

    private Handler mHandler = new Handler();
    private boolean isInMianAcitivity = false;

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
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toMain();
            }
        }, 1500);
    }

    private void initView() {
        mTitle = (TextView) findViewById(R.id.txt_splash_count);
        mTxtPass = (TextView) findViewById(R.id.txt_splash_pass);

        mTxtPass.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        toMain();
    }

    private void toMain() {
        if (isInMianAcitivity) {
            return;
        }
        startActivity(new Intent(SplashActivity.this, TabActivity.class));
        overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
        SplashActivity.this.finish();
        isInMianAcitivity = true;
    }
}