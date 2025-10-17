package com.example.todoapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.*
import com.example.todoapp.notifications.TodoNotificationScheduler
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val database = TodoDatabase.getDatabase(application)
    private val repository = TodoRepository(database.todoDao(), database.todoCompletionHistoryDao())

    private val _viewMode = MutableLiveData(ViewMode.TODAY)
    val viewMode: LiveData<ViewMode> = _viewMode

    private val _selectedDate = MutableLiveData<Long>()

    private val _hideCompleted = MutableLiveData(false)
    val hideCompleted: LiveData<Boolean> = _hideCompleted

    private val rawTodos: LiveData<List<TodoEntity>> = viewMode.switchMap { mode ->
        when (mode) {
            ViewMode.ALL -> repository.getAllTodos().asLiveData()
            ViewMode.TODAY -> repository.getTodayTodos().asLiveData()
            ViewMode.DATE -> _selectedDate.switchMap { date ->
                repository.getTodosByDate(date).asLiveData()
            }
        }
    }

    val todos: LiveData<List<TodoEntity>> = MediatorLiveData<List<TodoEntity>>().apply {
        addSource(rawTodos) { todoList ->
            value = if (_hideCompleted.value == true) {
                todoList.filter { !it.isCompleted }
            } else {
                todoList
            }
        }
        addSource(_hideCompleted) { shouldHide ->
            rawTodos.value?.let { todoList ->
                value = if (shouldHide) {
                    todoList.filter { !it.isCompleted }
                } else {
                    todoList
                }
            }
        }
    }

    val completionHistory: LiveData<List<TodoCompletionHistory>> = repository.getCompletionHistory().asLiveData()

    fun setViewMode(mode: ViewMode) {
        _viewMode.value = mode
    }

    fun setSelectedDate(date: Long) {
        _selectedDate.value = date
        _viewMode.value = ViewMode.DATE
    }

    fun setHideCompleted(hide: Boolean) {
        _hideCompleted.value = hide
    }

    fun getTodoById(id: Long): LiveData<TodoEntity?> {
        val result = MutableLiveData<TodoEntity?>()
        viewModelScope.launch {
            result.postValue(repository.getTodoById(id))
        }
        return result
    }

    fun addTodo(
        text: String,
        dueDateTime: Long? = null,
        recurrencePattern: RecurrencePattern = RecurrencePattern.none(),
        notificationEnabled: Boolean = true
    ) {
        viewModelScope.launch {
            val todo = TodoEntity(
                text = text,
                dueDateTime = dueDateTime,
                recurrencePattern = recurrencePattern,
                notificationEnabled = notificationEnabled
            )
            val id = repository.insertTodo(todo)

            // Schedule notification if needed
            if (dueDateTime != null && notificationEnabled) {
                val insertedTodo = repository.getTodoById(id)
                insertedTodo?.let {
                    TodoNotificationScheduler.scheduleTodoNotification(getApplication(), it)
                }
            }
        }
    }

    fun toggleTodoCompletion(todo: TodoEntity) {
        viewModelScope.launch {
            repository.toggleTodoCompletion(todo)

            // Cancel notification if completed, reschedule if uncompleted
            if (todo.isCompleted) {
                TodoNotificationScheduler.cancelTodoNotification(getApplication(), todo.id)
            } else {
                TodoNotificationScheduler.scheduleTodoNotification(getApplication(), todo)
            }
        }
    }

    fun deleteTodo(todo: TodoEntity) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
            TodoNotificationScheduler.cancelTodoNotification(getApplication(), todo.id)
        }
    }

    fun updateTodo(todo: TodoEntity) {
        viewModelScope.launch {
            repository.updateTodo(todo)
            TodoNotificationScheduler.scheduleTodoNotification(getApplication(), todo)
        }
    }

    enum class ViewMode {
        ALL, TODAY, DATE
    }
}
