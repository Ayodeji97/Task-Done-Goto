package com.mylearning.todoapp_goto.ui.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mylearning.todoapp_goto.R
import com.mylearning.todoapp_goto.databinding.FragmentAddEditTaskBinding
import com.mylearning.todoapp_goto.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

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

            addTaskNameTaskEt.addTextChangedListener {
                viewModel.taskName = it.toString()
            }

            fragmentEditAddImportantCheckbox.setOnCheckedChangeListener { _, isChecked ->
                viewModel.tasKImportance = isChecked
            }

            fragmentAddEditTaskFab.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.addEditTaskEvent.collect { event ->
                when (event) {
                    is AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidMessage -> {
                        // show snack bar
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                    is AddEditTaskViewModel.AddEditTaskEvent.NavigateBackWithResult -> {
                        binding.addTaskNameTaskEt.clearFocus()
                        // new api that makes it easy to send result between fragment
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result )
                        )

                        findNavController().popBackStack()

                    }
                }.exhaustive
            }
        }
    }

}