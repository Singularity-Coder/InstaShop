package com.singularitycoder.instashop.cart.repository;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.singularitycoder.instashop.cart.model.CartItem;
import com.singularitycoder.instashop.helpers.HelperConstants;
import com.singularitycoder.instashop.helpers.HelperSharedPreference;
import com.singularitycoder.instashop.helpers.InstaShopRoomDatabase;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;
import com.singularitycoder.instashop.cart.dao.CartDao;

import java.util.List;

public final class CartRepository {

    @NonNull
    private static final String TAG = "ProductCartRepository";

    @NonNull
    private static CartRepository _instance;

    @Nullable
    private CartDao cartDao;

    @Nullable
    private LiveData<List<CartItem>> cartProductList;

    public CartRepository() {
    }

    public CartRepository(Application application) {
        InstaShopRoomDatabase database = InstaShopRoomDatabase.getInstance(application);
        cartDao = database.productCartDao();
        cartProductList = cartDao.getAllProducts();
    }

    @NonNull
    public static CartRepository getInstance() {
        if (null == _instance) {
            _instance = new CartRepository();
        }
        return _instance;
    }

    // ROOM START______________________________________________________________

    public final void insertIntoRoomDb(CartItem cartItem) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> cartDao.insertProduct(cartItem));
    }

    public final void updateInRoomDb(CartItem cartItem) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> cartDao.updateProduct(cartItem));
    }

    public final void deleteFromRoomDb(CartItem cartItem) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> cartDao.deleteProduct(cartItem));
    }

    public final void deleteAllFromRoomDb() {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(() -> cartDao.deleteAllProducts());
    }

    public final LiveData<List<CartItem>> getAllFromRoomDb() {
        return cartProductList;
    }

    // ROOM END______________________________________________________________

    public final MutableLiveData<RequestStateMediator> getCartItemsFromFirestore(@NonNull final Context context) {

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
    public final MutableLiveData<RequestStateMediator> addCartItemsToFirestore(
            @NonNull final Context context, @NonNull final CartItem cartItem) {

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
                .add(cartItem)
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
