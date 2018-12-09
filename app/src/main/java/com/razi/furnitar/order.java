package com.razi.furnitar;

public class order {
    private String userid;
    private String id;
    private String name;
    private double price;
    private int quantity;

    public order() {
    }

    public order(String userid, String id, String name, double price, int quantity) {

        this.userid = userid;
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
