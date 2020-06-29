package com.singularitycoder.instashop.categories.model;

public final class CategoriesItem {

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

    public final int getIntHomeImage() {
        return intHomeImage;
    }

    public final String getStrHomeTitle() {
        return strHomeTitle;
    }

    public final String getStrImageUrl() {
        return strImageUrl;
    }

    public final String getStrUserName() {
        return strUserName;
    }
}
