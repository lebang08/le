package com.leday.entity;

import java.sql.Date;

/**
 * Created by Administrator on 2016/10/27.
 */
public class Note {

    private Date date;
    private String time;
    private String Title;
    private String Content;

    public Note() {
    }

    public Note(Date date, String time, String title, String content) {
        this.date = date;
        this.time = time;
        Title = title;
        Content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    @Override
    public String toString() {
        return "Note{" +
                "date=" + date +
                ", time='" + time + '\'' +
                ", Title='" + Title + '\'' +
                ", Content='" + Content + '\'' +
                '}';
    }
}
