package com.example.todolist.data

import kotlinx.coroutines.flow.Flow

class OnlineTasksRepo(private val tasksDAO: TaskDao) : TasksRepo {
    override fun getAllTasks(): Flow<List<Task?>> = tasksDAO.getAllTasks()
    override fun getTask(id: Int): Flow<Task?> = tasksDAO.getTask(id)
    override suspend fun insert(task: Task) = tasksDAO.insert(task)
    override suspend fun delete(task: Task) = tasksDAO.delete(task)
    override suspend fun update(task: Task) = tasksDAO.update(task)
    override suspend fun getUnSyncedTasks(): Flow<List<Task?>> = tasksDAO.getUnSyncedTasks()
    override suspend fun deleteAll() = tasksDAO.deleteAll()
    override suspend fun getUnSyncedOrDeletedTasks(): Flow<List<Task?>> =
        tasksDAO.getDeletedOrUnSyncedTasks()
}

class OfflineTasksRepo(private val tasksDAO: OfflineTasksDao) : OfflineTasksRepoInterface {
    override fun getAllTasks(): Flow<List<OfflineTask?>> = tasksDAO.getAllTasks()
    override fun getTask(id: String): Flow<OfflineTask?> = tasksDAO.getTask(id)
    override suspend fun insert(task: OfflineTask) = tasksDAO.insert(task)
    override suspend fun delete(task: OfflineTask) = tasksDAO.delete(task)
    override suspend fun update(task: OfflineTask) = tasksDAO.update(task)
    override suspend fun deleteAll() = tasksDAO.deleteAll()
}

class LocalCollaborationsRepo(private val collaborationDao: CollaborationDao) : CollaborationsRepo {
    override fun getAllCollaborations(): Flow<List<CollaborationDb?>> =
        collaborationDao.getAllCollaborations()

    override fun getCollaboration(id: String): Flow<CollaborationDb?> =
        collaborationDao.getCollaboration(id)

    override suspend fun insert(collaboration: CollaborationDb) =
        collaborationDao.insert(collaboration)

    override suspend fun delete(collaboration: CollaborationDb) =
        collaborationDao.delete(collaboration)

    override suspend fun update(collaboration: CollaborationDb) =
        collaborationDao.update(collaboration)

    override suspend fun deleteAll() = collaborationDao.deleteAll()
}