package com.example.attendanceapp.ui.selfie

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.attendanceapp.data.pref.UserModel
import com.example.attendanceapp.data.remote.response.FaceClassificationResponse
import com.example.attendanceapp.injection.DependencyInjection
import com.example.attendanceapp.repository.Repository
import com.example.attendanceapp.repository.Result
import com.example.attendanceapp.ui.main.MainViewModel

class SelfieViewModel(private val repository: Repository): ViewModel() {

    fun uploadSelfie(context: Context, imageUri: Uri, userId: String): LiveData<Result<FaceClassificationResponse>> =
        repository.uploadSelfie(context, imageUri, userId)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SelfieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SelfieViewModel(DependencyInjection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}