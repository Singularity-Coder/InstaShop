package com.singularitycoder.instashop.products.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.singularitycoder.instashop.cart.model.ProductCartItem;

import java.util.List;

@Dao
public interface ProductCartDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProduct(ProductCartItem productCartItem);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateProduct(ProductCartItem productCartItem);

    @Delete
    void deleteProduct(ProductCartItem productCartItem);

    @Query("SELECT * FROM product_cart_table WHERE id=:id")
    ProductCartItem getProduct(int id);

    @Query("SELECT * FROM product_cart_table ORDER BY productName DESC")
    LiveData<List<ProductCartItem>> getAllProducts();

    @Query("DELETE FROM product_cart_table")
    void deleteAllProducts();
}