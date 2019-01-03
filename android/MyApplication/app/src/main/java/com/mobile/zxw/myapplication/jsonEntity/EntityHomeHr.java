package com.mobile.zxw.myapplication.jsonEntity;

import com.mobile.zxw.myapplication.bean.HomeZPBean;
import com.mobile.zxw.myapplication.bean.HomeZPJZBean;

import java.util.List;

public class EntityHomeHr {

    private String code;
    private List<HomeZPBean> hrQuanZhi;
    private List<HomeZPJZBean> hrJianZhi;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<HomeZPBean> getHrQuanZhi() {
        return hrQuanZhi;
    }

    public void setHrQuanZhi(List<HomeZPBean> hrQuanZhi) {
        this.hrQuanZhi = hrQuanZhi;
    }

    public List<HomeZPJZBean> getHrJianZhi() {
        return hrJianZhi;
    }

    public void setHrJianZhi(List<HomeZPJZBean> hrJianZhi) {
        this.hrJianZhi = hrJianZhi;
    }
}
