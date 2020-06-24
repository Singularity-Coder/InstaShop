package com.singularitycoder.instashop.auth.repository;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;

import java.util.HashMap;
import java.util.Map;

public class AuthRepository {

    @NonNull
    private static final String TAG = "AuthRepository";

    @NonNull
    private static AuthRepository _instance;

    public AuthRepository() {
    }

    @NonNull
    public static synchronized AuthRepository getInstance() {
        if (null == _instance) {
            _instance = new AuthRepository();
        }
        return _instance;
    }

    public LiveData<RequestStateMediator> signUp(
            @NonNull final Activity activity,
            @NonNull final String memberType,
            @NonNull final String name,
            @NonNull final String email,
            @NonNull final String password,
            @NonNull final String epochTime,
            @NonNull final String date) {

        final MutableLiveData<RequestStateMediator> liveData = new MutableLiveData<>();
        final RequestStateMediator requestStateMediator = new RequestStateMediator();

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveData.postValue(requestStateMediator);

        FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        Map<String, String> map = new HashMap<>();
                        map.put("MEMBER_TYPE", memberType);
                        map.put("NAME", name);
                        map.put("EMAIL", email);
                        map.put("PASSWORD", password);
                        map.put("EPOCH_TIME", epochTime);
                        map.put("DATE", date);

                        requestStateMediator.set(map, UiState.SUCCESS, "Got Data!", "SIGNUP");
                        liveData.postValue(requestStateMediator);
                    }
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveData.postValue(requestStateMediator);
                });

        return liveData;
    }

    public LiveData<RequestStateMediator> signIn(
            @NonNull final Activity activity,
            @NonNull final String email,
            @NonNull final String password) {

        final MutableLiveData<RequestStateMediator> liveData = new MutableLiveData<>();
        final RequestStateMediator requestStateMediator = new RequestStateMediator();

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

}
