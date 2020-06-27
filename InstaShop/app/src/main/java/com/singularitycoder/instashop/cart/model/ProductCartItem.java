package com.singularitycoder.instashop.cart.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;

@Entity(tableName = "product_cart_table")
public class ProductCartItem {

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
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getProductQty() {
        return productQty;
    }

    public void setProductQty(String productQty) {
        this.productQty = productQty;
    }

    @Exclude
    public String getProductDocId() {
        return productDocId;
    }

    public void setProductDocId(String productDocId) {
        this.productDocId = productDocId;
    }
}
