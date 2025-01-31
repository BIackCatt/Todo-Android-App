package com.example.todolist.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetWorkerUpdate(
    appContext: Context, workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        CoroutineScope(Dispatchers.IO).launch {
            TasksWidget().updateAll(applicationContext)
        }
        return Result.success()
    }
}