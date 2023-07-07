package com.example.festivalwatch.ticketQR

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TicketQRViewModelFactory(
    private val token: String,
    private val username: String,
    private val title: String,
    private val date: String,
    private val time: String,
    private val price: Float,
    private val codeQR: String,
    private val photoDesc: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TicketQRViewModel::class.java)) {
            return TicketQRViewModel(token, username, title, date, time, price, codeQR, photoDesc) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}