package com.example.festivalwatch.ticket

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.festivalwatch.festivalMaker.FestivalMakerViewModel


class TicketViewModelFactory(
    private val application: Application,
    private val token: String,
    private val username: String,
    private val title: String,
    private val date: String,
    private val time: String,
    private val price: Float,
    private val photoDesc: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TicketViewModel::class.java)) {
            return TicketViewModel(application, token, username, title, date, time, price, photoDesc) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}