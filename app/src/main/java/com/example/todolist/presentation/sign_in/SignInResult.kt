package com.example.todolist.presentation.sign_in


data class SignInResult (
    val data: AuthenticatedUserData?,
    val errorMessage: String? = null
)

data class AuthenticatedUserData (
    val userId: String,
    val username: String? = null,
    val profilePictureUrl: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null

)