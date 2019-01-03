package com.mobile.zxw.myapplication.jsonEntity;

import com.mobile.zxw.myapplication.bean.LoginBean;

public class EntityLogin {

    private int code;
    private String keys;
    private LoginBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public LoginBean getLoginBean() {
        return data;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.data = loginBean;
    }
}
