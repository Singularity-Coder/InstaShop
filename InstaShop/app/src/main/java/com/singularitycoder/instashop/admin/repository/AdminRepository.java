package com.singularitycoder.instashop.admin.repository;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.storage.FirebaseStorage;
import com.singularitycoder.instashop.helpers.HelperConstants;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;
import com.singularitycoder.instashop.products.model.ProductItem;

public class AdminRepository {

    @NonNull
    private static final String TAG = "AuthRepository";

    @NonNull
    private static AdminRepository _instance;

    public AdminRepository() {
    }

    @NonNull
    public static synchronized AdminRepository getInstance() {
        if (null == _instance) {
            _instance = new AdminRepository();
        }
        return _instance;
    }

    // UPLOAD TO FIREBASE STORAGE
    public MutableLiveData<RequestStateMediator> uploadImage(@NonNull final ProductItem productItem) {

        final MutableLiveData<RequestStateMediator> liveData = new MutableLiveData<>();
        final RequestStateMediator requestStateMediator = new RequestStateMediator();

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveData.postValue(requestStateMediator);


        FirebaseStorage
                .getInstance()
                .getReference()
                .child(HelperConstants.DIR_PRODUCT_IMAGES_PATH)
                .child(productItem.getProductImageName())
                .putFile(productItem.getProductImageUri())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult().getUploadSessionUri();
                        requestStateMediator.set(productItem, UiState.SUCCESS, "Got Data!", "UPLOAD IMAGE");
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
