package com.example.attendanceapp.data.remote.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    fun getApiService(): ApiService {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS) // Timeout untuk koneksi (60 detik)
            .readTimeout(120, TimeUnit.SECONDS)    // Timeout untuk membaca data (60 detik)
            .writeTimeout(120, TimeUnit.SECONDS)   // Timeout untuk menulis
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://renewed-kettie-personal-usage-only-4ccf7891.koyeb.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }
}