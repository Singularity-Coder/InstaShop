package com.singularitycoder.instashop.auth.model;

import com.google.firebase.firestore.Exclude;

public final class AuthUserItem {

    private String docId;
    private String memberType;
    private String name;
    private String email;
    private String password;
    private String epochTime;
    private String date;
    private String fcmToken;

    @Exclude
    public final String getDocId() {
        return docId;
    }

    public final void setDocId(String docId) {
        this.docId = docId;
    }

    public final String getMemberType() {
        return memberType;
    }

    public final void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        this.email = email;
    }

    public final String getPassword() {
        return password;
    }

    public final void setPassword(String password) {
        this.password = password;
    }

    public final String getEpochTime() {
        return epochTime;
    }

    public final void setEpochTime(String epochTime) {
        this.epochTime = epochTime;
    }

    public final String getDate() {
        return date;
    }

    public final void setDate(String date) {
        this.date = date;
    }

    public final String getFcmToken() {
        return fcmToken;
    }

    public final void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
