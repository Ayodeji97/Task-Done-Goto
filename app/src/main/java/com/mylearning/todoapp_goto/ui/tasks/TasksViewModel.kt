package com.mylearning.todoapp_goto.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mylearning.todoapp_goto.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class TasksViewModel @ViewModelInject constructor (private val taskDao: TaskDao): ViewModel() {

    val searchQuery = MutableStateFlow("")

    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted = MutableStateFlow(false)

    private val tasksFlow = searchQuery.flatMapLatest {
        taskDao.getTasks(it)
    }
    val task = tasksFlow.asLiveData()

}

enum class SortOrder {BY_Name, BY_DATE }