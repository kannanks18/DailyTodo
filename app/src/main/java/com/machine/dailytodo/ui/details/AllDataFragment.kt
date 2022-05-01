package com.machine.dailytodo.ui.details

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.machine.dailytodo.R
import com.machine.dailytodo.databinding.FragmentAllDataBinding
import com.machine.dailytodo.ui.dashboard.DashboardViewModel
import com.machine.dailytodo.ui.dashboard.model.TodoModel
import com.machine.dailytodo.utils.showProgressDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllDataFragment : Fragment(R.layout.fragment_all_data) {
    var mobile: String? = ""
    private val db = Firebase.firestore
    var databaseReference: DatabaseReference? = null
    private val todoAdapter by lazy { AllTodoAdapter() }
    private lateinit var todoList: ArrayList<TodoModel>
    var loading: Dialog? = null
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var binding: FragmentAllDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentAllDataBinding.bind(view)
        mobile = viewModel.mobileNumber()
        binding.recyclerView.adapter = todoAdapter
        todoList = arrayListOf()
        fetchData()

    }
    private fun fetchData() {
        initLoading()
        todoList.clear()
        db.collection("todo").document(mobile!!).collection(mobile!!)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val todo: TodoModel = document.toObject(TodoModel::class.java)
                    Log.d("TAG", "${document.id} => ${document.data}")
                    todoList.add(todo)
                }
                fillData(todoList)

            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
    }

    private fun fillData(todoList: List<TodoModel>) {
        Log.d("TAG", "$todoList")
        if (todoList.isNotEmpty()) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.nodatamessage.visibility = View.GONE
            todoAdapter.submitList(todoList)
            todoAdapter.notifyDataSetChanged()
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.nodatamessage.visibility = View.VISIBLE
            todoAdapter.submitList(todoList)
            todoAdapter.notifyDataSetChanged()

        }
        cancelLoading()
    }


    private fun initLoading() {
        loading = showProgressDialog(requireContext())
    }

    private fun cancelLoading() {
        if (loading != null) {
            loading?.cancel()
            loading = null
        }
    }

}