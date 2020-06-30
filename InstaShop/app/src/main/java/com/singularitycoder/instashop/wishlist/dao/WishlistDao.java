package com.singularitycoder.instashop.wishlist.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.singularitycoder.instashop.wishlist.model.WishlistItem;

import java.util.List;

@Dao
public interface WishlistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProduct(WishlistItem wishlistItem);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateProduct(WishlistItem wishlistItem);

    @Delete
    void deleteProduct(WishlistItem wishlistItem);

    @Query("SELECT * FROM wishlist_table WHERE RoomId=:id")
    WishlistItem getProduct(int id);

    @Query("SELECT * FROM wishlist_table ORDER BY productName ASC")
    LiveData<List<WishlistItem>> getAllProducts();

    @Query("DELETE FROM wishlist_table")
    void deleteAllProducts();
}