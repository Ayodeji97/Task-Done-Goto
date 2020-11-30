package com.mylearning.todoapp_goto.ui.tasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.mylearning.todoapp_goto.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel : TasksViewModel by viewModels ()






}