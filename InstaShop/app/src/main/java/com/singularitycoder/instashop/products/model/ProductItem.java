package com.singularitycoder.instashop.products.model;

import android.net.Uri;

import com.google.firebase.firestore.Exclude;

public final class ProductItem {

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
    public final String getProductDocId() {
        return productDocId;
    }

    public final void setProductDocId(String productDocId) {
        this.productDocId = productDocId;
    }

    @Exclude
    public final Uri getProductImageUri() {
        return productImageUri;
    }

    public final void setProductImageUri(Uri productImageUri) {
        this.productImageUri = productImageUri;
    }

    public final String getProductName() {
        return productName;
    }

    public final void setProductName(String productName) {
        this.productName = productName;
    }

    public final String getProductImageUrl() {
        return productImageUrl;
    }

    public final void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public final String getProductImageName() {
        return productImageName;
    }

    public final void setProductImageName(String productImageName) {
        this.productImageName = productImageName;
    }

    public final String getProductPrice() {
        return productPrice;
    }

    public final void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public final String getProductCategory() {
        return productCategory;
    }

    public final void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public final String getProductCreationEpochTime() {
        return productCreationEpochTime;
    }

    public final void setProductCreationEpochTime(String productCreationEpochTime) {
        this.productCreationEpochTime = productCreationEpochTime;
    }

    public final String getProductCreationDate() {
        return productCreationDate;
    }

    public final void setProductCreationDate(String productCreationDate) {
        this.productCreationDate = productCreationDate;
    }

    public final String getProductDescription() {
        return productDescription;
    }

    public final void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}
