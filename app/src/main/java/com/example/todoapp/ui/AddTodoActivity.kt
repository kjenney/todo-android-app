package com.example.todoapp.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AddTodoActivity : AppCompatActivity() {

    private lateinit var todoTextInput: TextInputEditText
    private lateinit var selectTimeButton: MaterialButton
    private lateinit var selectedTimeText: TextView
    private lateinit var saveButton: MaterialButton
    private lateinit var cancelButton: MaterialButton

    private val viewModel: TodoViewModel by viewModels()
    private val calendar = Calendar.getInstance()
    private var selectedDateTime: Long? = null
    private var editingTodoId: Long? = null
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo)

        // Check if we're editing an existing todo
        editingTodoId = intent.getLongExtra("TODO_ID", -1L).takeIf { it != -1L }
        isEditMode = editingTodoId != null

        initViews()
        setupListeners()

        // Set default time to 9 AM if creating new todo
        if (!isEditMode) {
            calendar.set(Calendar.HOUR_OF_DAY, 9)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            updateSelectedTime()
        }

        // Load existing todo if editing
        if (isEditMode) {
            supportActionBar?.title = "Edit Todo"
            saveButton.text = "Update"
            loadTodoForEditing()
        } else {
            supportActionBar?.title = "Add Todo"
        }
    }

    private fun initViews() {
        todoTextInput = findViewById(R.id.todoTextInput)
        selectTimeButton = findViewById(R.id.selectTimeButton)
        selectedTimeText = findViewById(R.id.selectedTimeText)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
    }

    private fun setupListeners() {
        selectTimeButton.setOnClickListener {
            showTimePicker()
        }

        saveButton.setOnClickListener {
            saveTodo()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun showTimePicker() {
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                updateSelectedTime()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun updateSelectedTime() {
        // Set to today's date with the selected time
        val todayCalendar = Calendar.getInstance()
        todayCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
        todayCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
        todayCalendar.set(Calendar.SECOND, 0)
        todayCalendar.set(Calendar.MILLISECOND, 0)

        selectedDateTime = todayCalendar.timeInMillis
        val format = SimpleDateFormat("h:mm a", Locale.getDefault())
        selectedTimeText.text = "Daily at ${format.format(Date(selectedDateTime!!))}"
    }

    private fun loadTodoForEditing() {
        editingTodoId?.let { id ->
            viewModel.getTodoById(id).observe(this) { todo ->
                todo?.let {
                    // Populate fields with existing todo data
                    todoTextInput.setText(it.text)

                    // Set time
                    if (it.dueDateTime != null) {
                        calendar.timeInMillis = it.dueDateTime
                        updateSelectedTime()
                    }
                }
            }
        }
    }

    private fun saveTodo() {
        val text = todoTextInput.text?.toString()?.trim()
        if (text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter todo text", Toast.LENGTH_SHORT).show()
            return
        }

        if (isEditMode && editingTodoId != null) {
            // Update existing todo
            viewModel.getTodoById(editingTodoId!!).observe(this) { existingTodo ->
                existingTodo?.let { todo ->
                    val updatedTodo = todo.copy(
                        text = text,
                        dueDateTime = selectedDateTime,
                        lastModifiedAt = System.currentTimeMillis()
                    )
                    viewModel.updateTodo(updatedTodo)
                    Toast.makeText(this, "Todo updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } else {
            // Add new todo - will automatically recur daily
            viewModel.addTodo(text, selectedDateTime)
            Toast.makeText(this, "Todo added", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
