package com.singularitycoder.instashop.dashboard.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
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
}
