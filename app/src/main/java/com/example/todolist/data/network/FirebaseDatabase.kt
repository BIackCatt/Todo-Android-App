package com.example.todolist.data.network

import android.util.Log
import com.example.todolist.data.Task
import com.example.todolist.ui.theme.viewmodels.TaskUiState
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseDatabase {
    private val database = Firebase.firestore

    suspend fun deleteTask(userId: String, task: Map<String, Any?>, onError: (String) -> Unit): Boolean {
        return try {
            val userRef = database.collection("users").document(userId)
            userRef.collection("tasks").document(task["id"].toString()).delete().await()
            true
        } catch (e: Exception) {
            onError(e.message.toString())
            false
        }
    }

    suspend fun saveData(userId: String, data: List<Map<String, Any?>>) {
        if (data.isEmpty()) {
            val userRef = database.collection("users").document(userId)
            val tasksCollection = userRef.collection("tasks")
            val querySnapshot = tasksCollection.get().await()

            for (document in querySnapshot.documents) {
                document.reference.delete().await()
            }
        }
        val userRef = database.collection("users").document(userId)
        data.map { task ->
            userRef.collection("tasks").document(task["id"] as String)
                .set(task).await()
        }
    }

    suspend fun addTask(userId: String?, task: Map<String, Any?>, onError: (String) -> Unit): Boolean {
        return try {
            val userRef = database.collection("users").document(userId!!)
            val taskId = task["id"] as? String ?: UUID.randomUUID().toString()

            userRef.collection("tasks")
                .document(taskId)
                .set(task)
                .await()

            true
        } catch (e: Exception) {
            Log.d("ErrorExport", e.message.toString())
            onError(e.message.toString())
            false
        }

    }

    suspend fun getData(
        userId: String,
        onErrorAction: (String?) -> Unit,
        onSuccessAction: (List<Task>) -> Unit
    ): List<Task?> {
        return try {
            val userRef = database.collection("users").document(userId)
            val querySnapshot = userRef.collection("tasks").get().await()
            val list = querySnapshot.map { task ->
                Task(
                    id = task["id"] as String,
                    title = task["title"] as String,
                    description = task["description"] as String,
                    date = task["date"] as String,
                    isCompleted = task["isCompleted"] as Boolean,
                    isSynced = task["isSynced"] as Boolean
                )
            }
            onSuccessAction(list)
            list
        } catch (e: Exception) {
            onErrorAction(e.message)
            listOf()
        }
    }
}
