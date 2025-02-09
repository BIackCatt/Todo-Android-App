package com.example.todolist.ui.theme.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.example.todolist.utils.SnackbarManager
import com.example.todolist.utils.SnackbarMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SnackBarViewModel : ViewModel() {
    private val _currentMessage = MutableStateFlow<SnackbarMessage?>(null)
    val currentMessage: StateFlow<SnackbarMessage?> = _currentMessage.asStateFlow()
    private val _isSnackbarVisible = MutableStateFlow(false)
    val isSnackbarVisible = _isSnackbarVisible.asStateFlow()
    private var currentDuration by mutableLongStateOf(0)

    suspend fun show(
        message: String,
        duration: Long = 3000,
        icon: ImageVector = Icons.Filled.Notifications
    ) {
        dismissCurrentMessage()
        _currentMessage.value = SnackbarMessage(message, duration, icon)
        currentDuration = duration
        _isSnackbarVisible.value = true
        if (duration > 0) {
            kotlinx.coroutines.delay(duration)
        }
        dismissCurrentMessage()
    }

    fun dismissCurrentMessage() {
        _isSnackbarVisible.value = false
        currentDuration = 0
    }
}

data class SnackbarMessage(
    val message: String = "",
    val duration: Long = 3000,
    val icon: ImageVector = Icons.Filled.Notifications
)