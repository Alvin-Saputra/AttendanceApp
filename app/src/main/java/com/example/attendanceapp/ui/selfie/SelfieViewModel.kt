package com.example.attendanceapp.ui.selfie

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.attendanceapp.data.remote.response.FaceClassificationResponse
import com.example.attendanceapp.injection.DependencyInjection
import com.example.attendanceapp.repository.Repository
import com.example.attendanceapp.repository.Result

class SelfieViewModel(private val repository: Repository): ViewModel() {

    fun uploadSelfie(context: Context, imageUri: Uri): LiveData<Result<FaceClassificationResponse>> =
        repository.uploadSelfie(context, imageUri)
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SelfieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SelfieViewModel(DependencyInjection.provideRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}