package com.example.lesson3.fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson3.MainActivity
import android.Manifest
import com.example.lesson3.NamesOfFragment
import com.example.lesson3.R
import com.example.lesson3.data.Group
import com.example.lesson3.data.Student
import com.example.lesson3.databinding.FragmentStudentBinding
import com.example.lesson3.interfaces.MainActivityCallbacks
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class StudentFragment : Fragment(){

    companion object {
        private lateinit var group: Group
        fun newInstance(group: Group): StudentFragment{
            this.group = group
            return StudentFragment()
        }
    }

    private lateinit var viewModel: StudentViewModel
    private lateinit var _binding : FragmentStudentBinding

    val binding
        get()= _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentStudentBinding.inflate(inflater, container, false)
        binding.rvStudent.layoutManager=
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this).get(StudentViewModel::class.java)
        viewModel.set_Group(group)
        viewModel.studentList.observe(viewLifecycleOwner){
            binding.rvStudent.adapter=StudentAdapter(it)
        }
        binding.fabNewStudent.setOnClickListener{
            editStudent(Student().apply { groupID = viewModel.group!!.id })
        }
    }

    private fun deleteDialog(){
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление!")
            .setMessage("Вы действительно хотите удалить студента ${viewModel.student?.shortName ?: ""}?")
            .setPositiveButton("да"){_,_ ->
                viewModel.deleteStudent()
            }
            .setNegativeButton("нет", null)
            .setCancelable(true)
            .create()
            .show()
    }

    private fun editStudent(student: Student? = null){
        (requireActivity() as MainActivityCallbacks).showFragment(NamesOfFragment.STUDENT, student)
        (requireActivity() as MainActivityCallbacks).newTitle("Группа${viewModel.group!!.name}")
    }

    private inner class StudentAdapter(private val items: List<Student>)
        : RecyclerView.Adapter<StudentAdapter.ItemHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): StudentAdapter.ItemHolder {
            val view = layoutInflater.inflate(R.layout.element_student_list, parent, false)
            return ItemHolder(view)
        }

        override fun getItemCount(): Int= items.size

        override fun onBindViewHolder(holder: StudentAdapter.ItemHolder, position: Int) {
            holder.bind(viewModel.studentList.value!![position])
        }

        private var lastView: View? = null

        private fun updateCurrentView(view: View){
            lastView?.findViewById<ConstraintLayout>(R.id.clStudent)?.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.white))
            view.findViewById<ConstraintLayout>(R.id.clStudent).setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.mygray))
            lastView= view
        }

        private inner class ItemHolder(view: View)
            : RecyclerView.ViewHolder(view) {

                private lateinit var student: Student

                fun bind(student: Student){
                    this.student= student
                    if (student==viewModel.student)
                        updateCurrentView(itemView)
                    val tv = itemView.findViewById<TextView>(R.id.tvStudentName)
                    tv.text=student.shortName
                    val cl = itemView.findViewById<ConstraintLayout>(R.id.clStudent)
                    cl.setOnClickListener {
                        viewModel.setCurrentStudent(student)
                        updateCurrentView(itemView)
                    }
                    itemView.findViewById<ImageButton>(R.id.ibEditStudent).setOnClickListener{
                        editStudent(student)
                    }
                    itemView.findViewById<ImageButton>(R.id.ibDeleteStudent).setOnClickListener{
                        deleteDialog()
                    }
                    itemView.findViewById<ImageButton>(R.id.ibPhone).setOnClickListener {
                        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${student.phone}"))
                            startActivity(intent)
                        }
                        else {
                            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CALL_PHONE), 2)
                        }
                    }

                    val llb = itemView.findViewById<LinearLayout>(R.id.llStudentButtons)
                    llb.visibility=View.INVISIBLE
                    llb?.layoutParams=llb?.layoutParams.apply { this?.width=1 }
                    val ib=itemView.findViewById<ImageButton>(R.id.ibPhone)
                    ib.visibility=View.INVISIBLE
                    cl.setOnLongClickListener{
                        cl.callOnClick()
                        llb.visibility=View.VISIBLE
                        if(student.phone.isNotBlank())
                            ib.visibility = View.VISIBLE
                        MainScope().
                        launch{
                            val lp= llb?.layoutParams
                            lp?.width= 1
                            val ip=ib.layoutParams
                            ip.width=1
                            while(lp?.width!!<350){
                                lp?.width=lp?.width!!+35
                                llb?.layoutParams=lp
                                ip.width=ip.width+10
                                if (ib.visibility==View.VISIBLE)
                                    ib.layoutParams=ip
                                delay(50)
                            }
                        }
                        true
                    }
                }
        }
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(StudentViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}