package com.example.todolist.ui.theme.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.todolist.R
import com.example.todolist.ui.theme.viewmodels.HomeScreenViewModel
import com.example.todolist.ui.theme.viewmodels.UserAccountViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    homeViewModel: HomeScreenViewModel,
    userAccountViewModel: UserAccountViewModel,
    onLoadingComplete: () -> Unit,
    isConnected: Boolean,
) {
    var isVisible by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val state by userAccountViewModel.state.collectAsState()

    // Handle sign-in error messages
    LaunchedEffect(key1 = state.signInErrorMessage) {
        state.signInErrorMessage?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }
    // Handle successful sign-in and data import
    LaunchedEffect(key1 = state.isSignInSuccessful) {
        if (state.isSignInSuccessful) {
            // Import user data
            homeViewModel.importData(
                isConnected = isConnected,
                userId = userAccountViewModel.userData.value?.userId,
                onSuccessAction = {
                },
                onErrorAction = { error ->
                    Toast.makeText(
                        context,
                        error ?: "Data import failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }


    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val lottieComposition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.startinganimations)
        )

        val progress by animateLottieCompositionAsState(
            lottieComposition,
            iterations = 1
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                progress = { progress }
                , composition = lottieComposition,
                modifier = Modifier.background(MaterialTheme.colorScheme.background))
        }
    }

    LaunchedEffect(Unit) {
        delay(2000)
        isVisible = false
        delay(500)
        onLoadingComplete()
    }
}