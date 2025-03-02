package com.example.attendanceapp.injection

import android.content.Context
import com.example.attendanceapp.data.remote.retrofit.ApiConfig
import com.example.attendanceapp.repository.Repository

object DependencyInjection {
    fun provideRepository(): Repository {
        val apiService = ApiConfig.getApiService()
        return Repository(apiService)
    }
}

