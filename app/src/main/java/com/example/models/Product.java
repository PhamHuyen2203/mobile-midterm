package com.example.models;

public class Product {

    private int productID;
    private int categoryID;
    private String categoryName;
    private String productName;
    private double originalPrice;
    private double promotionalPrice;
    private String imageURL;
    private double rating;
    private String description;

    public Product() {
    }

    public Product(
            int productID,
            int categoryID,
            String categoryName,
            String productName,
            double originalPrice,
            double promotionalPrice,
            String imageURL,
            double rating,
            String description
    ) {
        this.productID = productID;
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.productName = productName;
        this.originalPrice = originalPrice;
        this.promotionalPrice = promotionalPrice;
        this.imageURL = imageURL;
        this.rating = rating;
        this.description = description;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(
            String categoryName
    ) {
        this.categoryName = categoryName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(
            String productName
    ) {
        this.productName = productName;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(
            double originalPrice
    ) {
        this.originalPrice = originalPrice;
    }

    public double getPromotionalPrice() {
        return promotionalPrice;
    }

    public void setPromotionalPrice(
            double promotionalPrice
    ) {
        this.promotionalPrice = promotionalPrice;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(
            String imageURL
    ) {
        this.imageURL = imageURL;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description = description;
    }
}