package com.example.festivalwatch.organizer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.festivalwatch.FestivalApi
import com.example.festivalwatch.FestivalClass
import com.example.festivalwatch.UserClass
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


class OrganizerViewModel(tokenArg: String, isAdminArg: String, usernameArg: String) : ViewModel() {
    val _token = tokenArg
    val _isAdmin = isAdminArg
    val username = usernameArg

    val _usersArrayList = MutableLiveData<ArrayList<UserClass>>()

    val usersAll = MutableLiveData<String>()
    val adminValues = MutableLiveData<String>()
    val _potentialOrganizer = MutableLiveData<String>()

    private val _eventOrganizerSuccess = MutableLiveData<Boolean>()
    val eventOrganizerSuccess: LiveData<Boolean>
        get() = _eventOrganizerSuccess

    private val _eventOrganizerFailed = MutableLiveData<Boolean>()
    val eventOrganizerFailed: LiveData<Boolean>
        get() = _eventOrganizerFailed

    override fun onCleared() {
        Log.i("OrganizerViewModel", "destroyed")
        super.onCleared()
    }

    init {
        Log.i("OrganizerViewModel", "Am intrat")
    }

    fun addUser(user: UserClass) {
        // Get the current list
        val currentList = _usersArrayList.value ?: arrayListOf()

        // Add the new user to the list
        currentList.add(user)

        // Post the updated list back to MutableLiveData
        _usersArrayList.postValue(currentList)
    }


    fun getData() {
        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token " + _token
        Log.i("OrganizerViewModel",  "addOrganiserFestivals() " + _token)

        CoroutineScope(Dispatchers.IO).launch {
            val response = FestivalApi.retrofitService.getUsersFestivals(headerMap)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )
                    val builderUsers = StringBuilder()
                    builderUsers.clear()
                    val builderAdminValues = StringBuilder()
                    builderAdminValues.clear()

                    val jsonArray = JSONArray(prettyJson)
                    for (i in 0 until jsonArray.length()) {
                        val jsonFestival = jsonArray.getJSONObject(i)
                        val jsonUsername = jsonFestival.getString("username")
                        val jsonIsAdmin = jsonFestival.getString("admin")
                        Log.i("OrganizerViewModel", "da am primit vectorul ")
                        val user = UserClass(
                            jsonUsername,
                            jsonIsAdmin
                        )
                        Log.i("OrganizerViewModel", jsonUsername)
                        addUser(user)
                        builderUsers.append(jsonUsername)
                                    .append("\n\n")

                        builderAdminValues.append("admin:  ")
                                    .append(jsonIsAdmin)
                                    .append("\n\n")

                        Log.i("FestivalListViewModel", user.toString())
                    }

                    usersAll.value = builderUsers.toString()
                    adminValues.value = builderAdminValues.toString()


                    Log.i("OrganizerViewModel", "A mers ")
                } else {
                    Log.i("OrganizerViewModel", "Error Response: " + response.errorBody()?.string())
                    Log.i("OrganizerViewModel", "Nu a mers")

                }
            }
        }
    }

    fun addOrganizer() {
        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token " + _token

        Log.i("OrganizerViewModel",  "addOrganiserFestivals() " + _token)

        val jsonObject = JSONObject()
        jsonObject.put("admin", "true")
        val jsonString = jsonObject.toString()
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        Log.i("OrganizerViewModel", jsonString)

        CoroutineScope(Dispatchers.IO).launch {
            val response = FestivalApi.retrofitService.addOrganizerFestivals(_potentialOrganizer.value!!, headerMap, requestBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    Log.i("OrganizerViewModel", "Response Body " + response.body())
                    Log.i("OrganizerViewModel", prettyJson)

                    val jsonObjectResponse = JSONObject(prettyJson)

                    getOrganizerSuccess()
                }
                else {
                    Log.i("LoginViewModel", "Error Response: " + response.errorBody()?.string())
                    Log.i("LoginViewModel", "Nu a mers")
                    getOrganizerFailed()
                }
            }
        }
    }

    fun getOrganizerSuccessComplete() {
        _eventOrganizerSuccess.value = false
    }

    fun getOrganizerSuccess() {
        _eventOrganizerSuccess.value = true
    }

    fun getOrganizerFailedComplete() {
        _eventOrganizerFailed.value = false
    }

    fun getOrganizerFailed() {
        _eventOrganizerFailed.value = true
    }

}