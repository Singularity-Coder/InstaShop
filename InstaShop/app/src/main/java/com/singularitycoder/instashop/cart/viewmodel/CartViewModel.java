package com.singularitycoder.instashop.cart.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.singularitycoder.instashop.cart.model.CartItem;
import com.singularitycoder.instashop.cart.repository.CartRepository;
import com.singularitycoder.instashop.helpers.RequestStateMediator;

import java.util.List;

public final class CartViewModel extends AndroidViewModel {

    @NonNull
    private CartRepository cartRepository = CartRepository.getInstance();

    @Nullable
    private LiveData<List<CartItem>> cartProductList;

    public CartViewModel(@NonNull Application application) {
        super(application);
        cartRepository = new CartRepository(application);
        cartProductList = cartRepository.getAllFromRoomDb();
    }

    // ROOM START______________________________________________________________

    public final void insertIntoRoomDbFromRepository(CartItem cartItem) {
        cartRepository.insertIntoRoomDb(cartItem);
    }

    public final void updateInRoomDbFromRepository(CartItem cartItem) {
        cartRepository.updateInRoomDb(cartItem);
    }

    public final void deleteFromRoomDbFromRepository(CartItem cartItem) {
        cartRepository.deleteFromRoomDb(cartItem);
    }

    public final void deleteAllFromRoomDbFromRepository() {
        cartRepository.deleteAllFromRoomDb();
    }

    public final LiveData<List<CartItem>> getAllFromRoomDbFromRepository() {
        return cartProductList;
    }

    // ROOM END______________________________________________________________

    public final LiveData<RequestStateMediator> getCartProductsFromFirestoreFromRepository(@NonNull final Context context) throws IllegalArgumentException {
        return cartRepository.getCartItemsFromFirestore(context);
    }

    public final LiveData<RequestStateMediator> addCartProductToFirestoreFromRepository(
            @NonNull final Context context, @NonNull final CartItem cartItem) throws IllegalArgumentException {
        return cartRepository.addCartItemsToFirestore(context, cartItem);
    }
}
