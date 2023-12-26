package com.example.lesson3.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.lesson3.R
import com.example.lesson3.data.Student
import com.example.lesson3.databinding.FragmentStudentInput2Binding
import com.example.lesson3.repository.AppRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date

private const val ARG_PARAM1 = "student_param"

class StudentInputFragment : Fragment() {
    private lateinit var student: Student
    private lateinit var _binding : FragmentStudentInput2Binding
//    private lateinit var et1: EditText
//    private lateinit var et2: EditText
//    private lateinit var et3: EditText
//    private lateinit var et4: EditText

    val binding
        get()=_binding
    var flag = false
    var flag_validation = true
    override fun onCreate(savedInstanceState: Bundle?) {
//        et1 = findViewById(R.id.etFirstName)
//        et2 = findViewById(R.id.etLastName)
//        et3 = findViewById(R.id.etPatrName)
//        et4 = findViewById(R.id.etPhone)
        super.onCreate(savedInstanceState)
        arguments?.let {
            val param1 = it.getString(ARG_PARAM1)
            if (param1==null) {
                student = Student()
            }
            else{
                val paramType= object : TypeToken<Student>(){}.type
                student = Gson().fromJson<Student>(param1, paramType)
            }
        }
    }

    fun validation (et1: String, et2: String, et3: String, et4: String ){
        flag_validation = true
        if (et1.isBlank() or ! et1.matches(Regex("^[a-zA-Z_]+$"))) {
            binding.etFirstName.requestFocus()
            binding.etFirstName.setError("Введите корректные данные")
            flag_validation = false
        }
        if (et2.isBlank() or ! et2.matches(Regex("^[a-zA-Z_]+$"))) {
            binding.etLastName.requestFocus()
            binding.etLastName.setError("Введите корректные данные")
            flag_validation = false
        }
        if (et3.isBlank() or ! et3.matches(Regex("^[a-zA-Z_]+$"))) {
            binding.etPatrName.requestFocus()
            binding.etPatrName.setError("Введите корректные данные")
            flag_validation = false
        }
        if (et4.isBlank()) {
            binding.etPhone.requestFocus()
            binding.etPhone.setError("Введите корректные данные")
            flag_validation = false
        }
    }
    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        flag = !(student.shortName.isBlank())

        _binding=FragmentStudentInput2Binding.inflate(inflater,container,false)
        binding.btSave.text = if (flag) "Изменить" else "Добавить"
        val sexArray = resources.getStringArray(R.array.SEX)
        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, sexArray)
        binding.spSex.adapter = adapter
        binding.spSex.setSelection(student.sex - 1)
        binding.spSex.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    student.sex=position + 1
                }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            }
        binding.etFirstName.setText(student.firstname)
        binding.etLastName.setText(student.lastname)
        binding.etPatrName.setText(student.middlename)
        binding.etPhone.setText(student.phone)
        binding.cvBirthDate.date = student.birthDate.time
        binding.cvBirthDate.setOnDateChangeListener {view, year, month, dayOfMonth ->
            student.birthDate.time  =
                    SimpleDateFormat("yyyy.MM.dd").parse("$year.$month.$dayOfMonth")?.time ?: student.birthDate.time}
        binding.btCancel.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.btSave.setOnClickListener {
            validation(binding.etFirstName.text.toString(), binding.etLastName.text.toString(),binding.etPatrName.text.toString(), binding.etPhone.text.toString() )
            if (flag_validation) {
                student.lastname = binding.etLastName.text.toString()
                student.firstname = binding.etFirstName.text.toString()
                student.middlename = binding.etPatrName.text.toString()
                student.phone = binding.etPhone.text.toString()
                if (flag)
                    AppRepository.getInstance().updateStudent(student)
                else
                    AppRepository.getInstance().addStudent(student)
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(student: Student) =
            StudentInputFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, Gson().toJson(student))
                }
            }
    }
}


