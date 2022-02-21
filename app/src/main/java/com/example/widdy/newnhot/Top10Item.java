package com.example.widdy.newnhot;

public class Top10Item {

    private String url;
    private String title;
    private String sub_title;
    private String detail;
    private String tag;
    private String ranking;

    public Top10Item(String url, String title, String sub_title, String detail, String tag,String ranking) {
        this.url = url;
        this.title = title;
        this.sub_title = sub_title;
        this.detail = detail;
        this.tag = tag;
        this.ranking = ranking;
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

    public String getRanking() {
        return ranking;
    }
}
