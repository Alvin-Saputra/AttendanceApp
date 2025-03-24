package com.example.attendanceapp.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.data.pref.UserModel
import com.example.attendanceapp.data.remote.response.LoginResponse
import com.example.attendanceapp.injection.DependencyInjection
import com.example.attendanceapp.repository.Repository
import com.example.attendanceapp.repository.Result
import kotlinx.coroutines.launch


class LoginViewModel(private val repository: Repository) : ViewModel() {

    fun loginRequest(userId: String, password: String): LiveData<Result<LoginResponse>> {
        return repository.loginRequest(userId, password)
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }

    }
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(DependencyInjection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}