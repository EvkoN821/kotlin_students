package com.example.lesson3.data

import com.google.gson.annotations.SerializedName

class Students {
    @SerializedName("item") lateinit var items: List<Student>
}