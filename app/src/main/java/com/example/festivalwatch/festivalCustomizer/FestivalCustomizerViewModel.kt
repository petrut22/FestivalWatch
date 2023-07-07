package com.example.festivalwatch.festivalCustomizer

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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class FestivalCustomizerViewModel(
    tokenArg: String,
    isAdminArg: String,
    idArg: Int,
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

    val id = MutableLiveData(idArg)
    val title = MutableLiveData(titleArg)
    val date = MutableLiveData(dateArg)
    val time = MutableLiveData(timeArg)
    val latitude = MutableLiveData(latitudeArg.toString())
    val longitude = MutableLiveData(longitudeArg.toString())
    val address = MutableLiveData(addressArg)
    val description = MutableLiveData(descriptionArg)
    val photoDesc = MutableLiveData(photoDescArg)
    val price = MutableLiveData(priceArg.toString())
    val admin = MutableLiveData(adminArg)


    private val _eventFestivalUpdateSuccess = MutableLiveData<Boolean>()
    val eventFestivalUpdateSuccess: LiveData<Boolean>
        get() = _eventFestivalUpdateSuccess

    private val _eventFestivalUpdateFailed = MutableLiveData<Boolean>()
    val eventFestivalUpdateFailed: LiveData<Boolean>
        get() = _eventFestivalUpdateFailed

    private val _eventFestivalDeleteSuccess = MutableLiveData<Boolean>()
    val eventFestivalDeleteSuccess: LiveData<Boolean>
        get() = _eventFestivalDeleteSuccess

    private val _eventFestivalDeleteFailed = MutableLiveData<Boolean>()
    val eventFestivalDeleteFailed: LiveData<Boolean>
        get() = _eventFestivalDeleteFailed

    init {
        Log.i("FestivalCustomizerVM", "A intrat")
        Log.i("FestivalCustomizerVM", tokenArg)
        Log.i("FestivalCustomizerVM", idArg.toString())
        Log.i("FestivalCustomizerVM", usernameArg)
        Log.i("FestivalCustomizerVM", titleArg)
        Log.i("FestivalCustomizerVM", dateArg)
        Log.i("FestivalCustomizerVM", timeArg)
        Log.i("FestivalCustomizerVM", latitudeArg.toString())
        Log.i("FestivalCustomizerVM", longitudeArg.toString())
        Log.i("FestivalCustomizerVM", addressArg)
        Log.i("FFestivalCustomizerVM", descriptionArg)
        Log.i("FestivalCustomizerVM", photoDescArg)
        Log.i("FestivalCustomizerVM", priceArg.toString())
        Log.i("FestivalCustomizerVM", adminArg)

    }


    fun updateFestival(imagePathCover: String, imagePathDescription: String) {
        Log.i("FestivalCustomizerVM", "postData() " + token)


        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token " + token


        val requestBodyBuilder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("festival_name", title.value ?: "")
            .addFormDataPart("date", date.value ?: "")
            .addFormDataPart("time", time.value ?: "")
            .addFormDataPart("location_lat", latitude.value.toString())
            .addFormDataPart("location_lon", longitude.value.toString())
            .addFormDataPart("location_address", address.value ?: "")
            .addFormDataPart("description", description.value ?: "")
            .addFormDataPart("price", price.value.toString())


        if(imagePathCover.equals("") == false) {
            val fileC = File(imagePathCover)
            val requestFileCover = RequestBody.create("image/*".toMediaTypeOrNull(), fileC)
            val imageCover =
                MultipartBody.Part.createFormData("photo_cover", "imageCover.jpg", requestFileCover)
            Log.i("FestivalCustomizerVM", "imageCover " + imagePathCover)
            requestBodyBuilder.addPart(imageCover)
        }

        if(imagePathDescription.equals("") == false) {
            val fileD = File(imagePathDescription)
            val requestFileDes = RequestBody.create("image/*".toMediaTypeOrNull(), fileD)
            val imageDescription =
                MultipartBody.Part.createFormData("photo_description", "imageDescription.jpg", requestFileDes)
            Log.i("FestivalCustomizerVM", "imageCover " + imagePathCover)
            requestBodyBuilder.addPart(imageDescription)
        }

        val requestBody = requestBodyBuilder.build()


        CoroutineScope(Dispatchers.IO).launch {
            val response = FestivalApi.retrofitService.updateFestival(id.value.toString(), headerMap, requestBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    Log.i("FestivalCustomizerVM", "A mers")
                    Log.i("FestivalCustomizerVM", prettyJson)

                    onFestivalUpdateSuccess()
                } else {
                    Log.i("FestivalCustomizerVM", "Nu a mers")
                    Log.i("FestivalCustomizerVM", "" + response.code() + " " + response.message())
                    onFestivalUpdateFailed()
                }
            }
        }
    }

    fun deleteFestival() {
        Log.i("FestivalCustomizerVM", "postData() " + token)

        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token " + token

        CoroutineScope(Dispatchers.IO).launch {
            val response = FestivalApi.retrofitService.deleteFestival(id.value.toString(), headerMap)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.i("FestivalCustomizerVM", "A mers")
                    onFestivalDeleteSuccess()
                }
                else {
                    Log.i("FestivalCustomizerVM", "Nu a mers")
                    Log.i("FestivalCustomizerVM", "" + response.code() + " " + response.message())
                    onFestivalDeleteFailed()
                }
            }
        }

        Log.i("FestivalCustomizerVMr", id.toString() + " " + token + " " + username)

    }

    fun onFestivalUpdateSuccess() {
        _eventFestivalUpdateSuccess.value = true
    }

    fun onFestivalUpdateFailed() {
        _eventFestivalUpdateFailed.value = true
    }

    fun onFestivalDeleteSuccess() {
        _eventFestivalUpdateSuccess.value = true
    }

    fun onFestivalDeleteFailed() {
        _eventFestivalUpdateFailed.value = true
    }

}