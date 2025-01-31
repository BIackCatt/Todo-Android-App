package com.example.todolist.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todolist.data.Task
import com.example.todolist.data.TasksDatabase
import com.example.todolist.data.convertTaskItemToMap
import com.example.todolist.data.network.FirebaseDatabase
import com.example.todolist.ui.theme.viewmodels.HomeUiState
import com.example.todolist.ui.theme.viewmodels.TaskUiState
import com.example.todolist.ui.theme.viewmodels.toTask
import com.example.todolist.ui.theme.viewmodels.toTaskUiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.tasks.await

class SyncWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    private val workerScope = CoroutineScope(Dispatchers.Default + Job())
    override suspend fun doWork(): Result {
        val taskDao = TasksDatabase.getDatabase(applicationContext).taskDAO()
        val unSyncedTasks = taskDao.getDeletedOrUnSyncedTasks()
            .map { HomeUiState(it.map {task -> task?.toTaskUiState()?: TaskUiState() }) }
            .stateIn(
                scope = workerScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = HomeUiState()
            )
        val firestore = FirebaseFirestore.getInstance()
        val fireBase = FirebaseDatabase()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        for (task in unSyncedTasks.value.tasks) {
            try {
                if (task != null)
                if (task.isDeleted) {
                    // 🔴 حذف المهمة من Firebase
                    if (userId != null) {
                        fireBase.deleteTask(userId, convertTaskItemToMap(task.toTask()), onError = {})
                        taskDao.delete(task.toTask())
                    }
                } else {
                    // 🔵 تحديث أو إضافة المهمة في Firebase
                    if (userId != null) {
                        fireBase.addTask(userId, convertTaskItemToMap(task.toTask()), onError = {})
                    }
                }

                // تحديث المهمة كمزامنة
                taskDao.update(task?.toTask()?.copy(isSynced = true)?: Task())

            } catch (e: Exception) {
                return Result.retry() // إعادة المحاولة لو فشل
            }
        }

        return Result.success()
    }
}
