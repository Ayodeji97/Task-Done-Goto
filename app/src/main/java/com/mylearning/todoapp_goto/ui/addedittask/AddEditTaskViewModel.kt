package com.mylearning.todoapp_goto.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mylearning.todoapp_goto.data.Task
import com.mylearning.todoapp_goto.data.TaskDao
import com.mylearning.todoapp_goto.ui.ADD_TASK_RESULT_OK
import com.mylearning.todoapp_goto.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

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

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    // convert the channel to a flow
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()


    fun onSaveClick () {
        if (taskName.isBlank()) {
            // Show Invalid input message : Send an event to fragment or activity to show snackbar
            showInvalidInputMessage("Name cannot be empty")
            return
        }

        // if the task is not null update the task in the database else create a new task
        if (task != null) {
            val updatedTask = task.copy(name = taskName, important = tasKImportance)

            updateTask(updatedTask)
        } else {
            val newTask = Task(name = taskName, important = tasKImportance)

            createNewTask(newTask)
        }
    }

    private fun createNewTask (task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        // navigate back
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }



    private fun updateTask(task: Task) = viewModelScope.launch {
       taskDao.update(task)
        // navigate back
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun showInvalidInputMessage( text : String) = viewModelScope.launch {
        // send thru our channel
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidMessage(text))
    }

    // create an event
    sealed class AddEditTaskEvent {
        data class ShowInvalidMessage (val message : String) : AddEditTaskEvent()
        data class NavigateBackWithResult (val result : Int) : AddEditTaskEvent()
    }




}