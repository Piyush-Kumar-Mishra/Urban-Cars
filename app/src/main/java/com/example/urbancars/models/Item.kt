package com.example.urbancars.models

import java.util.UUID

data class Item(
    var ItemRandomId: String = UUID.randomUUID().toString(),
    var ItemName: String? = null,
    var ItemCompany: String? = null,
    var ItemYear: Int? = null,
    var ItemPrice: Int? = null,
    var ItemFuelType: String? = null,
    var ItemDistanceCovered: Int? = null,
    var ItemOtherDetails: String? = null,
    var ItemImagesUris: ArrayList<String?>?=null,
    var AdminUid: String? = null,
    var itemCount:Int?=null,
)
