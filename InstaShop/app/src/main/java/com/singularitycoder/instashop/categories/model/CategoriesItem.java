package com.singularitycoder.instashop.categories.model;

public class CategoriesItem {

    private int intHomeImage;
    private String strHomeTitle;

    private String strImageUrl;
    private String strUserName;

    // Header
    public CategoriesItem(String strImageUrl, String strUserName) {
        this.strImageUrl = strImageUrl;
        this.strUserName = strUserName;
    }

    public CategoriesItem(int intHomeImage, String strHomeTitle) {
        this.intHomeImage = intHomeImage;
        this.strHomeTitle = strHomeTitle;
    }

    public int getIntHomeImage() {
        return intHomeImage;
    }

    public String getStrHomeTitle() {
        return strHomeTitle;
    }

    public String getStrImageUrl() {
        return strImageUrl;
    }

    public String getStrUserName() {
        return strUserName;
    }
}
