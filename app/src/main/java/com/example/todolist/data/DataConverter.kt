package com.example.todolist.data

import com.example.todolist.presentation.sign_in.AuthenticatedUserData
import com.example.todolist.ui.theme.viewmodels.TaskUiState


fun convertListToHashedMap(
    data: List<TaskUiState?>
): List<Map<String, Any?>> {
    val convertedData = data.map { task ->
        hashMapOf(
            "id" to task?.id,
            "title" to task?.title,
            "description" to task?.description,
            "isCompleted" to task?.isCompleted,
            "date" to task?.date,
            "isSynced" to task?.isSynced,
            "isDeleted" to task?.isDeleted
        )
    }
    return convertedData
}


fun convertTaskItemToMap(task: Task): Map<String, Any?> {
    return task.run {
        hashMapOf(
            "id" to id,
            "title" to title,
            "description" to description,
            "isCompleted" to isCompleted,
            "date" to date,
            "isSynced" to isSynced,
        )
    }
}

fun convertUserDataToMap(user: AuthenticatedUserData?): Map<String, Any?>? {
    return user?.run {
        mapOf(
            "userId" to userId,
            "username" to username,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "profilePic" to profilePictureUrl,
            "collaborations" to collaborations,
        )
    }
}

fun convertCollabTaskToMap(task: CollabTask): MutableMap<String, Any?> {
    return task.run {
        mutableMapOf(
            "id" to this.id,
            "title" to this.title,
            "description" to this.description,
            "assignedTo" to this.assignedTo,
            "date" to this.date
        )
    }
}


fun convertCollabToMap(collaboration: Collaboration): MutableMap<String, Any?> {
    return collaboration.run {
        mutableMapOf(
            "id" to this.id,
            "username" to this.username,
            "password" to this.password,
            "members" to this.members,
            "admins" to this.admins,
            "tasks" to this.tasks,
            )
    }
}

fun convertCollabOperationToMap(operation: Operation): Map<String, Any?> {
    return operation.run {
        mapOf(
            "id" to id,
            "userId" to userId,
            "username" to username,
            "userPic" to userPic,
            "timestamp" to timestamp,
        )
    }
}
