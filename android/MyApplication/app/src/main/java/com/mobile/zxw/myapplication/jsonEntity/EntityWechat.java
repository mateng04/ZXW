package com.mobile.zxw.myapplication.jsonEntity;

import com.mobile.zxw.myapplication.bean.WechatBean;

import java.util.List;

public class EntityWechat {

    private String code;
    private List<WechatBean> data;
    private int countPage;
    private int currtPage;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<WechatBean> getData() {
        return data;
    }

    public void setData(List<WechatBean> data) {
        this.data = data;
    }

    public int getCountPage() {
        return countPage;
    }

    public void setCountPage(int countPage) {
        this.countPage = countPage;
    }

    public int getCurrtPage() {
        return currtPage;
    }

    public void setCurrtPage(int currtPage) {
        this.currtPage = currtPage;
    }
}
