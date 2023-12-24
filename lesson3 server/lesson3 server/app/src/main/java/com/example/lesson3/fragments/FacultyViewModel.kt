package com.example.lesson3.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.lesson3.data.Faculty
import com.example.lesson3.data.ListOfFaculty
import com.example.lesson3.myConsts.TAG
import com.example.lesson3.repository.AppRepository

class FacultyViewModel : ViewModel() {

    var facultyList: LiveData<List<Faculty>> = AppRepository.getInstance().listOfFaculty
    private var _faculty : Faculty = Faculty()
    val faculty
        get()=_faculty

    init{
        AppRepository.getInstance().faculty.observeForever{
            _faculty=it
            Log.d(TAG, "получен student v studlistviewmodel")
        }
    }

    fun deleteFaculty(){
        if (faculty!=null)
            AppRepository.getInstance().deleteFaculty(faculty!!)
    }

    fun appendFaculty(facultyName: String){
        val faculty=Faculty()
        faculty.name=facultyName
        AppRepository.getInstance().addFaculty(faculty)
    }

    fun updateFaculty(facultyName: String){
        if (_faculty!=null){
            _faculty!!.name=facultyName
            AppRepository.getInstance().updateFaculty(_faculty!!)
        }
    }

    fun setFaculty(faculty: Faculty){
        AppRepository.getInstance().setCurrentFaculty(faculty)
    }
}






