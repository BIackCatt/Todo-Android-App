package com.example.todolist.data.network

import android.util.Log
import com.example.todolist.data.Collaboration
import com.example.todolist.data.convertCollabToMap
import com.example.todolist.presentation.sign_in.AuthenticatedUserData
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class CollaborationsFireStore(
    private val database: FirebaseFirestore,
) {
    fun addCollaboration(
        collaboration: MutableMap<String, Any?>,
        onError: (String) -> Unit,
        onSuccess: (id: String, name: String) -> Unit
    ): Boolean {
        return try {

            val collaborationId = collaboration["id"] as? String
            if (collaborationId == null) {
                onError("Collaboration ID is missing")
                return false
            }

            val collaborationRef = database.collection("collaboration").document(collaborationId)
            collaborationRef.set(collaboration).addOnSuccessListener {
                onSuccess((collaboration["id"] as String), (collaboration["username"] as String))
            }
            true
        } catch (e: Exception) {
            onError(e.message ?: "An unknown error occurred")
            false
        }
    }


    fun addTask(
        collaborationId: String,
        task: MutableMap<String, Any?>,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        val collaborationRef = database.collection("collaboration").document(collaborationId)
        collaborationRef.collection("tasks").document(task["id"] as String).set(task, SetOptions.merge())
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnFailureListener {
                onError.invoke(it.toString())
            }

    }

    fun deleteTask(
        collabId: String,
        task: MutableMap<String, Any?>,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        val collabRef = database.collection("collaboration").document(collabId)
        collabRef.collection("tasks").document(task["id"] as String)
            .delete()
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnFailureListener {
                onError.invoke(it.toString())
            }

    }

    fun deleteCompletedTask(
        collabId: String,
        task: MutableMap<String, Any?>,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        val collabRef = database.collection("collaboration").document(collabId)
        collabRef.collection("completed_tasks").document(task["id"] as String)
            .delete()
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnFailureListener {
                onError.invoke(it.toString())
            }

    }

    fun addOperation(
        operation: Map<String, Any?>,
        collabId: String,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        val collabRef = database.collection("collaboration").document(collabId)
        collabRef.collection("operations").document(operation["id"] as String)
            .set(operation, SetOptions.merge())
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnFailureListener {
                onError.invoke(it.message.toString())
            }
    }

    suspend fun clearOperations(
        collabId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val collectionRef = database.collection("collaboration").document(collabId)
            .collection("operations")
        val batchSize = 500 // Max batch size

        try {
            var query = collectionRef.limit(batchSize.toLong())

            while (true) {
                // Get a batch of documents
                val snapshot = query.get().await()

                // If no documents left, break the loop
                if (snapshot.isEmpty) break

                // Create a batch
                val batch = database.batch()

                // Add delete operations to the batch
                for (document in snapshot.documents) {
                    batch.delete(document.reference)
                }

                // Commit the batch
                batch.commit().await()

                // Get the last document in the batch for pagination
                val lastDocument = snapshot.documents.last()
                query = collectionRef.limit(batchSize.toLong()).startAfter(lastDocument)
            }
        } catch (e: Exception) {
            onError("Failed to delete collection: ${e.message}")
        }
    }


    suspend fun deleteOperation(
        operationId: String,
        collabId: String,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        val collabRef = database.collection("collaboration").document(collabId)
        collabRef.collection("operations").document(operationId)
            .delete()
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnFailureListener {
                onError.invoke(it.message.toString())
            }
    }
    fun checkIsExist(username: String, onResult: (Boolean) -> Unit) {
        val collaborationRef =
            database.collection("collaboration") // Replace with your collection name

        // Query to check if a document with the given username exists
        collaborationRef
            .whereEqualTo("username", username) // Assuming "username" is the field name
            .get()
            .addOnSuccessListener { querySnapshot ->
                // If the query returns any documents, the username exists
                val exists = !querySnapshot.isEmpty
                onResult(exists)
            }
            .addOnFailureListener { exception ->
                // Handle the error
                println("Error checking username: ${exception.message}")
                onResult(false) // Assume username doesn't exist in case of error
            }
    }

    suspend fun checkAuth(username: String, password: String): List<Collaboration> {
        return try {
            val collaborationRef = database.collection("collaboration")
            val querySnapshot = collaborationRef
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .await() // Await the result of the Firestore query

            if (!querySnapshot.isEmpty) {
                querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Collaboration::class.java)
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun joinCollab(
        user: AuthenticatedUserData?,
        collaboration: Collaboration,
        onError: (String) -> Unit,
    ): Boolean {
        if (user == null) {
            onError("User ID is null")
            return false
        }
        if (user.toMemberData() in collaboration.members) {
            onError("You are already on this collab")
            return false
        }
        return try {
            collaboration.members.add(user.toMemberData())
            database.collection("collaboration").document(collaboration.id)
                .set(convertCollabToMap(collaboration)).await()
            true

        } catch (e: Exception) {
            onError(e.message.toString())
            false
        }


    }

    suspend fun getCollaborations(
        collabsIds: List<String?>?,
        onError: (String) -> Unit
    ): List<Collaboration?> {
        try {
            if (!collabsIds.isNullOrEmpty()) {
                val collaborationRef =
                    database.collection("collaboration").whereIn("id", collabsIds)
                val querySnapshot = collaborationRef.get().await()
                return querySnapshot.toObjects(Collaboration::class.java)
            } else {
                return listOf()
            }
        } catch (e: Exception) {
            onError(e.message.toString())
            return listOf()
        }
    }

    suspend fun updateCollaboration(
        collaboration: Collaboration,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        try {
            database.collection("collaboration").document(collaboration.id)
                .update(
                    mapOf(
                        "tasks" to collaboration.tasks,
                        "members" to collaboration.members,
                        "admins" to collaboration.admins,
                    )
                )
                .await()
            onSuccess()
        } catch (e: Exception) {
            onError(e.message.toString())
        }
    }

}


fun AuthenticatedUserData.toMemberData(): Map<String, String?> = mapOf(
    "id" to this.userId,
    "username" to this.username,
    "profilePic" to this.profilePictureUrl
)