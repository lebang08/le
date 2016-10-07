package com.leday.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.leday.Impl.ListViewHightImpl;
import com.leday.R;
import com.leday.Util.LogUtil;
import com.leday.activity.TodayActivity;
import com.leday.application.MyApplication;
import com.leday.entity.Today;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentA extends Fragment implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private List<String> mDataList = new ArrayList<>();
    private List<Today> mTodayList = new ArrayList<>();

    private Calendar mCalendar = Calendar.getInstance();
    private static final String URL_TODAY = "http://v.juhe.cn/todayOnhistory/queryEvent.php?key=776cbc23ec84837a647a7714a0f06bff&date=";

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
        mListView = (ListView) view.findViewById(R.id.listview_fragment_a);

        mListView.setOnItemClickListener(this);
    }

    public void getJson() {
        //获取时间,月和日
        int localMonth = mCalendar.get(Calendar.MONTH);
        int localDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        String URL_TOTAL = URL_TODAY + (localMonth + 1) + "/" + localDay;
        //请求网络
        StringRequest todayrequest = new StringRequest(Request.Method.GET, URL_TOTAL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Dosuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        todayrequest.setTag("fragmenta");
        MyApplication.getHttpQueue().add(todayrequest);
    }

    //请求成功的处理
    private void Dosuccess(String response) {
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
                today = new Today();
                today.setDate(obj.getString("date"));
                today.setTitle(obj.getString("title"));
                today.setE_id(obj.getString("e_id"));
                merge = (i + 1) + "、 " + today.getDate() + ": " + today.getTitle();
                mDataList.add(merge);
                mTodayList.add(today);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter mAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mDataList);
        mListView.setAdapter(mAdapter);
        new ListViewHightImpl(mListView).setListViewHeightBasedOnChildren();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), TodayActivity.class);
        intent.putExtra("local_id", mTodayList.get(position).getE_id());
        intent.putExtra("local_title", mTodayList.get(position).getTitle());
        intent.putExtra("local_date", mTodayList.get(position).getDate());
        startActivity(intent);
    }
}