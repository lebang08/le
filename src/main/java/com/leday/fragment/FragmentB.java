package com.leday.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.leday.View.ListViewHightImpl;
import com.leday.R;
import com.leday.activity.NoteActivity;
import com.leday.activity.StarActivity;
import com.leday.adapter.StarAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentB extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView mListView;
    private List<String> mData = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b, container, false);

        initView(view);
        DoEvent();
        return view;
    }

    private void initView(View view) {
        FloatingActionButton mFab = (FloatingActionButton) view.findViewById(R.id.fab_fragment_b);
        mListView = (ListView) view.findViewById(R.id.listview_fragment_b);

        mFab.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
    }

    private void DoEvent() {
        mData.add("白羊座 ： 3月21日-4月19日");
        mData.add("金牛座 ： 4月20日-5月20日");
        mData.add("双子座 ： 5月21日-6月21日");
        mData.add("巨蟹座 ： 6月22日-7月22日");
        mData.add("獅子座 ： 7月23日-8月22日");
        mData.add("处女座 ： 8月23日-9月22日");
        mData.add("天秤座 ： 9月23日-10月23日");
        mData.add("天蝎座 ： 10月24日-11月22日");
        mData.add("射手座 ： 11月23日-12月21日");
        mData.add("摩羯座 ： 12月22日-1月19日");
        mData.add("水瓶座 ： 1月20日-2月18日");
        mData.add("双鱼座 ： 2月19日-3月20日");
        StarAdapter mAdapter = new StarAdapter(getActivity(), mData);
        mListView.setAdapter(mAdapter);
        new ListViewHightImpl(mListView).setListViewHeightBasedOnChildren();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), StarActivity.class);
        intent.putExtra("imgId", position);
        switch (position) {
            case 0:
                intent.putExtra("star", "白羊座");
                startActivity(intent);
                break;
            case 1:
                intent.putExtra("star", "金牛座");
                startActivity(intent);
                break;
            case 2:
                intent.putExtra("star", "双子座");
                startActivity(intent);
                break;
            case 3:
                intent.putExtra("star", "巨蟹座");
                startActivity(intent);
                break;
            case 4:
                intent.putExtra("star", "狮子座");
                startActivity(intent);
                break;
            case 5:
                intent.putExtra("star", "处女座");
                startActivity(intent);
                break;
            case 6:
                intent.putExtra("star", "天秤座");
                startActivity(intent);
                break;
            case 7:
                intent.putExtra("star", "天蝎座");
                startActivity(intent);
                break;
            case 8:
                intent.putExtra("star", "射手座");
                startActivity(intent);
                break;
            case 9:
                intent.putExtra("star", "摩羯座");
                startActivity(intent);
                break;
            case 10:
                intent.putExtra("star", "水瓶座");
                startActivity(intent);
                break;
            case 11:
                intent.putExtra("star", "双鱼座");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getActivity(),NoteActivity.class));
    }
}