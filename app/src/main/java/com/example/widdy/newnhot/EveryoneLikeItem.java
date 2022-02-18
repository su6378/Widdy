package com.example.widdy.newnhot;

public class EveryoneLikeItem {

    private String url;
    private String title;
    private String sub_title;
    private String detail;
    private String tag;

    public EveryoneLikeItem(String url, String title, String sub_title, String detail, String tag) {
        this.url = url;
        this.title = title;
        this.sub_title = sub_title;
        this.detail = detail;
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return sub_title;
    }

    public String getDetail() {
        return detail;
    }

    public String getTag() {
        return tag;
    }
}
