package com.example.todolist.ui.theme.viewmodels

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.CollaborationsRepo
import com.example.todolist.data.convertUserDataToMap
import com.example.todolist.data.network.CollaborationsFireStore
import com.example.todolist.data.network.NetworkConnectivityObserver
import com.example.todolist.data.network.UserFireStore
import com.example.todolist.presentation.sign_in.AuthenticatedUserData
import com.example.todolist.presentation.sign_in.GoogleAuthUiClient
import com.example.todolist.presentation.sign_in.SignInState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class UserAccountViewModel(
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val networkConnectivityObserver: NetworkConnectivityObserver,
    private val collaborationsRepo: CollaborationsRepo,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val userFireBase = UserFireStore(firestore)
    private val collabFireBase = CollaborationsFireStore(firestore)
    val isConnected = networkConnectivityObserver.observeNetworkConnectivity()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )


    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()
    private val _userData: MutableStateFlow<AuthenticatedUserData?> = MutableStateFlow(null)
    val userData: StateFlow<AuthenticatedUserData?> = _userData
        .onStart {
            getLastSignIn()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    fun updateFirestoreUserData(onErrorAction: (String) -> Unit) {
        viewModelScope.launch {
            userFireBase.addUserData(
                _userData.value?.userId,
                convertUserDataToMap(_userData.value),
                onError = onErrorAction,
                )
        }
    }

    suspend fun onSignInResult(intent: Intent) {
        val result = googleAuthUiClient.signInWithIntent(intent)
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInErrorMessage = result.errorMessage
            )
        }
        if (result.data != null) {
            _userData.value = result.data
        }
    }


    fun signinIntent(): Intent = googleAuthUiClient.buildSignInRequest()

    fun resetState() {
        _state.update { SignInState() }
    }

    suspend fun signOut(onSignOut: () -> Unit) {
        googleAuthUiClient.signOut()
        _userData.value = null
        onSignOut.invoke()
        resetState()
    }

    fun updateUserCollabs(collabsIds: List<String?>) {
        _userData.update {
            it?.copy(
                collaborations = collabsIds.toMutableList()
            )
        }
    }
    private fun getLastSignIn() {
        val user = Firebase.auth.currentUser
        if (user != null) {
            viewModelScope.launch {
                _userData.value = user.run {
                    AuthenticatedUserData(
                        userId = uid,
                        username = displayName,
                        email = email,
                        phoneNumber = phoneNumber,
                        profilePictureUrl = photoUrl?.toString(),
                    )
                }
                _state.update {
                    it.copy(
                        isSignInSuccessful = true,
                        signInErrorMessage = null
                    )
                }
            }
        } else {
            _userData.value = null
        }
    }

}