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
    val objectDao: ObjectDao,
    val sortType: Int
): AndroidViewModel(application) {

    private var listDutyObjects: LiveData<List<ObjectEntity>> = objectDao.getAllDutyObjectsAsLiveData()!!
    private var listObjects: LiveData<List<ObjectEntity>> = objectDao.getAllObjectsAsLiveData()!!
    private var objectsList: ArrayList<ObjectEntity>? = null

    fun getListDutyObjects(): LiveData<List<ObjectEntity>> {
        return listDutyObjects
    }

    fun getListObjects(): LiveData<List<ObjectEntity>> {
        if(sortType == 0) {
            return listObjects
        }
        return listDutyObjects
    }

    fun getListObjectAsList() : List<ObjectEntity> {
        runBlocking {
            objectsList = ArrayList(objectDao.getAllNotesAsList())
        }
        return objectsList!!
    }
}