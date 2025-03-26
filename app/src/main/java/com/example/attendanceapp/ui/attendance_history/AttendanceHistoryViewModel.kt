package com.example.attendanceapp.ui.attendance_history

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.attendanceapp.data.pref.UserModel
import com.example.attendanceapp.data.remote.response.AttendanceDataItem
import com.example.attendanceapp.injection.DependencyInjection
import com.example.attendanceapp.repository.Repository
import com.example.attendanceapp.repository.Result
import com.example.attendanceapp.ui.login.LoginViewModel

class AttendanceHistoryViewModel(private val repository: Repository) : ViewModel() {

    fun getAttendanceHistory(userId: String): LiveData<Result<List<AttendanceDataItem>>> {
        return repository.getAttendanceHistory(userId)
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttendanceHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AttendanceHistoryViewModel(DependencyInjection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}