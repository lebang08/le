package com.leday.UI.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leday.R;
import com.leday.entity.Talk;

import java.util.List;

public class TalkAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Talk> mDatas;

    public TalkAdapter(Context context, List<Talk> mDatas) {
        mInflater = LayoutInflater.from(context);
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        Talk talk = mDatas.get(position);
        if (talk.getType() == Talk.Type.INCOMING) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Talk talk = mDatas.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            // 通过ItemType设置不同的布局
            if (getItemViewType(position) == 0) {
                convertView = mInflater.inflate(R.layout.item_from_msg, parent,
                        false);
                viewHolder = new ViewHolder();
                viewHolder.mDate = (TextView) convertView
                        .findViewById(R.id.id_form_msg_date);
                viewHolder.mMsg = (TextView) convertView
                        .findViewById(R.id.id_from_msg_info);
            } else {
                convertView = mInflater.inflate(R.layout.item_to_msg, parent,
                        false);
                viewHolder = new ViewHolder();
                viewHolder.mDate = (TextView) convertView
                        .findViewById(R.id.id_to_msg_date);
                viewHolder.mMsg = (TextView) convertView
                        .findViewById(R.id.id_to_msg_info);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 设置数据
        viewHolder.mDate.setText(talk.getTime());
        viewHolder.mMsg.setText(talk.getMsg());
        return convertView;
    }

    private class ViewHolder {
        TextView mDate;
        TextView mMsg;
    }
}