package com.singularitycoder.instashop.cart.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;

@Entity(tableName = "table_cart")
public final class CartItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "RoomId")
    private int roomId;

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

    @ColumnInfo(name = "ProductDocId")
    private String productDocId;

    @ColumnInfo(name = "AddedOnDate")
    private String addedOnDate;

    public CartItem() {
    }

    @Exclude
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
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

    public final String getProductDocId() {
        return productDocId;
    }

    public final void setProductDocId(String productDocId) {
        this.productDocId = productDocId;
    }

    public String getAddedOnDate() {
        return addedOnDate;
    }

    public void setAddedOnDate(String addedOnDate) {
        this.addedOnDate = addedOnDate;
    }
}
