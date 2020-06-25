package com.singularitycoder.instashop.admin.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.singularitycoder.instashop.admin.repository.AdminRepository;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.products.model.ProductItem;

public class AdminViewModel extends ViewModel {

    @NonNull
    private final AdminRepository adminRepository = AdminRepository.getInstance();

    public LiveData<RequestStateMediator> uploadProductFromRepository(@NonNull final ProductItem productItem) throws IllegalArgumentException {
        return adminRepository.uploadImage(productItem);
    }

}
