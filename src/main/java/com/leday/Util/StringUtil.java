package com.leday.Util;

/**
 * Created by Administrator on 2017/3/20
 */
public class StringUtil {

    public static String transferString(String orginal) {
        return orginal.replace("\"", "“")
                .replace("\'", "‘");
    }
}