package com.example.attendanceapp.ui.map

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MapsViewModel: ViewModel(){
    private val _geofenceStatus = MutableLiveData<String>()
    val geofenceStatus = _geofenceStatus

    fun setGeofenceStatus(status: String) {
        _geofenceStatus.value = status
    }
}


class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}