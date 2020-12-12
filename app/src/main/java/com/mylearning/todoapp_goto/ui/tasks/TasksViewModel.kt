package com.mylearning.todoapp_goto.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mylearning.todoapp_goto.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class TasksViewModel @ViewModelInject constructor (private val taskDao: TaskDao): ViewModel() {

    val searchQuery = MutableStateFlow("")

    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted = MutableStateFlow(false)

    private val tasksFlow = combine(searchQuery, sortOrder, hideCompleted ) { query, sortOrder, hideComplete ->
        // lamda func : whenever any of the flow emit a new value, this will be called
        Triple(query, sortOrder, hideComplete)
    }.flatMapLatest { (query, sortOrder, hideComplete) ->
        taskDao.getTasks(query, sortOrder, hideComplete)
    }
    val task = tasksFlow.asLiveData()

}

enum class SortOrder {BY_NAME, BY_DATE }