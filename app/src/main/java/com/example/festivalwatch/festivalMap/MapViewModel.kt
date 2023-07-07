package com.example.festivalwatch.festivalMap

import android.app.Application
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapViewModel(
    application: Application,
    private val tokenArg: String,
    private val festivalNameArg: String,
    private val usernameArg: String,
    private val latitudeArg: Float,
    private val longitudeArg: Float) : AndroidViewModel(application) {

    var token = tokenArg
    var festival_name = festivalNameArg
    var username = usernameArg
    val latitude = MutableLiveData(latitudeArg)
    val longitude = MutableLiveData(longitudeArg)

    private var map: GoogleMap? = null

    fun initMap(fragmentManager: FragmentManager, containerId: Int) {
        val mapFragment = fragmentManager.findFragmentById(containerId) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            map = googleMap

            val festival = LatLng(latitude.value?.toDouble() ?: 0.0,
                longitude.value?.toDouble() ?: 0.0)
            map?.moveCamera(CameraUpdateFactory.newLatLng(festival))

            val zoomLevel = 10f
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(festival, zoomLevel)
            map?.moveCamera(cameraUpdate)

            map!!.addMarker(MarkerOptions().position(festival).title(festival_name))
            map!!.moveCamera(CameraUpdateFactory.newLatLng(festival))
        }
    }


}