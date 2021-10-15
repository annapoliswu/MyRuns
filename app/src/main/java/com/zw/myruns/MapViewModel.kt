package com.zw.myruns

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapViewModel  : ViewModel(), ServiceConnection {

    private val _distance = MutableLiveData<Float>()
    val distance: LiveData<Float>
        get() = _distance

    /**
     *  @ColumnInfo(name = "duration")
    var duration: Float = 0F,   //in minutes

    @ColumnInfo(name = "distance")  //stored in miles
    var distance: Float = 0F,

    @ColumnInfo(name = "average_pace")
    var avgPace: Float = 0F,

    @ColumnInfo(name = "average_speed")
    var avgSpeed: Float = 0F,

    @ColumnInfo(name = "calories")
    var calories: Int = 0,

    @ColumnInfo(name = "climb")
    var climb: Float = 0F,

    @ColumnInfo(name = "heart_rate")
    var heartRate: Int = 0,

    @ColumnInfo(name = "comment")
    var comment: String = "",

    @ColumnInfo(name = "locations")
    var locations: ArrayList<LatLng> = ArrayList()
     */

    override fun onServiceConnected(compName: ComponentName?, iBinder: IBinder?) {
        //TODO("Not yet implemented")
        Log.d("MapViewModel", "onServiceConnected")
        val trackingBinder = iBinder as TrackingService.MyBinder
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        //TODO("Not yet implemented")
        Log.d("MapViewModel", "onServiceDisconnected")

    }


}