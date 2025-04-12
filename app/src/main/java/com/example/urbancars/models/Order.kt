package com.example.urbancars.models

import com.example.urbancars.Room.CartItems

//data class Order(
//    val orderId:String?=null,
//    val orderList:List<CartItems>?=null,
//    val userAddress:String?=null,
//    val orderDate:String?=null,
//    val orderStatus:Int?=0,
//    val orderingUserId: String= ""
//    )

//package com.example.urbancars.models
//
//import com.example.urbancars.Room.CartItems
//data class Order(
//    val orderId: String? = null,
//    val orderList: List<CartItems>? = null,
//    val userAddress: String? = null,
//    val orderDate: String? = null,
//    val orderStatus: Int? = 0,
//    val orderingUserId: String = ""
//)

//
data class Order(
    val orderId: String? = null,
    val orderDate: String? = null,
    val orderStatus: Int? = null,
    val orderingUserId: String? = null,
    val userAddress: String? = null,
    val orderList:List<CartItems>?=null
)

