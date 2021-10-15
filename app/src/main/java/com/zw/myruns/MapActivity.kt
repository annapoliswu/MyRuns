package com.zw.myruns

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
class MapActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap : GoogleMap
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private lateinit var mapViewModel : MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        //applicationContext

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        mapViewModel.distance.observe(this, Observer { it ->
            //intValueLabel.text = "Int Message: $it"
        })

        //start explicitly so service exists without things bound, bind service
        val serviceIntent = Intent(this, TrackingService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, mapViewModel, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
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
        mMap.setOnMapClickListener(this)
        mMap.setOnMapLongClickListener(this)
        /*
        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLACK)
        polylines = ArrayList()
        markerOptions = MarkerOptions()
*/
        Util.checkPermissions(this, PERMISSIONS)
    }

    override fun onLocationChanged(p0: Location) {
        //TODO("Not yet implemented")
    }

    override fun onMapClick(p0: LatLng?) {
        //TODO("Not yet implemented")
    }

    override fun onMapLongClick(p0: LatLng?) {
        //TODO("Not yet implemented")
    }
}