package com.leday.Util;

import android.util.Log;
import com.leday.entity.Talk;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TalkHttpUtils {
    private static final String URL = "http://op.juhe.cn/robot/index?key=98209c7c466333813006983278b34438&info=";
    /**
     * 发送一个消息，得到返回的消息
     * @param msg
     * @return talk
     */
    public static Talk sendMessage(String msg) {
        Talk talk = new Talk();
        String stringresult = doGet(msg);
        JSONObject obj;
        try {
            obj = new JSONObject(stringresult);
            Log.e("linx", "obj = " + obj);
            obj = obj.getJSONObject("result");
            String localmsg = obj.getString("text");
            talk.setMsg(localmsg);
        } catch (JSONException e) {
            talk.setMsg("网络不佳，再试试呗");
        }
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String localdate = dateformat.format(new Date());
        talk.setTime(localdate);
        talk.setType(Talk.Type.INCOMING);
        return talk;
    }

    private static String doGet(String msg) {
        String result = "";
        String url = URL + msg;
        ByteArrayOutputStream baos = null;
        InputStream is = null;
        try {
            java.net.URL urlNet = new java.net.URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlNet
                    .openConnection();
            conn.setReadTimeout(3 * 1000);
            conn.setConnectTimeout(3 * 1000);
            conn.setRequestMethod("GET");
            is = conn.getInputStream();
            int len = -1;
            byte[] buf = new byte[128];
            baos = new ByteArrayOutputStream();

            while ((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            baos.flush();
            result = new String(baos.toByteArray());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}