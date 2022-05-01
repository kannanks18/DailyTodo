package com.machine.dailytodo.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.machine.dailytodo.databinding.ItemTodosBinding
import com.machine.dailytodo.ui.dashboard.model.TodoModel

class DashboardAdapter(private val todoClickListener: TODOClickListener) :
    ListAdapter<TodoModel, RecyclerView.ViewHolder>(TodoDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemTodosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TodoViewHolder).bind(getItem(position))
    }

    inner class TodoViewHolder(private val binding: ItemTodosBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: TodoModel) {
            binding.apply {
                titleText.text=todo.todoName
                titleTime.text=todo.todoTime
                if (todo.status == "1"){
                    status.text ="Completed"
                }else{
                    status.text =""
                }
                root.setOnClickListener {
                    todoClickListener.onClick(todo)
                }
            }

        }
    }
}
interface TODOClickListener {
    fun onClick(todo: TodoModel)
}
object TodoDiff : DiffUtil.ItemCallback<TodoModel>() {
    override fun areItemsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
        return oldItem.key == newItem.key
    }

    override fun areContentsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
        return oldItem == newItem
    }
}