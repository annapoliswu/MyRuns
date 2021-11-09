package com.zw.myruns

import android.Manifest
import android.app.Activity
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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

/**
 *  Display google maps with this, view varies for display vs tracking based on intent passed in
 */
class MapActivity : AppCompatActivity(), OnMapReadyCallback{


    private lateinit var mMap : GoogleMap
    private lateinit var mapViewModel : MapViewModel
    private lateinit var serviceIntent : Intent
    private var serviceBound : Boolean = false
    private val PERMISSION_REQUEST_CODE = 1
    private lateinit var exerciseViewModel : ExerciseViewModel
    var id by Delegates.notNull<Long>()

    private var mapCentered : Boolean = false
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
    private lateinit var units : String

    //for displays and passing bundle
    private var averageSpeed: Float = 0F
    private var currentSpeed: Float = 0F
    private var climb: Float = 0F
    private var calories: Int = 0
    private var distance: Float = 0F
    private var duration: Float = 0F
    private var dateTime : String = Util.calendarToString(Calendar.getInstance())
    private var activityType : String = ""
    private var inputType : String = ""
    private var locations : ArrayList<LatLng> = ArrayList()
    private val placeholderPosition: LatLng = LatLng(0.0, 0.0)

    private var redrawnMap: Boolean = true



    //varies view depending on if displaying an entry or tracking a new entry
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        serviceIntent = Intent(this, TrackingService::class.java)

        //unit preferences
        val sharedPref = getSharedPreferences(
            getString(R.string.settings_preference_key),
            Context.MODE_PRIVATE
        )
        units = sharedPref.getString("units_key", getString(R.string.default_units))!!

        //stat views setup
        activityTypeTV = findViewById(R.id.mapstat_activity_type)
        averageSpeedTV = findViewById(R.id.mapstat_average_speed)
        currentSpeedTV = findViewById(R.id.mapstat_current_speed)
        climbTV = findViewById(R.id.mapstat_climb)
        caloriesTV = findViewById(R.id.mapstat_calories)
        distanceTV = findViewById(R.id.mapstat_distance)

        //swap toolbars and hide buttons
        val defaultToolbar: Toolbar = findViewById(R.id.map_default_toolbar)
        val displayToolbar: Toolbar = findViewById(R.id.map_display_toolbar)
        val extras = intent.extras
        if(extras != null){
            if(extras.containsKey("entry_id")){
                setSupportActionBar(displayToolbar)
                defaultToolbar.visibility = View.GONE

                val mapButtons: LinearLayout = findViewById(R.id.mapButtons)
                mapButtons.visibility = View.GONE

                val database = ExerciseDatabase.getInstance(this)
                val databaseDao = database.exerciseDatabaseDao
                val viewModelFactory = ExerciseViewModelFactory(databaseDao)
                exerciseViewModel = ViewModelProvider(this, viewModelFactory).get(ExerciseViewModel::class.java)
            }else{
                setSupportActionBar(defaultToolbar)
                displayToolbar.visibility = View.GONE
            }
        }

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


    //when map is ready, either start tracking or display entry
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.RED)
        polylines = ArrayList()
        markerOptions = MarkerOptions()

        val extras = intent.extras
        if(extras != null){

            if(extras.containsKey("entry_id")){ //if display only
                val id = extras.getLong("entry_id")
                exerciseViewModel.allEntries.observe(this, Observer { changedList ->
                    val entry = changedList.find { ee -> ee.id == id }
                    if (entry != null) {
                        this.id = entry.id
                        activityType = entry.activityType
                        inputType = entry.inputType
                        val bundle = Util.getBundleFromEntry(entry)
                        drawMap(bundle)
                        currentSpeedTV.text = "Current Speed: n/a"
                    }
                })
            }else{ //if recording run
                checkPermissionStartService()
                activityType = extras.getString("activity_type", "")
                inputType = extras.getString("input_type", "")

                //if automatic, observe the weka classification message.
                if(inputType == "Automatic"){
                    activityType = "Standing"      //starts on 'standing' in myruns5 demo
                    mapViewModel.classfiedActivityType.observe(this, Observer { classStr ->
                        activityType = classStr
                        Log.d("MapAct", "classStr: $classStr")
                    })
                }


                //placeholder starter marker for loading? idk it's in the demo
                addPlaceholderMarker()

                mapViewModel.bundle.observe(this, Observer { bundle ->
                    drawMap(bundle)
                })


            }
        }
    }

    //update the map view with an exercise entry bundle
    fun drawMap(bundle: Bundle){

        //update drawn line
        locations = Util.toArrayList( bundle.getString("locations")!! )

        if(locations.isEmpty()){ //for edge case of saving before any locations read
            addPlaceholderMarker()
            val statsView : LinearLayout= findViewById(R.id.map_stats)
            statsView.visibility = View.GONE
        }else {
            polylineOptions = PolylineOptions()
            polylineOptions.addAll(locations)
            mMap.addPolyline(polylineOptions)

            val latLng = locations.last()

            //center map
            mapCentered = isCenter(latLng)
            if (!mapCentered) {
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 20f)
                mMap.animateCamera(cameraUpdate)
                mapCentered = true
            }

            //add first marker again if map is recreated
            if (redrawnMap) {
                mMap.addMarker(
                    MarkerOptions()
                        .position(locations.first())
                        .title("Start location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .zIndex(-1f)
                )
                redrawnMap = false
            }
            //update current location marker
            moveMarker(latLng)

            //change map stats views
            averageSpeed = bundle.getFloat("average_speed")
            currentSpeed = bundle.getFloat("average_pace")
            distance = bundle.getFloat("distance")
            climb = bundle.getFloat("climb")
            calories = bundle.getInt("calories")
            duration = bundle.getFloat("duration")
            dateTime = bundle.getString("date_time", "")
            changeStats(activityType, averageSpeed, currentSpeed, climb, calories, distance)
        }
    }

    //checks if point is within visible map camera region
    fun isCenter(latLng : LatLng):Boolean{
        val region =  mMap.projection.visibleRegion
        if(serviceBound){
            return region.latLngBounds.contains(latLng)
        }else{
            return false
        }
    }

    //updates marker for current location on map
    fun moveMarker(latLng : LatLng){
        lastMarker?.remove()
        markerOptions.position(latLng).title("End location")
        lastMarker = mMap.addMarker(markerOptions)
    }

    fun addPlaceholderMarker(){
        mMap.addMarker(
            MarkerOptions()
                .position(placeholderPosition)
                .title("Placeholder Position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )
    }


    private fun startTrackingService(){
        try {
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

        var avgSpeedPrefUnits = averageSpeed
        var currSpeedPrefUnits = currentSpeed
        var climbPrefUnits = climb
        var distancePrefUnits = distance

        var unitAbrev : String
        if(units == "Miles"){ //miles is what we're storing in, don't need to convert
            unitAbrev = "miles"
        }else{
            unitAbrev = "km"
            avgSpeedPrefUnits = Util.milesToKm(averageSpeed) //same conversion since both in /hrs
            currSpeedPrefUnits = Util.milesToKm(currentSpeed)
            climbPrefUnits = Util.milesToKm(climb)
            distancePrefUnits = Util.milesToKm(distance)
        }

        activityTypeTV.text = "Activity Type: ${activityType}"
        averageSpeedTV.text = "Average Speed: ${String.format(strFormat, avgSpeedPrefUnits)} $unitAbrev/hr"
        currentSpeedTV.text = "Current Speed: ${String.format(strFormat, currSpeedPrefUnits)} $unitAbrev/hr"
        climbTV.text = "Climb: ${String.format(strFormat, climbPrefUnits)} $unitAbrev"
        caloriesTV.text = "Calories: " + calories + " cal"
        distanceTV.text = "Distance: ${String.format(strFormat, distancePrefUnits)} $unitAbrev"
    }

    //make sure permissions on for tracking service to start
    fun checkPermissionStartService() {
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


    fun onSaveClicked(view :View){
        val intent = Util.createEntryIntent(inputType, activityType, dateTime, duration, distance, calories, 0, "",climb, currentSpeed, averageSpeed, locations)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
    fun onCancelClicked(view: View){
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
    fun onDeleteClicked(view: View){
        exerciseViewModel.delete(id)
        val toast = Toast.makeText(this, "Entry #${id} deleted", Toast.LENGTH_SHORT)
        toast.show()
        finish()
    }
    fun onBackClicked(view: View){
        finish()
    }


}