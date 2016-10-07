package com.leday.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/8
 */
public class Today implements Serializable {

    private String date;
    private String e_id;
    private String title;

    public Today() {
    }

    public Today(String day, String date, String e_id, String title) {
        this.date = date;
        this.e_id = e_id;
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getE_id() {
        return e_id;
    }

    public void setE_id(String e_id) {
        this.e_id = e_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Today{" +
                ", date='" + date + '\'' +
                ", e_id='" + e_id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}