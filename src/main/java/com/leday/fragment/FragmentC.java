package com.leday.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.leday.Common.Constant;
import com.leday.Interface.VolleyInterface;
import com.leday.R;
import com.leday.Util.VolleyUtils;
import com.leday.View.ListViewHightHelper;
import com.leday.activity.NoteActivity;
import com.leday.activity.WebViewActivity;
import com.leday.adapter.WechatAdapter;
import com.leday.application.MyApplication;
import com.leday.entity.Wechat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentC extends BaseFragment implements
        AdapterView.OnItemClickListener, View.OnClickListener, VolleyInterface {

    private FloatingActionButton mFtn;

    private ListView mListView;
    private List<Wechat> wechatList = new ArrayList<>();

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getHttpQueue().cancelAll("fragmentc");
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

    private void initEvent() {
        //请求数据
        progressShow(getActivity());
        new VolleyUtils(getActivity(), this).GetRequest(Constant.URL_WECHAT, "fragmentc");
    }

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

    @Override
    public void onSuccess(String response) {
        Dosuccess(response);
        progressCancel();
    }

    @Override
    public void onFail(VolleyError error) {
        progressCancel();
    }
}