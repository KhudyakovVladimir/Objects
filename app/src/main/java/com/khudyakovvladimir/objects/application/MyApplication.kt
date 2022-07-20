package com.khudyakovvladimir.objects.application

import android.app.Application
import android.content.Context
import com.khudyakovvladimir.objects.dependencies.AppComponent
import com.khudyakovvladimir.objects.dependencies.DaggerAppComponent
import java.util.concurrent.TimeUnit

class MyApplication: Application() {

    lateinit var appComponent: AppComponent
    private set

    override fun onCreate() {
        super.onCreate()

        TimeUnit.SECONDS.sleep(2)

        appComponent = DaggerAppComponent
            .builder()
            .application(this)
            .build()
    }
}

val Context.appComponent: AppComponent
get() = when(this) {
    is MyApplication -> appComponent
    else -> applicationContext.appComponent
}