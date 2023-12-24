package com.example.lesson3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.addCallback
import com.example.lesson3.data.Group
import com.example.lesson3.data.Student
import com.example.lesson3.fragments.FacultyFragment
import com.example.lesson3.fragments.GroupFragment
import com.example.lesson3.fragments.StudentFragment
import com.example.lesson3.fragments.StudentInputFragment
import com.example.lesson3.interfaces.MainActivityCallbacks
import com.example.lesson3.repository.AppRepository
import java.util.jar.Attributes.Name

class MainActivity : AppCompatActivity(), MainActivityCallbacks {
    interface Edit{
        fun append()
        fun update()
        fun delete()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onBackPressedDispatcher.addCallback(this){
            if(supportFragmentManager.backStackEntryCount>0){
                supportFragmentManager.popBackStack()
                when (activeFragment){
                    NamesOfFragment.FACULTY->{
                        finish()
                    }
                    NamesOfFragment.GROUP->{
                        activeFragment=NamesOfFragment.FACULTY
                    }
                    NamesOfFragment.STUDENT->{
                        activeFragment=NamesOfFragment.GROUP
                    }
                    else ->{}
                }
                updateMenu(activeFragment)
            }
            else{
                finish()
            }
        }
        showFragment(activeFragment, null)
    }

    private var _miAppendFaculty: MenuItem?= null
    private var _miUpdateFaculty: MenuItem?= null
    private var _miDeleteFaculty: MenuItem?= null
    private var _miAppendGroup: MenuItem?= null
    private var _miUpdateGroup: MenuItem?= null
    private var _miDeleteGroup: MenuItem?= null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        _miAppendFaculty = menu?.findItem(R.id.miAppendFaculty)
        _miUpdateFaculty = menu?.findItem(R.id.miUpdateFaculty)
        _miDeleteFaculty = menu?.findItem(R.id.miDeleteFaculty)
        _miAppendGroup = menu?.findItem(R.id.miAppendGroup)
        _miUpdateGroup = menu?.findItem(R.id.miUpdateGroup)
        _miDeleteGroup = menu?.findItem(R.id.miDeleteGroup)
        updateMenu(activeFragment)
        return true
    }

    var activeFragment : NamesOfFragment=NamesOfFragment.FACULTY

    private fun updateMenu(fragmentType: NamesOfFragment){
        _miAppendFaculty?.isVisible = fragmentType==NamesOfFragment.FACULTY
        _miUpdateFaculty?.isVisible = fragmentType==NamesOfFragment.FACULTY
        _miDeleteFaculty?.isVisible = fragmentType==NamesOfFragment.FACULTY
        _miAppendGroup?.isVisible = fragmentType==NamesOfFragment.GROUP
        _miUpdateGroup?.isVisible = fragmentType==NamesOfFragment.GROUP
        _miDeleteGroup?.isVisible = fragmentType==NamesOfFragment.GROUP
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.miAppendFaculty -> {
                val fedit: Edit = FacultyFragment.getInstance()
                fedit.append()
                true
            }
            R.id.miUpdateFaculty -> {
                val fedit: Edit = FacultyFragment.getInstance()
                fedit.update()
                true
            }
            R.id.miDeleteFaculty -> {
                val fedit: Edit = FacultyFragment.getInstance()
                fedit.delete()
                true
            }
            R.id.miAppendGroup -> {
                val fedit: Edit = GroupFragment.getInstance()
                fedit.append()
                true
            }
            R.id.miUpdateGroup -> {
                val fedit: Edit = GroupFragment.getInstance()
                fedit.update()
                true
            }
            R.id.miDeleteGroup -> {
                val fedit: Edit = GroupFragment.getInstance()
                fedit.delete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showFragment(fragmentType: NamesOfFragment, student: Student?){
        when(fragmentType){
            NamesOfFragment.FACULTY->{
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fcMain, FacultyFragment.getInstance())
                    .addToBackStack(null)
                    .commit()
            }
            NamesOfFragment.GROUP->{
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fcMain, GroupFragment.getInstance())
                    .addToBackStack(null)
                    .commit()
            }
            NamesOfFragment.STUDENT->{
                if(student!=null)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fcMain, StudentInputFragment.newInstance(student))
                        .addToBackStack(null)
                        .commit()
            }
        }
        activeFragment=fragmentType
        updateMenu(fragmentType)
    }

    override fun newTitle(_title: String) {
        title = _title
    }

    override fun onStop() {
//        AppRepository.getInstance().saveData()
        super.onStop()
    }
}