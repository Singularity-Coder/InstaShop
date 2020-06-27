package com.singularitycoder.instashop.cart.repository;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.singularitycoder.instashop.cart.model.ProductCartItem;
import com.singularitycoder.instashop.helpers.HelperConstants;
import com.singularitycoder.instashop.helpers.HelperSharedPreference;
import com.singularitycoder.instashop.helpers.InstaShopRoomDatabase;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;
import com.singularitycoder.instashop.products.dao.ProductCartDao;

import java.util.List;

public class ProductCartRepository {

    @NonNull
    private static final String TAG = "ProductCartRepository";

    @NonNull
    private static ProductCartRepository _instance;

    @Nullable
    private ProductCartDao productCartDao;

    @Nullable
    private LiveData<List<ProductCartItem>> cartProductList;

    public ProductCartRepository() {
    }

    public ProductCartRepository(Application application) {
        InstaShopRoomDatabase database = InstaShopRoomDatabase.getInstance(application);
        productCartDao = database.productCartDao();
        cartProductList = productCartDao.getAllProducts();
    }

    @NonNull
    public static ProductCartRepository getInstance() {
        if (null == _instance) {
            _instance = new ProductCartRepository();
        }
        return _instance;
    }

    // ROOM START______________________________________________________________

    public void insertIntoRoomDb(ProductCartItem productCartItem) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> productCartDao.insertProduct(productCartItem));
    }

    public void updateInRoomDb(ProductCartItem productCartItem) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> productCartDao.updateProduct(productCartItem));
    }

    public void deleteFromRoomDb(ProductCartItem productCartItem) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> productCartDao.deleteProduct(productCartItem));
    }

    public void deleteAllFromRoomDb() {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(() -> productCartDao.deleteAllProducts());
    }

    public LiveData<List<ProductCartItem>> getAllFromRoomDb() {
        return cartProductList;
    }

    // ROOM END______________________________________________________________

    public MutableLiveData<RequestStateMediator> getCartItemsFromFirestore(@NonNull final Context context) {

        final MutableLiveData<RequestStateMediator> liveData = new MutableLiveData<>();
        final RequestStateMediator requestStateMediator = new RequestStateMediator();

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveData.postValue(requestStateMediator);

        HelperSharedPreference helperSharedPreference = HelperSharedPreference.getInstance(context);

        FirebaseFirestore
                .getInstance()
                .collection(HelperConstants.COLL_AUTH_USERS)
                .document(helperSharedPreference.getUserDocId())
                .collection(HelperConstants.SUB_COLL_CART)
                .get()
                .addOnSuccessListener(documentReference -> {
                    requestStateMediator.set(documentReference, UiState.SUCCESS, "Got Data!", "STATE_GET_CART_FIRESTORE");
                    liveData.postValue(requestStateMediator);
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveData.postValue(requestStateMediator);
                });
        return liveData;
    }

    // UI is in Product Detail frag. So loading happens there.
    public MutableLiveData<RequestStateMediator> addCartItemsToFirestore(
            @NonNull final Context context, @NonNull final ProductCartItem productCartItem) {

        final MutableLiveData<RequestStateMediator> liveData = new MutableLiveData<>();
        final RequestStateMediator requestStateMediator = new RequestStateMediator();

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveData.postValue(requestStateMediator);

        HelperSharedPreference helperSharedPreference = HelperSharedPreference.getInstance(context);

        FirebaseFirestore
                .getInstance()
                .collection(HelperConstants.COLL_AUTH_USERS)
                .document(helperSharedPreference.getUserDocId())
                .collection(HelperConstants.SUB_COLL_CART)
                .add(productCartItem)
                .addOnSuccessListener(documentReference -> {
                    requestStateMediator.set(null, UiState.SUCCESS, "Item Added!", "STATE_ADD_CART_FIRESTORE");
                    liveData.postValue(requestStateMediator);
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveData.postValue(requestStateMediator);
                });
        return liveData;
    }
}
