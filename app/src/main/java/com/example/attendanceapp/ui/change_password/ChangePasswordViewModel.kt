package com.example.attendanceapp.ui.change_password

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.attendanceapp.data.pref.UserModel
import com.example.attendanceapp.data.remote.response.LoginResponse
import com.example.attendanceapp.data.remote.response.UpdatePasswordResponse
import com.example.attendanceapp.injection.DependencyInjection
import com.example.attendanceapp.repository.Repository
import com.example.attendanceapp.repository.Result
import com.example.attendanceapp.ui.login.LoginViewModel

class ChangePasswordViewModel (private val repository: Repository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
    fun updatePasswordRequest(userId: String, currentPassword: String, newPassword:String): LiveData<Result<UpdatePasswordResponse>> {
        return repository.updatePasswordRequest(userId, currentPassword, newPassword)
    }
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChangePasswordViewModel(DependencyInjection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}