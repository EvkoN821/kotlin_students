package com.example.lesson3.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.UUID

@Entity(tableName = "groups",
    indices = [Index("id"),Index("faculty_id")],
    foreignKeys = [
        ForeignKey(
            entity = Faculty::class,
            parentColumns = ["id"],
            childColumns = ["faculty_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Group(
    @SerializedName("id") @PrimaryKey var id: Int=0, //UUID = UUID.randomUUID(),
    @SerializedName("name") @ColumnInfo(name= "group_name") var name: String="",
    @SerializedName("faculty_id") @ColumnInfo(name= "faculty_id") var facultyID: Int=0 //UUID?=null
)
