package com.leday;

import android.support.v4.app.Fragment;

/**
 * Created by apple on 2017/5/10s
 */
public abstract class LazyFragment extends BaseFragment {
    protected boolean isVisible;

    /**
     * 在这里实现Fragment数据的缓加载.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected abstract void lazyLoad();


    protected void onInvisible() {
    }
}
