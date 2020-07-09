package com.singularitycoder.instashop.auth.repository;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.singularitycoder.instashop.auth.model.AuthUserItem;
import com.singularitycoder.instashop.helpers.HelperConstants;
import com.singularitycoder.instashop.helpers.HelperSharedPreference;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;

public final class AuthRepository {

    @NonNull
    private static final String TAG = "AuthRepository";

    @NonNull
    private static AuthRepository _instance;

    @NonNull
    private final MutableLiveData<RequestStateMediator<Object, UiState, String, String>> liveDataSignUp = new MutableLiveData<>();

    @NonNull
    private final RequestStateMediator<Object, UiState, String, String> requestStateMediator = new RequestStateMediator<>();

    public AuthRepository() {
    }

    @NonNull
    public static synchronized AuthRepository getInstance() {
        if (null == _instance) {
            _instance = new AuthRepository();
        }
        return _instance;
    }

    public final LiveData<RequestStateMediator<Object, UiState, String, String>> signIn(
            @NonNull final Activity activity,
            @NonNull final String email,
            @NonNull final String password) {

        final MutableLiveData<RequestStateMediator<Object, UiState, String, String>> liveData = new MutableLiveData<>();
        final RequestStateMediator<Object, UiState, String, String> requestStateMediator = new RequestStateMediator<>();

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveData.postValue(requestStateMediator);

        FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        requestStateMediator.set(email, UiState.SUCCESS, "Got Data!", "SIGNIN");
                        liveData.postValue(requestStateMediator);
                    }
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveData.postValue(requestStateMediator);
                });
        return liveData;
    }

    public final LiveData<RequestStateMediator<Object, UiState, String, String>> signUp(
            @NonNull final Activity activity,
            @NonNull final String memberType,
            @NonNull final String name,
            @NonNull final String email,
            @NonNull final String password,
            @NonNull final String epochTime,
            @NonNull final String date) {

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveDataSignUp.postValue(requestStateMediator);

        FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        createFirestoreUser(activity, memberType, name, email, password, epochTime, date);
                        requestStateMediator.set(null, UiState.SUCCESS, "Got Data!", "SIGNUP");
                        liveDataSignUp.postValue(requestStateMediator);
                    }
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveDataSignUp.postValue(requestStateMediator);
                });

        return liveDataSignUp;
    }

    private void createFirestoreUser(
            @NonNull final Activity activity,
            @NonNull final String memberType,
            @NonNull final String name,
            @NonNull final String email,
            @NonNull final String password,
            @NonNull final String epochTime,
            @NonNull final String date) {

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveDataSignUp.postValue(requestStateMediator);

        // AuthUserItem obj
        AuthUserItem authUserItem = new AuthUserItem();
        authUserItem.setMemberType(memberType);
        authUserItem.setName(name);
        authUserItem.setEmail(email);
        authUserItem.setPassword(password);
        authUserItem.setEpochTime(epochTime);
        authUserItem.setDate(date);

        // Shared Pref
        HelperSharedPreference helperSharedPreference = HelperSharedPreference.getInstance(activity);
        helperSharedPreference.setMemberType(memberType);
        helperSharedPreference.setName(name);
        helperSharedPreference.setEmail(email);

        // Save AuthUserItem obj to Firestore - Add a new document with a generated ID
        FirebaseFirestore
                .getInstance()
                .collection(HelperConstants.COLL_AUTH_USERS)
                .add(authUserItem)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    authUserItem.setDocId(documentReference.getId());
                    helperSharedPreference.setUserDocId(documentReference.getId());

                    requestStateMediator.set(null, UiState.SUCCESS, "Firestore User Created!", "CREATE_FIRESTORE_USER");
                    liveDataSignUp.postValue(requestStateMediator);
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveDataSignUp.postValue(requestStateMediator);
                });
    }

    public final MutableLiveData<RequestStateMediator<Object, UiState, String, String>> resetPassword(
            @NonNull final String email, @NonNull final DialogFragment dialog) {

        // todo wrong email error message is not working - possible firebase issue

        final MutableLiveData<RequestStateMediator<Object, UiState, String, String>> liveData = new MutableLiveData<>();
        final RequestStateMediator<Object, UiState, String, String> requestStateMediator = new RequestStateMediator<>();

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveData.postValue(requestStateMediator);

        FirebaseAuth
                .getInstance()
                .sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        requestStateMediator.set(null, UiState.SUCCESS, "We have sent instructions to your email to reset your password. Please check!", "RESET_PASSWORD");
                        liveData.postValue(requestStateMediator);
                    }
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveDataSignUp.postValue(requestStateMediator);
                });
        return liveData;
    }

    public final MutableLiveData<RequestStateMediator<Object, UiState, String, String>> getFcmDeviceToken() {

        final MutableLiveData<RequestStateMediator<Object, UiState, String, String>> liveData = new MutableLiveData<>();
        final RequestStateMediator<Object, UiState, String, String> requestStateMediator = new RequestStateMediator<>();

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveData.postValue(requestStateMediator);

        FirebaseInstanceId
                .getInstance()
                .getInstanceId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult().getToken();
                        requestStateMediator.set(token, UiState.SUCCESS, "Registered Token ID: " + token, "FCM_DEVICE_TOKEN");
                        liveData.postValue(requestStateMediator);
                    }
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveDataSignUp.postValue(requestStateMediator);
                });
        return liveData;
    }

    public final MutableLiveData<RequestStateMediator<Object, UiState, String, String>> subscribeToFcmTopic(@NonNull final String topic) {

        final MutableLiveData<RequestStateMediator<Object, UiState, String, String>> liveData = new MutableLiveData<>();
        final RequestStateMediator<Object, UiState, String, String> requestStateMediator = new RequestStateMediator<>();

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);

        FirebaseMessaging
                .getInstance()
                .subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        requestStateMediator.set(null, UiState.SUCCESS, "Successfully subscribed!", "FCM_SUBSCRIBE_TOPIC");
                        liveData.postValue(requestStateMediator);
                    }
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveDataSignUp.postValue(requestStateMediator);
                });
        return liveData;
    }

}
