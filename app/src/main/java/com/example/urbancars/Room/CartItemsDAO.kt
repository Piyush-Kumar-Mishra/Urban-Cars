package com.example.urbancars.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CartItemsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartProduct(items: CartItems)

    @Update
    suspend fun updateCartProduct(items: CartItems)

    @Query("SELECT * FROM CART_ITEMS")
    fun getAllItems(): LiveData<List<CartItems>>

    @Query("DELETE FROM CART_ITEMS WHERE ItemId = :ItemId")
    suspend fun deleteCartItem(ItemId :String)

}


