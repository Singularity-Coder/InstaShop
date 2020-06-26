package com.singularitycoder.instashop.dashboard.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.singularitycoder.instashop.dashboard.repository.DashboardRepository;
import com.singularitycoder.instashop.helpers.RequestStateMediator;

public class DashboardViewModel extends ViewModel {

    @NonNull
    private final DashboardRepository dashboardRepository = DashboardRepository.getInstance();

    public LiveData<RequestStateMediator> getAuthUserDataFromRepository(
            @NonNull final Context context,
            @NonNull final String email) throws IllegalArgumentException {
        return dashboardRepository.readAuthUserData(context, email);
    }

    public LiveData<RequestStateMediator> deleteAccountFromRepository() throws IllegalArgumentException {
        return dashboardRepository.deleteAccount();
    }

    public LiveData<RequestStateMediator> updateEmailFromRepository(
            @NonNull final DialogFragment dialog, @NonNull final String newEmail) throws IllegalArgumentException {
        return dashboardRepository.updateEmail(dialog, newEmail);
    }

    public LiveData<RequestStateMediator> changePasswordFromRepository(
            @NonNull final DialogFragment dialog, @NonNull final String password) throws IllegalArgumentException {
        return dashboardRepository.changePassword(dialog, password);
    }

    public LiveData<RequestStateMediator> signOurFromRepository() throws IllegalArgumentException {
        return dashboardRepository.signOut();
    }
}
