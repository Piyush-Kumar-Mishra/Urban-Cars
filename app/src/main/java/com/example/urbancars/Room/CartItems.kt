
package com.example.urbancars.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CART_ITEMS")
data class CartItems(
    @PrimaryKey var ItemId: String = "random",
    var AdminUid: String? = null,
    var ItemName: String? = null,
    var ItemCount: Int? = null,
    var ItemCompany: String? = null,
    var ItemYear: Int? = null,
    var ItemPrice: String,
    var ItemFuelType: String? = null,
    var ItemDistanceCovered: Int? = null,
    var ItemOtherDetails: String? = null,
    var ItemImages: String? = null
)