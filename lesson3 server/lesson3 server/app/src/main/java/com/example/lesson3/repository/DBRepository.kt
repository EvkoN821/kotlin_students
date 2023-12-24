package com.example.lesson3.repository

import com.example.lesson3.data.Faculty
import com.example.lesson3.data.Group
import com.example.lesson3.data.Student
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface DBRepository {
    fun getFaculty(): Flow<List<Faculty>>
    suspend fun insertFaculty(faculty: Faculty)
    suspend fun updateFaculty(faculty: Faculty)
    suspend fun insertAllFaculty(facultyList: List<Faculty>)
    suspend fun deleteFaculty(faculty: Faculty)
    suspend fun deleteAllFaculty()

    fun getAllGroups(): Flow<List<Group>>
    fun getFacultyGroups(facultyID : UUID): Flow<List<Group>>
    suspend fun insertGroup(group: Group)
    suspend fun deleteGroup(group: Group)
    suspend fun deleteAllGroups()

    fun getAllStudents(): Flow<List<Student>>
    fun getGroupStudents(groupID: UUID): Flow<List<Student>>
    suspend fun insertStudent(student: Student)
    suspend fun deleteStudent(student: Student)
    suspend fun deleteAllStudents()
}
