package com.example.todolist.data

import com.example.todolist.ui.theme.viewmodels.TaskUiState
import java.util.UUID


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
