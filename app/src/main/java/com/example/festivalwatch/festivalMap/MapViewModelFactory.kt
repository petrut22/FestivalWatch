package com.example.festivalwatch.festivalMap

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.festivalwatch.festivalList.FestivalListViewModel
import java.lang.IllegalArgumentException

class MapViewModelFactory (private val application: Application, private val token: String, private val festival_name: String, private val username: String, private val latitude: Float, private val longitude: Float) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(application, token, festival_name, username, latitude, longitude) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}