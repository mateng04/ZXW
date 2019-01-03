package com.mobile.zxw.myapplication.jsonEntity;

import com.mobile.zxw.myapplication.bean.JobBean;

import java.util.List;

public class EntityJob {

    private String code;
    private List<JobBean> data;
    private int countPage;
    private int currtPage;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<JobBean> getData() {
        return data;
    }

    public void setData(List<JobBean> data) {
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
