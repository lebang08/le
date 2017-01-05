package com.leday.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.leday.Common.Constant;
import com.leday.Interface.OkHttpInterface;
import com.leday.R;
import com.leday.Util.OkHttpUtils;
import com.leday.Util.ToastUtil;
import com.leday.application.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class StarActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mImge;
    private TextView mTxt;

    private String localstar;
    private String localtime = "today";

    private static final String Tag_STAR_ACTIVITY = "staractivity";

    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.getHttpQueue().cancelAll(Tag_STAR_ACTIVITY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getJson();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);

        initView();
    }

    //    初始化控件
    private void initView() {
        Intent intent = getIntent();
        localstar = intent.getStringExtra("star");
        int localimgId = intent.getIntExtra("imgId", 0);
        ToastUtil.showMessage(this, "哇! 快看,是" + localstar + "!");

        mImge = (ImageView) findViewById(R.id.img_activity_star);
        mTxt = (TextView) findViewById(R.id.txt_star_content);
        ImageView mImgBack = (ImageView) findViewById(R.id.img_star_back);
        TextView mTitle = (TextView) findViewById(R.id.txt_star_title);
        Button mBtnToday = (Button) findViewById(R.id.btn_star_today);
        Button mBtnWeek = (Button) findViewById(R.id.btn_star_week);
        Button mBtnTomorrow = (Button) findViewById(R.id.btn_star_tomorrow);
        Button mBtnNextweek = (Button) findViewById(R.id.btn_star_nextweek);

        mImgBack.setOnClickListener(this);
        mBtnToday.setOnClickListener(this);
        mBtnWeek.setOnClickListener(this);
        mBtnTomorrow.setOnClickListener(this);
        mBtnNextweek.setOnClickListener(this);

        mTitle.setText(localstar);
        setImgView(localimgId);
    }

    /**
     * 设置对应的星座图片
     */
    private void setImgView(int localimgId) {
        switch (localimgId) {
            case 0:
                mImge.setImageResource(R.drawable.img_star_1);
                break;
            case 1:
                mImge.setImageResource(R.drawable.img_star_2);
                break;
            case 2:
                mImge.setImageResource(R.drawable.img_star_3);
                break;
            case 3:
                mImge.setImageResource(R.drawable.img_star_4);
                break;
            case 4:
                mImge.setImageResource(R.drawable.img_star_5);
                break;
            case 5:
                mImge.setImageResource(R.drawable.img_star_6);
                break;
            case 6:
                mImge.setImageResource(R.drawable.img_star_7);
                break;
            case 7:
                mImge.setImageResource(R.drawable.img_star_8);
                break;
            case 8:
                mImge.setImageResource(R.drawable.img_star_9);
                break;
            case 9:
                mImge.setImageResource(R.drawable.img_star_10);
                break;
            case 10:
                mImge.setImageResource(R.drawable.img_star_11);
                break;
            case 11:
                mImge.setImageResource(R.drawable.img_star_12);
                break;
        }
    }

    private void getJson() {
        progressdialogShow(this);
        new OkHttpUtils().OkHttpGet(this, Constant.URL_STAR + localstar + "&type=" + localtime, new OkHttpInterface() {
            @Override
            public void onSuccess(Response response) {
                try {
                    DoSuccess(response.body().string());
                    progressCancel();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(Call call) {
                progressCancel();
            }
        });
    }

    private void DoSuccess(String response) {
        JSONObject obj;
        try {
            obj = new JSONObject(response);
//                    今日或明日的数据处理
            if (localtime.equals("today") || localtime.equals("tomorrow")) {
                String j_datetime = obj.getString("datetime");
                String j_color = obj.getString("color");
                String j_number = obj.getString("number");
                String j_QFriend = obj.getString("QFriend");
                String j_love = obj.getString("love");
                String j_money = obj.getString("money");
                String j_summary = obj.getString("summary");
                String all = j_datetime + "\n幸运色: " + j_color + "\n幸运数: " + j_number + "\n幸运星座: " + j_QFriend +
                        "\n爱情指数: " + j_love + "\r\r\r\r\r\r\r\r财运指数: " + j_money + "\n\n总星势: " + j_summary;
                mTxt.setText(all);
            }
//                    本周或下周的数据处理
            else if (localtime.equals("week") || localtime.equals("nextweek")) {
                String j_date = obj.getString("date");
                String j_health = obj.getString("health");
                String j_job = obj.getString("job");
                String j_love = obj.getString("love");
                String j_money = obj.getString("money");
                String j_work = obj.getString("work");
                String all = j_date + "\n" + j_health + "\n" + j_job + "\n" +
                        "\n" + j_love + "\n" + j_money + "\n" + j_work;
                mTxt.setText(all);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_star_back:
                finish();
                break;
            case R.id.btn_star_today:
                localtime = "today";
                getJson();
                break;
            case R.id.btn_star_week:
                localtime = "week";
                getJson();
                break;
            case R.id.btn_star_tomorrow:
                localtime = "tomorrow";
                getJson();
                break;
            case R.id.btn_star_nextweek:
                localtime = "nextweek";
                getJson();
                break;
        }
    }
}