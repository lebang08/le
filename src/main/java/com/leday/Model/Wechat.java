package com.leday.Model;

/**
 * Created by Administrator on 2016/6/23
 */
public class Wechat {
    private String id;
    private String title;
    private String source;
    private String firstImg;
    private String mark;
    private String url;

    public Wechat() {
    }

    public Wechat(String id, String title, String source, String firstImg, String mark, String url) {
        this.id = id;
        this.title = title;
        this.source = source;
        this.firstImg = firstImg;
        this.mark = mark;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFirstImg() {
        return firstImg;
    }

    public void setFirstImg(String firstImg) {
        this.firstImg = firstImg;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Wechat{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", source='" + source + '\'' +
                ", firstImg='" + firstImg + '\'' +
                ", mark='" + mark + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

