package com.khudyakovvladimir.objects.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.khudyakovvladimir.objects.view.MainActivity

class Receiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == "MyBroadcastReceiverAction") {
            Log.d("TAG", "RECEIVED")
            //val i = Intent()
            val i = Intent(p0!!.applicationContext, MainActivity::class.java )
            //i.setClassName("com.khudyakovvladimir.objects.view", "com.khudyakovvladimir.objects.view.MainActivity")
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            p0.startActivity(i)
        }
    }
}