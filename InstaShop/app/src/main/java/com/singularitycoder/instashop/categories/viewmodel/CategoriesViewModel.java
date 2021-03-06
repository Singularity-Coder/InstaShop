package com.singularitycoder.instashop.categories.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.singularitycoder.instashop.categories.repository.CategoriesRepository;
import com.singularitycoder.instashop.helpers.RequestStateMediator;

public final class CategoriesViewModel extends ViewModel {

    @NonNull
    private final CategoriesRepository categoriesRepository = CategoriesRepository.getInstance();

    public final LiveData<RequestStateMediator> getAuthUserDataFromRepository(
            @NonNull final Context context,
            @NonNull final String email) throws IllegalArgumentException {
        return categoriesRepository.readAuthUserData(context, email);
    }

    public final LiveData<RequestStateMediator> deleteAccountFromRepository() throws IllegalArgumentException {
        return categoriesRepository.deleteAccount();
    }

    public final LiveData<RequestStateMediator> updateEmailFromRepository(
            @NonNull final DialogFragment dialog, @NonNull final String newEmail) throws IllegalArgumentException {
        return categoriesRepository.updateEmail(dialog, newEmail);
    }

    public final LiveData<RequestStateMediator> changePasswordFromRepository(
            @NonNull final DialogFragment dialog, @NonNull final String password) throws IllegalArgumentException {
        return categoriesRepository.changePassword(dialog, password);
    }

    public final LiveData<RequestStateMediator> signOurFromRepository() throws IllegalArgumentException {
        return categoriesRepository.signOut();
    }
}
