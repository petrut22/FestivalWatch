package com.example.festivalwatch.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.festivalwatch.festivalMenu.FestivalMenuViewModel
import java.lang.IllegalArgumentException

class ProfileViewModelFactory(private val token: String, private val isAdmin: String, private val username: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom( ProfileViewModel::class.java)) {
            return ProfileViewModel(token, isAdmin, username) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}