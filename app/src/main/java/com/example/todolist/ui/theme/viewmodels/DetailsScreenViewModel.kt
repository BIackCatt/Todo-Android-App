package com.example.todolist.ui.theme.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.todolist.data.TasksRepo

class DetailsScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val tasksRepo: TasksRepo
): ViewModel() {

    val taskId = savedStateHandle.get<Int>("id")

}