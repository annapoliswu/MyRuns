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
import com.google.android.gms.maps.model.LatLng
import android.hardware.*
import kotlinx.coroutines.CoroutineScope
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import kotlin.collections.ArrayList
import kotlin.math.sqrt
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * gets location, calculates speeds and other stats, sends bundle to MapViewModel
 */
class TrackingService : Service(), LocationListener, SensorEventListener {
    private lateinit var locationManager: LocationManager
    private var msgHandler: Handler? = null
    private lateinit var notificationManager: NotificationManager
    private val NOTIFICATION_ID = 123
    private lateinit var  myBinder: MyBinder
    private val CHANNEL_ID = "notification channel"
    companion object{
        val MSG_INT_VALUE = 0
        val CLASSIFY_MSG_INT_VAL = 1
        val CLASSIFY_KEY = "classified_activity_key"
    }

    private lateinit var locationList : ArrayList<LatLng>
    private var distance : Float = 0F
    private var duration : Float = 0F
    private var average_speed : Float = 0F
    private var current_speed : Float = 0F
    private var climb : Float = 0F
    private var calories : Int = 0
    private var dateTime : String = ""

    private lateinit var lastLocation : Location
    private var startTime : Long = 0L
    private var lastTime : Long = 0L

    private lateinit var sensorManager : SensorManager
    private lateinit var mAccBuffer: ArrayBlockingQueue<Double>
    var classifyTask: Job? = null

    override fun onCreate() {
        super.onCreate()

        msgHandler = null
        myBinder = MyBinder()
        locationList = ArrayList()
        initLocationManager()

        mAccBuffer = ArrayBlockingQueue<Double>(Globals.ACCELEROMETER_BUFFER_CAPACITY)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)

        classifyTask = CoroutineScope(IO).launch{
            classifyTask()
        }

        showNotification()

        dateTime = Util.calendarToString( Calendar.getInstance() )
        startTime = getTime()
    }



    override fun onBind(intent: Intent): IBinder {
        return myBinder
    }


    //public functions go here
    inner class MyBinder : Binder() {
        fun setmsgHandler(msgHandler: Handler) {
            this@TrackingService.msgHandler = msgHandler
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
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
        classifyTask?.cancel()
    }

    //when location changes, do calculations for everything, then send message notifying update
    override fun onLocationChanged(location: Location) {
        Log.d("TrackingService", "location changed")
        val lat = location.latitude
        val lng = location.longitude
        val latLng = LatLng(lat, lng)
        locationList.add(latLng)

        //only calculate if there is a previous data point
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
                val bundle = Util.createEntryBundle("GPS", "", dateTime, duration, distance, calories, 0, "", climb, current_speed, average_speed, locationList )

                val message: Message = tempHandler.obtainMessage()
                message.data = bundle
                message.what = MSG_INT_VALUE
                tempHandler.sendMessage(message)
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
            }

        } catch (e: SecurityException) {
        }
    }

    //overrides so tracking doesn't crash upon location services on/off when using service
    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}


    //when accelorometer data changed, get magnitude and add magnitude to buffer
    override fun onSensorChanged(event: SensorEvent?) {
        if(event != null && event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION){

            val x = (event.values[0]).toDouble()
            val y = (event.values[1]).toDouble()
            val z = (event.values[2]).toDouble()
            val mag = sqrt(x*x + y*y + z*z)

            try {
                mAccBuffer.add(mag)
            } catch (e: IllegalStateException) {
                // Exception happens when reach the capacity.
                // Doubling the buffer. ListBlockingQueue has no such issue, but generally has worse performance
                val newBuf = ArrayBlockingQueue<Double>(mAccBuffer.size * 2)
                mAccBuffer.drainTo(newBuf)
                mAccBuffer = newBuf
                mAccBuffer.add(mag)
            }
        }
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //not needed
    }


    /* basically the part from the dataCollector to get a feature vector. runs in background in a coroutine.
     * once buffer of 64 magnitudes is full, creates a feature vector w/ max and calls WekaClassifier function on it to classify
     */
    private suspend fun classifyTask() {

        // Create the feature vector for classification
        val featVect = ArrayList<Double>(Globals.ACCELEROMETER_BLOCK_CAPACITY)
        var blockSize = 0
        val fft = FFT(Globals.ACCELEROMETER_BLOCK_CAPACITY)
        val accBlock = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
        val im = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
        var max = Double.MIN_VALUE
        while (true) {
            try {
                // Dumping buffer
                accBlock[blockSize++] = mAccBuffer.take().toDouble()
                if (blockSize == Globals.ACCELEROMETER_BLOCK_CAPACITY) {
                    //Buffer size reached
                    blockSize = 0
                    max = .0
                    for (`val` in accBlock) { //find max value
                        if (max < `val`) {
                            max = `val`
                        }
                    }
                    fft.fft(accBlock, im)
                    for (i in accBlock.indices) {
                        val mag = Math.sqrt(
                            accBlock[i] * accBlock[i] + im[i]
                                    * im[i]
                        )
                        im[i] = .0 // Clear the field
                        featVect.add(mag)
                    }
                    featVect.add(max)

                    //Use Weka function to classify and send message
                    val classifiedVal = WekaClassifier.classify( featVect.toArray() ).toInt()
                    sendClassifyMessage(Globals.CLASS_ACTIVITY_ARRAY[classifiedVal])

                    featVect.clear()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    //format to send a separate message with weka classified activity_type
    private fun sendClassifyMessage(classifiedVal: String){
        try {
            val tempHandler = msgHandler
            if (tempHandler != null) {
                val bundle = Bundle()
                bundle.putString(CLASSIFY_KEY, classifiedVal)
                val message: Message = tempHandler.obtainMessage()
                message.data = bundle
                message.what = CLASSIFY_MSG_INT_VAL
                tempHandler.sendMessage(message)
            }
        } catch (t: Throwable) {
            Log.d("TrackingService", t.toString())
        }
    }



}