package com.example.festivalwatch.ticket

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.festivalwatch.FestivalApi
import com.example.festivalwatch.PaymentsUtil
import com.example.festivalwatch.R
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.pay.Pay
import com.google.android.gms.pay.PayApiAvailabilityStatus
import com.google.android.gms.pay.PayClient
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
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
import org.json.JSONObject
import java.io.File

class TicketViewModel(
    application: Application,
    private val tokenArg: String,
    private val usernameArg: String,
    private val titleArg: String,
    private val dateArg: String,
    private val timeArg: String,
    private val priceArg: Float,
    private val photoDescArg: String) : AndroidViewModel(application) {

    var token = tokenArg
    var username = usernameArg
    var priceVal = priceArg
    val price = MutableLiveData(priceArg.toString() + " Lei")
    val title = MutableLiveData(titleArg)
    val date = MutableLiveData(dateArg)
    val time = MutableLiveData(timeArg)
    val photoDesc = MutableLiveData(photoDescArg)
    var qr_code = ""


    //A client for interacting with the Google Pay API.
    private val paymentsClient: PaymentsClient = PaymentsUtil.createPaymentsClient(application)

    // LiveData with the result of whether the user can pay using Google Pay
    private val _canUseGooglePay: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().also {
            fetchCanUseGooglePay()
        }
    }


    val canUseGooglePay: LiveData<Boolean> = _canUseGooglePay

    /**
     * Determine the user's ability to pay with a payment method supported by your app and display
     * a Google Pay payment button.
     *
     * @return a [LiveData] object that holds the future result of the call.
     * @see [](https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient.html.isReadyToPay)
    ) */
    private fun fetchCanUseGooglePay() {
        val isReadyToPayJson = PaymentsUtil.isReadyToPayRequest()
        if (isReadyToPayJson == null) _canUseGooglePay.value = false

        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())
        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            try {
                _canUseGooglePay.value = completedTask.getResult(ApiException::class.java)
            } catch (exception: ApiException) {
                Log.w("isReadyToPay failed", exception)
                _canUseGooglePay.value = false
            }
        }
    }


    fun getLoadPaymentDataTask(priceCents: Long): Task<PaymentData> {
        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(priceCents)
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        return paymentsClient.loadPaymentData(request)
    }

    fun postData(qr_code: String) {
        Log.i("TicketViewModel",  "postData() " + token)

        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token " + token

        val jsonObject = JSONObject()
        jsonObject.put("qr_code", qr_code)
        jsonObject.put("festival_name", title.value)

        Log.i("TicketViewModel",  "qr_code " + qr_code)

        val jsonString = jsonObject.toString()
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())
        Log.i("TicketViewModel", jsonString)


        CoroutineScope(Dispatchers.IO).launch {
            val response = FestivalApi.retrofitService.updatePaymentInformation(headerMap, requestBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    Log.i("TicketViewModel", "A mers")
                    Log.i("TicketViewModel", prettyJson)


                }
                else {
                    Log.i("TicketViewModel", "Nu a mers")
                    Log.i("TicketViewModel", "" + response.code() + " " + response.message())
                }
            }
        }
    }


    }

