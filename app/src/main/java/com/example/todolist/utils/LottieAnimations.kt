package com.example.todolist.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.todolist.R

@Composable
fun ImportAnimation(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Lottie Animation
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.import_animation))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )

        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Text
        Text(
            text = "Fetching data",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )


    }
}

@Composable
fun StartingAppAnimation(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.startingapp)
    )
    val animationState by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = 1
    )

    LottieAnimation(
        composition = composition,
        progress = { animationState },
        modifier = modifier
    )
}

@Composable
fun DefaultList(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.loadinglistitems)
    )
    val animationState by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { animationState },
        modifier = modifier
    )
}