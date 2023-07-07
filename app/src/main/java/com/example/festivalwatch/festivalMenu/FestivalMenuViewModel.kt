package com.example.festivalwatch.festivalMenu

import android.util.Log
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
import org.json.JSONObject

class FestivalMenuViewModel(
    tokenArg: String,
    isAdminArg: String,
    usernameArg: String,
    titleArg: String,
    dateArg: String,
    timeArg: String,
    latitudeArg: Float,
    longitudeArg: Float,
    addressArg: String,
    descriptionArg: String,
    photoDescArg: String,
    priceArg: Float,
    adminArg: String) : ViewModel() {

    var token = tokenArg
    var isAdmin = isAdminArg
    var username = usernameArg
    val title = MutableLiveData(titleArg)
    val date = MutableLiveData(dateArg)
    val time = MutableLiveData(timeArg)
    val latitude = MutableLiveData(latitudeArg)
    val longitude = MutableLiveData(longitudeArg)
    val address = MutableLiveData(addressArg)
    val description = MutableLiveData(descriptionArg)
    val photoDesc = MutableLiveData(photoDescArg)
    val price = MutableLiveData(priceArg)
    val admin= MutableLiveData(adminArg)

    val _qr_code = MutableLiveData<String>()
    val qr_code: LiveData<String>
        get() = _qr_code

    private val _eventDataFailed = MutableLiveData<Boolean>()
    val eventDataFailed: LiveData<Boolean>
        get() = _eventDataFailed

    private val _eventDataSuccess = MutableLiveData<Boolean>()
    val eventDataSuccess: LiveData<Boolean>
        get() = _eventDataSuccess

    init {
        Log.i("FestivalMenuViewModel", "A intrat")
        Log.i("FestivalMenuViewModel", tokenArg)
        Log.i("FestivalMenuViewModel", usernameArg)
        Log.i("FestivalMenuViewModel", titleArg)
        Log.i("FestivalMenuViewModel", dateArg)
        Log.i("FestivalMenuViewModel", timeArg)
        Log.i("FestivalMenuViewModel", latitudeArg.toString())
        Log.i("FestivalMenuViewModel", longitudeArg.toString())
        Log.i("FestivalMenuViewModel", addressArg)
        Log.i("FestivalMenuViewModel", descriptionArg)
        Log.i("FestivalMenuViewModel", photoDescArg)
        Log.i("FestivalMenuViewModel", priceArg.toString())
        Log.i("FestivalMenuViewModel", adminArg)

    }

    fun getQRData() {
        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token " + token
        Log.i("TicketViewModel",  "getData() " + token)

        CoroutineScope(Dispatchers.IO).launch {
            val response = FestivalApi.retrofitService.getPaymentInformation(username, title.value!!, headerMap)

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

                    Log.i("TicketViewModel", "jsonObject " + jsonObject.toString())

                    _qr_code.value = jsonObject.getString("qr_code")

                    _eventDataSuccess.value = true

                    Log.i("TicketViewModel", "A mers " + _qr_code.value)
                } else {
                    Log.i("TicketViewModel", "Error Response: " + response.errorBody()?.string())
                    Log.i("TicketViewModel", "Nu a mers")

                    _eventDataFailed.value = true
                }
            }
        }
    }

    fun getDataComplete() {
        _eventDataSuccess.value = false
    }

    fun getDataFailed() {
        _eventDataFailed.value = false
    }
}