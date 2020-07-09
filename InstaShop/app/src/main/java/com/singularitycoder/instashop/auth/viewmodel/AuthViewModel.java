package com.singularitycoder.instashop.auth.viewmodel;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.singularitycoder.instashop.auth.repository.AuthRepository;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;

public final class AuthViewModel extends ViewModel {

    @NonNull
    private AuthRepository authRepository = AuthRepository.getInstance();

    public final LiveData<RequestStateMediator<Object, UiState, String, String>> signInFromRepository(
            @NonNull final Activity activity,
            @NonNull final String email,
            @NonNull final String password
    ) throws IllegalArgumentException {
        return authRepository.signIn(activity, email, password);
    }

    public final LiveData<RequestStateMediator<Object, UiState, String, String>> signUpFromRepository(
            @NonNull final Activity activity,
            @NonNull final String memberType,
            @NonNull final String name,
            @NonNull final String email,
            @NonNull final String password,
            @NonNull final String epochTime,
            @NonNull final String date
    ) throws IllegalArgumentException {
        return authRepository.signUp(activity, memberType, name, email, password, epochTime, date);
    }

    public final LiveData<RequestStateMediator<Object, UiState, String, String>> resetPasswordFromRepository(
            @NonNull final String email, @NonNull final DialogFragment dialog) throws IllegalArgumentException {
        return authRepository.resetPassword(email, dialog);
    }

    public final LiveData<RequestStateMediator<Object, UiState, String, String>> getFcmTokenFromRepository() throws IllegalArgumentException {
        return authRepository.getFcmDeviceToken();
    }

    public final LiveData<RequestStateMediator<Object, UiState, String, String>> subscribeToFcmTopicFromRepository(@NonNull final String topic) throws IllegalArgumentException {
        return authRepository.subscribeToFcmTopic(topic);
    }
}
