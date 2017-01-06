package com.leday.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leday.R;
import com.leday.entity.Note;

import java.util.ArrayList;

public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<NoteRecyclerViewAdapter.MyViewHolder> {

    public static final int TYPE_FOOTER = 1;  //说明是带有Footer的
    public static final int TYPE_NORMAL = 2;  //说明是不带有header和footer的

    private ArrayList<Note> mList;
    private LayoutInflater mLayoutInflater;

    private View mFooterView;

    public NoteRecyclerViewAdapter(Context context, ArrayList<Note> list) {
        this.mList = list;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void setFooterView(View view) {
        this.mFooterView = view;
        notifyItemInserted(getItemCount() - 1);
    }

    public View getFooterView() {
        return mFooterView;
    }

    @Override
    public int getItemViewType(int position) {
        if (mFooterView == null) {
            return TYPE_NORMAL;
        }
        if (position == getItemCount() - 1) {
            //最后一个,应该加载Footer
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public NoteRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mFooterView != null && viewType == TYPE_FOOTER) {
            return new MyViewHolder(mFooterView);
        }
        View layout = mLayoutInflater.inflate(R.layout.item_note, parent, false);
        return new MyViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(NoteRecyclerViewAdapter.MyViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            holder.mTime.setText(mList.get(position).getTime());
            holder.mContent.setText(mList.get(position).getTitle());
        }
    }

    @Override
    public int getItemCount() {
        if (mFooterView != null) {
            return mList.size() + 1;
        }
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mContent, mTime;

        public MyViewHolder(View view) {
            super(view);
            if (view == mFooterView) {
                return;
            }
            mTime = (TextView) view.findViewById(R.id.item_note_time);
            mContent = (TextView) view.findViewById(R.id.item_note_content);
        }
    }
}