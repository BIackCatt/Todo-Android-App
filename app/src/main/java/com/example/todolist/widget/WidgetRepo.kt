package com.example.todolist.widget

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todolist.data.OfflineTask
import com.example.todolist.data.OfflineTasksDao
import com.example.todolist.data.TasksDatabase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MarkTaskCompleteAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val gsonTask = parameters[Task] ?: return
        val task = Gson().fromJson(gsonTask, OfflineTask::class.java)
        val taskDao = TasksDatabase.getDatabase(context).offlineTasksDao()
        taskDao.update(task.copy(isCompleted = !task.isCompleted))
        // Update the widget instantly
        TasksWidget().updateAll(context)
    }
    private fun scheduleWidgetUpdateWorker(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<WidgetWorkerUpdate>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}

// Key for passing task ID
val Task = ActionParameters.Key<String>("task_id")