package com.mobile.zxw.myapplication.bean;

public class HomeSCBean {

    //shopTile(商品名称)	shopPrice(商品价格)		shopID(商品ID)		shopPicUrl(商品图片URL)
    private  String shopTile;
    private  String shopPrice;
    private  String shopID;
    private  String shopPicUrl;

    public String getShopTile() {
        return shopTile;
    }

    public void setShopTile(String shopTile) {
        this.shopTile = shopTile;
    }

    public String getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(String shopPrice) {
        this.shopPrice = shopPrice;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getShopPicUrl() {
        return shopPicUrl;
    }

    public void setShopPicUrl(String shopPicUrl) {
        this.shopPicUrl = shopPicUrl;
    }
}
