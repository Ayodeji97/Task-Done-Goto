package com.mylearning.todoapp_goto.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.mylearning.todoapp_goto.data.PreferencesManager
import com.mylearning.todoapp_goto.data.SortOrder
import com.mylearning.todoapp_goto.data.Task
import com.mylearning.todoapp_goto.data.TaskDao
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

    fun onTaskSelected (task : Task ) {

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

    // sealed to send snack bar response from the viewModel
    sealed class TasksEvent {
        data class ShowUndoDeleteTaskMessage (val task: Task) : TasksEvent()
    }
}

