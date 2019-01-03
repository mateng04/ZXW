package com.mobile.zxw.myapplication.bean;

import java.util.List;

public class AdverContentBean {

    private String title;
    private String content;
    private String click;
    private String daoqishijian;
    private String del;
    private String state;
    private List<String> imgs;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public String getDaoqishijian() {
        return daoqishijian;
    }

    public void setDaoqishijian(String daoqishijian) {
        this.daoqishijian = daoqishijian;
    }

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }
}
