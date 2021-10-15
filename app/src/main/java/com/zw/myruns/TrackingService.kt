package com.zw.myruns

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
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

class TrackingService : Service(), LocationListener {
    private lateinit var locationManager: LocationManager
    private var msgHandler: Handler? = null
    private lateinit var notificationManager: NotificationManager
    private val NOTIFICATION_ID = 123
    private lateinit var  myBinder: MyBinder
    private val CHANNEL_ID = "notification channel"
    companion object{
        val INT_KEY = "int key"
        val MSG_INT_VALUE = 0
    }
    private lateinit var locationList : ArrayList<LatLng>

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

    override fun onUnbind(intent: Intent?): Boolean {
        super.onUnbind(intent)
        Log.d("TrackingService", "onUnbind")
        msgHandler = null
        cleanup()
        return true
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("TrackingService", "onDestroy")
        cleanup()

    }
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d("TrackingService", "onTaskRem")
        cleanup()
        stopSelf()
    }

    private fun cleanup(){
        notificationManager.cancel(NOTIFICATION_ID)
        locationList.clear()
    }

    override fun onLocationChanged(location: Location) {
        val lat = location.latitude
        val lng = location.longitude
        val latLng = LatLng(lat, lng)
        locationList.add(latLng)
        Log.d("TrackingService", "location changed")
        try {

            val tempHandler = msgHandler //see the reason why we need a local val in this situation https://stackoverflow.com/questions/44595529/smart-cast-to-type-is-impossible-because-variable-is-a-mutable-property-tha
            if (tempHandler != null) {
                val bundle = Bundle()
                bundle.putString(INT_KEY, Util.fromArrayList(locationList) )
                val message: Message = tempHandler.obtainMessage()
                message.data = bundle
                message.what = MSG_INT_VALUE
                tempHandler.sendMessage(message)
                Log.d("TrackingService", "Sent message")
            }
        } catch (t: Throwable) { // you should always ultimately catch all // exceptions in timer tasks.
            println("hmmm")
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


    fun initLocationManager() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            val provider = locationManager.getBestProvider(criteria, true)
            val location = locationManager.getLastKnownLocation(provider!!)
            if (location != null)
                onLocationChanged(location)
            locationManager.requestLocationUpdates(provider, 0, 0f, this)
        } catch (e: SecurityException) {
        }
    }

}