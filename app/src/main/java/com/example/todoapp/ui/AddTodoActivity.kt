package com.example.todoapp.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.R
import com.example.todoapp.data.RecurrencePattern
import com.example.todoapp.data.RecurrenceType
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AddTodoActivity : AppCompatActivity() {

    private lateinit var todoTextInput: TextInputEditText
    private lateinit var selectDateButton: MaterialButton
    private lateinit var selectTimeButton: MaterialButton
    private lateinit var selectedDateTimeText: TextView
    private lateinit var recurrenceSpinner: Spinner
    private lateinit var intervalLayout: LinearLayout
    private lateinit var intervalInput: EditText
    private lateinit var intervalUnitText: TextView
    private lateinit var daysOfWeekLayout: LinearLayout
    private lateinit var notificationSwitch: SwitchMaterial
    private lateinit var saveButton: MaterialButton
    private lateinit var cancelButton: MaterialButton

    private val viewModel: TodoViewModel by viewModels()
    private val calendar = Calendar.getInstance()
    private var selectedDateTime: Long? = null
    private var editingTodoId: Long? = null
    private var isEditMode = false

    private val dayCheckboxes = mutableMapOf<Int, CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo)

        // Check if we're editing an existing todo
        editingTodoId = intent.getLongExtra("TODO_ID", -1L).takeIf { it != -1L }
        isEditMode = editingTodoId != null

        initViews()
        setupRecurrenceSpinner()
        setupListeners()

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
        selectDateButton = findViewById(R.id.selectDateButton)
        selectTimeButton = findViewById(R.id.selectTimeButton)
        selectedDateTimeText = findViewById(R.id.selectedDateTimeText)
        recurrenceSpinner = findViewById(R.id.recurrenceSpinner)
        intervalLayout = findViewById(R.id.intervalLayout)
        intervalInput = findViewById(R.id.intervalInput)
        intervalUnitText = findViewById(R.id.intervalUnitText)
        daysOfWeekLayout = findViewById(R.id.daysOfWeekLayout)
        notificationSwitch = findViewById(R.id.notificationSwitch)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)

        dayCheckboxes[1] = findViewById(R.id.mondayCheckbox)
        dayCheckboxes[2] = findViewById(R.id.tuesdayCheckbox)
        dayCheckboxes[3] = findViewById(R.id.wednesdayCheckbox)
        dayCheckboxes[4] = findViewById(R.id.thursdayCheckbox)
        dayCheckboxes[5] = findViewById(R.id.fridayCheckbox)
        dayCheckboxes[6] = findViewById(R.id.saturdayCheckbox)
        dayCheckboxes[7] = findViewById(R.id.sundayCheckbox)
    }

    private fun setupRecurrenceSpinner() {
        val recurrenceOptions = arrayOf("None", "Hourly", "Daily", "Weekly", "Monthly", "Yearly")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, recurrenceOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        recurrenceSpinner.adapter = adapter

        recurrenceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> { // None
                        intervalLayout.visibility = View.GONE
                        daysOfWeekLayout.visibility = View.GONE
                    }
                    1 -> { // Hourly
                        intervalLayout.visibility = View.VISIBLE
                        intervalUnitText.text = "hour(s)"
                        daysOfWeekLayout.visibility = View.GONE
                    }
                    2 -> { // Daily
                        intervalLayout.visibility = View.VISIBLE
                        intervalUnitText.text = "day(s)"
                        daysOfWeekLayout.visibility = View.GONE
                    }
                    3 -> { // Weekly
                        intervalLayout.visibility = View.VISIBLE
                        intervalUnitText.text = "week(s)"
                        daysOfWeekLayout.visibility = View.VISIBLE
                    }
                    4 -> { // Monthly
                        intervalLayout.visibility = View.VISIBLE
                        intervalUnitText.text = "month(s)"
                        daysOfWeekLayout.visibility = View.GONE
                    }
                    5 -> { // Yearly
                        intervalLayout.visibility = View.VISIBLE
                        intervalUnitText.text = "year(s)"
                        daysOfWeekLayout.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupListeners() {
        selectDateButton.setOnClickListener {
            showDatePicker()
        }

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

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateSelectedDateTime()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                updateSelectedDateTime()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun updateSelectedDateTime() {
        selectedDateTime = calendar.timeInMillis
        val format = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault())
        selectedDateTimeText.text = format.format(Date(selectedDateTime!!))
    }

    private fun loadTodoForEditing() {
        editingTodoId?.let { id ->
            viewModel.getTodoById(id).observe(this) { todo ->
                todo?.let {
                    // Populate fields with existing todo data
                    todoTextInput.setText(it.text)

                    // Set due date/time
                    if (it.dueDateTime != null) {
                        selectedDateTime = it.dueDateTime
                        calendar.timeInMillis = it.dueDateTime
                        val format = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault())
                        selectedDateTimeText.text = format.format(Date(it.dueDateTime))
                    }

                    // Set recurrence pattern
                    val recurrence = it.recurrencePattern
                    when (recurrence.type) {
                        RecurrenceType.NONE -> recurrenceSpinner.setSelection(0)
                        RecurrenceType.HOURLY -> {
                            recurrenceSpinner.setSelection(1)
                            intervalInput.setText(recurrence.interval.toString())
                        }
                        RecurrenceType.DAILY -> {
                            recurrenceSpinner.setSelection(2)
                            intervalInput.setText(recurrence.interval.toString())
                        }
                        RecurrenceType.WEEKLY -> {
                            recurrenceSpinner.setSelection(3)
                            intervalInput.setText(recurrence.interval.toString())
                            // Set day checkboxes
                            recurrence.daysOfWeek.forEach { dayNum ->
                                dayCheckboxes[dayNum]?.isChecked = true
                            }
                        }
                        RecurrenceType.MONTHLY -> {
                            recurrenceSpinner.setSelection(4)
                            intervalInput.setText(recurrence.interval.toString())
                        }
                        RecurrenceType.YEARLY -> {
                            recurrenceSpinner.setSelection(5)
                            intervalInput.setText(recurrence.interval.toString())
                        }
                        else -> recurrenceSpinner.setSelection(0)
                    }

                    // Set notification switch
                    notificationSwitch.isChecked = it.notificationEnabled
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

        val recurrencePattern = buildRecurrencePattern()
        val notificationEnabled = notificationSwitch.isChecked

        if (isEditMode && editingTodoId != null) {
            // Update existing todo
            viewModel.getTodoById(editingTodoId!!).observe(this) { existingTodo ->
                existingTodo?.let { todo ->
                    val updatedTodo = todo.copy(
                        text = text,
                        dueDateTime = selectedDateTime,
                        recurrencePattern = recurrencePattern,
                        notificationEnabled = notificationEnabled,
                        lastModifiedAt = System.currentTimeMillis()
                    )
                    viewModel.updateTodo(updatedTodo)
                    Toast.makeText(this, "Todo updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } else {
            // Add new todo
            viewModel.addTodo(text, selectedDateTime, recurrencePattern, notificationEnabled)
            Toast.makeText(this, "Todo added", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun buildRecurrencePattern(): RecurrencePattern {
        val position = recurrenceSpinner.selectedItemPosition
        val interval = intervalInput.text.toString().toIntOrNull() ?: 1

        return when (position) {
            0 -> RecurrencePattern.none()
            1 -> RecurrencePattern.hourly(interval)
            2 -> RecurrencePattern.daily(interval)
            3 -> {
                val daysOfWeek = dayCheckboxes.filter { it.value.isChecked }.keys
                RecurrencePattern.weekly(interval, daysOfWeek)
            }
            4 -> RecurrencePattern.monthly(interval)
            else -> RecurrencePattern.none()
        }
    }
}
