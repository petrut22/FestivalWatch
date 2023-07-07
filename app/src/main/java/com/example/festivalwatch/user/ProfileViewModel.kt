package com.example.festivalwatch.user

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.festivalwatch.FestivalApi
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File


class ProfileViewModel(tokenArg: String, isAdminArg: String, usernameArg: String): ViewModel()  {
    val _token = tokenArg
    val _isAdmin = isAdminArg
    val usernameArgument = usernameArg

    private val _navigationToLogin = MutableLiveData<Boolean>(false)
    val navigationToLogin: LiveData<Boolean>
        get() = _navigationToLogin

    private val _eventDataSuccess = MutableLiveData<Boolean>()
    val eventDataSuccess: LiveData<Boolean>
        get() = _eventDataSuccess

    private val _eventDataFailed = MutableLiveData<Boolean>()
    val eventDataFailed: LiveData<Boolean>
        get() = _eventDataFailed

    private val _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _phoneNumber = MutableLiveData<String>()
    val phone: LiveData<String>
        get() = _phoneNumber


    private val _countryName = MutableLiveData<String>()
    val country: LiveData<String>
        get() = _countryName

    val _image = MutableLiveData<String>()
    val image: LiveData<String>
        get() = _image



    private val _eventEditprofileSuccess = MutableLiveData<Boolean>()
    val eventEditprofileSuccess: LiveData<Boolean>
        get() = _eventEditprofileSuccess

    private val _eventEditprofileFailed = MutableLiveData<Boolean>()
    val eventEditprofileFailed: LiveData<Boolean>
        get() = _eventEditprofileFailed

    fun navigateToLogin() {
        Log.i("ProfileViewModel", "Ma duc la pagina de loggare")
        _navigationToLogin.value = true
    }

    fun updateValues(usernameArg: String, emailArg: String, phoneArg: String, countryArg: String) {
        Log.i("ProfileViewModel", "Updated the values of profile")
        _username.value = usernameArg
        _email.value = emailArg
        _phoneNumber.value = phoneArg
        _countryName.value = countryArg
    }


    fun getData() {
        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token " + _token
        Log.i("ProfileViewModel",  "getData() " + _token)

        CoroutineScope(Dispatchers.IO).launch {
            val response = FestivalApi.retrofitService.getInfoUser(usernameArgument, headerMap)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    val jsonObject = JSONObject(prettyJson)

                    _username.value = jsonObject.getString("username")
                    _email.value = jsonObject.getString("email")
                    _phoneNumber.value = jsonObject.getString("phone")
                    _countryName.value = jsonObject.getString("country")

                    if (jsonObject.has("photo")) {
                        _image.value = jsonObject.getString("photo")
                    }

                    Log.i("ProfileViewModel",  "photo " + _image.value)

                    _eventDataSuccess.value = true

                    Log.i("ProfileViewModel", "A mers " + _username.value + " " + _email.value + " " + _phoneNumber.value +" " + _countryName.value)
                } else {
                    Log.i("ProfileViewModel", "Error Response: " + response.errorBody()?.string())
                    Log.i("ProfileViewModel", "Nu a mers")

                    _eventDataFailed.value = true
                }
            }
        }
    }


    fun postImageData(imagePath: String) {
        Log.i("ProfileViewModel",  "postData() " + _token)

        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token " + _token

        val requestBodyBuilder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("email", email.value ?: "")
            .addFormDataPart("username", username.value ?: "")
            .addFormDataPart("phone", phone.value ?: "")
            .addFormDataPart("country", country.value ?: "")

        if(imagePath.equals("") == false) {
            val fileC = File(imagePath)
            val requestFileCover = RequestBody.create("image/*".toMediaTypeOrNull(), fileC)
            val imagePart = MultipartBody.Part.createFormData("photo", "image.jpg", requestFileCover)
            Log.i("ProfileViewModel", "imageCover " + imagePath)
            requestBodyBuilder.addPart(imagePart)
        }

        val requestBody = requestBodyBuilder.build()

        print(requestBody)


        CoroutineScope(Dispatchers.IO).launch {
            val response = FestivalApi.retrofitService.editUser(usernameArgument, headerMap, requestBody)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    Log.i("ProfileViewModel", "A mers")
                    Log.i("ProfileViewModel", prettyJson)

                    onEditSuccess()
                }
                else {
                    Log.i("ProfileViewModel", "Nu a mers")
                    Log.i("ProfileViewModel", "" + response.code() + " " + response.message())
                    onEditFailed()
                }
            }
        }
    }




    fun onEditSuccessComplete() {
        _eventEditprofileSuccess.value = false
    }

    fun onEditSuccess() {
        _eventEditprofileSuccess.value = true
    }

    fun onEditFailedComplete() {
        _eventEditprofileFailed.value = false
    }

    fun onEditFailed() {
        _eventEditprofileFailed.value = true
    }

    fun getDataComplete() {
        _eventDataSuccess.value = false
    }

    fun getDataFailed() {
        _eventDataFailed.value = false
    }


}