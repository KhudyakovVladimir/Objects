package com.khudyakovvladimir.objects.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "objects")
data class ObjectEntity(

    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "title", typeAffinity = ColumnInfo.TEXT)
    var name: String,

    @ColumnInfo(name = "type", typeAffinity = ColumnInfo.TEXT)
    var type: String,

    @ColumnInfo(name = "status", typeAffinity = ColumnInfo.TEXT)
    var status: String,

    @ColumnInfo(name = "duty", typeAffinity = ColumnInfo.TEXT)
    var duty: String,

    @ColumnInfo(name = "coordinates", typeAffinity = ColumnInfo.TEXT)
    var coordinates: String,

    @ColumnInfo(name = "comment", typeAffinity = ColumnInfo.TEXT)
    var comment: String,

    @ColumnInfo(name = "icon", typeAffinity = ColumnInfo.TEXT)
    var icon: String,
)