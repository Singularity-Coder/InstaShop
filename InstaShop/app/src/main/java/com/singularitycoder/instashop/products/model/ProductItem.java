package com.singularitycoder.instashop.products.model;

import android.net.Uri;

import com.google.firebase.firestore.Exclude;

public class ProductItem {

    // Exclude from Firestore
    private Uri productImageUri;
    private String productDocId;

    private String productName;
    private String productImageUrl;
    private String productPrice;
    private String productCategory;
    private String productImageName;
    private String productCreationEpochTime;
    private String productCreationDate;
    private String productDescription;

    public ProductItem() {
    }

    @Exclude
    public String getProductDocId() {
        return productDocId;
    }

    public void setProductDocId(String productDocId) {
        this.productDocId = productDocId;
    }

    @Exclude
    public Uri getProductImageUri() {
        return productImageUri;
    }

    public void setProductImageUri(Uri productImageUri) {
        this.productImageUri = productImageUri;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getProductImageName() {
        return productImageName;
    }

    public void setProductImageName(String productImageName) {
        this.productImageName = productImageName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductCreationEpochTime() {
        return productCreationEpochTime;
    }

    public void setProductCreationEpochTime(String productCreationEpochTime) {
        this.productCreationEpochTime = productCreationEpochTime;
    }

    public String getProductCreationDate() {
        return productCreationDate;
    }

    public void setProductCreationDate(String productCreationDate) {
        this.productCreationDate = productCreationDate;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}
