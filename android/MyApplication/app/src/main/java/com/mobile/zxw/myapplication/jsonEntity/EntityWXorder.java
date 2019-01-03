package com.mobile.zxw.myapplication.jsonEntity;

import com.mobile.zxw.myapplication.bean.OrderArr;

public class EntityWXorder {

    private int success;
    private String ordersn;
    private OrderArr order_arr;
    private String data;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getOrdersn() {
        return ordersn;
    }

    public void setOrdersn(String ordersn) {
        this.ordersn = ordersn;
    }

    public OrderArr getOrder_arr() {
        return order_arr;
    }

    public void setOrder_arr(OrderArr order_arr) {
        this.order_arr = order_arr;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
