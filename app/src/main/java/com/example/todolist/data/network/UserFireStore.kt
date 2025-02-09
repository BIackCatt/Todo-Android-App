package com.example.todolist.data.network

import android.util.Log
import androidx.compose.animation.core.rememberTransition
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.todolist.data.Collaboration
import com.example.todolist.data.convertUserDataToMap
import com.example.todolist.presentation.sign_in.AuthenticatedUserData
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.tasks.await

class UserFireStore(
    private val fireStore: FirebaseFirestore,
) {
    fun addUserData(
        userId: String?,
        userData: Map<String, Any?>?,
        onError: (String) -> Unit
    ): Boolean {
        if (userId != null) {
            try {
                Log.d("AddingCollab", userData.toString())
                val userRef = fireStore.collection("users").document(userId)
                userRef.set(
                    mapOf("data" to userData!!)
                )
                return true
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
                return false
            }
        } else {
            onError("No User Id provider")
            return false
        }
    }

    suspend fun getUserData(userId: String?, onError: (String) -> Unit): AuthenticatedUserData? {
        // Check if userId is null
        if (userId == null) {
            onError("No User Id provided")
            return null
        }

        return try {
            // Fetch the user document from Firestore
            val userRef = fireStore.collection("users").document(userId).get().await()

            // Check if the document exists
            if (!userRef.exists()) {
                onError("User not found")
                return null
            }

            // Access the "data" map
            val data = userRef.get("data") as? Map<String, Any>
            if (data == null) {
                onError("Data field is missing or invalid")
                return null
            }

            // Extract fields from the "data" map
            val username = data["username"] as? String ?: run {
                onError("Username field is missing or invalid")
                return null
            }

            val profilePictureUrl = data["profilePic"] as? String ?: "" // Optional field, default to empty string

            // Return the AuthenticatedUserData object
            AuthenticatedUserData(
                userId = userId,
                username = username,
                profilePictureUrl = profilePictureUrl
            )
        } catch (e: Exception) {
            // Log the error and return null
            Log.e("getUserData", "Error fetching user data: ${e.message}", e)
            onError("Failed to fetch user data: ${e.message ?: "Unknown error"}")
            null
        }
    }

    fun getUserCollabs(userId: String?, onError: (String) -> Unit, onSuccess:(List<String?>) -> Unit) {
        if (userId == null) {
            onError("No provided user")
            return
        }

        val userRef = fireStore.collection("users").document(userId)

        userRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onError("Failed to listen to user collaborations: ${error.message}")
                return@addSnapshotListener
            }

            if (snapshot == null || !snapshot.exists()) {
                onError("User not found")
                return@addSnapshotListener
            }

            val data = snapshot.get("data") as? Map<String, Any>
            if (data == null) {
                onError("Data field is missing or invalid")
                return@addSnapshotListener
            }

            val collaborations = data["collaborations"] as? List<String>
            if (collaborations == null) {
                onError("Collaborations field is missing or invalid")
                return@addSnapshotListener
            }

            onSuccess(collaborations)
        }
    }

    suspend fun exitCollab(
        user: AuthenticatedUserData?,
        onError: (String) -> Unit,
        collabId: String?,
    ) {
        if (user == null) {
            onError("No provided user")
            return
        }
        if (collabId == null) {
            onError("No provided collaboration")
            return
        }
        user.collaborations.remove(collabId)
        addUserData(user.userId, convertUserDataToMap(user), onError)
    }
}

