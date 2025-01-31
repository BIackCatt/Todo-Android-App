package com.example.todolist.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.ToDoListTheme
import com.example.todolist.R
import com.stevdzasan.onetap.GoogleButtonTheme

@Composable
fun GoogleButtonFab(
    label: String = "Sign In with Google",
    iconOnly: Boolean = false,
    theme: GoogleButtonTheme = if (isSystemInDarkTheme()) GoogleButtonTheme.Dark
    else GoogleButtonTheme.Light,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = when (theme) {
            GoogleButtonTheme.Light -> Color.White
            GoogleButtonTheme.Dark -> Color(0xFF131314)
            GoogleButtonTheme.Neutral -> Color(0xFFF2F2F2)
        },
        contentColor = when (theme) {
            GoogleButtonTheme.Dark -> Color(0xFFE3E3E3)
            else -> Color(0xFF1F1F1F)
        },
    ),
    border: BorderStroke? = when (theme) {
        GoogleButtonTheme.Light -> BorderStroke(
            width = 1.dp,
            color = Color(0xFF747775),
        )

        GoogleButtonTheme.Dark -> BorderStroke(
            width = 1.dp,
            color = Color(0xFF8E918F),
        )

        GoogleButtonTheme.Neutral -> null
    },
    shape: Shape = ButtonDefaults.shape,
    onClick: () -> Unit,
    padding: Dp = 100.dp,
    modifier: Modifier = Modifier
) {

    // Floating Google Button on the Left Side
    Button(
        modifier = modifier
            .width(if (iconOnly) 40.dp else Dp.Unspecified),
        onClick = onClick,
        shape = shape,
        colors = colors,
        contentPadding = PaddingValues(horizontal = if (iconOnly) 9.5.dp else 12.dp),
        border = border,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Google Logo
            Image(
                painter = painterResource(id = R.drawable.google_logo), // Add Google logo to your drawable resources
                contentDescription = "Google Logo",
                modifier = Modifier.size(if (iconOnly) 40.dp else 24.dp),
            )

            // "Sign in with Google" Text (if not iconOnly)
            if (!iconOnly) {
                Text(
                    text = label,
                    maxLines = 1,
                )
            }
        }
    }
}


@Composable
fun GoogleButtonIcon(
    modifier: Modifier = Modifier,
    size: Int,
    theme: GoogleButtonTheme = if (isSystemInDarkTheme()) GoogleButtonTheme.Dark
    else GoogleButtonTheme.Light,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = when (theme) {
            GoogleButtonTheme.Light -> Color.White
            GoogleButtonTheme.Dark -> Color(0xFF131314)
            GoogleButtonTheme.Neutral -> Color(0xFFF2F2F2)
        },
        contentColor = when (theme) {
            GoogleButtonTheme.Dark -> Color(0xFFE3E3E3)
            else -> Color(0xFF1F1F1F)
        },
    ),
    border: BorderStroke? = when (theme) {
        GoogleButtonTheme.Light -> BorderStroke(
            width = 1.dp,
            color = Color(0xFF747775),
        )

        GoogleButtonTheme.Dark -> BorderStroke(
            width = 1.dp,
            color = Color(0xFF8E918F),
        )

        GoogleButtonTheme.Neutral -> null
    },
    onClick: () -> Unit,
) {
    OutlinedIconButton(
        border = border,
        onClick = onClick,
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(
                when (theme) {
                    GoogleButtonTheme.Light -> Color.White
                    GoogleButtonTheme.Dark -> Color(0xFF131314)
                    GoogleButtonTheme.Neutral -> Color(0xFFF2F2F2)
                }
            )
    ) {

        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.google_logo), // Add Google logo to your drawable resources
                contentDescription = "Google Logo",
                modifier = Modifier
                    .size(size.dp)
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

    }
}

@Preview
@Composable
private fun GoogleButton() {
    ToDoListTheme {
        GoogleButtonFab(onClick = {})
    }

}