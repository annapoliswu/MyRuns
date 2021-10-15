package com.zw.myruns

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat

class TrackingService : Service() {
    private lateinit var notificationManager: NotificationManager
    private val NOTIFICATION_ID = 123
    private lateinit var  myBinder: MyBinder
    private val CHANNEL_ID = "notification channel"

    override fun onCreate() {
        super.onCreate()
        showNotification()
        myBinder = MyBinder()

        println("created service")
    }

    override fun onBind(intent: Intent): IBinder {
        println("bound service")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun setmsgHandler(msgHandler: Handler) {
            //this@CounterService.msgHandler = msgHandler
        }
    }

    private fun showNotification() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        ) //XD: see book p1019 why we do not use Notification.Builder
        notificationBuilder.setSmallIcon(R.drawable.ic_notification)
        notificationBuilder.setContentTitle("Service has started")
        notificationBuilder.setContentText("Tap me to go back")
        notificationBuilder.setContentIntent(pendingIntent)
        val notification = notificationBuilder.build()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "channel name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}