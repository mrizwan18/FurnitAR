package com.razi.furnitar;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private String name;
    private Double price;
    private List<String> images;
    private boolean isAR;

    public Item(){}

    public Item(String name, Double price, ArrayList<String> img, boolean isAR) {
        this.name = name;
        this.price = price;
        this.images = img;
        this.isAR = isAR;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public List<String> getImages() {
        return images;
    }

    public boolean getIsAR() {
        return isAR;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", images=" + images +
                ", isAR=" + isAR +
                '}';
    }
}
