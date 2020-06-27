package com.singularitycoder.instashop.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HelperSharedPreference {

    @NonNull
    private static final String KEY_NAME = "name";

    @NonNull
    private static final String KEY_USER_DOC_ID = "userDocId";

    @NonNull
    private static final String KEY_PRODUCT_DOC_ID = "productDocId";

    @NonNull
    private static final String KEY_EMAIL = "email";

    @NonNull
    private static final String KEY_MEMBER_TYPE = "memberType";

    @NonNull
    private static final String KEY_FCM_TOKEN = "fcmToken";

    @NonNull
    private static HelperSharedPreference _instance;

    @Nullable
    private SharedPreferences sharedPref;

    @Nullable
    private SharedPreferences.Editor sharedPrefEditor;

    @NonNull
    public static synchronized HelperSharedPreference getInstance(Context context) {
        if (null == _instance) {
            _instance = new HelperSharedPreference();
            _instance.configSessionUtils(context);
        }
        return _instance;
    }

    private void configSessionUtils(Context context) {
        sharedPref = context.getSharedPreferences("AppPreferences", Activity.MODE_PRIVATE);
        sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.apply();
    }

    public void setMemberType(String memberType) {
        sharedPrefEditor.putString(KEY_MEMBER_TYPE, memberType);
        sharedPrefEditor.commit();
    }

    public String getMemberType() {
        return sharedPref.getString(KEY_MEMBER_TYPE, "");
    }

    public void setName(String name) {
        sharedPrefEditor.putString(KEY_NAME, name);
        sharedPrefEditor.commit();
    }

    public String getName() {
        return sharedPref.getString(KEY_NAME, "");
    }

    public void setEmail(String email) {
        sharedPrefEditor.putString(KEY_EMAIL, email);
        sharedPrefEditor.commit();
    }

    public String getEmail() {
        return sharedPref.getString(KEY_EMAIL, "");
    }

    public void setUserDocId(String docId) {
        sharedPrefEditor.putString(KEY_USER_DOC_ID, docId);
        sharedPrefEditor.commit();
    }

    public String getUserDocId() {
        return sharedPref.getString(KEY_USER_DOC_ID, "");
    }

    public void setProductDocId(String docId) {
        sharedPrefEditor.putString(KEY_PRODUCT_DOC_ID, docId);
        sharedPrefEditor.commit();
    }

    public String getProductDocId() {
        return sharedPref.getString(KEY_PRODUCT_DOC_ID, "");
    }

    public void setFcmToken(String firebaseToken) {
        sharedPrefEditor.putString(KEY_FCM_TOKEN, firebaseToken);
        sharedPrefEditor.commit();
    }

    public String getFcmToken() {
        return sharedPref.getString(KEY_FCM_TOKEN, "");
    }
}
