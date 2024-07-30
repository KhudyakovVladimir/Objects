package com.khudyakovvladimir.objects.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.khudyakovvladimir.objects.database.ObjectDao
import com.khudyakovvladimir.objects.database.ObjectEntity
import com.khudyakovvladimir.objects.utils.TimeHelper
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ObjectViewModel @Inject constructor(
    application: Application,
    val objectDao: ObjectDao,
    val sortType: Int,
    val timeHelper: TimeHelper
): AndroidViewModel(application) {

    private var listOfStatusObjects: LiveData<List<ObjectEntity>> = objectDao.getAllStatusObjectsAsLiveData()
    private var listDutyObjects: LiveData<List<ObjectEntity>> = objectDao.getAllDutyObjectsAsLiveData()!!
    private var listObjects: LiveData<List<ObjectEntity>> = objectDao.getAllObjectsAsLiveData()!!
    private var objectsList: ArrayList<ObjectEntity>? = null
    private var countOfRows: LiveData<Int> = objectDao.getCountOfRows()
    private var status: LiveData<Int> = objectDao.getStatus("проверен")
    private var duty: LiveData<Int> = objectDao.getDuty("")

    fun getListOfStatusObjects(): LiveData<List<ObjectEntity>> {
        return listOfStatusObjects
    }

    fun getListDutyObjects(): LiveData<List<ObjectEntity>> {
        return listDutyObjects
    }

    fun getListObjects(): LiveData<List<ObjectEntity>> {
        return listObjects
    }

    fun getListObjectAsList() : List<ObjectEntity> {
        runBlocking {
            objectsList = ArrayList(objectDao.getAllNotesAsList())
        }
        return objectsList!!
    }

    fun getCountOfRows(): LiveData<Int> {
        return countOfRows
    }

    fun getStatus(): LiveData<Int> {
        return status
    }

    fun getDuty(): LiveData<Int> {
        return duty
    }
}