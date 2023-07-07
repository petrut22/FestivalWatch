package com.example.festivalwatch.festivalMaker

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
import org.json.JSONObject
import java.io.File

class FestivalMakerViewModel(
    tokenArg: String,
    isAdminArg: String,
    usernameArg: String) : ViewModel() {

    var token = tokenArg
    var isAdmin = isAdminArg
    var username = usernameArg

    val _title = MutableLiveData<String>()

    val _date = MutableLiveData<String>()

    val _time = MutableLiveData<String>()

    val _latitude = MutableLiveData<String>()

    val _longitude = MutableLiveData<String>()

    val _address = MutableLiveData<String>()

    val _description = MutableLiveData<String>()

    val _price = MutableLiveData<String>()

    val _emailAdmin = MutableLiveData<String>()

    val _countryAdmin = MutableLiveData<String>()

    val _phoneAdmin = MutableLiveData<String>()



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
        Log.i("FestivalMakerVM", "A intrat")
        Log.i("FestivalMakerVM", tokenArg)
        Log.i("FestivalMakerVM", usernameArg)


    }

    fun userDataGetFestival(imagePathCover: String, imagePathDescription: String) {
        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token " + token
        Log.i("FestivalMakerVM",  "getUserData() " + token)

        CoroutineScope(Dispatchers.IO).launch {
            val response = FestivalApi.retrofitService.getInfoUser(username, headerMap)

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

                    _emailAdmin.value = jsonObject.getString("email")
                    _phoneAdmin.value = jsonObject.getString("phone")
                    _countryAdmin.value = jsonObject.getString("country")


                    Log.i("FestivalMakerVM", "A mers " + username + " " + _emailAdmin.value + " " + _phoneAdmin.value +" " + _countryAdmin.value)
                    createFestival(imagePathCover, imagePathDescription)
                } else {
                    Log.i("FestivalMakerVM", "Error Response: " + response.errorBody()?.string())
                    Log.i("FestivalMakerVM", "Nu a mers")

                }
            }
        }
    }


    fun createFestival(imagePathCover: String, imagePathDescription: String) {
        Log.i("FestivalMakerVM", "postData() " + token)

        Log.i("FestivalMakerVM", "A mers2 " + username + " " + _emailAdmin.value + " " + _phoneAdmin.value +" " + _countryAdmin.value)


        Log.i("FestivalMakerVM", "Valorile din getUserData " + username
                + " " + _emailAdmin.value + " " + _phoneAdmin.value +" " + _countryAdmin.value)


        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token " + token


        val requestBodyBuilder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("festival_name", _title.value!!)
            .addFormDataPart("date", _date.value!!)
            .addFormDataPart("time", _time.value!!)
            .addFormDataPart("location_lat", _latitude.value!!)
            .addFormDataPart("location_lon", _longitude.value!!)
            .addFormDataPart("location_address", _address.value!!)
            .addFormDataPart("description", _description.value!!)
            .addFormDataPart("price", _price.value!!)
            .addFormDataPart("festival_admin[email]", _emailAdmin.value!!)
            .addFormDataPart("festival_admin[username]", username)
            .addFormDataPart("festival_admin[phone]", _phoneAdmin.value!!)
            .addFormDataPart("festival_admin[country]", _countryAdmin.value!!)


        if(imagePathCover.equals("") == false) {
            val fileC = File(imagePathCover)
            val requestFileCover = RequestBody.create("image/*".toMediaTypeOrNull(), fileC)
            val imageCover =
                MultipartBody.Part.createFormData("photo_cover", "imageCover.jpg", requestFileCover)
            Log.i("FestivalMakerVM", "imageCover " + imagePathCover)
            requestBodyBuilder.addPart(imageCover)
        }

        if(imagePathDescription.equals("") == false) {
            val fileD = File(imagePathDescription)
            val requestFileDes = RequestBody.create("image/*".toMediaTypeOrNull(), fileD)
            val imageDescription =
                MultipartBody.Part.createFormData("photo_description", "imageDescription.jpg", requestFileDes)
            Log.i("FestivalMakerVM", "imageCover " + imagePathCover)
            requestBodyBuilder.addPart(imageDescription)
        }

        val requestBody = requestBodyBuilder.build()

        Log.i("FestivalMakerVM", "requestul este " + requestBody.toString())


        CoroutineScope(Dispatchers.IO).launch {
            val response = FestivalApi.retrofitService.createFestival(headerMap, requestBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    Log.i("FestivalMakerVM", "A mers")
                    Log.i("FestivalMakerVM", prettyJson)

                    onFestivalUpdateSuccess()
                } else {
                    Log.i("FestivalMakerVM", "Nu a mers")
                    Log.i("FestivalMakerVM", "" + response.code() + " " + response.message())
                    onFestivalUpdateFailed()
                }
            }
        }
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