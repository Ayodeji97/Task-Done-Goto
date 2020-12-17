package com.mylearning.todoapp_goto.ui.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mylearning.todoapp_goto.R
import com.mylearning.todoapp_goto.databinding.FragmentAddEditTaskBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {

    private val viewModel : AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTaskBinding.bind(view)

        binding.apply {
            addTaskNameTaskEt.setText(viewModel.taskName)
            fragmentEditAddImportantCheckbox.isChecked = viewModel.tasKImportance
            fragmentEditAddImportantCheckbox.jumpDrawablesToCurrentState()
            fragmentAddEditDateCreatedTv.isVisible = viewModel.task != null
            fragmentAddEditDateCreatedTv.text = "Created : ${viewModel.task?.currentDateFormatted}"
        }
    }

}