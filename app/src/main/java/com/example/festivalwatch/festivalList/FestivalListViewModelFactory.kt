package com.example.festivalwatch.festivalList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException


class FestivalListViewModelFactory(private val token: String, private val isAdmin: String, private val username: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FestivalListViewModel::class.java)) {
            return FestivalListViewModel(token, isAdmin, username) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
