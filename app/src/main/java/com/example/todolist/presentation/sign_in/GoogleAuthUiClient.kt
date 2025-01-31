package com.example.todolist.presentation.sign_in

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.todolist.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await


class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    val auth = Firebase.auth
    private lateinit var googleSignInClient: GoogleSignInClient

    init {
        buildSignInRequest()
    }

    @SuppressWarnings("deprecation")
    suspend fun signIn(): IntentSender? {
        val signinIntent = googleSignInClient.signInIntent
        return signinIntent.getParcelableExtra<IntentSender>("android.intent.extra.INTENT")
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        val account = task.getResult(ApiException::class.java)
        val googleIdToken = account?.idToken
        val googleCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredential).await().user
            SignInResult(
                data = user?.run {
                    AuthenticatedUserData(
                        userId = uid,
                        profilePictureUrl = photoUrl?.toString(),
                        username = displayName?: "No UserName",
                        email = email.toString(),
                        phoneNumber = phoneNumber,
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }


    fun buildSignInRequest(): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
        return googleSignInClient.signInIntent
    }

    fun getSignedInUserData(): AuthenticatedUserData? = auth.currentUser?.run {
        AuthenticatedUserData(
            userId = uid,
            profilePictureUrl = photoUrl?.toString(),
            username = displayName?: "No UserName",
            email = email.toString(),
            phoneNumber = phoneNumber,
        )
    }

    suspend fun signOut() {
        try {
            auth.signOut()
            googleSignInClient.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

}