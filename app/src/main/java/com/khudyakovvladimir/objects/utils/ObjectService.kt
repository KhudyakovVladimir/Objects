package com.khudyakovvladimir.objects.utils

import android.app.*
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.khudyakovvladimir.objects.R
import java.util.concurrent.TimeUnit

class ObjectService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TAG", "intent!!.action = ${intent!!.action}")

        if (intent.action == "stopService") {
            stopForeground(true)
            stopSelf()
        }else {
            val extras = intent.extras?.get("id")
            Log.d("TAG", "extras = $extras")
            val i = extras.toString().toInt()

            val bundle = Bundle()
            bundle.putInt("objectID", i)

            val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ applicationContext.packageName + "/" + R.raw.bell)
            val audioAttributes: AudioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val notificationChannel = NotificationChannel(
                    "3",
                    3.toString(),
                    NotificationManager.IMPORTANCE_HIGH)

                notificationChannel.lightColor = Color.YELLOW
                notificationChannel.setSound(soundUri, audioAttributes)

                notificationManager.createNotificationChannel(notificationChannel)

                val pendingIntent = NavDeepLinkBuilder(applicationContext)
                    .setGraph(R.navigation.graph)
                    .setDestination(R.id.objectFragment)
                    .setArguments(bundle)
                    .createPendingIntent()

                val stopIntent = Intent(this, ObjectService::class.java)
                stopIntent.action = "stopService"
                val pi = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_MUTABLE)

                val builder: NotificationCompat.Builder =
                    NotificationCompat.Builder(applicationContext, "3")
                        .setSmallIcon(R.drawable.eso_icon)
                        .setLargeIcon((AppCompatResources.getDrawable(applicationContext, R.drawable.eso_icon)!! as BitmapDrawable).bitmap)
                        .setContentTitle("Объект №")
                        .setContentText("$extras")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setOngoing(false)
                        .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                        .setSound(Uri.parse(soundUri.toString()))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setLights(-16711936, 0,1)
                        .setContentIntent(pendingIntent)
                        .addAction(R.drawable.eso_icon, "close", pi)

                val notification = builder.build()
                notification.flags = Notification.FLAG_AUTO_CANCEL

                notificationManager.notify(1, notification)

                startForeground(1, notification)
            }
        }

        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
    }
}