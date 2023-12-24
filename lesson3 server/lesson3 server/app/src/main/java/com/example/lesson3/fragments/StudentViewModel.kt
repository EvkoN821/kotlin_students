package com.example.lesson3.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lesson3.data.Group
import com.example.lesson3.data.Student
import com.example.lesson3.repository.AppRepository
import java.util.Date

class StudentViewModel : ViewModel() {
    var studentList: MutableLiveData<List<Student>> = MutableLiveData()

    private var _student: Student?= null
    val student
        get()= _student

    var group: Group? = null

    fun set_Group(group: Group) {
        this.group = group
        AppRepository.getInstance().listOfStudent.observeForever{
            studentList.postValue(AppRepository.getInstance().getGroupStudents(group.id))
        }
        AppRepository.getInstance().student.observeForever{
            _student=it
        }
    }

    fun deleteStudent() {
        if(student!=null)
            AppRepository.getInstance().deleteStudent(student!!)
    }

    fun appendStudent(lastName:String, firstName:String, middleName:String, birthDate: Date, phone:String){
        val student = Student()
        student.lastname = lastName
        student.firstname = firstName
        student.middlename= middleName
        student.birthDate = birthDate
        student.phone=phone
        student.groupID = group!!.id
        AppRepository.getInstance().addStudent(student)
    }

    fun updateStudent(lastName:String, firstName:String, middleName:String, birthDate: Date, phone:String){
        if (_student!=null){
            _student!!.lastname = lastName
            _student!!.firstname = firstName
            _student!!.middlename= middleName
            _student!!.birthDate = birthDate
            _student!!.phone = phone
            AppRepository.getInstance().updateStudent(_student!!)
        }
    }

    fun setCurrentStudent(student: Student){
        AppRepository.getInstance().setCurrentStudent(student)
    }

}