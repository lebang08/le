package com.leday.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/2/15
 */
public class DbUtil {

    public static DbHelper getHelper(Context context, String path) {
        return new DbHelper(context, path);
    }

    //API方法,返回新增成功的 row ID,若发生错误返回-1
    public static long insert(SQLiteDatabase database, String table, ContentValues values) {
        return database.insert(table, null, values);
    }

    public static String queryToString(SQLiteDatabase database, String table, String column, String condition, String condition_value) {
        String target = "none";
        Cursor cursor = database.query(table, new String[]{column}, condition + "= \"" + condition_value + "\"", null, null, null, null);
        //开启事务批量操作
        if (cursor != null) {
            while (cursor.moveToNext()) {
                target = cursor.getString(0);
                cursor.close();
                return target;
            }
            cursor.close();
        }
        return target;
    }

    //重载方法,还可以加group,order,having子句条件
    public static ArrayList<String> queryToArrayList(SQLiteDatabase database, String table, String order_by, String column_name) {
        ArrayList<String> mList = new ArrayList<>();
        Cursor cursor = database.query(table, null, null, null, null, null, order_by);
        //开启事务批量操作
        database.beginTransaction();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String target = cursor.getString(cursor.getColumnIndex(column_name));
                mList.add(target);
            }
            cursor.close();
        }
        //批量操作成功,关闭事务
        database.setTransactionSuccessful();
        database.endTransaction();
        return mList;
    }

    public static ArrayList<String> queryToArrayList(SQLiteDatabase database, String table, String column, String condition, String value, String order_by) {
        ArrayList<String> mList = new ArrayList<>();
        Cursor cursor = database.query(table, new String[]{column}, condition, new String[]{"\"" + value + "\""}, null, null, order_by);
        //开启事务批量操作
        database.beginTransaction();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String target = cursor.getString(0);
                mList.add(target);
            }
            cursor.close();
        }
        //批量操作成功,关闭事务
        database.setTransactionSuccessful();
        database.endTransaction();
        return mList;
    }

    public static ArrayList<String> queryToArrayList(SQLiteDatabase database, String table, String column, String condition, String condition_value) {
        ArrayList<String> mList = new ArrayList<>();
        Cursor cursor = database.query(table, new String[]{column}, condition, new String[]{condition_value}, null, null, null);
        //开启事务批量操作
        database.beginTransaction();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String target = cursor.getString(0);
                mList.add(target);
            }
            cursor.close();
        }
        //批量操作成功,关闭事务
        database.setTransactionSuccessful();
        database.endTransaction();
        return mList;
    }

    public static ArrayList<String> cursorToArrayList(Cursor cursor) {
        ArrayList<String> mList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String target = cursor.getString(0);
                mList.add(target);
            }
            cursor.close();
        }
        return mList;
    }

    public static String cursorToString(Cursor cursor) {
        String target = "none";
        if (cursor != null) {
            while (cursor.moveToNext()) {
                target = cursor.getString(0);
            }
            cursor.close();
        }
        return target;
    }

    public static String cursorToNotNullString(Cursor cursor) {
        String target = "none";
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (TextUtils.isEmpty(cursor.getString(0))) {
                    cursor.close();
                    return target;
                }
                target = cursor.getString(0);
            }
            cursor.close();
        }
        return target;
    }
}