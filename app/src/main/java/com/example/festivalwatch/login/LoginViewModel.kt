package com.example.festivalwatch.login

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.festivalwatch.FestivalApi
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject

class LoginViewModel: ViewModel() {
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _token = MutableLiveData<String>()
    private val _isAdmin = MutableLiveData<String>()
    val token: LiveData<String>
        get() = _token

    val isAdmin: LiveData<String>
        get() = _isAdmin


    private val _navigationState = MutableLiveData<Boolean>(false)
    val navigationState: LiveData<Boolean>
        get() = _navigationState

    private val _navigationUser = MutableLiveData<Boolean>(false)
    val navigationUser: LiveData<Boolean>
        get() = _navigationUser


    private val _eventLoginSuccess = MutableLiveData<Boolean>()
    val eventLoginSuccess: LiveData<Boolean>
        get() = _eventLoginSuccess

    private val _eventLoginFailed = MutableLiveData<Boolean>()
    val eventLoginFailed: LiveData<Boolean>
        get() = _eventLoginFailed

    init {
        Log.i("LoginViewModel", "init")
    }

    override fun onCleared() {
        Log.i("LoginViewModel", "destroyed")
        super.onCleared()
    }

    fun onLogin() {
        val jsonObject = JSONObject()
        jsonObject.put("email_username", username.value)
        jsonObject.put("password", password.value)

        val jsonString = jsonObject.toString()
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())
        Log.i("LoginViewModel", jsonString)
        CoroutineScope(Dispatchers.IO).launch {
            val response = FestivalApi.retrofitService.login(requestBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    Log.i("LoginViewModel", "Response Body " + response.body())
                    Log.i("LoginViewModel", prettyJson)

                    val jsonObjectResponse = JSONObject(prettyJson)
                    val token = jsonObjectResponse.getString("token")
                    val isAdmin = jsonObjectResponse.getString("admin")

                    _token.value = token
                    _isAdmin.value = isAdmin
                    Log.i("LoginViewModel", token)
                    Log.i("LoginViewModel", isAdmin)

                    onLoginSuccess()
                }
                else {
                    Log.i("LoginViewModel", "Error Response: " + response.errorBody()?.string())
                    Log.i("LoginViewModel", "Nu a mers")
                    onLoginFailed()
                }
            }
        }
    }

    fun navigateToRegister() {
        Log.i("LoginViewModel", "Ma duc la pagina de inregistrare")
        _navigationState.value = true
    }


    fun resetNavigationState() {
        _navigationState.value = false
    }

    fun loginInApp() {
        Log.i("LoginViewModel", "Ma duc la pagina de user")
        _navigationUser.value = true

    }

    fun onLoginSuccessComplete() {
        _eventLoginSuccess.value = false
    }

    fun onLoginSuccess() {
        _eventLoginSuccess.value = true
    }

    fun onLoginFailedComplete() {
        _eventLoginFailed.value = false
    }

    fun onLoginFailed() {
        _eventLoginFailed.value = true
    }



}