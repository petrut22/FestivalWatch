package com.example.festivalwatch.ticketQR

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TicketQRViewModel(
    private val tokenArg: String,
    private val usernameArg: String,
    private val titleArg: String,
    private val dateArg: String,
    private val timeArg: String,
    private val priceArg: Float,
    private val qr_codeArg: String,
    private val photoDescArg: String) :  ViewModel() {

    var token = tokenArg
    var username = usernameArg
    val price = MutableLiveData(priceArg.toString() + " Lei")
    val title = MutableLiveData(titleArg)
    val date = MutableLiveData(dateArg)
    val time = MutableLiveData(timeArg)
    val qr_code = MutableLiveData(qr_codeArg)
    val photoDesc = MutableLiveData(photoDescArg)



}

