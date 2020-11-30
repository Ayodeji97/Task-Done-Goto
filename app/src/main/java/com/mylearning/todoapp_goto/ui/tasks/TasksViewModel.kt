package com.mylearning.todoapp_goto.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.mylearning.todoapp_goto.data.TaskDao

class TasksViewModel @ViewModelInject constructor (private val taskDao: TaskDao): ViewModel() {

}