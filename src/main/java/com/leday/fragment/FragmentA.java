package com.leday.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.leday.Common.Constant;
import com.leday.R;
import com.leday.Util.LogUtil;
import com.leday.View.ListViewHightHelper;
import com.leday.activity.NoteActivity;
import com.leday.activity.TodayActivity;
import com.leday.application.MyApplication;
import com.leday.entity.Today;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentA extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private FloatingActionButton mFtn;

    private ListView mListView;
    private List<String> mDataList = new ArrayList<>();
    private List<Today> mTodayList = new ArrayList<>();

    private Calendar mCalendar = Calendar.getInstance();

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getHttpQueue().cancelAll("fragmenta");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a, container, false);

        initView(view);
        getJson();
        return view;
    }

    private void initView(View view) {
        mFtn = (FloatingActionButton) view.findViewById(R.id.fab_fragment_a);
        mFtn.setOnClickListener(this);

        mListView = (ListView) view.findViewById(R.id.listview_fragment_a);
        mListView.setOnItemClickListener(this);
    }

    public void getJson() {
        progressShow(getActivity());
        //获取时间,月和日
        int localMonth = mCalendar.get(Calendar.MONTH);
        int localDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        //请求网络
        StringRequest todayrequest = new StringRequest(Request.Method.GET,
                Constant.URL_TODAY + (localMonth + 1) + "/" + localDay, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Dosuccess(response);
                progressCancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressCancel();
            }
        });
        todayrequest.setTag("fragmenta");
        MyApplication.getHttpQueue().add(todayrequest);
    }

    //请求成功的处理
    private void Dosuccess(String response) {
        Gson gson = new Gson();

        JSONObject obj;
        JSONArray arr;
        Today today;
        String merge;
        try {
            obj = new JSONObject(response);
            LogUtil.e("URL_TOTAL", obj.toString());
            arr = obj.getJSONArray("result");
            LogUtil.e("URL_TOTAL", arr.toString());
            for (int i = 0; i <= arr.length(); i++) {
                obj = arr.getJSONObject(i);
                //Gson解析对象
                today = gson.fromJson(obj.toString(), Today.class);
                merge = (i + 1) + "、 " + today.getDate() + ": " + today.getTitle();
                mDataList.add(merge);
                mTodayList.add(today);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter mAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mDataList);
        mListView.setAdapter(mAdapter);
        new ListViewHightHelper(mListView).setListViewHeightBasedOnChildren();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), TodayActivity.class);
        intent.putExtra("local_id", mTodayList.get(position).getE_id());
        intent.putExtra("local_title", mTodayList.get(position).getTitle());
        intent.putExtra("local_date", mTodayList.get(position).getDate());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getActivity(), NoteActivity.class));
    }
}