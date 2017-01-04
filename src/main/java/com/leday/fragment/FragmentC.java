package com.leday.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.leday.Common.Constant;
import com.leday.Interface.OkHttpInterface;
import com.leday.R;
import com.leday.Util.OkHttpUtils;
import com.leday.View.ListViewHightHelper;
import com.leday.activity.NoteActivity;
import com.leday.activity.WebViewActivity;
import com.leday.adapter.WechatAdapter;
import com.leday.application.MyApplication;
import com.leday.entity.Wechat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class FragmentC extends BaseFragment implements
        AdapterView.OnItemClickListener, View.OnClickListener {

    private FloatingActionButton mFtn;

    private ListView mListView;
    private List<Wechat> wechatList = new ArrayList<>();

    private static final String TAG_FRAGMENT_C = "fragmentc";

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getHttpQueue().cancelAll(TAG_FRAGMENT_C);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_c, container, false);
        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        mFtn = (FloatingActionButton) view.findViewById(R.id.fab_fragment_c);
        mFtn.setOnClickListener(this);

        mListView = (ListView) view.findViewById(R.id.listview_fragment_c);
        mListView.setOnItemClickListener(this);
    }

    /**
     * 请求数据
     */
    private void initEvent() {
        progressShow(getActivity());
        OkHttpUtils.OkHttpGet(getActivity(), Constant.URL_WECHAT, new OkHttpInterface() {
            @Override
            public void onSuccess(Response response) {
                try {
                    Dosuccess(response.body().string());
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

    /**
     * 请求成功后对data结果进行处理
     *
     * @param response
     */
    private void Dosuccess(String response) {
        JSONObject obj;
        JSONArray arr;
        try {
            obj = new JSONObject(response);
            obj = obj.getJSONObject("result");
            arr = obj.getJSONArray("list");
            Wechat wechat;
            for (int i = 0; i < arr.length(); i++) {
                obj = arr.getJSONObject(i);
                wechat = new Wechat();
                if (obj.getString("firstImg").equals("")) {
                    continue;
                }
                wechat.setFirstImg(obj.getString("firstImg"));
                wechat.setTitle(obj.getString("title"));
                wechat.setSource(obj.getString("source"));
                wechat.setUrl(obj.getString("url"));
                wechatList.add(wechat);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WechatAdapter mAdapter = new WechatAdapter(getActivity(), wechatList);
        mListView.setAdapter(mAdapter);
        new ListViewHightHelper(mListView).setListViewHeightBasedOnChildren();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("localurl", wechatList.get(position).getUrl());
        intent.putExtra("localtitle", wechatList.get(position).getTitle());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getActivity(), NoteActivity.class));
    }
}