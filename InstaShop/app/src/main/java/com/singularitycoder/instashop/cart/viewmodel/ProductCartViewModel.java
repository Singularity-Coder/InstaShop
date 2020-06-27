package com.singularitycoder.instashop.cart.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.singularitycoder.instashop.cart.model.ProductCartItem;
import com.singularitycoder.instashop.cart.repository.ProductCartRepository;
import com.singularitycoder.instashop.helpers.RequestStateMediator;

import java.util.List;

public class ProductCartViewModel extends AndroidViewModel {

    @NonNull
    private ProductCartRepository productCartRepository = ProductCartRepository.getInstance();

    @Nullable
    private LiveData<List<ProductCartItem>> cartProductList;

    public ProductCartViewModel(@NonNull Application application) {
        super(application);
        productCartRepository = new ProductCartRepository(application);
        cartProductList = productCartRepository.getAllFromRoomDb();
    }

    // ROOM START______________________________________________________________

    public void insertIntoRoomDbFromRepository(ProductCartItem note) {
        productCartRepository.insertIntoRoomDb(note);
    }

    public void updateInRoomDbFromRepository(ProductCartItem note) {
        productCartRepository.updateInRoomDb(note);
    }

    public void deleteFromRoomDbFromRepository(ProductCartItem note) {
        productCartRepository.deleteFromRoomDb(note);
    }

    public void deleteAllFromRoomDbFromRepository() {
        productCartRepository.deleteAllFromRoomDb();
    }

    public LiveData<List<ProductCartItem>> getAllFromRoomDbFromRepository() {
        return cartProductList;
    }

    // ROOM END______________________________________________________________

    public LiveData<RequestStateMediator> getCartProductsFromFirestoreFromRepository(@NonNull final Context context) throws IllegalArgumentException {
        return productCartRepository.getCartItemsFromFirestore(context);
    }

    public LiveData<RequestStateMediator> addCartProductToFirestoreFromRepository(
            @NonNull final Context context, @NonNull final ProductCartItem productCartItem) throws IllegalArgumentException {
        return productCartRepository.addCartItemsToFirestore(context, productCartItem);
    }
}
