package com.leday.Controller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leday.R;

import java.util.List;

public class StarAdapter extends BaseAdapter {

    private List<String> mList;
    private LayoutInflater mInflater;

    public StarAdapter(Context mContext, List<String> mList) {
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
            convertView = mInflater.inflate(R.layout.item_star, null);
            viewHold.mImg = (ImageView) convertView.findViewById(R.id.item_star_img);
            viewHold.mContent = (TextView) convertView.findViewById(R.id.item_star_content);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }
        switch (position) {
            case 0:
                viewHold.mImg.setImageResource(R.drawable.star_1);
                break;
            case 1:
                viewHold.mImg.setImageResource(R.drawable.star_2);
                break;
            case 2:
                viewHold.mImg.setImageResource(R.drawable.star_3);
                break;
            case 3:
                viewHold.mImg.setImageResource(R.drawable.star_4);
                break;
            case 4:
                viewHold.mImg.setImageResource(R.drawable.star_5);
                break;
            case 5:
                viewHold.mImg.setImageResource(R.drawable.star_6);
                break;
            case 6:
                viewHold.mImg.setImageResource(R.drawable.star_7);
                break;
            case 7:
                viewHold.mImg.setImageResource(R.drawable.star_8);
                break;
            case 8:
                viewHold.mImg.setImageResource(R.drawable.star_9);
                break;
            case 9:
                viewHold.mImg.setImageResource(R.drawable.star_10);
                break;
            case 10:
                viewHold.mImg.setImageResource(R.drawable.star_11);
                break;
            case 11:
                viewHold.mImg.setImageResource(R.drawable.star_12);
                break;
        }
        viewHold.mContent.setText(mList.get(position));
        return convertView;
    }

    private class ViewHold {
        private TextView mContent;
        private ImageView mImg;
    }
}