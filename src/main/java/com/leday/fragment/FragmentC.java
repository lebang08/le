package com.leday.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.leday.Impl.ListViewHightImpl;
import com.leday.R;
import com.leday.Util.LogUtil;
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

public class FragmentC extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private FloatingActionButton mFtn;

    private ListView mListView;
    private List<Wechat> wechatList = new ArrayList<>();

    private static final String URL_WECHAT = "http://v.juhe.cn/weixin/query?key=4d8f538fca6369950978621cf6287bde";

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
        StringRequest filmrequest = new StringRequest(Request.Method.GET, URL_WECHAT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Dosuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("Wrong-BACK", "联接错误原因： " + error.getMessage());
            }
        });
        filmrequest.setTag("fragmentc");
        MyApplication.getHttpQueue().add(filmrequest);
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
        new ListViewHightImpl(mListView).setListViewHeightBasedOnChildren();
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