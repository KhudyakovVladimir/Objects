package com.khudyakovvladimir.objects.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ObjectDao {

    @Query("SELECT * FROM objects")
    fun getAllObjects(): List<ObjectEntity>

    @Query("SELECT * FROM objects")
    fun getAllObjectsAsLiveData(): LiveData<List<ObjectEntity>>?

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

    @Update
    fun updateObjEntity(objectEntity: ObjectEntity)
}