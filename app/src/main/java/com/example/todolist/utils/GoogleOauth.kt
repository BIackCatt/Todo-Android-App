package com.example.todolist.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.todolist.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.credentials.GetCredentialRequest.Builder as CredsBuilder

class GoogleOauth(
    private val context: Context,
) {
    private var googleIdOption: GetGoogleIdOption? = null


    private fun getGoogleId(): GetGoogleIdOption? {
        try {
            googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(context.getString(R.string.client_id))
                .setAutoSelectEnabled(true)
                .setNonce("add_databases")
                .build()
        } catch (e: Exception) {
            Log.d("TAG", "getGoogleId: ${e.message}")
            googleIdOption = null
        }
        return googleIdOption
    }


    suspend fun signInFlow(
        context: Context,
        launcher: ManagedActivityResultLauncher<Intent, ActivityResult>?,
        login: (FirebaseUser?) -> Unit,
        onError: (String) -> Unit
    ) {
        val googleIdOption = getGoogleId() ?: return

        val request: GetCredentialRequest = CredsBuilder()
            .addCredentialOption(googleIdOption)
            .build()

        coroutineScope {
            try {
                val credentialManager = CredentialManager.create(context)
                val credential = credentialManager.getCredential(
                    context,
                    request
                )
                when (val result = credential.credential) {
                    is CustomCredential -> {
                        if (result.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(result.data)
                            val googleTokenId = googleIdTokenCredential.idToken
                            val authCredential =
                                GoogleAuthProvider.getCredential(googleTokenId, null)
                            val user =
                                Firebase.auth.signInWithCredential(authCredential).await().user
                            user?.let {
                                if (it.isAnonymous.not()) {
                                    login(
                                        user
                                    )
                                }
                            }
                        } else {

                        }
                    }

                    else -> {}
                }

            } catch (e: NoCredentialException) {
                launcher?.launch(getIntent())
            } catch (e: GetCredentialException) {
                e.printStackTrace()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Sign-in failed")
            }
        }

    }

    fun handleSignInResult(
        intent: Intent?,
        onSuccess: (FirebaseUser?) -> Unit,
        onError: (String) -> Unit,
        scope: CoroutineScope
    ) {
        scope.launch {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                val account: GoogleSignInAccount = task.getResult(Exception::class.java)!!

                // Extract the ID token from the account
                val idToken = account.idToken

                // Authenticate with Firebase using the ID token
                if (idToken != null) {
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    val authResult: AuthResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
                    onSuccess(authResult.user)
                }
            } catch (e: ApiException) {
                onError(e.localizedMessage ?: "Sign-in failed")
            }
        }
    }


    private fun getIntent(): Intent {
        return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
            putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        }
    }



    fun signOut(onComplete: () -> Unit, onFailure: () -> Unit, scope: CoroutineScope) {
        scope.launch {
            try {
                FirebaseAuth.getInstance().signOut()
                val credentialManager = CredentialManager.create(context)
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)

                onComplete()
            } catch (e: Exception) {
                onFailure()
            }
        }

    }


    fun getLatestSignIn(): FirebaseUser? {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        return currentUser
    }
}