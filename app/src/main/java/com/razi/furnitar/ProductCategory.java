package com.razi.furnitar;

public class ProductCategory {

    Integer productId;
    String productName;

    public ProductCategory(Integer productId, String productName) {
        this.productId = productId;
        this.productName = productName;
    }

    public ProductCategory() {
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}