//package com.leday.activity;
//
//import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.leday.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Administrator on 2016/10/26.
// */
//public class Note_Activity extends BaseActivity {
//
//    private RecyclerView mRecyclerView;
//    private FloatingActionButton mFloatBtn;
//
//    private List<String> mDatas = new ArrayList<>();
//    private RecyclerAdapter mAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_note);
//
//        initView();
//        initData();
//    }
//
//    private void initView() {
//        mRecyclerView = (RecyclerView) findViewById(R.id.recycview_activity_note);
//        mFloatBtn = (FloatingActionButton) findViewById(R.id.ftn_activity_note);
//    }
//
//    private void initData() {
//        for (int i = 'A'; i < 'z'; i++) {
//            mDatas.add("" + (char) i);
//        }
//        mAdapter = new RecyclerAdapter();
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 8));
//        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//    }
//
//    public void doChange(View view) {
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//    }
//
//    //内部类Adapter
//    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
//
//        @Override
//        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(Note_Activity.this).inflate(R.layout.item_recycler, parent, false));
//            return holder;
//        }
//
//        @Override
//        public void onBindViewHolder(MyViewHolder holder, int position) {
//            holder.tv.setText(mDatas.get(position));
//        }
//
//        @Override
//        public int getItemCount() {
//            return mDatas.size();
//        }
//
//        class MyViewHolder extends RecyclerView.ViewHolder {
//            TextView tv;
//
//            public MyViewHolder(View view) {
//                super(view);
//                tv = (TextView) view.findViewById(R.id.id_num);
//            }
//        }
//    }
//}
