package com.example.veritas.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    // Replace this with your actual Render Web Service URL
    private const val BASE_URL = "https://veritas-generation-api.onrender.com/"

    val api: VeritasApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VeritasApiService::class.java)
    }
}