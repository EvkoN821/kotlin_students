package com.example.lesson3.fragments

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.lesson3.MainActivity
import com.example.lesson3.NamesOfFragment
import com.example.lesson3.R
import com.example.lesson3.data.Group
import com.example.lesson3.databinding.FragmentGroupBinding
import com.example.lesson3.interfaces.MainActivityCallbacks
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class GroupFragment : Fragment(), MainActivity.Edit {

    companion object {
        private var INSTANCE : GroupFragment?= null
        fun getInstance(): GroupFragment {
            if (INSTANCE == null) INSTANCE= GroupFragment()
            return INSTANCE ?: throw Exception("GroupFragment ne sozdan")
        }
        fun newInstance() : GroupFragment{
            INSTANCE= GroupFragment()
            return INSTANCE!!
        }
    }

    private lateinit var viewModel: GroupViewModel
    private var tabPosition: Int=0
    private lateinit var _binding: FragmentGroupBinding
    private val binding get()= _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(GroupViewModel::class.java)
        val ma= (requireActivity() as MainActivityCallbacks)
        ma.newTitle("ФАКУЛЬТЕТ  \"${viewModel.faculty?.name}\"")

        viewModel.groupList.observe(viewLifecycleOwner
        ) {
            createUI(it)
        }
    }

    private inner class GroupPageAdapter(fa: FragmentActivity, private val groups: List<Group>?): FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return (groups?.size ?: 0)
        }

        override fun createFragment(position: Int): Fragment{
            return StudentFragment.newInstance((groups!![position]))
        }
    }

    private fun createUI(groupList: List<Group>){
        binding.tlGroup.clearOnTabSelectedListeners()
        binding.tlGroup.removeAllTabs()

        for (i in 0 until (groupList.size)){
            binding.tlGroup.addTab(binding.tlGroup.newTab().apply {
                text= groupList.get(i).name
            })
        }

        val adapter= GroupPageAdapter(requireActivity(), viewModel.groupList.value)
        binding.vpGroup.adapter=adapter
        TabLayoutMediator(binding.tlGroup, binding.vpGroup, true, true){
                tab,pos ->
            tab.text=groupList.get(pos).name
        }.attach()
        tabPosition=0
        if(viewModel.group!=null)
            tabPosition= if(viewModel.getGroupListPosition>=0)
                viewModel.getGroupListPosition
        else
            0

        viewModel.setCurrentGroup(tabPosition)
        binding.tlGroup.selectTab(binding.tlGroup.getTabAt(tabPosition), true)
        binding.tlGroup.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabPosition=tab?.position!!
                viewModel.setCurrentGroup(groupList[tabPosition])
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    override fun append(){
        editGroup()
    }

    override fun update(){
        editGroup(viewModel.group?.name ?: "")
    }

    override fun delete(){
        deleteDialog()
    }

    private fun deleteDialog(){
        if (viewModel.group==null) return
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление!")
            .setMessage("Вы действительно хотите удалить группу ${viewModel.group?.name ?: ""}?")
            .setPositiveButton("ДА"){_,_ ->
                viewModel.deleteGroup()
            }
            .setNegativeButton("НЕТ", null)
            .setCancelable(true)
            .create()
            .show()
    }

    private fun editGroup(groupName: String=""){
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog__string, null)
        val messageText = mDialogView.findViewById<TextView>(R.id.tvInfo)
        val inputString = mDialogView.findViewById<EditText>(R.id.etString)
        inputString.setText(groupName)
        messageText.text="Укажите наименование группы"

        AlertDialog.Builder(requireContext())
            .setTitle("ИЗМЕНЕНИЕ ДАННЫХ")
            .setView(mDialogView)
            .setPositiveButton("Ok"){_,_ ->
                if (inputString.text.isNotBlank()){
                    if (groupName.isBlank())
                        viewModel.appendGroup(inputString.text.toString())
                    else
                        viewModel.updateGroup(inputString.text.toString())
                }
            }
            .setNegativeButton("Back",null)
            .setCancelable(true)
            .create()
            .show()
    }



//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(GroupViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}