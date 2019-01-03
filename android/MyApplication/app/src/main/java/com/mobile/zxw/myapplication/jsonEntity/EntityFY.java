package com.mobile.zxw.myapplication.jsonEntity;

public class EntityFY<T> {

    private String code;
    private T data;
    private int countPage;
    private int currtPage;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
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
