package com.example.todoapp

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(
    private val todos: MutableList<Todo>,
    private val onDeleteClick: (Todo) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.todoCheckBox)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todos[position]

        holder.checkBox.text = todo.text
        holder.checkBox.isChecked = todo.isCompleted

        // Update text style based on completion status
        updateTextStyle(holder.checkBox, todo.isCompleted)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            todo.isCompleted = isChecked
            updateTextStyle(holder.checkBox, isChecked)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(todo)
        }
    }

    private fun updateTextStyle(checkBox: CheckBox, isCompleted: Boolean) {
        if (isCompleted) {
            checkBox.paintFlags = checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            checkBox.paintFlags = checkBox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    override fun getItemCount() = todos.size

    fun addTodo(todo: Todo) {
        todos.add(0, todo)
        notifyItemInserted(0)
    }

    fun removeTodo(todo: Todo) {
        val position = todos.indexOf(todo)
        if (position != -1) {
            todos.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
