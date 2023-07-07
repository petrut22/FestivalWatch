package com.example.festivalwatch.organizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.festivalwatch.home.HomeViewModel
import java.lang.IllegalArgumentException


class OrganizerViewModelFactory(private val token: String, private val isAdmin: String, private val username: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrganizerViewModel::class.java)) {
            return OrganizerViewModel(token, isAdmin, username) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}