package com.example.todolist.utils

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.util.DebugLogger

object CoilImageFormatter {

    @Composable
    fun ProfileImage(
        onNotSignedClick: () -> Unit,
        onClick: () -> Unit,
        image: String?,
        modifier: Modifier = Modifier
    ) {
        Log.d("ImageURL", "Loading image: ${image?.replace("http://", "https://")}")
        SubcomposeAsyncImage(
            modifier = modifier,
            model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .build(),
            contentDescription = "Profile Image",
            imageLoader = ImageLoader.Builder(LocalContext.current)
                .diskCachePolicy(CachePolicy.ENABLED)
                .logger(DebugLogger())
                .build(),
            success = { state ->
                Image(
                    painter = state.painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = modifier.clickable { onClick() }
                )
            },
            loading = {
                CircularProgressIndicator(modifier = modifier)
            },
            error = { error ->
                Log.e("ImageError", error.result.throwable.toString())

                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = modifier.clickable { onNotSignedClick() }
                )
            }
        )
    }

}