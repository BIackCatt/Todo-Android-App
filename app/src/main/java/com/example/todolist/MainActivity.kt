package com.example.todolist

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.compose.ToDoListTheme
import com.example.todolist.notifications.TasksNotificationsManager
import com.example.todolist.ui.theme.navigation.TodoAppEntry
import com.example.todolist.widget.TasksWidget
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContent {
            ToDoListTheme(dynamicColor = true) {


                val postNotificationPermission =
                    rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

                val accessPermission =
                    rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
                val readAccessper =
                    rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)

                val tasksNotificationsManager = TasksNotificationsManager(this)



                LaunchedEffect(key1 = true) {
                    if (!postNotificationPermission.status.isGranted) {
                        postNotificationPermission.launchPermissionRequest()
                    }
                    if (!accessPermission.status.isGranted) {
                        accessPermission.launchPermissionRequest()
                    }
                    if (!readAccessper.status.isGranted) {
                        readAccessper.launchPermissionRequest()
                    }
                }
                Surface {
                    TodoAppEntry(
                        lifecycleScope = lifecycleScope,
                        notificationsManager = tasksNotificationsManager,
                    )
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            TasksWidget().updateAll(applicationContext)
        }
    }
}

