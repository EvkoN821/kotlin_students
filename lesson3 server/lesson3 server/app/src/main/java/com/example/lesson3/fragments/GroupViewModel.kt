package com.example.lesson3.fragments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lesson3.data.Group
import com.example.lesson3.myConsts.TAG
import com.example.lesson3.repository.AppRepository

class GroupViewModel : ViewModel() {

    var groupList: MutableLiveData<List<Group>> = MutableLiveData()
    private var _group : Group? = null
    val group
        get()=_group

    init {
        AppRepository.getInstance().listOfGroup.observeForever{
            groupList.postValue(AppRepository.getInstance().facultyGroups)
        }

        AppRepository.getInstance().group.observeForever{
            _group=it
            Log.d(TAG, "GroupViewModel текущая группа $it")
        }

        AppRepository.getInstance().faculty.observeForever{
            groupList.postValue(AppRepository.getInstance().facultyGroups)
        }
    }

    fun deleteGroup(){
        if(group!=null)
            AppRepository.getInstance().deleteGroup(group!!)
    }

    fun appendGroup(groupName: String){
        val group=Group()
        group.id = 0
        group.name=groupName
        group.facultyID=faculty!!.id
        AppRepository.getInstance().addGroup(group)
    }

    fun updateGroup(groupName: String){
        if (_group!=null){
            _group!!.name=groupName
            AppRepository.getInstance().updateGroup(_group!!)
        }
    }

    fun setCurrentGroup(position: Int){
        if ((groupList.value?.size ?: 0)>position)
            groupList.value?.let{ AppRepository.getInstance().setCurrentGroup(it.get(position))}
    }

    fun setCurrentGroup(group: Group){
        AppRepository.getInstance().setCurrentGroup(group)
    }

    val getGroupListPosition
        get()= groupList.value?.indexOfFirst { it.id==group?.id } ?: -1

    val faculty
        get()=AppRepository.getInstance().faculty.value
}