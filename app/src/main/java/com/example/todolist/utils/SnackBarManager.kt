package com.example.todolist.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.time.delay

class SnackbarManager {
    private val _currentMessage = MutableStateFlow<SnackbarMessage?>(null)
    val currentMessage: StateFlow<SnackbarMessage?> = _currentMessage.asStateFlow()
    private val _isSnackbarVisible = MutableStateFlow(false)
    val isSnackbarVisible = _isSnackbarVisible.asStateFlow()

    suspend fun show(
        message: String,
        duration: Long = 3000,
        icon: ImageVector = Icons.Filled.Notifications
    ) {
        _currentMessage.value = SnackbarMessage(message, duration, icon)
        _isSnackbarVisible.value = true
        kotlinx.coroutines.delay(duration)
        dismissCurrentMessage()
    }

    fun dismissCurrentMessage() {
        _isSnackbarVisible.value = false
        _currentMessage.value = null
    }
}

data class SnackbarMessage(
    val message: String = "",
    val duration: Long = 3000,
    val icon: ImageVector = Icons.Filled.Notifications
)
