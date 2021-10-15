package com.zw.myruns

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        //applicationContext

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        mapViewModel.locationList.observe(this, Observer { it ->
            //intValueLabel.text = "Int Message: $it"
            println("new location added "+ it.last().toString())
        })

        serviceIntent = Intent(this, TrackingService::class.java)

        Log.d("MapAct", "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MapAct", "onDestroy")
        unbindService(mapViewModel)
        stopService(serviceIntent)
    }

    fun onSaveClicked(view :View){
        finish()
    }
    fun onCancelClicked(view: View){
        finish()
    }



    override fun onMapReady(googleMap: GoogleMap) {
        //TODO("Not yet implemented")
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        //mMap.setOnMapClickListener(this)
        //mMap.setOnMapLongClickListener(this)

        /*
        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLACK)
        polylines = ArrayList()
        markerOptions = MarkerOptions()
        */

        Util.checkPermissions(this, PERMISSIONS)
        try {
            //start explicitly so service exists without things bound, bind service
            startService(serviceIntent)
            bindService(serviceIntent, mapViewModel, Context.BIND_AUTO_CREATE)
        }catch (e: SecurityException){
            Log.d("Exception caught", e.toString())
        }
    }


}