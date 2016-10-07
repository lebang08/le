package com.leday.Util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/6/24
 */
public class ToastUtil {

    private ToastUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    //控制是否允许Toast
    public static boolean isShow = true;

    //19行到34行是使Toast可以即时刷新的方法，其余的是鸿洋的方法，控制全局是否允许Toast
    private static Toast toast = null;

    public static void showMessage(Context context, String msg) {
        if (isShow)
            showMessage(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showMessage(Context context, final String msg, final int duration) {
        if (isShow) {
            if (toast == null)
                toast = Toast.makeText(context, msg, duration);
            else {
                toast.setText(msg);
                toast.setDuration(duration);
            }
            toast.show();
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, int message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, CharSequence message, int duration) {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration) {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }
}
