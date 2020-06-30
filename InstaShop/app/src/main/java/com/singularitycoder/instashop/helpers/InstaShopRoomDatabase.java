package com.singularitycoder.instashop.helpers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.singularitycoder.instashop.cart.dao.CartDao;
import com.singularitycoder.instashop.cart.model.CartItem;
import com.singularitycoder.instashop.wishlist.model.WishlistItem;

@Database(entities = {
        CartItem.class,
        WishlistItem.class
}, version = 1, exportSchema = false)
public abstract class InstaShopRoomDatabase extends RoomDatabase {

    @Nullable
    private static InstaShopRoomDatabase instance;

    @Nullable
    public abstract CartDao productCartDao();

    @NonNull
    public static synchronized InstaShopRoomDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room
                    .databaseBuilder(context.getApplicationContext(), InstaShopRoomDatabase.class, "instashop_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}