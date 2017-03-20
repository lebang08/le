package com.leday.Common;

/**
 * Created by LeBang on 2016/12/30
 */
public class Constant {

    public static final int REFRESH_DATA = 0;
    public static final int LOAD_MORE_DATA = 1;

    public static final int DATABASE_VERSION = 1;

    public static final String NONE = "none";

    //数据库名
    public static final String DATABASE_LEBANG = "LeBang.db";
    //数据表名
    public static final String TABLE_SQLITE_MASTER = "sqlite_master";   //sql系统表
    public static final String TABLE_TODAY = "le_today_table";
    public static final String TABLE_WECHAT = "le_wechat_table";
    //数据表字段
    public static final String COLUMN_NAME = "name";            //sql系统表中的字段
    public static final String COLUMN_TABLE_NAME = "tbl_name";  //sql系统表中的字段
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_URL = "url";

    //今时今往
    public static final String URL_TODAY = "http://v.juhe.cn/todayOnhistory/queryEvent.php?key=776cbc23ec84837a647a7714a0f06bff&date=";

    //星座
    public static final String URL_STAR = "http://web.juhe.cn:8080/constellation/getAll?key=c86828899c7c2b9cd39281ee48f90105&consName=";

    //微信精选
    public static final String URL_WECHAT = "http://v.juhe.cn/weixin/query?key=4d8f538fca6369950978621cf6287bde";

    //图灵机器人
    public static final String URL_TALK = "http://op.juhe.cn/robot/index?key=98209c7c466333813006983278b34438&info=";
}
