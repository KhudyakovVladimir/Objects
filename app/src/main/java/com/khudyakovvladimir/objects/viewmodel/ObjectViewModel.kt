package com.khudyakovvladimir.objects.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.khudyakovvladimir.objects.database.ObjectDao
import com.khudyakovvladimir.objects.database.ObjectEntity
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ObjectViewModel @Inject constructor(
    application: Application,
    val objectDao: ObjectDao
): AndroidViewModel(application) {

    private var listObjects: LiveData<List<ObjectEntity>> = objectDao.getAllObjectsAsLiveData()!!
    private var objectsList: ArrayList<ObjectEntity>? = null

    fun getListObjects(): LiveData<List<ObjectEntity>> {
        return listObjects
    }

    fun getListObjectAsList() : List<ObjectEntity> {
        runBlocking {
            objectsList = ArrayList(objectDao.getAllNotesAsList())
        }
        return objectsList!!
    }
}