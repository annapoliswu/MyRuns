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
import android.hardware.*
import android.provider.Settings
import android.widget.Toast
import com.google.android.material.timepicker.TimeFormat


class TrackingService : Service(), LocationListener, SensorEventListener {
    private lateinit var locationManager: LocationManager
    private var msgHandler: Handler? = null
    private lateinit var notificationManager: NotificationManager
    private val NOTIFICATION_ID = 123
    private lateinit var  myBinder: MyBinder
    private val CHANNEL_ID = "notification channel"

    companion object{
        val LOCATIONS_KEY = "locations_key"
        val DISTANCE_KEY = "distance_key"
        val DURATION_KEY = "duration_key"
        val AVERAGE_SPEED_KEY = "average_speed_key"
        val CURRENT_SPEED_KEY = "current_speed_key"
        val CLIMB_KEY = "climb_key"
        val CALORIES_KEY = "calories_key"
        val MSG_INT_VALUE = 0
    }
    private lateinit var locationList : ArrayList<LatLng>
    private var distance : Float = 0F
    private var duration : Float = 0F
    private var average_speed : Float = 0F
    private var current_speed : Float = 0F
    private var climb : Float = 0F
    private var calories : Int = 0

    private lateinit var lastLocation : Location
    private var startTime : Long = 0L
    private var lastTime : Long = 0L

    private lateinit var sensorManager : SensorManager

    override fun onCreate() {
        super.onCreate()

        msgHandler = null
        myBinder = MyBinder()
        locationList = ArrayList()
        initLocationManager()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)

        showNotification()
        startTime = getTime()
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

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TrackingService", "onStartCommand")
        return START_NOT_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("TrackingService", "unBind")
        return super.onUnbind(intent)
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


    //unregistering listeners and clearing stuff
    private fun cleanup(){
        msgHandler = null
        notificationManager.cancel(NOTIFICATION_ID)
        if (locationManager != null)
            locationManager.removeUpdates(this)
        sensorManager.unregisterListener(this)
        locationList.clear()
    }

    //do calculations for everything, then send message to view model to update the stats
    override fun onLocationChanged(location: Location) {        //climb use location.altitude
        Log.d("TrackingService", "location changed")
        val lat = location.latitude
        val lng = location.longitude
        val latLng = LatLng(lat, lng)
        locationList.add(latLng)

        if(::lastLocation.isInitialized){
            val travelledDistance = Util.metersToMiles(lastLocation.distanceTo(location) )
            distance += travelledDistance

            val currentTime = getTime()
            duration = elapsedMinutes(startTime, currentTime)
            average_speed = distance / elapsedHours(startTime, currentTime)

            val hrsFromPreviousTime = elapsedHours(lastTime, currentTime)
            if(hrsFromPreviousTime > 0) {
                current_speed = travelledDistance / hrsFromPreviousTime
            }else{
                current_speed = 0F
            }
            climb = Util.metersToMiles( (lastLocation.altitude - location.altitude).toFloat() )

            //just going to set this to 120 cal / mile (since it's weight based anyways)
            calories = (distance * 120).toInt()

            Log.d("Tracking Service","Duration: $duration" )
        }
        sendMessage()
        lastLocation = location
        lastTime = getTime()
    }

    //gets elapsed time in seconds from times in milliseconds
    private fun elapsedSeconds(t1 : Long, t2 : Long) : Float{
        return ((t2 - t1)/1000).toFloat()
    }

    private fun elapsedMinutes(t1 : Long, t2 : Long) : Float{
        return elapsedSeconds(t1, t2)/60
    }

    private fun elapsedHours(t1 : Long, t2 : Long) : Float{
        return elapsedSeconds(t1, t2)/3600
    }

    //for changing fetching time implementation easier
    private fun getTime() : Long {
        return System.currentTimeMillis()
    }

    //format message with stats data to send to mapview model
    private fun sendMessage(){
        try {
            val tempHandler = msgHandler
            if (tempHandler != null) {
                val bundle = Bundle()
                bundle.putString(LOCATIONS_KEY, Util.fromArrayList(locationList) )
                bundle.putFloat(AVERAGE_SPEED_KEY, average_speed)
                bundle.putFloat(CURRENT_SPEED_KEY, current_speed)
                bundle.putFloat(CLIMB_KEY, climb)
                bundle.putFloat(DURATION_KEY, duration)
                bundle.putFloat(DISTANCE_KEY, distance)
                bundle.putInt(CALORIES_KEY, calories)

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

    //Show notification when GPS is in use
    private fun showNotification() {

        //Setup notification
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


    //for myruns5 later
    override fun onSensorChanged(event: SensorEvent?) {
        if(event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER){
            val x = (event.values[0] / SensorManager.GRAVITY_EARTH).toDouble()
            val y = (event.values[1] / SensorManager.GRAVITY_EARTH).toDouble()
            val z = (event.values[2] / SensorManager.GRAVITY_EARTH).toDouble()
            //onresume registerlistener, onpause unregister listener..
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //TODO: not implemented / maybe not needed
    }


}