package com.mobile.zxw.myapplication.jsonEntity;

import com.mobile.zxw.myapplication.bean.RecruitBean;

import java.util.List;

public class EntityRecruit {

    private String code;
    private List<RecruitBean> data;
    private int countPage;
    private int currtPage;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<RecruitBean> getData() {
        return data;
    }

    public void setData(List<RecruitBean> data) {
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
