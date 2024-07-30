package com.khudyakovvladimir.objects.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ObjectDao {

    @Query("SELECT * FROM objects")
    fun getAllObjects(): List<ObjectEntity>

    @Query("SELECT * FROM objects")
    fun getAllNotesAsList(): List<ObjectEntity>

    @Query("SELECT icon FROM objects")
    fun getListOfDays(): List<String>

    @Query("SELECT * FROM objects")
    fun getAllObjectsAsLiveData(): LiveData<List<ObjectEntity>>?

    @Query("SELECT * FROM objects ORDER by duty DESC")
    fun getAllDutyObjectsAsLiveData(): LiveData<List<ObjectEntity>>?

    @Query("SELECT * FROM objects ORDER by status DESC")
    fun getAllStatusObjectsAsLiveData(): LiveData<List<ObjectEntity>>

    @Query("SELECT COUNT(id) FROM objects")
    fun getCountOfRows(): LiveData<Int>

    @Query("SELECT COUNT(id) FROM objects")
    fun getCountOfRowsAsInt(): Int

    @Query("SELECT COUNT(id) FROM objects WHERE status LIKE :value")
    fun getStatus(value: String): LiveData<Int>

    @Query("SELECT COUNT(id) FROM objects WHERE duty != :value")
    fun getDuty(value: String): LiveData<Int>

    @Query("SELECT COUNT(*) FROM objects")
    fun getCount(): Int

    @Query("SELECT * FROM objects WHERE id = :id")
    fun getObjectById(id: Int): ObjectEntity

    @Query("DELETE FROM objects")
    fun deleteAllObjects()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObjectEntity(objectEntity: ObjectEntity)

    @Delete
    fun deleteObjectEntity(objectEntity: ObjectEntity)

    @Query("DELETE FROM objects WHERE id = :id")
    fun deleteObjectByID(id : Int)

    @Update
    fun updateObjEntity(objectEntity: ObjectEntity)
}