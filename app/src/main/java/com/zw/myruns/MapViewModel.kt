package com.zw.myruns

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

//TODO: INSTEAD OF THIS, broadcast receiver in mapact, broadcast in tracking, send intent
class MapViewModel  : ViewModel(), ServiceConnection {



    private val _distance = MutableLiveData<Float>()
    val distance: LiveData<Float>
        get() = _distance

    private val _locationList = MutableLiveData<ArrayList<LatLng>>()
    val locationList: LiveData<ArrayList<LatLng>>
        get() = _locationList


    private lateinit var messHandler: MyMessageHandler
    init {
        messHandler = MyMessageHandler(Looper.getMainLooper())
    }

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
        trackingBinder.setmsgHandler(messHandler)
    }

    override fun onServiceDisconnected(compName: ComponentName?) {
        //TODO("Not yet implemented")
        Log.d("MapViewModel", "onServiceDisconnected")

    }

    inner class MyMessageHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(mess: Message) {
            if (mess.what == TrackingService.MSG_INT_VALUE) {
                val bundle = mess.data
                _locationList.value = Util.toArrayList( bundle.getString(TrackingService.LOCATIONS_KEY)!! )
                //_polyLines.value = locationList
            }
        }
    }


}