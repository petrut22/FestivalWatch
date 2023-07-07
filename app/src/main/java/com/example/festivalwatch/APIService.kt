package com.example.festivalwatch

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

//private const val BASE_URL = "http://192.168.137.11:8000"

//private const val BASE_URL = "http://192.168.1.129:8000"

private const val BASE_URL = "http://192.168.97.11:8000"



private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .build()

interface APIService {

    @POST("/api/authentication/login")
    suspend fun login(@Body requestBody: RequestBody) : Response<ResponseBody>

    @POST("/api/authentication/register")
    suspend fun register(@Body requestBody: RequestBody) : Response<ResponseBody>

    @GET("/api/users/user/{username}")
    suspend fun getInfoUser(@Path("username") username: String, @HeaderMap headers: Map<String, String>): Response<ResponseBody>

    @PUT("/api/users/user/{username}")
    suspend fun editUser(@Path("username") username: String, @HeaderMap headers: Map<String, String>, @Body requestBody: MultipartBody): Response<ResponseBody>

    @GET("/api/festivals")
    suspend fun getFestivals(@HeaderMap headers: Map<String, String>): Response<ResponseBody>

    @PUT("/api/festivals/{id}")
    suspend fun updateFestival(@Path("id") id: String, @HeaderMap headers: Map<String, String>, @Body requestBody: MultipartBody): Response<ResponseBody>

    @POST("/api/festivals")
    suspend fun createFestival(@HeaderMap headers: Map<String, String>, @Body requestBody: MultipartBody): Response<ResponseBody>

    @DELETE("/api/festivals/{id}")
    suspend fun deleteFestival(@Path("id") id: String, @HeaderMap headers: Map<String, String>): Response<ResponseBody>

    @GET("/api/tickets/{username}/{festival}")
    suspend fun getPaymentInformation(@Path("username") username: String, @Path("festival") festival: String, @HeaderMap headers: Map<String, String>): Response<ResponseBody>

    @POST("/api/tickets")
    suspend fun updatePaymentInformation(@HeaderMap headers: Map<String, String>, @Body requestBody: RequestBody): Response<ResponseBody>

    @GET("/api/users")
    suspend fun getUsersFestivals(@HeaderMap headers: Map<String, String>): Response<ResponseBody>

    @PUT("api/users/organizer/{username}")
    suspend fun addOrganizerFestivals(@Path("username") username: String, @HeaderMap headers: Map<String, String>, @Body requestBody: RequestBody): Response<ResponseBody>
}

object FestivalApi {
    val retrofitService : APIService by lazy { retrofit.create(APIService::class.java) }
}