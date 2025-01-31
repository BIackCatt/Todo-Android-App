package com.example.todolist.notifications

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.core.app.NotificationCompat
import com.example.todolist.R
import kotlin.random.Random

class TasksNotificationsManager(
    private val context: Context
) {
    private val notificationsManager = context.getSystemService(NotificationManager::class.java)

    fun showBasicNotifications(message: String) {
        val notification = NotificationCompat.Builder(context, "Tasks_notifications")
            .setContentTitle("Tasks Reminder")
            .setContentText(message)
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        notificationsManager.notify(
            Random.nextInt(),
            notification
        )
    }

    fun showExpandableNotification() {
        val notification = NotificationCompat.Builder(context, "Tasks_notifications")
            .setContentTitle("Tasks Reminder")
            .setContentText("You have tasks to complete!")
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(
                        context.bitmapFromResource(R.drawable.logo)
                    )

            ).build()

        notificationsManager.notify(
            Random.nextInt(),
            notification
        )
    }

    fun showInboxStyleNotifications() {
        val notification = NotificationCompat.Builder(context, "Tasks_notifications")
            .setContentTitle("Tasks Reminder")
            .setContentText("You have tasks to complete!")
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.InboxStyle()
                    .addLine("line1")
                    .addLine("line2")
                    .addLine("line3")
                    .addLine("line4")
                    .addLine("line5")
                    .addLine("line6")
                    .addLine("line7")
            ).build()

        notificationsManager.notify(
            Random.nextInt(),
            notification
        )
    }

    fun groupSummary() {

        val groupId = "tasks_reminder"
        val summaryId = 0

        val notification1 = NotificationCompat.Builder(context, "Tasks_notifications")
            .setContentTitle("Tasks Reminder")
            .setContentText("You have tasks to complete!")
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.InboxStyle()
                    .addLine("line1")
                    .addLine("line2")
            )
            .setGroup(groupId)
            .build()

        val notification2 = NotificationCompat.Builder(context, "Tasks_notifications")
            .setContentTitle("Tasks Reminder")
            .setContentText("You have tasks to complete!")
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.InboxStyle()
                    .addLine("line1")
            )
            .setGroup(groupId)
            .build()


        val summaryNotify = NotificationCompat.Builder(context, "Tasks_notifications")
            .setContentTitle("Tasks Reminder")
            .setContentText("You have tasks to complete!")
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setSummaryText("You have 2 tasks to complete")
                    .setBigContentTitle("Tasks Reminders")
            )
            .setGroup(groupId)
            .setGroupSummary(true)
            .build()

        notificationsManager.apply {
            notify(Random.nextInt(), summaryNotify)
            notify(Random.nextInt(), notification1)
            notify(Random.nextInt(), notification2)
        }
    }


    private fun Context.bitmapFromResource(
        @DrawableRes resId:Int
    )= BitmapFactory.decodeResource(
        resources,
        resId
    )
}