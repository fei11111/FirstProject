package com.fei.firstproject.entity;

/**
 * Created by Administrator on 2017/8/14.
 */

public class NcwEntity {

    private String catid;
    private String catname;
    private String description;
    private String id;
    private String inputtime;
    private String thumb;
    private String title;
    private String url;

    public String getCatid() {
        return catid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInputtime() {
        return inputtime;
    }

    public void setInputtime(String inputtime) {
        this.inputtime = inputtime;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "NcwEntity{" +
                "catid='" + catid + '\'' +
                ", catname='" + catname + '\'' +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", inputtime='" + inputtime + '\'' +
                ", thumb='" + thumb + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
