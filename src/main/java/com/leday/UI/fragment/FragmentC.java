package com.leday.UI.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.leday.BaseFragment;
import com.leday.Common.Constant;
import com.leday.R;
import com.leday.UI.adapter.WechatAdapter;
import com.leday.entity.Wechat;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class FragmentC extends BaseFragment implements XRecyclerView.LoadingListener {

    private XRecyclerView mRecyclerView;
    private WechatAdapter mAdapter;
    private List<Wechat> wechatList = new ArrayList<>();

    private static final String TAG_FRAGMENT_C = "fragmentc";
    private int page_num = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.REFRESH_DATA:
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.refreshComplete();
                    break;
                case Constant.LOAD_MORE_DATA:
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.loadMoreComplete();
                    break;
            }
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        OkGo.getInstance().cancelTag(TAG_FRAGMENT_C);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_c, container, false);
        initView(view);
        requestData(Constant.REFRESH_DATA);
        return view;
    }

    private void initView(View view) {
        mRecyclerView = (XRecyclerView) view.findViewById(R.id.listview_fragment_c);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new WechatAdapter(getActivity(), wechatList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLoadingListener(this);
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.header_view, null, false);
        mRecyclerView.addHeaderView(headerView);
    }

    /**
     * 请求数据
     */
    private void requestData(final int code) {
        OkGo.get(Constant.URL_WECHAT + "&pno=" + page_num)
                .tag(TAG_FRAGMENT_C)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Dosuccess(s, code);
                    }
                });
    }

    /**
     * 请求成功后对data结果进行处理
     *
     * @param response
     */
    private void Dosuccess(String response, int code) {
        JSONObject obj;
        JSONArray arr;
        try {
            obj = new JSONObject(response);
            obj = obj.getJSONObject("result");
            arr = obj.getJSONArray("list");
            Wechat wechat;
            if (code == Constant.REFRESH_DATA) {
                wechatList.clear();
            }
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
                Message msg = new Message();
                if (code == Constant.REFRESH_DATA) {
                    msg.what = Constant.REFRESH_DATA;
                }
                if (code == Constant.LOAD_MORE_DATA) {
                    msg.what = Constant.LOAD_MORE_DATA;
                }
                msg.obj = wechatList;
                mHandler.sendMessageDelayed(msg, 500);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        page_num = 1;
        requestData(Constant.REFRESH_DATA);
    }

    @Override
    public void onLoadMore() {
        page_num++;
        requestData(Constant.LOAD_MORE_DATA);
    }
}