package com.example.wcsapp.util;

public class ReplenishEntity {
    private String sku;
    private String createtime;
    private String qty;
    private String goodsname;
    private String location;

    public ReplenishEntity() {
    }

    public ReplenishEntity(String sku, String createtime, String qty, String goodsname, String location) {
        this.sku = sku;
        this.createtime = createtime;
        this.qty = qty;
        this.goodsname = goodsname;
        this.location = location;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "ReplenishEntity{" +
                "sku='" + sku + '\'' +
                ", createtime='" + createtime + '\'' +
                ", qty='" + qty + '\'' +
                ", goodsname='" + goodsname + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
