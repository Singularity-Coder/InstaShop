package com.singularitycoder.instashop.products.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.products.model.ProductItem;
import com.singularitycoder.instashop.products.repository.ProductRepository;
import com.singularitycoder.instashop.wishlist.model.WishlistItem;

public final class ProductViewModel extends ViewModel {

    @NonNull
    private ProductRepository productRepository = ProductRepository.getInstance();

    public final LiveData<RequestStateMediator> getProductListRepository(@NonNull final String category) throws IllegalArgumentException {
        return productRepository.getProductsListFromFirestore(category);
    }

    public final LiveData<RequestStateMediator> getProductFromRepository(@NonNull final String docId) throws IllegalArgumentException {
        return productRepository.getProductInfo(docId);
    }

    public final LiveData<RequestStateMediator> addWishlistProductToFirestoreFromRepository(
            @NonNull final Context context, @NonNull final WishlistItem wishlistItem) throws IllegalArgumentException {
        return productRepository.addWishlistItemsToFirestore(context, wishlistItem);
    }
}
