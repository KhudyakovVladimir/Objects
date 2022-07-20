package com.khudyakovvladimir.objects.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.khudyakovvladimir.objects.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}