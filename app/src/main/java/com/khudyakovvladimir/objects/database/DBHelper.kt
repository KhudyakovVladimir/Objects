package com.khudyakovvladimir.objects.database

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DBHelper(private val context: Context) {

    lateinit var objectDatabase: ObjectDatabase

    fun  createDatabase() {
        objectDatabase = ObjectDatabase.getInstance(context)!!

        val sharedPreferences = context.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)

        if(sharedPreferences.contains("database")) {

        }else {
            CoroutineScope(Dispatchers.IO).launch {
                objectDatabase.objectDao().insertObjectEntity(
                    ObjectEntity(1,
                        "Object",
                        "type",
                        "status",
                        "duty",
                        "coordinates",
                        "comment",
                        "icon"
                    ))
            }
        }
    }
}