package com.example.todolist.data

import kotlinx.coroutines.flow.Flow

interface TasksRepo {
    fun getAllTasks(): Flow<List<Task?>>
    suspend fun getUnSyncedTasks(): Flow<List<Task?>>
    suspend fun getUnSyncedOrDeletedTasks(): Flow<List<Task?>>
    fun getTask(id: Int): Flow<Task?>
    suspend fun insert(task: Task)
    suspend fun delete(task: Task)
    suspend fun update(task: Task)
    suspend fun deleteAll()
}

interface OfflineTasksRepoInterface {
    fun getAllTasks(): Flow<List<OfflineTask?>>
    fun getTask(id: String): Flow<OfflineTask?>
    suspend fun insert(task: OfflineTask)
    suspend fun delete(task: OfflineTask)
    suspend fun update(task: OfflineTask)
    suspend fun deleteAll()

}

