package com.example.attendanceapp.injection

import android.content.Context
import com.example.attendanceapp.data.pref.UserPreference
import com.example.attendanceapp.data.remote.retrofit.ApiConfig
import com.example.attendanceapp.repository.Repository
import com.example.attendanceapp.data.pref.dataStore

object DependencyInjection {
    fun provideRepository(context: Context): Repository {
        val apiService = ApiConfig.getApiService()
        val pref = UserPreference.getInstance(context.dataStore)
        return Repository(apiService, pref)
    }
}

