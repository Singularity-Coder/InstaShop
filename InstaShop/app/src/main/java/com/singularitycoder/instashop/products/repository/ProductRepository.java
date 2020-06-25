package com.singularitycoder.instashop.products.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.singularitycoder.instashop.helpers.HelperConstants;
import com.singularitycoder.instashop.helpers.InstaShopRoomDatabase;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;
import com.singularitycoder.instashop.products.dao.ProductCartDao;
import com.singularitycoder.instashop.cart.model.ProductCartItem;
import com.singularitycoder.instashop.products.model.ProductItem;

import java.util.List;

import static java.lang.String.valueOf;

public class ProductRepository {

    @NonNull
    private static final String TAG = "ProductDetailRepository";

    @NonNull
    private static ProductRepository _instance;

    @Nullable
    private ProductCartDao productCartDao;

    @Nullable
    private LiveData<List<ProductCartItem>> cartProductList;

    public ProductRepository() {
    }

    public ProductRepository(Application application) {
        InstaShopRoomDatabase database = InstaShopRoomDatabase.getInstance(application);
        productCartDao = database.productCartDao();
        cartProductList = productCartDao.getAllProducts();
    }

    @NonNull
    public static ProductRepository getInstance() {
        if (null == _instance) {
            _instance = new ProductRepository();
        }
        return _instance;
    }

    // ROOM START______________________________________________________________

    public void insert(ProductCartItem productCartItem) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> productCartDao.insertProduct(productCartItem));
    }

    public void update(ProductCartItem productCartItem) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> productCartDao.updateProduct(productCartItem));
    }

    public void delete(ProductCartItem productCartItem) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> productCartDao.deleteProduct(productCartItem));
    }

    public void deleteAll() {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(() -> productCartDao.deleteAllProducts());
    }

    public LiveData<List<ProductCartItem>> getAll() {
        return cartProductList;
    }

    // ROOM END______________________________________________________________

    public MutableLiveData<RequestStateMediator> getProductsListFromFirestore(@NonNull final String category) {
        final MutableLiveData<RequestStateMediator> liveData = new MutableLiveData<>();
        final RequestStateMediator requestStateMediator = new RequestStateMediator();

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveData.postValue(requestStateMediator);

        FirebaseFirestore
                .getInstance()
                .collection(HelperConstants.COLL_PRODUCTS)
                .whereEqualTo(HelperConstants.KEY_PRODUCT_CATEGORY, category)
                .orderBy(HelperConstants.KEY_PRODUCT_CREATED_EPOCH_TIME)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    requestStateMediator.set(queryDocumentSnapshots, UiState.SUCCESS, "Got Data!", "PRODUCT_LIST");
                    liveData.postValue(requestStateMediator);
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveData.postValue(requestStateMediator);
                });
        return liveData;
    }

    public MutableLiveData<RequestStateMediator> getProductInfo(@NonNull final String docId) {
        final MutableLiveData<RequestStateMediator> liveData = new MutableLiveData<>();
        final RequestStateMediator requestStateMediator = new RequestStateMediator();

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveData.postValue(requestStateMediator);

        FirebaseFirestore.getInstance()
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

                            requestStateMediator.set(productItem, UiState.SUCCESS, "Got Basic Info!", "PRODUCT_INFO");
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
}
