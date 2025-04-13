package com.example.urbancars.models

import com.example.urbancars.Room.CartItems


data class Order(
    val orderId: String? = null,
    val orderDate: String? = null,
    val orderStatus: Int? = null,
    val orderingUserId: String? = null,
    val userAddress: String? = null,
    val orderList:List<CartItems>?=null
)

