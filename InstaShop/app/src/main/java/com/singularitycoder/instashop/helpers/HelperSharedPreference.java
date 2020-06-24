package com.singularitycoder.instashop.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class HelperSharedPreference {
    private static final String KEY_NAME = "name";
    private static final String KEY_DOC_ID = "docId";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MEMBER_TYPE = "memberType";

    private static HelperSharedPreference _instance;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedPrefEditor;

    public static synchronized HelperSharedPreference instance() {
        return _instance;
    }

    public static HelperSharedPreference getInstance(Context context) {
        if (_instance == null) {
            _instance = new HelperSharedPreference();
            _instance.configSessionUtils(context);
        }
        return _instance;
    }

    private void configSessionUtils(Context context) {
        Context context1 = context;
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
        sharedPrefEditor.putString(KEY_DOC_ID, docId);
        sharedPrefEditor.commit();
    }

    public String getUserDocId() {
        return sharedPref.getString(KEY_DOC_ID, "");
    }
}
