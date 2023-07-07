package com.example.festivalwatch.festivalList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.festivalwatch.FestivalApi
import com.example.festivalwatch.FestivalClass
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class FestivalListViewModel(tokenArg: String, isAdminArg: String, usernameArg: String) : ViewModel() {
    var festivalsArrayList : ArrayList<FestivalClass> = arrayListOf()
    var token = tokenArg
    var isAdmin = isAdminArg
    var username = usernameArg

    private val _getDataSuccess = MutableLiveData<Boolean>()
    val getDataSuccess: LiveData<Boolean>
        get() = _getDataSuccess

    fun getData() {
        festivalsArrayList.clear()

        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token " + token

        CoroutineScope(Dispatchers.IO).launch {
            val response = FestivalApi.retrofitService.getFestivals(headerMap)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    Log.i("FestivalListViewModel", "ok1 ")

                    val jsonArray = JSONArray(prettyJson)
                    Log.i("FestivalListViewModel", "ok2 ")
                    for (i in 0 until jsonArray.length()) {
                        val jsonFestival = jsonArray.getJSONObject(i)
                        val jsonFestivalId = jsonFestival.getInt("id")
                        val jsonFestivalName = jsonFestival.getString("festival_name")
                        val jsonFestivalDate = jsonFestival.getString("date")
                        val jsonFestivalTime = jsonFestival.getString("time")
                        val jsonFestivalLat = jsonFestival.getDouble("location_lat")
                        val jsonFestivalLon = jsonFestival.getDouble("location_lon")
                        val jsonFestivalAddress = jsonFestival.getString("location_address")
                        val jsonFestivalDescription = jsonFestival.getString("description")
                        val jsonFestivalPhotoCover = jsonFestival.getString("photo_cover")
                        val jsonFestivalPhotoDesc = jsonFestival.getString("photo_description")
                        val jsonFestivalPrice = jsonFestival.getString("price")
                        val jsonFestivalAdminObj = jsonFestival.getJSONObject("festival_admin")
                        val jsonFestivalAdmin = jsonFestivalAdminObj.getString("username")
                        Log.i("FestivalListViewModel", "ok3 ")
                        val festival = FestivalClass(
                            jsonFestivalId,
                            jsonFestivalName,
                            jsonFestivalDate,
                            jsonFestivalTime.substring(0, 5),
                            jsonFestivalLat.toFloat(),
                            jsonFestivalLon.toFloat(),
                            jsonFestivalAddress,
                            jsonFestivalDescription,
                            jsonFestivalPhotoCover,
                            jsonFestivalPhotoDesc,
                            jsonFestivalPrice.toFloat(),
                            jsonFestivalAdmin
                        )
                        Log.i("FestivalListViewModel", "ok4 ")
                        festivalsArrayList.add(festival)
                    }
                    Log.i("FestivalListViewModel", "ok5 ")
                    _getDataSuccess.value = true

                    Log.i("FestivalListViewModel", "A mers " + jsonArray + "\n" + prettyJson)
                    Log.i("FestivalListViewModel", "A mers " + response.body()!!.string())
                }
                else {
                    Log.i("FestivalListViewModel", "Error Response: " + response.errorBody()?.string())
                    Log.i("FestivalListViewModel", "Nu a mers")
                }
            }
        }


        _getDataSuccess.value = true

        Log.i("FestivalListViewModel", "Am populat vectorul!")

    }


    fun getDataComplete() {
        _getDataSuccess.value = false
    }
}