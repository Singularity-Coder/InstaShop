package com.singularitycoder.instashop.products.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.products.repository.ProductRepository;

public class ProductViewModel extends ViewModel {

    @NonNull
    private ProductRepository productRepository = ProductRepository.getInstance();

    public LiveData<RequestStateMediator> getProductListRepository(@NonNull final String category) throws IllegalArgumentException {
        return productRepository.getProductsListFromFirestore(category);
    }

    public LiveData<RequestStateMediator> getProductFromRepository(@NonNull final String docId) throws IllegalArgumentException {
        return productRepository.getProductInfo(docId);
    }
}
