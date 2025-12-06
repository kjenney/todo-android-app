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
import com.example.todoapp.data.RecurrenceType
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

        // Display due date
        if (todo.dueDateTime != null) {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault())
            holder.dueDateText.text = dateFormat.format(Date(todo.dueDateTime))
            holder.dueDateText.visibility = View.VISIBLE
        } else {
            holder.dueDateText.visibility = View.GONE
        }

        // Display recurrence info
        if (todo.recurrencePattern.type != RecurrenceType.NONE) {
            holder.recurrenceText.text = getRecurrenceText(todo)
            holder.recurrenceText.visibility = View.VISIBLE
        } else {
            holder.recurrenceText.visibility = View.GONE
        }

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

    private fun getRecurrenceText(todo: TodoEntity): String {
        val pattern = todo.recurrencePattern
        return when (pattern.type) {
            RecurrenceType.HOURLY -> "Every ${pattern.interval} hour(s)"
            RecurrenceType.TWICE_DAILY -> "Twice daily"
            RecurrenceType.DAILY -> "Every ${pattern.interval} day(s)"
            RecurrenceType.WEEKLY -> {
                if (pattern.daysOfWeek.isNotEmpty()) {
                    val days = pattern.daysOfWeek.sorted().joinToString(", ") { dayNum ->
                        when (dayNum) {
                            1 -> "Mon"
                            2 -> "Tue"
                            3 -> "Wed"
                            4 -> "Thu"
                            5 -> "Fri"
                            6 -> "Sat"
                            7 -> "Sun"
                            else -> ""
                        }
                    }
                    "Weekly: $days"
                } else {
                    "Every ${pattern.interval} week(s)"
                }
            }
            RecurrenceType.MONTHLY -> "Every ${pattern.interval} month(s)"
            RecurrenceType.YEARLY -> "Every ${pattern.interval} year(s)"
            else -> ""
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
