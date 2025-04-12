package com.example.urbancars.viewmodels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.urbancars.Room.CartItemDB
import com.example.urbancars.Room.CartItems
import com.example.urbancars.Room.CartItemsDAO
import com.example.urbancars.Utils
import com.example.urbancars.models.All
import com.example.urbancars.models.Item
import com.example.urbancars.models.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("Pref", MODE_PRIVATE)
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
        val db =
            FirebaseDatabase.getInstance().getReference("AllAdmin").child("Items").child(category)
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

    fun updateItemCount(product: Item, itemCount: Int) {
        FirebaseDatabase.getInstance().getReference("AllAdmin").child("Items")
            .child("${product.ItemFuelType}").child(product.ItemRandomId)
            .child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("AllAdmin").child("Company")
            .child("${product.ItemCompany}").child(product.ItemRandomId)
            .child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("AllAdmin").child("Price")
            .child(product.ItemRandomId).child("ItemCount").setValue(itemCount)
    }

    fun saveAddressStatus(status: Boolean) {
        sharedPreferences.edit().putBoolean("AddressStatus", status).apply()
    }

//    fun saveAddressStatus() {
//        sharedPreferences.edit().putBoolean("AddressStatus", true).apply()
//    }

    fun getAddressStatus(): MutableLiveData<Boolean> {
        val status = MutableLiveData<Boolean>()
        status.value = sharedPreferences.getBoolean("AddressStatus", false)
        return status
    }

    fun savingAddressInFB(address: String) {
        FirebaseDatabase.getInstance().getReference("AllUsers").child("Users")
            .child(Utils.getCurrentUserId()).child("userAddress").setValue(address)


    }

    fun getUserAddress(callback: (String) -> Unit) {
        val db = FirebaseDatabase.getInstance().getReference("AllUsers").child("Users")
            .child(Utils.getCurrentUserId()).child("userAddress")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val address = snapshot.getValue(String::class.java)
                    if (address != null) {
                        callback(address)
                    }
                } else {
                    callback(null.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


//    fun saveOrderedProducts(orders: Order) {
//        FirebaseDatabase.getInstance().getReference("AllAdmin").child("Orders")
//            .child(orders.orderId!!).setValue(orders)
//    }


    fun saveOrderedProducts(orders: Order) {
        Log.d("FirebaseDebug", "Saving order: $orders")
        FirebaseDatabase.getInstance().getReference("AllAdmin").child("Orders")
            .child(orders.orderId!!).setValue(orders)
            .addOnSuccessListener {
                Log.d("FirebaseDebug", "Order saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseDebug", "Failed to save order", e)
            }
    }


    fun deleteCartItems() {
        CoroutineScope(Dispatchers.IO).launch {
            cartItemDAO.deleteCartItems()
        }
    }

    fun saveAfterOrdering(stock: Int, product: CartItems) {
        val database = FirebaseDatabase.getInstance().getReference("AllAdmin")

        Log.d("FirebaseDebug", "Updating itemCount and itemInStock for ${product.ItemId}")

        product.ItemFuelType?.let {
            database.child("Items").child(it).child(product.ItemId)
                .child("itemCount").setValue(0)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) Log.d("FirebaseUpdate", "Items -> itemCount set to 0")
                    else Log.e(
                        "FirebaseError",
                        "Failed to update itemCount in Items",
                        task.exception
                    )
                }
        }

        product.ItemCompany?.let {
            database.child("Company").child(it).child(product.ItemId)
                .child("itemCount").setValue(0)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) Log.d("FirebaseUpdate", "Company -> itemCount set to 0")
                    else Log.e(
                        "FirebaseError",
                        "Failed to update itemCount in Company",
                        task.exception
                    )
                }
        }

        database.child("Price").child(product.ItemId).child("ItemCount")
            .setValue(0)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) Log.d("FirebaseUpdate", "Price -> ItemCount set to 0")
                else Log.e("FirebaseError", "Failed to update ItemCount in Price", task.exception)
            }

        product.ItemFuelType?.let {
            database.child("Items").child(it).child(product.ItemId)
                .child("itemInStock").setValue(stock)
        }

        product.ItemCompany?.let {
            database.child("Company").child(it).child(product.ItemId)
                .child("itemInStock").setValue(stock)
        }
        database.child("Price").child(product.ItemId).child("itemInStock").setValue(stock)
    }

    fun getAllOrders(): Flow<List<Order>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("AllAdmin").child("Orders")
            .orderByChild("orderStatus")
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Order>()
                for (orders in snapshot.children) {
                    val order = orders.getValue(Order::class.java)
                    if (order?.orderingUserId == Utils.getCurrentUserId()) {
                        orderList.add(order)
                    }
                }
                Log.d("FirebaseData", "Orders: $orderList")
                trySend(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Database error: ${error.message}")
            }
        }
        db.addValueEventListener(eventListener)
        awaitClose {
            db.removeEventListener(eventListener)
        }

        db.addValueEventListener(eventListener)
        awaitClose {
            db.removeEventListener(eventListener)
        }

    }


    fun getOrderedItems(orderId: String): Flow<List<CartItems>> = callbackFlow {
        val db =
            FirebaseDatabase.getInstance().getReference("AllAdmin").child("Orders").child(orderId)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Order::class.java)
                trySend(order?.orderList!!)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }

    fun getItemTypes(): Flow<List<All>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("AllAdmin/Items")

        val eventListner = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemTypeList = ArrayList<All>()
                for (itemtype in snapshot.children) {
                    val itemTypeName = itemtype.key
                    val itemList = ArrayList<Item>()
                    for (items in itemtype.children) {
                        val item = items.getValue(Item::class.java)
                        itemList.add(item!!)

                    }
                    val all = All(itemType = itemTypeName, items = itemList)
                    itemTypeList.add(all)
                }
                trySend(itemTypeList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        db.addValueEventListener(eventListner)
        awaitClose { db.removeEventListener(eventListner) }
    }

fun saveAddress(address:String){
    FirebaseDatabase.getInstance().getReference("AllUsers").child("Users")
        .child(Utils.getCurrentUserId()).child("userAddress").setValue(address)
}
    fun logout(){
        FirebaseAuth.getInstance().signOut()
    }

}