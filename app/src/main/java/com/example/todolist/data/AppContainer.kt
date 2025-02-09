package com.example.todolist.data

import android.content.Context
import com.example.todolist.data.network.CollaborationsFireStore
import com.example.todolist.data.network.NetworkConnectivityObserver
import com.example.todolist.data.network.UserFireStore
import com.example.todolist.presentation.sign_in.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore


interface AppContainer {
    val tasksRepo: TasksRepo
    val offlineTasksRepo: OfflineTasksRepoInterface
    val googleAuthUiClient: GoogleAuthUiClient
    val networkConnectivityObserver: NetworkConnectivityObserver
    val collaborationsRepo: CollaborationsRepo
    val firestore: FirebaseFirestore

}

class DefaultAppContainer(private val context: Context): AppContainer {
    override val offlineTasksRepo: OfflineTasksRepo by lazy {
        OfflineTasksRepo(TasksDatabase.getDatabase(context = context).offlineTasksDao())
    }
    override val tasksRepo: TasksRepo by lazy {
        OnlineTasksRepo(TasksDatabase.getDatabase(context = context).taskDAO())
    }
    override val googleAuthUiClient: GoogleAuthUiClient by lazy {
        GoogleAuthUiClient(context, Identity.getSignInClient(context))
    }
    override val networkConnectivityObserver: NetworkConnectivityObserver by lazy {
        NetworkConnectivityObserver(context)
    }

    override val collaborationsRepo: CollaborationsRepo by lazy {
        LocalCollaborationsRepo(TasksDatabase.getDatabase(context).collaborationDao())
    }

    override val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

}