package com.mobile.zxw.myapplication.bean;

public class HomeWSBean {

    //shopTile(商品名称)	shopPrice(商品价格)		shopID(商品ID)		shopPicUrl(商品图片URL)
    private  String wsTile;
    private  String wsPopularity;
    private  String wsID;
    private  String swsPicUrl;


    public String getWsTile() {
        return wsTile;
    }

    public void setWsTile(String wsTile) {
        this.wsTile = wsTile;
    }

    public String getWsPopularity() {
        return wsPopularity;
    }

    public void setWsPopularity(String wsPopularity) {
        this.wsPopularity = wsPopularity;
    }

    public String getWsID() {
        return wsID;
    }

    public void setWsID(String wsID) {
        this.wsID = wsID;
    }

    public String getSwsPicUrl() {
        return swsPicUrl;
    }

    public void setSwsPicUrl(String swsPicUrl) {
        this.swsPicUrl = swsPicUrl;
    }
}
