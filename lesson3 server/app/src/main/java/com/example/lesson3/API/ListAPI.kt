package com.example.lesson3.API

import com.example.lesson3.data.Faculties
import com.example.lesson3.data.Faculty
import com.example.lesson3.data.Group
import com.example.lesson3.data.Groups
import com.example.lesson3.data.Student
import com.example.lesson3.data.Students
import com.example.lesson3.data.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PUT

interface ListAPI{
    @GET("faculty")
    fun getFaculties(): Call<Faculties>

    @Headers("Content-Type: application/json")
    @POST("faculty")
    fun updateFaculty(@Body faculty: Faculty): Call<PostResult>

    @Headers("Content-Type: application/json")
    @POST("faculty/delete")
    fun deleteFaculty(@Body id: PostId): Call<PostResult>

    @Headers("Content-Type: application/json")
    @PUT("faculty")
    fun insertFaculty(@Body faculty: Faculty): Call<PostResult>

    @GET("group")
    fun getGroups(): Call<Groups>
    @Headers("Content-Type: application/json")
    @POST("group")
    fun updateGroup(@Body group: Group): Call<PostResult>

    @Headers("Content-Type: application/json")
    @POST("group/delete")
    fun deleteGroup(@Body id: PostId): Call<PostResult>

    @Headers("Content-Type: application/json")
    @PUT("group")
    fun insertGroup(@Body group: Group): Call<PostResult>

    @GET("student")
    fun getStudents(): Call<Students>

    @Headers("Content-Type: application/json")
    @POST("student")
    fun updateStudent(@Body student: Student): Call<PostResult>

    @Headers("Content-Type: application/json")
    @POST("student/delete")
    fun deleteStudent(@Body id: PostId): Call<PostResult>

    @Headers("Content-Type: application/json")
    @PUT("student")
    fun insertStudent(@Body student: Student): Call<PostResult>

    @Headers("Content-Type: application/json")
    @POST("user")
    fun login(@Body user: User): Call<User>

}