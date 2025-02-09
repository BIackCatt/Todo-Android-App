package com.example.todolist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM Tasks_database WHERE isDeleted = 0")
    fun getAllTasks(): Flow<List<Task?>>

    @Query("SELECT * FROM Tasks_database WHERE id = :id")
    fun getTask(id: Int): Flow<Task?>

    @Query("SELECT * FROM Tasks_database WHERE isSynced = 0")
    fun getUnSyncedTasks(): Flow<List<Task?>>

    @Query("SELECT * FROM tasks_database WHERE isSynced = 0 OR isDeleted = 1")
    fun getDeletedOrUnSyncedTasks(): Flow<List<Task?>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("DELETE FROM Tasks_database")
    suspend fun deleteAll()

}

@Dao
interface OfflineTasksDao {
    @Query("SELECT * FROM offline_tasks")
    fun getAllTasks(): Flow<List<OfflineTask?>>

    @Query("SELECT * FROM offline_tasks WHERE id = :id")
    fun getTask(id: String): Flow<OfflineTask?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: OfflineTask)

    @Delete
    suspend fun delete(task: OfflineTask)

    @Update
    suspend fun update(task: OfflineTask)

    @Query("DELETE FROM Offline_Tasks")
    suspend fun deleteAll()
}

@Dao
interface CollaborationDao {
    @Query("SELECT * FROM Collaboration_table")
    fun getAllCollaborations(): Flow<List<CollaborationDb?>>

    @Query("SELECT * FROM Collaboration_table WHERE id = :id")
    fun getCollaboration(id: String): Flow<CollaborationDb?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(collaboration: CollaborationDb)

    @Delete
    suspend fun delete(collaboration: CollaborationDb)

    @Update
    suspend fun update(collaboration: CollaborationDb)

    @Query("DELETE FROM Collaboration_table")
    suspend fun deleteAll()
}
