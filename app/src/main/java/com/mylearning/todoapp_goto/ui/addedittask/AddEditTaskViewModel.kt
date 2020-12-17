package com.mylearning.todoapp_goto.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mylearning.todoapp_goto.data.Task
import com.mylearning.todoapp_goto.data.TaskDao

class AddEditTaskViewModel @ViewModelInject constructor (private val taskDao: TaskDao, @Assisted private val state : SavedStateHandle,) : ViewModel() {

    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
    set(value) {
        field = value
        state.set("taskName", value)
    }

    var tasKImportance = state.get<Boolean>("tasKImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("tasKImportance", value)
        }




}