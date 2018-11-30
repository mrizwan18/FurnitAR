package com.razi.furnitar;

import android.widget.ImageView;

public class Item {
    private String name;
    private Double price;
    private ImageView img;
    private boolean status;

    public Item(String name, Double price, ImageView img, boolean status) {
        this.name = name;
        this.price = price;
        this.img = img;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public ImageView getImg() {
        return img;
    }

    public boolean isStatus() {
        return status;
    }
}
