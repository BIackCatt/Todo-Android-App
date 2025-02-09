package com.example.todolist.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todolist.utils.getFormattedDate
import com.google.firebase.firestore.FieldValue
import java.util.Date
import java.util.UUID


@Entity("Collaboration_table")
data class CollaborationDb(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val username: String = "",
)


data class Collaboration(
    val id: String = UUID.randomUUID().toString(),
    val username: String = "",
    val password: String = "",
    val members: MutableList<Map<String, String?>?> = mutableListOf(),
    val admins: MutableList<Map<String, String?>?> = mutableListOf(),
    val tasks: List<CollabTask?> = listOf(),
    val completedTasks: List<CollabTask?> = listOf(),
    val operations: List<Operation?> = listOf(),
)



data class CollabTask(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val date: String = getFormattedDate(),
    val assignedTo: Map<String, String?> = mapOf(),
)

sealed class Actions(val type: String) {
    class Add: Actions("Add")
    class Update: Actions("Update")
    class Complete: Actions("Complete")
    class Uncomplete: Actions("Uncomplete")
    data class Delete(val delType: String): Actions(type = "Delete $delType")
}

data class Operation(
    val id: String = UUID.randomUUID().toString(),
    val userId: String? = null,
    val username: String? = null,
    val userPic: String? = null,
    val message: String? = null,
    val timestamp: Date? = null,
)