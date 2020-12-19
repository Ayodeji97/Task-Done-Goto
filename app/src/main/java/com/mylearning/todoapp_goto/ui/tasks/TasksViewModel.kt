package com.mylearning.todoapp_goto.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.mylearning.todoapp_goto.data.PreferencesManager
import com.mylearning.todoapp_goto.data.SortOrder
import com.mylearning.todoapp_goto.data.Task
import com.mylearning.todoapp_goto.data.TaskDao
import com.mylearning.todoapp_goto.ui.ADD_TASK_RESULT_OK
import com.mylearning.todoapp_goto.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor (private val taskDao: TaskDao, private val preferencesManager : PreferencesManager,  @Assisted private val state: SavedStateHandle): ViewModel() {

    //val searchQuery = MutableStateFlow("")
    val searchQuery = state.getLiveData("searchQuery", "")

    val preferenceFlow = preferencesManager.preferencesFlow

    // create an channel to be passed to the fragment
    private val tasksEventChannel = Channel<TasksEvent>()

    val tasksEvent = tasksEventChannel.receiveAsFlow()


    private val tasksFlow = combine(searchQuery.asFlow(), preferenceFlow ) { query, filterPrefernces ->
        // lamda func : whenever any of the flow emit a new value, this will be called
        Pair(query, filterPrefernces)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }
    val task = tasksFlow.asLiveData()

    fun onSortOrderSelected (sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick (hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected (task : Task ) = viewModelScope.launch{
        tasksEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
    }

    fun onTaskCheckedChanged (task: Task, isChecked : Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    fun onTaskSwiped (task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick (task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTaskClick () = viewModelScope.launch{
        tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult (result : Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
        }

    }

    private fun showTaskSavedConfirmationMessage (text : String) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.ShowTaskConfirmationMessage(text))
    }

    // sealed to send snack bar response from the viewModel
    sealed class TasksEvent {
        object NavigateToAddTaskScreen : TasksEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TasksEvent()
        data class ShowUndoDeleteTaskMessage (val task: Task) : TasksEvent()
        data class ShowTaskConfirmationMessage (val message : String) : TasksEvent()
    }


}

