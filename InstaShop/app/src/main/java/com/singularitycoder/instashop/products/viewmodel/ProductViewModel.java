package com.singularitycoder.instashop.products.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.cart.model.ProductCartItem;
import com.singularitycoder.instashop.products.repository.ProductRepository;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {

    @NonNull
    private ProductRepository productRepository = ProductRepository.getInstance();

    @Nullable
    private LiveData<List<ProductCartItem>> cartProductList;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        cartProductList = productRepository.getAll();
    }

    // ROOM START______________________________________________________________

    public void insert(ProductCartItem note) {
        productRepository.insert(note);
    }

    public void update(ProductCartItem note) {
        productRepository.update(note);
    }

    public void delete(ProductCartItem note) {
        productRepository.delete(note);
    }

    public void deleteAll() {
        productRepository.deleteAll();
    }

    public LiveData<List<ProductCartItem>> getAll() {
        return cartProductList;
    }

    // ROOM END______________________________________________________________

    public LiveData<RequestStateMediator> getProductListRepository(@NonNull final String category) throws IllegalArgumentException {
        return productRepository.getProductsListFromFirestore(category);
    }

    public LiveData<RequestStateMediator> getProductFromRepository(@NonNull final String docId) throws IllegalArgumentException {
        return productRepository.getProductInfo(docId);
    }
}
