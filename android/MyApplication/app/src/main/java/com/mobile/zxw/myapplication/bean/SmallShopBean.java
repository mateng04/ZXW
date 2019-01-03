package com.mobile.zxw.myapplication.bean;

public class SmallShopBean {

//    "BigClassID": "11",
//            "SmallClassID": "141",
//            "SmallClassName": "生物制药技术",
//            "BigClassName": "制药类"

    private String BigClassID;
    private String BigClassName;
    private String SmallClassID;
    private String SmallClassName;

    public String getBigClassID() {
        return BigClassID;
    }

    public void setBigClassID(String bigClassID) {
        BigClassID = bigClassID;
    }

    public String getBigClassName() {
        return BigClassName;
    }

    public void setBigClassName(String bigClassName) {
        BigClassName = bigClassName;
    }

    public String getSmallClassID() {
        return SmallClassID;
    }

    public void setSmallClassID(String smallClassID) {
        SmallClassID = smallClassID;
    }

    public String getSmallClassName() {
        return SmallClassName;
    }

    public void setSmallClassName(String smallClassName) {
        SmallClassName = smallClassName;
    }
}
