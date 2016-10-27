package com.leday.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leday.R;
import com.leday.entity.Note;

import java.util.List;

public class NoteAdapter extends BaseAdapter {

    private List<Note> mList;
    private LayoutInflater mInflater;

    public NoteAdapter(Context mContext, List<Note> mList) {
        this.mList = mList;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold viewHold;
        if (convertView == null) {
            viewHold = new ViewHold();
            convertView = mInflater.inflate(R.layout.item_note, null);
            viewHold.mTime = (TextView) convertView.findViewById(R.id.item_note_time);
            viewHold.mContent = (TextView) convertView.findViewById(R.id.item_note_content);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }
        viewHold.mTime.setText(mList.get(position).getTime());
        viewHold.mContent.setText("\r\r\r\r" + mList.get(position).getContent());
        return convertView;
    }

    private class ViewHold {
        private TextView mContent, mTime;
        private ImageView mImg;
    }
}