package com.singularitycoder.instashop.cart.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.singularitycoder.instashop.cart.model.CartItem;

import java.util.List;

@Dao
public interface CartDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProduct(CartItem cartItem);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateProduct(CartItem cartItem);

    @Delete
    void deleteProduct(CartItem cartItem);

    @Query("SELECT * FROM cart_table WHERE RoomId=:id")
    CartItem getProduct(int id);

    @Query("SELECT * FROM cart_table ORDER BY productName ASC")
    LiveData<List<CartItem>> getAllProducts();

    @Query("DELETE FROM cart_table")
    void deleteAllProducts();
}