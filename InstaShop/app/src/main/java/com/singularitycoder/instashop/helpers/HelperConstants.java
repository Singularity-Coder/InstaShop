package com.singularitycoder.instashop.helpers;

public class HelperConstants {

    // Firebase Firestore Auth Collection
    public static final String COLL_AUTH_USERS = "InstaShopUsers";
    public static final String COLL_PRODUCTS = "InstaShopProducts";

    // Firebase Firestore Sub-Collection
    public static final String SUB_COLL_CART = "Cart";

    // Firebase Storage
    public static final String DIR_PRODUCT_IMAGES_PATH = "ProductImages/";

    // Auth User Keys
    public static final String KEY_MEMBER_TYPE = "memberType";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_ACCOUNT_CREATED_EPOCH_TIME = "epochTime";
    public static final String KEY_ACCOUNT_CREATED_DATE = "date";

    // Product Keys
    public static final String KEY_PRODUCT_NAME = "productName";
    public static final String KEY_PRODUCT_IMAGE = "productImageUrl";
    public static final String KEY_PRODUCT_PRICE = "productPrice";
    public static final String KEY_PRODUCT_CATEGORY = "productCategory";
    public static final String KEY_PRODUCT_DESCRIPTION = "productDescription";
    public static final String KEY_PRODUCT_CREATED_EPOCH_TIME = "productCreationEpochTime";
    public static final String KEY_PRODUCT_CREATED_DATE = "productCreationDate";
}
