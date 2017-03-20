package com.leday.Controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leday.Controller.activity.WebViewActivity;
import com.leday.Model.Wechat;
import com.leday.R;
import com.leday.Util.LogUtil;

import java.util.List;

public class WechatAdapter extends RecyclerView.Adapter<WechatAdapter.ViewHolder> {

    private List<Wechat> mList;
    private LayoutInflater mInflaterLayout;
    private Context mContext;

    public WechatAdapter(Context mContext, List<Wechat> mList) {
        this.mList = mList;
        this.mInflaterLayout = LayoutInflater.from(mContext);
        this.mContext = mContext;
    }

    @Override
    public WechatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = mInflaterLayout.inflate(R.layout.item_wechat, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(mContext).load(mList.get(position).getFirstImg()).crossFade(500)
                .error(R.mipmap.img_threepoint).placeholder(R.mipmap.img_threepoint)
                .into(holder.mImg);
        holder.mTitle.setText((position + 1) + "、 " + mList.get(position).getTitle());
        holder.mAuthor.setText("来自微信: " + mList.get(position).getSource());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra("local_wechat_web", mList.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImg;
        TextView mTitle, mAuthor;

        public ViewHolder(View view) {
            super(view);
            mImg = (ImageView) view.findViewById(R.id.item_wechat_img);
            mTitle = (TextView) view.findViewById(R.id.item_wechat_title);
            mAuthor = (TextView) view.findViewById(R.id.item_wechat_author);
        }
    }
}