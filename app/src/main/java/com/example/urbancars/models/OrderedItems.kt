package com.example.urbancars.models

data class OrderedItems(
    val orderId: String? = null,
    val itemTitle: String? = null,
    val itemPrice: String = "",
    val userAddress: String? = null,
    val itemDate: String? = null,
    val itemStatus: String? = null
)