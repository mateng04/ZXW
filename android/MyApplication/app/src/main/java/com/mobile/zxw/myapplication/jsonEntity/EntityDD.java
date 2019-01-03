package com.mobile.zxw.myapplication.jsonEntity;

import com.mobile.zxw.myapplication.bean.OrderSHXX;
import com.mobile.zxw.myapplication.bean.OrderSPXX;
import com.mobile.zxw.myapplication.bean.OrderWLXQ;
import com.mobile.zxw.myapplication.bean.OrderZT;
import com.mobile.zxw.myapplication.bean.OrderZZ;

import java.util.List;

public class EntityDD {

    private int code;
    private OrderZT jbxx;
    private OrderSPXX spxq;
    private OrderSHXX shxx;
    private List<OrderZZ> ddgz;
    private OrderWLXQ wlxq;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public OrderZT getJbxx() {
        return jbxx;
    }

    public void setJbxx(OrderZT jbxx) {
        this.jbxx = jbxx;
    }

    public OrderSPXX getSpxq() {
        return spxq;
    }

    public void setSpxq(OrderSPXX spxq) {
        this.spxq = spxq;
    }

    public OrderSHXX getShxx() {
        return shxx;
    }

    public void setShxx(OrderSHXX shxx) {
        this.shxx = shxx;
    }

    public OrderWLXQ getWlxq() {
        return wlxq;
    }

    public void setWlxq(OrderWLXQ wlxq) {
        this.wlxq = wlxq;
    }

    public List<OrderZZ> getDdgz() {
        return ddgz;
    }

    public void setDdgz(List<OrderZZ> ddgz) {
        this.ddgz = ddgz;
    }
}
