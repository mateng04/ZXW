package com.mobile.zxw.myapplication.jsonEntity;

import com.mobile.zxw.myapplication.bean.HomeSCBean;
import com.mobile.zxw.myapplication.bean.HomeWSBean;

import java.util.List;

public class EntityHomeShop {

    private String code;
    private List<HomeSCBean> shop;
    private List<HomeWSBean> ws;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<HomeSCBean> getShop() {
        return shop;
    }

    public void setShop(List<HomeSCBean> shop) {
        this.shop = shop;
    }

    public List<HomeWSBean> getWs() {
        return ws;
    }

    public void setWs(List<HomeWSBean> ws) {
        this.ws = ws;
    }
}
