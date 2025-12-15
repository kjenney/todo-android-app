package com.example.todoapp.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.data.TodoEntity
import java.text.SimpleDateFormat
import java.util.*

class TodoEntityAdapter(
    private val onToggleComplete: (TodoEntity) -> Unit,
    private val onDeleteClick: (TodoEntity) -> Unit,
    private val onItemClick: (TodoEntity) -> Unit
) : ListAdapter<TodoEntity, TodoEntityAdapter.TodoViewHolder>(TodoDiffCallback()) {

    private var selectedTodoId: Long? = null

    class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.todoCheckBox)
        val dueDateText: TextView = view.findViewById(R.id.dueDateText)
        val recurrenceText: TextView = view.findViewById(R.id.recurrenceText)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
        val cardView: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = getItem(position)

        // CRITICAL: Remove old listener BEFORE setting checkbox state
        // to prevent triggering toggles during view recycling
        holder.checkBox.setOnCheckedChangeListener(null)

        holder.checkBox.text = todo.text
        holder.checkBox.isChecked = todo.isCompleted

        // Update text style based on completion status
        updateTextStyle(holder.checkBox, todo.isCompleted)

        // Display notification time
        if (todo.dueDateTime != null) {
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            holder.dueDateText.text = timeFormat.format(Date(todo.dueDateTime))
            holder.dueDateText.visibility = View.VISIBLE
        } else {
            holder.dueDateText.visibility = View.GONE
        }

        // All todos are daily recurring
        holder.recurrenceText.text = "Daily"
        holder.recurrenceText.visibility = View.VISIBLE

        // Apply selection state
        holder.cardView.isSelected = todo.id == selectedTodoId

        // Set new listener AFTER setting checkbox state
        holder.checkBox.setOnCheckedChangeListener { _, _ ->
            onToggleComplete(todo)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(todo)
        }

        holder.itemView.setOnClickListener {
            // Update selection state
            val previousSelectedId = selectedTodoId
            selectedTodoId = todo.id

            // Notify adapter to update the previously selected item and the new one
            if (previousSelectedId != null) {
                val previousPosition = currentList.indexOfFirst { it.id == previousSelectedId }
                if (previousPosition != -1) {
                    notifyItemChanged(previousPosition)
                }
            }
            notifyItemChanged(position)

            onItemClick(todo)
        }
    }

    private fun updateTextStyle(checkBox: CheckBox, isCompleted: Boolean) {
        if (isCompleted) {
            checkBox.paintFlags = checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            checkBox.paintFlags = checkBox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    class TodoDiffCallback : DiffUtil.ItemCallback<TodoEntity>() {
        override fun areItemsTheSame(oldItem: TodoEntity, newItem: TodoEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TodoEntity, newItem: TodoEntity): Boolean {
            return oldItem == newItem
        }
    }
}
