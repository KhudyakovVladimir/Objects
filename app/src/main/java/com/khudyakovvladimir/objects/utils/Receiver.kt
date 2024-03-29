package com.khudyakovvladimir.objects.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startForegroundService
import com.khudyakovvladimir.objects.R
import com.khudyakovvladimir.objects.view.MainActivity

class Receiver: BroadcastReceiver() {
    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(p0: Context?, p1: Intent?) {

        if (p1?.action == "activity") {
            val i = Intent(p0!!.applicationContext, MainActivity::class.java )
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            p0.startActivity(i)
        }

        if(p1?.action == "service") {
            val i = Intent(p0!!.applicationContext, ObjectService::class.java )
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            val v = p1.extras
            val vv = v!!.get("id")
            i.putExtra("id", "$vv")
            p0.startForegroundService(i)
        }
    }
}