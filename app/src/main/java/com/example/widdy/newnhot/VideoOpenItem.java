package com.example.widdy.newnhot;

public class VideoOpenItem {
    private String url;
    private String month;
    private String day;
    private String open_title;
    private String detail_text;
    private String tag;
    private String title;

    public VideoOpenItem(String url, String month, String day, String title, String open_title, String detail_text, String tag) {

        this.url = url;
        this.month = month;
        this.day = day;
        this.open_title = open_title;
        this.detail_text = detail_text;
        this.tag = tag;
        this.title = title;

    }

    public String getUrl() {
        return url;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getTitle() {
        return title;
    }

    public String getOpenTitle() {
        return open_title;
    }

    public String getDetailText() {
        return detail_text;
    }

    public String getTag() {
        return tag;
    }

}
