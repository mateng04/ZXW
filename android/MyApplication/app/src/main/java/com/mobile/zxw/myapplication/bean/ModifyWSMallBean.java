package com.mobile.zxw.myapplication.bean;

import java.util.List;

public class ModifyWSMallBean {

    public String youxiaoqi;
    public String title;
    public String content;
    public String erweima;
    public String weixin;
    public List<String> imgs;

    public String getYouxiaoqi() {
        return youxiaoqi;
    }

    public void setYouxiaoqi(String youxiaoqi) {
        this.youxiaoqi = youxiaoqi;
    }

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

    public String getErweima() {
        return erweima;
    }

    public void setErweima(String erweima) {
        this.erweima = erweima;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }
}
