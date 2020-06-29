package com.singularitycoder.instashop.cart.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;

@Entity(tableName = "product_cart_table")
public final class ProductCartItem {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "ID")
    private int id;

    @ColumnInfo(name = "ProductName")
    private String productName;

    @ColumnInfo(name = "ProductImageUrl")
    private String productImageUrl;

    @ColumnInfo(name = "ProductPrice")
    private String productPrice;

    @ColumnInfo(name = "ProductCategory")
    private String productCategory;

    @ColumnInfo(name = "ProductQty")
    private String productQty;

    private String productDocId;

    public ProductCartItem() {
    }

    @Exclude
    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
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

    public final String getProductQty() {
        return productQty;
    }

    public final void setProductQty(String productQty) {
        this.productQty = productQty;
    }

    @Exclude
    public final String getProductDocId() {
        return productDocId;
    }

    public final void setProductDocId(String productDocId) {
        this.productDocId = productDocId;
    }
}
