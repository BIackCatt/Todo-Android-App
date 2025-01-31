package com.example.todolist.ui.theme.viewmodels

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todolist.TodoApp

object AppViewModelsProvider {
    /**
     * Factory for creating instances of [HomeScreenViewModel] and [DetailsScreenViewModel].
     *
     * This factory utilizes the [viewModelFactory] function to define the creation logic
     * for each ViewModel type, including providing dependencies and SavedStateHandle.
     *
     * It leverages initializers to specify how each ViewModel should be instantiated,
     * with [HomeScreenViewModel] depending on [TasksRepository] and [DetailsScreenViewModel]
     * additionally requiring a [SavedStateHandle].
     */
    val Factory = viewModelFactory {
        initializer {
            HomeScreenViewModel(
                offlineTasksRepo = todoApplication().container.offlineTasksRepo,
                onlineTasksRepo = todoApplication().container.tasksRepo,
                context = this.todoApplication().applicationContext,

            )
        }

        initializer {
            DetailsScreenViewModel(
                this.createSavedStateHandle(),
                todoApplication().container.tasksRepo
            )
        }

        initializer {
            UserAccountViewModel(
                this.todoApplication().container.googleAuthUiClient,
                networkConnectivityObserver = this.todoApplication().container.networkConnectivityObserver

            )
        }
    }
}

fun CreationExtras.todoApplication(): TodoApp =
    (this[APPLICATION_KEY] as TodoApp)