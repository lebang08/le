package com.leday.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leday.R;
import com.leday.activity.NoteDetailActivity;
import com.leday.entity.Note;

import java.util.ArrayList;

public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<NoteRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Note> mList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public NoteRecyclerViewAdapter(Context context, ArrayList<Note> list) {
        this.mList = list;
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public NoteRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = mLayoutInflater.inflate(R.layout.item_note, parent, false);
        return new MyViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(NoteRecyclerViewAdapter.MyViewHolder holder, final int position) {
        holder.mTime.setText(mList.get(position).getTime());
        holder.mContent.setText(mList.get(position).getTitle());
        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NoteDetailActivity.class);
                intent.putExtra("local_date", mList.get(position).getTime());
                intent.putExtra("local_title", mList.get(position).getTitle());
                intent.putExtra("local_content", mList.get(position).getContent());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mItemView;
        TextView mContent, mTime;

        public MyViewHolder(View view) {
            super(view);
            mItemView = (LinearLayout) view.findViewById(R.id.itemview_activity_note);
            mTime = (TextView) view.findViewById(R.id.item_note_time);
            mContent = (TextView) view.findViewById(R.id.item_note_content);
        }
    }
}