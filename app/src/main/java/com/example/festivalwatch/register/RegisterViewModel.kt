package com.example.festivalwatch.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.festivalwatch.FestivalApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class RegisterViewModel: ViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val username = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val country = MutableLiveData<String>()

    private val _eventRegisterSuccess = MutableLiveData<Boolean>()
    val eventRegisterSuccess: LiveData<Boolean>
        get() = _eventRegisterSuccess

    private val _eventRegisterFailed = MutableLiveData<Boolean>()
    val eventRegisterFailed: LiveData<Boolean>
        get() = _eventRegisterFailed

    private val _navigationState = MutableLiveData<Boolean>(false)
    val navigationState: LiveData<Boolean>
        get() = _navigationState

    init {
        Log.i("RegisterViewModel", "init")
    }

    override fun onCleared() {
        Log.i("RegisterViewModel", "destroyed")
        super.onCleared()
    }

    fun onRegister() {
        val jsonObject = JSONObject()
        jsonObject.put("email", email.value)
        jsonObject.put("password", password.value)
        jsonObject.put("username", username.value)
        jsonObject.put("phone", phone.value)
        jsonObject.put("country", country.value)

        val jsonString = jsonObject.toString()
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())
        CoroutineScope(Dispatchers.IO).launch {
            val response = FestivalApi.retrofitService.register(requestBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    Log.i("RegisterViewModel", "Response Body " +  response.body())
                    Log.i("RegisterViewModel", prettyJson)

                    onRegisterSuccess()
                }
                else {
                    Log.i("RegisterViewModel", "Error Response: " + response.errorBody()?.string())
                    Log.i("RegisterViewModel", "Nu a mers")
                    onRegisterFailed()
                }
            }
        }
    }

    fun navigateToLogin() {
        Log.i("RegisterViewModel", "Ma duc la pagina de loggare")
        _navigationState.value = true
    }

    fun resetNavigationState() {
        _navigationState.value = false
    }

    fun registerInApp() {
        Log.i("RegisterViewModel", "email" + email)
        Log.i("RegisterViewModel", "username" + username)
        Log.i("RegisterViewModel", "password" + password)
        Log.i("RegisterViewModel", "phone" + phone)
        Log.i("RegisterViewModel", "country" + country)
    }

    fun onRegisterSuccessComplete() {
        _eventRegisterSuccess.value = false
    }

    fun onRegisterSuccess() {
        _eventRegisterSuccess.value = true
    }

    fun onRegisterFailedComplete() {
        _eventRegisterFailed.value = false
    }

    fun onRegisterFailed() {
        _eventRegisterFailed.value = true
    }


}