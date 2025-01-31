package com.example.todolist.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todolist.utils.getFormattedDate
import java.util.UUID
import kotlin.uuid.Uuid

@Entity(tableName = "Tasks_database")
data class Task(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val date: String = getFormattedDate(),
    val notifyTime: String =  "",
    val isCompleted: Boolean = false,
    val isSynced: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)

@Entity(tableName = "Offline_Tasks")
data class OfflineTask(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val date: String = getFormattedDate(),
    val isCompleted: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis(),
)