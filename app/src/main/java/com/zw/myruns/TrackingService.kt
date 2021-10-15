package com.zw.myruns

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BADGE_ICON_LARGE

class TrackingService : Service(), LocationListener {
    private lateinit var notificationManager: NotificationManager
    private val NOTIFICATION_ID = 123
    private lateinit var  myBinder: MyBinder
    private val CHANNEL_ID = "notification channel"

    override fun onCreate() {
        super.onCreate()

        myBinder = MyBinder()

        Log.d("TrackingService", "onCreate")
    }


    override fun onBind(intent: Intent): IBinder {
        Log.d("TrackingService", "onBind")
        return myBinder
    }

    //public functions go here
    inner class MyBinder : Binder() {
        fun setmsgHandler(msgHandler: Handler) {
            //this@CounterService.msgHandler = msgHandler
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
    }

    override fun onLocationChanged(location: Location) {
        //TODO("Not yet implemented")
    }


    private fun showNotification() {

        //Setup notification
        //do NOT use Notification.Builder !!
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
        notificationBuilder.setSmallIcon(R.drawable.ic_notification)
        notificationBuilder.setContentTitle("MyRuns")
        notificationBuilder.setContentText("Recording your path now")
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




}