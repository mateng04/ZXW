package com.mobile.zxw.myapplication.jsonEntity;

import com.mobile.zxw.myapplication.bean.HomeQZBean;
import com.mobile.zxw.myapplication.bean.HomeQZJZBean;

import java.util.List;

public class EntityHomeJob {

    private String code;
    private List<HomeQZBean> jobQuanZhi;
    private List<HomeQZJZBean> jobJianZhi;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<HomeQZBean> getJobQuanZhi() {
        return jobQuanZhi;
    }

    public void setJobQuanZhi(List<HomeQZBean> jobQuanZhi) {
        this.jobQuanZhi = jobQuanZhi;
    }

    public List<HomeQZJZBean> getJobJianZhi() {
        return jobJianZhi;
    }

    public void setJobJianZhi(List<HomeQZJZBean> jobJianZhi) {
        this.jobJianZhi = jobJianZhi;
    }
}
