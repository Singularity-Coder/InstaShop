package com.singularitycoder.instashop.admin.repository;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.singularitycoder.instashop.helpers.HelperConstants;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;
import com.singularitycoder.instashop.products.model.ProductItem;

import static java.lang.String.valueOf;

public class AdminRepository {

    @NonNull
    private static final String TAG = "AdminRepository";

    @NonNull
    private static AdminRepository _instance;

    @NonNull
    final MutableLiveData<RequestStateMediator> liveData = new MutableLiveData<>();

    @NonNull
    final RequestStateMediator requestStateMediator = new RequestStateMediator();

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
                        getUriFromFirebaseStorage(HelperConstants.DIR_PRODUCT_IMAGES_PATH, productItem);
                        requestStateMediator.set(null, UiState.SUCCESS, "Uploaded Data!", "UPLOAD_IMAGE");
                        liveData.postValue(requestStateMediator);
                    }
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveData.postValue(requestStateMediator);
                });
        return liveData;
    }

    // GET URI FROM FIREBASE STORAGE
    private void getUriFromFirebaseStorage(@NonNull final String fileDirectory, @NonNull final ProductItem productItem) {

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveData.postValue(requestStateMediator);

        FirebaseStorage
                .getInstance()
                .getReference()
                .child(fileDirectory)
                .child(productItem.getProductImageName())
                .getDownloadUrl()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        String storageImageUrl = valueOf(task1.getResult());
                        addFileItemToFirestore(storageImageUrl, productItem);
                        requestStateMediator.set(null, UiState.SUCCESS, "Got Uri from Storage!", "GET_STORAGE_URI");
                        liveData.postValue(requestStateMediator);
                    } else {
                        deleteFromFirebaseStorage(fileDirectory, productItem);
                        requestStateMediator.set(null, UiState.EMPTY, "Couldn't get Uri ", null);
                        liveData.postValue(requestStateMediator);
                    }
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveData.postValue(requestStateMediator);
                });
    }

    // DELETE FROM FIREBASE STORAGE
    private void deleteFromFirebaseStorage(@NonNull final String fileDirectory, @NonNull final ProductItem productItem) {
        FirebaseStorage
                .getInstance()
                .getReference()
                .child(fileDirectory)
                .child(productItem.getProductImageName())
                .delete();
    }

    // CREATE FROM FIRESTORE
    private void addFileItemToFirestore(@NonNull final String storageImageUrl, @NonNull final ProductItem productItem) {
        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveData.postValue(requestStateMediator);

        productItem.setProductImageUrl(storageImageUrl);

        FirebaseFirestore
                .getInstance()
                .collection(HelperConstants.COLL_PRODUCTS)
                .add(productItem)
                .addOnSuccessListener(documentReference -> {
                    requestStateMediator.set(null, UiState.SUCCESS, "Item Added!", "ADD_TO_FIRESTORE");
                    liveData.postValue(requestStateMediator);
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveData.postValue(requestStateMediator);
                });
    }

}
