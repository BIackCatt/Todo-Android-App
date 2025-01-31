package com.example.todolist.ui.theme.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.OfflineTask
import com.example.todolist.data.OfflineTasksRepo
import com.example.todolist.data.OfflineTasksRepoInterface
import com.example.todolist.data.Task
import com.example.todolist.data.TasksRepo
import com.example.todolist.data.convertTaskItemToMap
import com.example.todolist.data.network.FirebaseDatabase
import com.example.todolist.utils.getFormattedDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface LoadingControl {
    data object FetchingData : LoadingControl
    data object Success : LoadingControl
    data class Error(val error: String?) : LoadingControl
}

data class SyncState(
    val isSynced: Boolean = true,
    val syncError: String? = null,
)

class HomeScreenViewModel(
    private val onlineTasksRepo: TasksRepo,
    private val offlineTasksRepo: OfflineTasksRepoInterface,
    private val context: Context,
) : ViewModel() {

    var isSignedIn: Boolean = false
    private val firebaseDatabase = FirebaseDatabase()
    private var loading: MutableStateFlow<LoadingControl> = MutableStateFlow(LoadingControl.Success)
    val isLoading = loading.asStateFlow()
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    init {
        updateDataSource(isSignedIn)
    }

    fun updateDataSource(isSignedIn: Boolean) {
        viewModelScope.launch {
            val tasksFlow = if (isSignedIn) {
                onlineTasksRepo.getAllTasks()
                    .map { HomeUiState(it.map { task -> task?.toTaskUiState() }) }
            } else {
                offlineTasksRepo.getAllTasks()
                    .map { HomeUiState(it.map { task -> task?.toTaskUiState() }) }
            }

            tasksFlow.collectLatest { newState ->
                _homeUiState.value = newState
            }
        }
    }

    val isAppSyncedFlow = homeUiState
        .map { uiState ->
            // Check if any task is unsynced or deleted
            val hasUnsyncedOrDeletedTasks = uiState.tasks.any { task ->
                task != null && (!task.isSynced || task.isDeleted)
            }
            // Return SyncState based on tasks' status
            SyncState(isSynced = !hasUnsyncedOrDeletedTasks)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SyncState(isSynced = true))

    suspend fun addOfflineTask(task: TaskUiState) {
        offlineTasksRepo.insert(task.toOfflineTask())
    }

    suspend fun updateOfflineTask(task: TaskUiState) {
        offlineTasksRepo.update(task.toOfflineTask())
    }

    suspend fun deleteOfflineTask(task: TaskUiState) {
        offlineTasksRepo.delete(task.toOfflineTask())
    }


    private val _appSyncState = MutableStateFlow(SyncState())
    val appSyncState = _appSyncState.asStateFlow()


    private suspend fun clearTasksList() {
        onlineTasksRepo.deleteAll()
    }

    private fun changeSync(boolean: Boolean) {
        _appSyncState.value = SyncState(isSynced = boolean)
    }

    private suspend fun addTaskLocally(task: TaskUiState) {
        onlineTasksRepo.insert(task.toTask())
    }

    private suspend fun updateTaskLocally(task: TaskUiState) {
        onlineTasksRepo.update(task.toTask())
    }

    private suspend fun deleteTaskLocally(task: TaskUiState) {
        onlineTasksRepo.update((task.copy(isDeleted = true)).toTask())
        Log.d("TaskDeleteUpdate", task.copy(isDeleted = true).toTask().isDeleted.toString())
    }

    private suspend fun addTaskOnline(task: TaskUiState, userId: String?): Boolean {
        return try {
            val convertedData = convertTaskItemToMap(task.toTask())
            firebaseDatabase.addTask(userId!!, convertedData) { error ->
                throw Exception(error)
            }
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun updateTaskOnline(task: TaskUiState, userId: String?): Boolean {
        return try {
            val convertedData = convertTaskItemToMap(task.toTask())
            firebaseDatabase.addTask(userId!!, convertedData) { error ->
                throw Exception(error)
            }
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun deleteTaskOnline(task: TaskUiState, userId: String?): Boolean {
        return try {
            val convertedData = convertTaskItemToMap(task.toTask())
            firebaseDatabase.deleteTask(userId!!, convertedData) { error ->
                throw Exception(error)
            }
        } catch (e: Exception) {
            false
        }
    }

    fun addTask(
        task: TaskUiState,
        userId: String?,
        onError: (String?) -> Unit,
        isConnected: Boolean
    ) {
        viewModelScope.launch {
            try {
                // Add task locally
                addTaskLocally(task.copy(isSynced = false))
                changeSync(false)

                // Sync with online database if connected
                if (isConnected) {
                    val isAdded = addTaskOnline(task, userId)
                    if (isAdded) {
                        updateTaskLocally(task.copy(isSynced = true))
                        _appSyncState.value = SyncState(isSynced = true)
                    } else {
                        onError("Failed to add task online")
                    }
                }
            } catch (e: Exception) {
                onError(e.message)
            }
        }
    }

    fun updateTask(
        task: TaskUiState,
        userId: String?,
        onError: (String?) -> Unit,
        isConnected: Boolean
    ) {
        viewModelScope.launch {
            try {
                // Update task locally
                updateTaskLocally(task.copy(isSynced = false))
                changeSync(false)

                // Sync with online database if connected
                if (isConnected) {
                    val isUpdated = updateTaskOnline(task, userId)
                    if (isUpdated) {
                        updateTaskLocally(task.copy(isSynced = true))
                        _appSyncState.value = SyncState(isSynced = true)
                    } else {
                        onError("Failed to update task online")
                    }
                }
            } catch (e: Exception) {
                onError(e.message)
            }
        }
    }

    fun deleteTask(
        task: TaskUiState,
        userId: String?,
        onError: (String?) -> Unit,
        isConnected: Boolean
    ) {
        viewModelScope.launch {
            try {
                // Delete task locally
                deleteTaskLocally(task)
                changeSync(false)

                // Sync with online database if connected
                if (isConnected) {
                    val isDeleted = deleteTaskOnline(task, userId)
                    if (!isDeleted) {
                        onError("Failed to delete task online")
                    } else {
                        _appSyncState.value = SyncState(isSynced = true)
                    }
                }
            } catch (e: Exception) {
                onError(e.message)
            }
        }
    }

    fun importData(
        userId: String?,
        onErrorAction: (String?) -> Unit,
        onSuccessAction: () -> Unit,
        isConnected: Boolean
    ) {
        viewModelScope.launch {
            loading.value = LoadingControl.FetchingData

            if (userId == null) {
                onErrorAction("No Provided Online User!")
                _appSyncState.value = SyncState(isSynced = false)
                loading.value = LoadingControl.Error("No User ID")
                return@launch
            }

            try {
                // Step 1: Sync unsynced or deleted tasks
                val unSyncedTasks = onlineTasksRepo.getUnSyncedOrDeletedTasks()
                    .first() // Get the current list of unsynced tasks

                unSyncedTasks.forEach { task ->
                    if (task != null) {
                        if (task.isDeleted) {
                            // Delete task online
                            val isDeletedOnline = firebaseDatabase.deleteTask(
                                task = convertTaskItemToMap(task),
                                onError = onErrorAction,
                                userId = userId
                            )
                            if (isDeletedOnline) {
                                onlineTasksRepo.delete(task) // Delete locally if online deletion succeeds
                            } else {
                                onErrorAction("Failed to delete task online")
                            }
                        } else {
                            // Update task online
                            val isUpdated = updateTaskOnline(task.toTaskUiState(), userId)
                            if (isUpdated) {
                                onlineTasksRepo.insert(task.copy(isSynced = true)) // Mark as synced
                            } else {
                                onErrorAction("Failed to update task online")
                            }
                        }
                    }
                }

                // Step 2: Fetch and sync online data
                firebaseDatabase.getData(userId, onErrorAction) { onlineTasks ->
                    if (onlineTasks.isEmpty()) {
                        viewModelScope.launch {
                            clearTasksList()
                        }
                    } else {
                        onlineTasks.forEach { onlineTask ->
                            // Insert or update local tasks
                            viewModelScope.launch {
                                onlineTasksRepo.insert(onlineTask.copy(isSynced = true))
                            }
                        }
                    }
                }

                // Step 3: Update sync state and call success action
                _appSyncState.value = SyncState(isSynced = true)
                onSuccessAction()
            } catch (e: Exception) {
                // Handle errors and update the loading state
                val errorMessage = e.message ?: "Something Went Wrong!"
                loading.value = LoadingControl.Error(errorMessage)
                _appSyncState.value = SyncState(isSynced = false)
                onErrorAction(errorMessage)
            } finally {
                // Update the loading state to indicate success
                loading.value = LoadingControl.Success
            }
        }
    }


}

data class HomeUiState(val tasks: List<TaskUiState?> = listOf())

data class TaskUiState(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val date: String = getFormattedDate(),
    val notifyTime: String = "",
    val isCompleted: Boolean = false,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val lastUpdated: Long = 0L
)

data class LoadingUiState(
    val isLoading: Boolean = false,
    val loadingMessage: String? = null
)

fun TaskUiState.toTask(): Task = Task(
    id = this.id,
    title = this.title,
    description = this.description,
    date = this.date,
    isCompleted = this.isCompleted,
    isSynced = this.isSynced,
    isDeleted = this.isDeleted
)
fun TaskUiState.toOfflineTask(): OfflineTask = OfflineTask(
    id = this.id,
    title = this.title,
    description = this.description,
    date = this.date,
    isCompleted = this.isCompleted,
)

fun Task.toTaskUiState(): TaskUiState = TaskUiState(
    id = this.id,
    title = this.title,
    description = this.description,
    date = this.date,
    isCompleted = this.isCompleted,
    isSynced = this.isSynced,
    isDeleted = this.isDeleted
)

fun OfflineTask.toTaskUiState(): TaskUiState = TaskUiState(
    id = this.id,
    title = this.title,
    description = this.description,
    date = this.date,
    isCompleted = this.isCompleted,
    )