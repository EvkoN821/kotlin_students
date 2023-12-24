package com.example.lesson3.data

import com.google.gson.annotations.SerializedName

class Faculties {
    @SerializedName("item") lateinit var items: List<Faculty>
}