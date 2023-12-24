package com.example.lesson3.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.lesson3.API.ListAPI
import com.example.lesson3.API.ListConnection
import com.example.lesson3.API.PostId
import com.example.lesson3.API.PostResult
import com.example.lesson3.MyApplication
import com.example.lesson3.data.Faculties
import com.example.lesson3.data.Faculty
import com.example.lesson3.data.Group
import com.example.lesson3.data.Groups
import com.example.lesson3.data.Student
import com.example.lesson3.data.Students
import com.example.lesson3.data.User
import com.example.lesson3.database.ListDatabase
import com.example.lesson3.myConsts.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppRepository {
    companion object{
        private var INSTANCE: AppRepository?=null

        fun getInstance(): AppRepository {
            if (INSTANCE==null){
                INSTANCE= AppRepository()
            }
            return INSTANCE?:
            throw IllegalStateException("реп не иниц")
        }
    }

    var faculty: MutableLiveData<Faculty> = MutableLiveData()
    var group: MutableLiveData<Group> = MutableLiveData()
    var student: MutableLiveData<Student> = MutableLiveData()


    fun getFacultyPosition(faculty: Faculty): Int=listOfFaculty.value?.indexOfFirst {
        it.id==faculty.id } ?:-1

    fun getFacultyPosition()=getFacultyPosition(faculty.value?: Faculty())

    fun setCurrentFaculty(position:Int){
        if (position<0 || (listOfFaculty.value?.size!! <= position))
            return setCurrentFaculty(listOfFaculty.value!![position])
    }

    fun setCurrentFaculty(_faculty: Faculty){
        faculty.postValue(_faculty)
    }

    fun saveData(){

    }

    fun loadData(){
        fetchFaculties()
    }

    fun getGroupPosition(group: Group): Int=listOfGroup.value?.indexOfFirst {
        it.id==group.id } ?:-1

    fun getGroupPosition()=getGroupPosition(group.value?: Group())

    fun setCurrentGroup(position:Int){
        if (listOfGroup.value==null || position<0 ||
            (listOfGroup.value?.size!! <=position))
            return setCurrentGroup(listOfGroup.value!![position])
    }

    fun setCurrentGroup(_group: Group){
        group.postValue(_group)
    }

    val facultyGroups
        get()= listOfGroup.value?.filter { it.facultyID == (faculty.value?.id ?: 0) }?.sortedBy { it.name }?: listOf()

    fun getStudentPosition(student: Student): Int=listOfStudent.value?.indexOfFirst {
        it.id==student.id } ?:-1

    fun getStudentPosition()=getStudentPosition(student.value?: Student())

    fun setCurrentStudent(position:Int){
        if (listOfStudent.value==null || position<0 ||
            (listOfStudent.value?.size!! <=position))
            return setCurrentStudent(listOfStudent.value!![position])
    }

    fun setCurrentStudent(_student: Student){
        student.postValue(_student)
    }

    fun getGroupStudents(groupID: Int) =
        (listOfStudent.value?.filter { it.groupID == groupID }?.sortedBy { it.shortName }?: listOf())

    private val listDB by lazy {OfflineDBRepository(ListDatabase.getDatabase(MyApplication.context).listDAO())}

    private val myCoroutineScope = CoroutineScope(Dispatchers.Main)

    fun onDestroy(){
        myCoroutineScope.cancel()
    }

    val listOfFaculty: LiveData<List<Faculty>> = listDB.getFaculty().asLiveData()
    val listOfGroup: LiveData<List<Group>> = listDB.getAllGroups().asLiveData()
    val listOfStudent: LiveData<List<Student>> = listDB.getAllStudents().asLiveData()


    private var listAPI = ListConnection.getClient().create(ListAPI::class.java)

    fun fetchFaculties(){
        listAPI.getFaculties().enqueue(object: Callback<Faculties> {
            override fun onFailure(call: Call<Faculties>, t :Throwable){
                Log.d(TAG,"Ошибка получения списка факультетов", t)
            }
            override fun onResponse(
                call : Call<Faculties>,
                response: Response<Faculties>
            ){
                if (response.code()==200){
                    val faculties = response.body()
                    val items = faculties?.items?:emptyList()
                    Log.d(TAG,"Получен список факультетов $items")
                    for (f in items){
                        println(f.id::class.java.typeName)
                        println(f.name::class.java.typeName)
                    }
                    myCoroutineScope.launch{
                        listDB.deleteAllFaculty()
                        for (f in items){
                            listDB.insertFaculty(f)
                        }
                    }
                    fetchGroups()
                }
            }
        })
    }

    fun addFaculty(faculty: Faculty){
        listAPI.insertFaculty(faculty)
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response: Response<PostResult>){
                    if (response.code()==200) fetchFaculties()
                }
                override fun onFailure(call:Call<PostResult>,t: Throwable){
                    Log.d(TAG,"Ошибка добавления факультета",t)
                }
            })
    }

    fun updateFaculty(faculty: Faculty){
        listAPI.updateFaculty(faculty)
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response: Response<PostResult>){
                    if (response.code()==200) fetchFaculties()
                }
                override fun onFailure(call:Call<PostResult>,t: Throwable){
                    Log.d(TAG,"Ошибка обновления факультета",t)
                }
            })
    }

    fun deleteFaculty(faculty: Faculty){
        listAPI.deleteFaculty(PostId(faculty.id))
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response: Response<PostResult>){
                    if (response.code()==200) fetchFaculties()
                }
                override fun onFailure(call:Call<PostResult>,t: Throwable){
                    Log.d(TAG,"Ошибка удаления факультета",t)
                }
            })
    }

    fun fetchGroups(){
        listAPI.getGroups().enqueue(object: Callback<Groups> {
            override fun onFailure(call: Call<Groups>, t: Throwable) {
                Log.d(TAG, "Ошибка получения списка групп", t)
            }

            override fun onResponse(
                call: Call<Groups>,
                response: Response<Groups>
            ) {
                if (response.code() == 200) {
                    val groups = response.body()
                    val items = groups?.items ?: emptyList()
                    Log.d(TAG, "Получен список групп $items")
                    myCoroutineScope.launch {
                        listDB.deleteAllGroups()
                        for (g in items) {
                            listDB.insertGroup(g)
                        }
                    }
                    fetchStudents()
                }
            }
        })
    }

    fun addGroup(group: Group){
        listAPI.insertGroup(group)
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response:Response<PostResult>){
                    if (response.code()==200) fetchGroups()
                }
                override fun onFailure(call:Call<PostResult>,t:Throwable){
                    Log.d(TAG,"Ошибка обновления группы", t)
                }
            })
    }

    fun updateGroup(group: Group){
        listAPI.updateGroup(group)
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response:Response<PostResult>){
                    if (response.code()==200) fetchGroups()
                }
                override fun onFailure(call:Call<PostResult>,t:Throwable){
                    Log.d(TAG,"Ошибка записи группы", t)
                }
            })
    }

    fun deleteGroup(group: Group){
        listAPI.deleteGroup(PostId(group.id))
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response:Response<PostResult>){
                    if (response.code()==200) fetchGroups()
                }
                override fun onFailure(call:Call<PostResult>,t:Throwable){
                    Log.d(TAG,"Ошибка удаления группы", t)
                }
            })
    }

    fun fetchStudents(){
        listAPI.getStudents().enqueue(object : Callback<Students>{
            override fun onFailure(call:Call<Students>,t : Throwable){
                Log.d(TAG,"Ошибка получения списка студентов",t)
            }
            override fun onResponse(
                call:Call<Students>,
                response: Response<Students>
            ){
                if(response.code()==200){
                    val students = response.body()
                    val items = students?.items?: emptyList()
                    Log.d(TAG,"Получен список студентов $items")
                    myCoroutineScope.launch {
                        listDB.deleteAllStudents()
                        for (s in items){
                            listDB.insertStudent(s)
                        }
                    }
                }
            }
        })
    }

    fun addStudent(student: Student){
        listAPI.insertStudent(student)
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response:Response<PostResult>){
                    if (response.code()==200) fetchStudents()
                }
                override fun onFailure(call:Call<PostResult>,t:Throwable){
                    Log.d(TAG,"Ошибка записи студента", t.fillInStackTrace())
                }
            })
    }

    fun updateStudent(student: Student){
        listAPI.updateStudent(student)
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response:Response<PostResult>){
                    if (response.code()==200) fetchStudents()
                }
                override fun onFailure(call:Call<PostResult>,t:Throwable){
                    Log.d(TAG,"Ошибка обновления студента", t.fillInStackTrace())
                }
            })
    }

    fun deleteStudent(student: Student){
        listAPI.deleteStudent(PostId(student.id))
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response:Response<PostResult>){
                    if (response.code()==200) fetchStudents()
                }
                override fun onFailure(call:Call<PostResult>,t:Throwable){
                    Log.d(TAG,"Ошибка удаления студента", t.fillInStackTrace())
                }
            })
    }

    fun login(user: User){
        listAPI.login(user).enqueue(object: Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d(TAG, "Ошибка получения списка групп", t)
            }

            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                if (response.code() == 200) {
                    //ЛОГИН ТУТ
//                    myCoroutineScope.launch {
//                        listDB.deleteAllGroups()
//                        for (g in items) {
//                            listDB.insertGroup(g)
//                        }
//                    }
//                    fetchStudents()
                }
            }
        })
    }

}





















