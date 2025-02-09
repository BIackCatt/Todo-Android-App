package com.example.todolist.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class, OfflineTask::class, CollaborationDb::class], version = 2, exportSchema = false)
abstract class TasksDatabase : RoomDatabase() {
    abstract fun taskDAO(): TaskDao
    abstract fun offlineTasksDao(): OfflineTasksDao
    abstract fun collaborationDao(): CollaborationDao

    companion object {
        @Volatile
        var Instance: TasksDatabase? = null

        fun getDatabase(context: Context): TasksDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    TasksDatabase::class.java,
                    "Tasks_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}