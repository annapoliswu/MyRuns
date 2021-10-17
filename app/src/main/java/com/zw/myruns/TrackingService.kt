package com.zw.myruns

import android.app.*
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BADGE_ICON_LARGE
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import android.content.DialogInterface
import android.provider.Settings
import android.widget.Toast


class TrackingService : Service(), LocationListener {
    private lateinit var locationManager: LocationManager
    private var msgHandler: Handler? = null
    private lateinit var notificationManager: NotificationManager
    private val NOTIFICATION_ID = 123
    private lateinit var  myBinder: MyBinder
    private val CHANNEL_ID = "notification channel"

    companion object{
        val LOCATIONS_KEY = "int key"
        val MSG_INT_VALUE = 0
    }
    private lateinit var locationList : ArrayList<LatLng>
    private var distance : Float = 0F
    private var duration : Float = 0F
    private var average_speed : Float = 0F
    private var climb : Float = 0F
    private var calories : Int = 0
    //private var heart_rate : Int = 0

    override fun onCreate() {
        super.onCreate()

        msgHandler = null
        myBinder = MyBinder()
        locationList = ArrayList()
        initLocationManager()

        Log.d("TrackingService", "onCreate")
    }


    override fun onBind(intent: Intent): IBinder {
        Log.d("TrackingService", "onBind")
        return myBinder
    }



    //public functions go here
    inner class MyBinder : Binder() {
        fun setmsgHandler(msgHandler: Handler) {
            this@TrackingService.msgHandler = msgHandler
        }

        fun getLocationList() : ArrayList<LatLng> {
            return locationList
        }


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TrackingService", "onStartCommand")
        showNotification()
        return START_NOT_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("TrackingService", "onDestroy")
        cleanup()
    }

    //for when app is closed, stop service
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        cleanup()
        stopSelf()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("TrackingService", "unBind")
        return super.onUnbind(intent)
    }

    private fun cleanup(){
        msgHandler = null
        notificationManager.cancel(NOTIFICATION_ID)
        locationList.clear()
        if (locationManager != null)
            locationManager.removeUpdates(this)
    }

    //update location, duration,
    override fun onLocationChanged(location: Location) {
        val lat = location.latitude
        val lng = location.longitude
        val latLng = LatLng(lat, lng)
        locationList.add(latLng)
        Log.d("TrackingService", "location changed")
        try {

            val tempHandler = msgHandler
            if (tempHandler != null) {
                val bundle = Bundle()
                bundle.putString(LOCATIONS_KEY, Util.fromArrayList(locationList) )
                val message: Message = tempHandler.obtainMessage()
                message.data = bundle
                message.what = MSG_INT_VALUE
                tempHandler.sendMessage(message)
                Log.d("TrackingService", "Sent message")
            }
        } catch (t: Throwable) {
            Log.d("TrackingService", t.toString())
        }
    }


    private fun showNotification() {

        //Setup notification
        //do NOT use Notification.Builder !!
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
        notificationBuilder.setSmallIcon(R.drawable.ic_notification)
        notificationBuilder.setContentTitle("MyRuns")
        notificationBuilder.setContentText("Recording your path now")
        notificationBuilder.setOngoing(true)
        notificationBuilder.setSilent(true)

        //Set notification to go back to activity once clicked
        val intent = Intent(this, MapActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(pendingIntent)

        //Build notification and check app notification settings
        val notification = notificationBuilder.build()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "channel name",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(NOTIFICATION_ID, notification)
    }


    private fun initLocationManager() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            val provider = locationManager.getBestProvider(criteria, false) //get provider even if location not enabled

            if(provider != null) { //location enabled
                val location = locationManager.getLastKnownLocation(provider)
                if (location != null)
                    onLocationChanged(location)
                locationManager.requestLocationUpdates(provider, 0, 0f, this)
            }else{

            }

        } catch (e: SecurityException) {
        }
    }

    //overrides so doesn't crash upon location services on/off when using service
    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}


}