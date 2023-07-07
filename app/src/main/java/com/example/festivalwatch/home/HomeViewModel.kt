package com.example.festivalwatch.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class HomeViewModel(tokenArg: String, isAdminArg: String, usernameArg: String) : ViewModel() {
    val _token = tokenArg
    val _isAdmin = isAdminArg
    val username = usernameArg

    private val _navigationToFestivals = MutableLiveData<Boolean>(false)
    val navigationToFestivals: LiveData<Boolean>
        get() = _navigationToFestivals

    private val _navigationToProfile = MutableLiveData<Boolean>(false)
    val navigationToProfile: LiveData<Boolean>
        get() = _navigationToProfile


    init {
        Log.i("HomeViewModel", "init")
    }

    override fun onCleared() {
        Log.i("HomeViewModel", "destroyed")
        super.onCleared()
    }

    fun navigateToFestivals() {
        Log.i("LoginViewModel", "Ma duc la pagina de inregistrare")
        _navigationToFestivals.value = true
    }

    fun navigateToProfile() {
        Log.i("LoginViewModel", "Ma duc la pagina de inregistrare")
        _navigationToProfile.value = true
    }

    init {
        Log.i("HomeViewModel", "Am intrat")
    }

}