package com.example.festivalwatch.festivalCustomizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.festivalwatch.festivalMenu.FestivalMenuViewModel
import java.lang.IllegalArgumentException

class FestivalCustomizerViewModelFactory(
    private val token: String,
    private val isAdmin: String,
    private val id: Int,
    private val username: String,
    private val title: String,
    private val date: String,
    private val time: String,
    private val latitude: Float,
    private val longitude: Float,
    private val address: String,
    private val description: String,
    private val photoDesc: String,
    private val price: Float,
    private val admin: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FestivalCustomizerViewModel::class.java)) {
            return FestivalCustomizerViewModel(token, isAdmin, id, username, title, date, time, latitude, longitude, address, description, photoDesc, price, admin) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}