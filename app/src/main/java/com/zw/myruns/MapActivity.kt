package com.zw.myruns

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.ArrayList

/**
 *  Display google maps in this
 */
class MapActivity : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var mMap : GoogleMap
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private lateinit var mapViewModel : MapViewModel
    private lateinit var serviceIntent : Intent
    private var serviceBound : Boolean = false
    private val PERMISSION_REQUEST_CODE = 1

    private var mapCentered = false
    private lateinit var  markerOptions: MarkerOptions
    private lateinit var  polylineOptions: PolylineOptions //list of latlng positions in
    private lateinit var  polylines: ArrayList<Polyline> //drawn polylines, what is used to render

    private var lastMarker : Marker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        //applicationContext

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        serviceIntent = Intent(this, TrackingService::class.java)

        Log.d("MapAct", "onCreate")
    }

    //onDestroy called every config change
    override fun onDestroy() {
        super.onDestroy()
        Log.d("MapAct", "onDestroy")
    }

    fun onSaveClicked(view :View){
        finish()
    }
    fun onCancelClicked(view: View){
        finish()
    }

    // not called on config change
    // when activity is closed or back is hit, unbind and stop service
    override fun finish() {
        super.finish()
        if(serviceBound) {
            applicationContext.unbindService(mapViewModel)
            stopService(serviceIntent)
            serviceBound = false
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        checkPermission()

        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL


        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.RED)
        polylines = ArrayList()
        markerOptions = MarkerOptions()

        //TODO: starting point maybe?
        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(-33.852, 151.211))
                .title("Marker in Sydney")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )

        mapViewModel.locationList.observe(this, Observer { locList ->
            polylineOptions = PolylineOptions()
            polylineOptions.addAll(locList)
            mMap.addPolyline(polylineOptions)

            val latLng = locList.last()
            //add start location stay on map
            if (!mapCentered) {
                mMap.addMarker(
                    MarkerOptions()
                    .position(locList.first())
                    .title("Start location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .zIndex(-1f)
                )
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 20f)
                mMap.animateCamera(cameraUpdate)
                mapCentered = true
            }

            //println("new location added "+ it.last().toString())
            moveMarker(latLng)

        })



    }

    fun moveMarker(latLng : LatLng){
        lastMarker?.remove()
        markerOptions.position(latLng).title("End location")
        lastMarker = mMap.addMarker(markerOptions)
    }

    private fun startTrackingService(){
        try {
            //start explicitly so service exists without things bound, bind service
            applicationContext.startService(serviceIntent)
            applicationContext.bindService(serviceIntent, mapViewModel, Context.BIND_AUTO_CREATE)
            serviceBound = true
        }catch (e: SecurityException){
            Log.d("Exception caught", e.toString())
        }
    }

    //make sure permissions on for tracking service to start
    fun checkPermission() {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        else
            startTrackingService()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTrackingService()
            }else{
                Toast.makeText(this, "Location permissions not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

}