package com.khudyakovvladimir.objects.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.khudyakovvladimir.objects.database.ObjectDao
import com.khudyakovvladimir.objects.database.ObjectEntity
import javax.inject.Inject

class ObjectViewModel @Inject constructor(
    application: Application,
    val objectDao: ObjectDao
): AndroidViewModel(application) {

    private var listObjects: LiveData<List<ObjectEntity>> = objectDao.getAllObjectsAsLiveData()!!

    fun getListObjects(): LiveData<List<ObjectEntity>> {
        return listObjects
    }
}