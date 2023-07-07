package com.example.festivalwatch.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class HomeViewModelFactory(private val token: String, private val isAdmin: String, private val username: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(token, isAdmin, username) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}