package com.singularitycoder.instashop.dashboard.model;

public class DashboardItem {

    private int intHomeImage;
    private String strHomeTitle;

    private String strImageUrl;
    private String strUserName;

    // Header
    public DashboardItem(String strImageUrl, String strUserName) {
        this.strImageUrl = strImageUrl;
        this.strUserName = strUserName;
    }

    public DashboardItem(int intHomeImage, String strHomeTitle) {
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
