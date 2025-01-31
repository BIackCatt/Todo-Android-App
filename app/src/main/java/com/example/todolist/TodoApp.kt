package com.example.todolist

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.content.getSystemService
import com.example.todolist.data.AppContainer
import com.example.todolist.data.DefaultAppContainer



class TodoApp: Application() {

    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        val notificationChannel = NotificationChannel(
            "Tasks_notifications",
            "Tasks",
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationChannel.description = "Tasks reminder"

        val notificationsManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationsManager.createNotificationChannel(notificationChannel)
    }
}
