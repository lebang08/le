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

    private Context mContext;
    private ArrayList<Note> mList;
    private LayoutInflater mLayoutInflater;

    public NoteRecyclerViewAdapter(Context context, ArrayList<Note> list) {
        this.mContext = context;
        this.mList = list;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public NoteRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewholder = new MyViewHolder(mLayoutInflater.inflate(R.layout.item_note, parent, false));
        return viewholder;
    }

    @Override
    public void onBindViewHolder(NoteRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.mTime.setText(mList.get(position).getTime());
        holder.mContent.setText(mList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mContent, mTime;

        public MyViewHolder(View view) {
            super(view);
            mTime = (TextView) view.findViewById(R.id.item_note_time);
            mContent = (TextView) view.findViewById(R.id.item_note_content);
        }
    }
}
