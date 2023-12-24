package com.example.lesson3.fragments

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Carousel.Adapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.SharedPreferencesCompat.EditorCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson3.MainActivity
import com.example.lesson3.NamesOfFragment
import com.example.lesson3.R
import com.example.lesson3.data.Faculty
import com.example.lesson3.databinding.FragmentFacultyBinding
import com.example.lesson3.interfaces.MainActivityCallbacks
import java.lang.Exception

const val FACULTY_TAG = "com.example.lesson3.FacultyFragment"
const val FACULTY_TITLE="Универ"

class FacultyFragment:Fragment(), MainActivity.Edit {

    interface Callback{
        fun newTitle(_title: String)
    }

    companion object{
//        fun newInstance()= FacultyFragment()
        private var INSTANCE : FacultyFragment?= null
        fun getInstance(): FacultyFragment{
            if (INSTANCE == null) INSTANCE= FacultyFragment()
            return INSTANCE ?: throw Exception("FacultyFragment не создан")
        }
    }

    private lateinit var viewModel: FacultyViewModel
    private var _binding: FragmentFacultyBinding?=null
    val binding
        get()=_binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ma = requireActivity() as MainActivityCallbacks
        ma.newTitle("СПИСОК ФАКУЛЬТЕТОВ")
        _binding = FragmentFacultyBinding.inflate(inflater,container,false)
        binding.rvFaculty.layoutManager=LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(FacultyViewModel::class.java)
        viewModel.facultyList.observe(viewLifecycleOwner){
            binding.rvFaculty.adapter=FacultyAdapter(it)
        }
    }

    override fun append() {
        editFaculty()
    }

    override fun update() {
        editFaculty(viewModel.faculty?.name ?: "")
    }

    override fun delete() {
        deleteDialog()
    }

    private fun deleteDialog(){
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление")
            .setMessage("Вы действительно хотите удалить факультет ${viewModel.faculty?.name ?: ""}?")
            .setPositiveButton("ДА") {_, _ ->
                viewModel.deleteFaculty()
            }
            .setNegativeButton("НЕТ", null)
            .setCancelable(true)
            .create()
            .show()
    }

    private fun editFaculty(facultyName : String=""){
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog__string, null)
        val messageText = mDialogView.findViewById<TextView>(R.id.tvInfo)
        val inputString = mDialogView.findViewById<EditText>(R.id.etString)
        inputString.setText(facultyName)
        messageText.text="укажите название факультета"

        AlertDialog.Builder(requireContext())
            .setTitle("ИЗМЕНЕНИЕ ДАННЫХ")
            .setView(mDialogView)
            .setPositiveButton("ok"){_, _ ->
                if (inputString.text.isNotBlank()) {
                    if (facultyName.isBlank())
                        viewModel.appendFaculty(inputString.text.toString())
                    else
                        viewModel.updateFaculty(inputString.text.toString())
                }
            }
            .setNegativeButton("otmena", null)
            .setCancelable(true)
            .create()
            .show()
    }

    private inner class FacultyAdapter(private val items: List<Faculty>)
        : RecyclerView.Adapter<FacultyAdapter.ItemHolder> () {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): FacultyAdapter.ItemHolder {
            val view = layoutInflater.inflate(R.layout.element_faculty_list, parent, false)
            return ItemHolder(view)
        }

        override fun getItemCount(): Int= items.size

        override fun onBindViewHolder(holder: FacultyAdapter.ItemHolder, position: Int) {
            holder.bind(viewModel.facultyList.value!![position])
        }

        private var lastView: View? = null

        private fun updateCurrentView(view: View){
            lastView?.findViewById<ConstraintLayout>(R.id.clFaculty)?.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.white)
            )
            lastView = view
            view.findViewById<ConstraintLayout>(R.id.clFaculty).setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.mygray)
            )
        }

        private inner class ItemHolder(view: View)
            : RecyclerView.ViewHolder(view) {
                private lateinit var faculty: Faculty
                fun bind(faculty: Faculty){
                    this.faculty= faculty
                    val tv = itemView.findViewById<TextView>(R.id.tvFaculty)
                    tv.text= faculty.name
                    if (faculty == viewModel.faculty)
                        updateCurrentView(itemView)
                    tv.setOnClickListener{
                        viewModel.setFaculty(faculty)
                        updateCurrentView(itemView)
                    }
                    tv.setOnLongClickListener {
                        tv.callOnClick()
                        (requireActivity() as MainActivityCallbacks).showFragment(NamesOfFragment.GROUP)
                        true
                    }
                }
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivityCallbacks).newTitle("Список факультетов")
    }
}