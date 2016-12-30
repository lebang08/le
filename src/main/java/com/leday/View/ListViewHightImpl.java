package com.leday.View;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.leday.View.ListViewHightHelper;

/**
 * Created by Administrator on 2016/6/8.
 * 解决嵌套ListView 和 Scrollview的问题
 */
public class ListViewHightImpl implements ListViewHightHelper {

    private ListView mListView;

    public ListViewHightImpl(ListView listview) {
        mListView = listview;
    }

    @Override
    public void setListViewHeightBasedOnChildren() {
        ListAdapter listAdapter = mListView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, mListView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = totalHeight + (mListView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        mListView.setLayoutParams(params);
    }
}