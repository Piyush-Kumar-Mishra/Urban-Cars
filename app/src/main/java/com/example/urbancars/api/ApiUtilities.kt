package com.example.urbancars.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtilities {
    val statusApi: ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.phonepe.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}