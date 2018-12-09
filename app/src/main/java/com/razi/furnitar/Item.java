package com.razi.furnitar;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private String name;
    private Double price;
    private List<String> images;
    private boolean isAR;
    private int quantity;
    private String description;

    public Item() {
    }

    public Item(String name, Double price, ArrayList<String> img, boolean isAR, String desc) {
        this.name = name;
        this.price = price;
        this.images = img;
        this.isAR = isAR;
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getQuantity() {
        return quantity;
    }
}
