package com.mobile.zxw.myapplication.bean;

public class ShopCarBean {

    //shopTile(商品名称)	shopPrice(商品价格)		shopID(商品ID)		shopUrl(商品图片URL)
    private  String shopTile;
    private  String shopPrice;
    private  String shopID;
    private  String freight;
    private String shopUrl;
    private String shopNum;

    public String getShopNum() {
        return shopNum;
    }

    public void setShopNum(String shopNum) {
        this.shopNum = shopNum;
    }

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

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
    }

    public String getShopUrl() {
        return shopUrl;
    }

    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
    }
}
