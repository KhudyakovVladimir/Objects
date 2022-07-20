package com.khudyakovvladimir.objects.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ObjectEntity::class], exportSchema = false, version = 1)
abstract class ObjectDatabase: RoomDatabase() {

    abstract fun objectDao() : ObjectDao

    companion object {
        private const val OBJECT_DATABASE = "object_db"
        var instance: ObjectDatabase? = null

        fun getInstance(context: Context): ObjectDatabase? {
            if(instance == null) {
                synchronized(this) {
                    instance = Room
                        .databaseBuilder(context, ObjectDatabase::class.java, OBJECT_DATABASE)
                        .build()
                }
            }
            return instance
        }
    }

    fun destroyInstance() {
        instance = null
    }
}