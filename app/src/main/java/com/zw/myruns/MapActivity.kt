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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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

    private lateinit var onBackPressedCallback: OnBackPressedCallback
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
    private lateinit var activityTypeTV : TextView
    private lateinit var averageSpeedTV : TextView
    private lateinit var currentSpeedTV : TextView
    private lateinit var climbTV : TextView
    private lateinit var caloriesTV : TextView
    private lateinit var distanceTV : TextView
    
    private var averageSpeed: Float = 0F
    private var currentSpeed: Float = 0F
    private var climb: Float = 0F
    private var calories: Int = 0
    private var distance: Float = 0F

    private var redrawnMap: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        //applicationContext

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        serviceIntent = Intent(this, TrackingService::class.java)

        activityTypeTV = findViewById(R.id.mapstat_activity_type)
        averageSpeedTV = findViewById(R.id.mapstat_average_speed)
        currentSpeedTV = findViewById(R.id.mapstat_current_speed)
        climbTV = findViewById(R.id.mapstat_climb)
        caloriesTV = findViewById(R.id.mapstat_calories)
        distanceTV = findViewById(R.id.mapstat_distance)

        /*
        //??
        onBackPressedCallback = object : OnBackPressedCallback (true){
            override fun handleOnBackPressed() {
                unbindService()
                applicationContext.stopService(intent)
                applicationContext.onBackPressedCallback()
            }
        }
        applicationContext.onBackPressedDispatcher.addCallback(
            requireActivity(),
            onBackPressedCallback()
        )
        */
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
                .title("Placeholder Position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )

        mapViewModel.bundle.observe(this, Observer { bundle ->

            //update drawn line
            val locList = Util.toArrayList( bundle.getString(TrackingService.LOCATIONS_KEY)!! )
            polylineOptions = PolylineOptions()
            polylineOptions.addAll(locList)
            mMap.addPolyline(polylineOptions)

            val latLng = locList.last()

            //center map
            mapCentered = isCenter(latLng)
            if (!mapCentered) {
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 20f)
                mMap.animateCamera(cameraUpdate)
                mapCentered = true
            }

            //add first marker again if map is recreated
            if(redrawnMap) {
                mMap.addMarker(
                    MarkerOptions()
                        .position(locList.first())
                        .title("Start location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .zIndex(-1f)
                )
                redrawnMap = false
            }
            //update current location marker
            moveMarker(latLng)

            //change map stats views
            averageSpeed = bundle.getFloat(TrackingService.AVERAGE_SPEED_KEY)
            currentSpeed = bundle.getFloat(TrackingService.CURRENT_SPEED_KEY)
            distance = bundle.getFloat(TrackingService.DISTANCE_KEY)
            climb = bundle.getFloat(TrackingService.CLIMB_KEY)
            calories = bundle.getInt(TrackingService.CALORIES_KEY)
            changeStats("Running Plc", averageSpeed, currentSpeed, climb, calories, distance)
        })

    }


    //checks if point is within visible map camera region
    fun isCenter(latLng : LatLng):Boolean{
        //gets visible screen region
        val region =  mMap.projection.visibleRegion
        return region.latLngBounds.contains(latLng)
    }


    //updates marker for current location on map
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



    //to change the map stats textviews
    private fun changeStats(activityType: String, averageSpeed: Float, currentSpeed : Float, climb : Float, calories : Int, distance : Float ){
        val strFormat = "%.2f"
        activityTypeTV.text = "Activity Type: ${activityType}"
        averageSpeedTV.text = "Average Speed: ${String.format(strFormat, averageSpeed)} m/h"
        currentSpeedTV.text = "Current Speed: ${String.format(strFormat, currentSpeed)} m/h"
        climbTV.text = "Climb: ${String.format(strFormat, climb)} miles"
        caloriesTV.text = "Calories: " + calories + " cal"
        distanceTV.text = "Distance: ${String.format(strFormat, distance)} miles"
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