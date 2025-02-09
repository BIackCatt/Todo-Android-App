package com.example.todolist.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTopAppBar(
    context: Context,
    onIconClick: () -> Unit,
    showDriveButtons: Boolean,
    title: String,
    onModelDrawerClicked: () -> Unit,
    drawerState: DrawerState,
    icon: @Composable (Modifier) -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    onImportClick: () -> Unit,
    isSync: Boolean,
    isConnected: Boolean,
) {
    val isDrawerOpened = animateFloatAsState(
        targetValue =  if (drawerState.isOpen) -90f else 1f,
    )
    var isImportEnabled by rememberSaveable { mutableStateOf(true) }
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onModelDrawerClicked
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.rotate(isDrawerOpened.value)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(5.dp)
                        .clickable {
                            if (showDriveButtons) {
                                onIconClick()
                            }
                        },
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.weight(1f))


                if (showDriveButtons) {
                    IconButton(
                        onClick = {
                            if (!isConnected) {
                                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                                context.startActivity(intent)
                            }
                        }) {
                        Icon(
                            imageVector = if (isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = if (isConnected) Color.Green else Color.Gray,
                        )
                    }
                    IconButton(
                        enabled = isImportEnabled,
                        onClick = {
                            if (isConnected) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    isImportEnabled = false
                                    onImportClick()
                                    delay(2500)
                                    isImportEnabled = true
                                }
                            } else {
                                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (!isSync) Icons.Default.Sync else Icons.Default.PublishedWithChanges,
                            contentDescription = "Import",
                            tint = if (!isSync) Color.Gray else Color.Green
                        )
                    }

                }
                icon(
                    Modifier
                        .padding(5.dp)
                        .size(50.dp)
                        .clip(CircleShape)
                )
            }


        },

        actions = actions,
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.shadow(elevation = 4.dp) // Add a subtle shadow
    )




}@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollaborationsTopAppBar(
    context: Context,
    onIconClick: () -> Unit,
    showDriveButtons: Boolean,
    title: String,
    onModelDrawerClicked: () -> Unit,
    drawerState: DrawerState,
    icon: @Composable (Modifier) -> Unit,
    onImportClick: () -> Unit,
    isSync: Boolean,
    isConnected: Boolean,
) {
    val isDrawerOpened = animateFloatAsState(
        targetValue =  if (drawerState.isOpen) -90f else 1f,
    )
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onModelDrawerClicked
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.rotate(isDrawerOpened.value)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(5.dp)
                        .clickable {
                            if (showDriveButtons) {
                                onIconClick()
                            }
                        },
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.weight(1f))


                if (showDriveButtons) {
                    IconButton(
                        onClick = {
                            if (!isConnected) {
                                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                                context.startActivity(intent)
                            }
                        }) {
                        Icon(
                            imageVector = if (isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = if (isConnected) Color.Green else Color.Gray,
                        )
                    }
                    IconButton(
                        enabled = true,
                        onClick = {
                            onImportClick.invoke()
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = "History",
                        )
                    }

                }
                icon(
                    Modifier
                        .padding(5.dp)
                        .size(50.dp)
                        .clip(CircleShape)
                )
            }


        },

        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.shadow(elevation = 4.dp) // Add a subtle shadow
    )
}