
package com.example.urbancars.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CartItems::class], version = 1, exportSchema = false)
abstract class CartItemDB: RoomDatabase() {
    abstract fun CartItemsDAO(): CartItemsDAO
    companion object {
        @Volatile
        var INSTANCE: CartItemDB? = null

        fun getDBInstance(context: Context): CartItemDB {
            val instance = INSTANCE
            if (instance != null) {
                return instance
            }
            synchronized(this) {
                val roomDB = Room.databaseBuilder(
                    context.applicationContext,
                    CartItemDB::class.java,
                    "CartItems"
                ).build()
                INSTANCE = roomDB
                return roomDB
            }
        }
    }
}
