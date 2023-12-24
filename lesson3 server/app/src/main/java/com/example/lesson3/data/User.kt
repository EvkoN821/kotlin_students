package com.example.lesson3.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.UUID

@Entity(
    indices = [Index("id")]
)

data class User(
    @SerializedName("id") @PrimaryKey val id: Int=0, //UUID=UUID.randomUUID(),
    @SerializedName("login") @ColumnInfo(name="login") var login: String="",
    @SerializedName("pwd") @ColumnInfo(name="pwd") var pwd: String="",
    @SerializedName("type") @ColumnInfo(name="type") var userType: Int=0
)
