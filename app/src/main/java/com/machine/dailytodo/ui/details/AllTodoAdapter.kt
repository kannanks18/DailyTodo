package com.machine.dailytodo.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.machine.dailytodo.databinding.ItemAllTodosBinding
import com.machine.dailytodo.databinding.ItemTodosBinding
import com.machine.dailytodo.ui.dashboard.TODOClickListener
import com.machine.dailytodo.ui.dashboard.model.TodoModel

class AllTodoAdapter() :
    ListAdapter<TodoModel, RecyclerView.ViewHolder>(TodoDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemAllTodosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TodoViewHolder).bind(getItem(position))
    }

    inner class TodoViewHolder(private val binding: ItemAllTodosBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: TodoModel) {
            binding.apply {
                titleText.text=todo.todoName
                titleTime.text=todo.todoTime
                titleDate.text=todo.todoDate
                if (todo.todoDesc.isEmpty()){
                    description.visibility=View.GONE
                    headerDesc.visibility=View.GONE
                }else{
                    description.visibility=View.VISIBLE
                    headerDesc.visibility=View.VISIBLE
                }
                description.text=todo.todoDesc
                comment.text=todo.comments
                if(todo.status=="0"){
                    status.visibility=View.GONE
                }else{
                    status.visibility=View.VISIBLE
                }
                root.setOnClickListener {
                    expandLayt.isVisible = !expandLayt.isVisible
                }
                if (todo.comments.isEmpty()){
                    comment.visibility=View.GONE
                    headerComment.visibility=View.GONE
                }else{
                    comment.visibility=View.VISIBLE
                    headerComment.visibility=View.VISIBLE
                }
            }

        }
    }
}

object TodoDiff : DiffUtil.ItemCallback<TodoModel>() {
    override fun areItemsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
        return oldItem.key == newItem.key
    }

    override fun areContentsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
        return oldItem == newItem
    }
}