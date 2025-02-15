package com.example.urbancars.viewmodels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.urbancars.Room.CartItemDB
import com.example.urbancars.Room.CartItems
import com.example.urbancars.Room.CartItemsDAO
import com.example.urbancars.models.Item
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("Pref", MODE_PRIVATE)
    private val cartItemDAO: CartItemsDAO = CartItemDB.getDBInstance(application).CartItemsDAO()

    // Fetch all products
    fun fetchAllProducts(): Flow<List<Item>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("AllAdmin").child("Items")
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Item>()
                for (fuelTypeSnapshot in snapshot.children) {
                    for (productSnapshot in fuelTypeSnapshot.children) {
                        val product = productSnapshot.getValue(Item::class.java)
                        product?.let { products.add(it) }
                    }
                }
                trySend(products).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }

    // Fetch products by category
    fun getProductFromCategory(category: String): Flow<List<Item>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("AllAdmin").child("Items").child(category)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Item>()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Item::class.java)
                    product?.let { products.add(it) }
                }
                trySend(products).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }

    // Save item count to SharedPreferences
    fun saveItemCount(itemCount: Int) {
        val currentCount = sharedPreferences.getInt("itemCount", 0)
        val newCount = currentCount + itemCount
        sharedPreferences.edit().putInt("itemCount", newCount).apply()
    }

    // Fetch item count from SharedPreferences
    private val _cartItemCount = MutableLiveData<Int>()
    val cartItemCount: LiveData<Int> get() = _cartItemCount

    fun fetchCartItemCount() {
        _cartItemCount.value = sharedPreferences.getInt("itemCount", 0)
    }

    // Insert cart product into Room database
    fun insertCartProduct(item: CartItems) {
        viewModelScope.launch(Dispatchers.IO) {
            cartItemDAO.insertCartProduct(item)
        }
    }

    // Update cart product in Room database
    suspend fun updateCartProduct(items: CartItems) {
        cartItemDAO.updateCartProduct(items)
    }

    // Fetch all cart items from Room database
    fun getCartItems(): LiveData<List<CartItems>> {
        return cartItemDAO.getAllItems()
    }

    // Delete cart item from Room database
    suspend fun deleteCartItem(itemId: String) {
        cartItemDAO.deleteCartItem(itemId)
    }

}