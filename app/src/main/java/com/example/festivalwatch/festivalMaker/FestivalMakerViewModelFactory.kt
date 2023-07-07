package com.example.festivalwatch.festivalMaker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FestivalMakerViewModelFactory(
    private val token: String,
    private val isAdmin: String,
    private val username: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FestivalMakerViewModel::class.java)) {
            return FestivalMakerViewModel(token, isAdmin, username) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}