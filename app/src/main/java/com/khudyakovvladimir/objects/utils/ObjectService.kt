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

class ObjectService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        Log.d("TAG", "ObjectService onBind()")

        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TAG", "ObjectService onStartCommand()")

        val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ applicationContext.packageName + "/" + R.raw.notification_sound)
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                "1",
                1.toString(),
                NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.lightColor = Color.YELLOW
            notificationChannel.setSound(soundUri, audioAttributes)

            notificationManager.createNotificationChannel(notificationChannel)

            val bundle = Bundle()
            bundle.putInt("notificationId", 1)

            val pendingIntent = NavDeepLinkBuilder(applicationContext)
                .setGraph(R.navigation.graph)
                .setDestination(R.id.listFragment)
                .setArguments(bundle)
                .createPendingIntent()

            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(applicationContext, "1")
                    .setSmallIcon(R.drawable.eso_icon)
                    .setLargeIcon((AppCompatResources.getDrawable(applicationContext, R.drawable.eso_icon)!! as BitmapDrawable).bitmap)
                    .setContentTitle("title")
                    .setContentText("text")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setSound(Uri.parse(soundUri.toString()))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setLights(-16711936, 0,1)
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.eso_icon, "open", pendingIntent)

            val notification = builder.build()
            notification.flags = Notification.FLAG_AUTO_CANCEL
            notificationManager.notify(1, notification)

            startForeground(1, notification)
        }

        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        Log.d("TAG", "ObjectService onCreate()")
        super.onCreate()

    }
}