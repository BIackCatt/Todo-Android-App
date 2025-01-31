package com.example.todolist.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class, OfflineTask::class], version = 1, exportSchema = false)
abstract class TasksDatabase : RoomDatabase() {
    abstract fun taskDAO(): TaskDao
    abstract fun offlineTasksDao(): OfflineTasksDao

    companion object {
        @Volatile
        var Instance: TasksDatabase? = null

        fun getDatabase(context: Context): TasksDatabase {
            Log.d("Database creation", "Database creation started")
            return Instance ?: synchronized(this) {
                Log.d("Database creation", "Database created $Instance")
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