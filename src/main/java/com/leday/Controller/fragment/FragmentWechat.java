package com.leday.Controller.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.leday.BaseFragment;
import com.leday.Common.Constant;
import com.leday.Controller.adapter.WechatAdapter;
import com.leday.Model.Wechat;
import com.leday.R;
import com.leday.Util.HttpUtil;
import com.leday.Util.Interface.HttpInterface;
import com.leday.Util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class FragmentWechat extends BaseFragment implements XRecyclerView.LoadingListener {

    private XRecyclerView mRecyclerView;
    private WechatAdapter mAdapter;
    private List<Wechat> wechatList = new ArrayList<>();

    private static final String TAG_FRAGMENT_C = "fragmentc";
    private int page_num = 1;

    private DisplayMetrics mDisplayMetric;

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
        HttpUtil.removeTag(TAG_FRAGMENT_C);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wechat, container, false);
        initDisplay();
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
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.header_view, (ViewGroup) view.findViewById(android.R.id.content), false);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = mDisplayMetric.widthPixels;
        params.height = mDisplayMetric.widthPixels / 5 * 2;
        headerView.setLayoutParams(params);
        mRecyclerView.addHeaderView(headerView);
    }

    private void initDisplay() {
        mDisplayMetric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetric);
        LogUtil.e("i",
                mDisplayMetric.heightPixels + "," + mDisplayMetric.widthPixels + "。dpi（X和Y是应该是相同的）xdpi= "
                        + mDisplayMetric.xdpi + ",ydpi = " + mDisplayMetric.ydpi + "。desityx（x的总密度）："
                        + mDisplayMetric.densityDpi + "，密度" + mDisplayMetric.density);
    }

    /**
     * 请求数据
     */
    private void requestData(final int code) {
        HttpUtil.getRequest(Constant.URL_WECHAT + "&pno=" + page_num, TAG_FRAGMENT_C, new HttpInterface() {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                Dosuccess(result, code);
            }
        });
    }

    /**
     * 请求成功后对data结果进行处理
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