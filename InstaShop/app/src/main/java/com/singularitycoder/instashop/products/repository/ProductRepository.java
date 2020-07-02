package com.singularitycoder.instashop.products.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.singularitycoder.instashop.helpers.HelperConstants;
import com.singularitycoder.instashop.helpers.HelperSharedPreference;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;
import com.singularitycoder.instashop.products.model.ProductItem;
import com.singularitycoder.instashop.wishlist.model.WishlistItem;

import javax.net.ssl.SSLEngineResult;

import static java.lang.String.valueOf;

public final class ProductRepository {

    @NonNull
    private static final String TAG = "ProductRepository";

    @NonNull
    private static ProductRepository _instance;

    public ProductRepository() {
    }

    @NonNull
    public static ProductRepository getInstance() {
        if (null == _instance) {
            _instance = new ProductRepository();
        }
        return _instance;
    }

    public final MutableLiveData<RequestStateMediator<Object, UiState, String, String>> getProductsListFromFirestore(@NonNull final String category) {
        final MutableLiveData<RequestStateMediator<Object, UiState, String, String>> liveData = new MutableLiveData<>();
        final RequestStateMediator<Object, UiState, String, String> requestStateMediator = new RequestStateMediator<>();

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveData.postValue(requestStateMediator);

        FirebaseFirestore
                .getInstance()
                .collection(HelperConstants.COLL_PRODUCTS)
                .whereEqualTo(HelperConstants.KEY_PRODUCT_CATEGORY, category)
                .orderBy(HelperConstants.KEY_PRODUCT_CREATED_EPOCH_TIME)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    requestStateMediator.set(queryDocumentSnapshots, UiState.SUCCESS, "Got Data!", "STATE_PRODUCT_LIST");
                    liveData.postValue(requestStateMediator);
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveData.postValue(requestStateMediator);
                });
        return liveData;
    }

    public final MutableLiveData<RequestStateMediator<Object, UiState, String, String>> getProductInfo(@NonNull final String docId) {
        final MutableLiveData<RequestStateMediator<Object, UiState, String, String>> liveData = new MutableLiveData<>();
        final RequestStateMediator<Object, UiState, String, String> requestStateMediator = new RequestStateMediator<>();

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveData.postValue(requestStateMediator);

        FirebaseFirestore
                .getInstance()
                .collection(HelperConstants.COLL_PRODUCTS)
                .document(docId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ProductItem productItem = documentSnapshot.toObject(ProductItem.class);
                        if (null != productItem) {

                            if (!("").equals(valueOf(documentSnapshot.getString("productCategory")))) {
                                productItem.setProductCategory(valueOf(documentSnapshot.getString("productCategory")));
                            } else {
                                productItem.setProductCategory("Empty");
                            }

                            if (!("").equals(valueOf(documentSnapshot.getString("productCreationDate")))) {
                                productItem.setProductCreationDate(valueOf(documentSnapshot.getString("productCreationDate")));
                            } else {
                                productItem.setProductCreationDate("Empty");
                            }

                            if (!("").equals(valueOf(documentSnapshot.getString("productDescription")))) {
                                productItem.setProductDescription(valueOf(documentSnapshot.getString("productDescription")));
                            } else {
                                productItem.setProductDescription("Empty");
                            }

                            if (!("").equals(valueOf(documentSnapshot.getString("productName")))) {
                                productItem.setProductName(valueOf(documentSnapshot.getString("productName")));
                            } else {
                                productItem.setProductName("Empty");
                            }

                            if (!("").equals(valueOf(documentSnapshot.getString("productPrice")))) {
                                productItem.setProductPrice(valueOf(documentSnapshot.getString("productPrice")));
                            } else {
                                productItem.setProductPrice("Empty");
                            }

                            if (!("").equals(valueOf(documentSnapshot.getString("productImageUrl")))) {
                                productItem.setProductImageUrl(valueOf(documentSnapshot.getString("productImageUrl")));
                            } else {
                                productItem.setProductImageUrl("Empty");
                            }

                            productItem.setProductDocId(documentSnapshot.getId());

                            requestStateMediator.set(productItem, UiState.SUCCESS, "Got Basic Info!", "STATE_PRODUCT_DETAIL");
                            liveData.postValue(requestStateMediator);
                        }
                        Log.d(TAG, "firedoc id: " + documentSnapshot.getId());
                    } else {
                        requestStateMediator.set(null, UiState.EMPTY, "Empty!", null);
                        liveData.postValue(requestStateMediator);
                    }
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveData.postValue(requestStateMediator);
                });
        return liveData;
    }

    public final MutableLiveData<RequestStateMediator<Object, UiState, String, String>> addWishlistItemsToFirestore(
            @NonNull final Context context, @NonNull final WishlistItem wishlistItem) {

        final MutableLiveData<RequestStateMediator<Object, UiState, String, String>> liveData = new MutableLiveData<>();
        final RequestStateMediator<Object, UiState, String, String> requestStateMediator = new RequestStateMediator<>();

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveData.postValue(requestStateMediator);

        HelperSharedPreference helperSharedPreference = HelperSharedPreference.getInstance(context);

        FirebaseFirestore
                .getInstance()
                .collection(HelperConstants.COLL_AUTH_USERS)
                .document(helperSharedPreference.getUserDocId())
                .collection(HelperConstants.SUB_COLL_WISHLIST)
                .add(wishlistItem)
                .addOnSuccessListener(documentReference -> {
                    requestStateMediator.set(null, UiState.SUCCESS, "Item Added!", "STATE_ADD_WISHLIST_FIRESTORE");
                    liveData.postValue(requestStateMediator);
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveData.postValue(requestStateMediator);
                });
        return liveData;
    }
}
