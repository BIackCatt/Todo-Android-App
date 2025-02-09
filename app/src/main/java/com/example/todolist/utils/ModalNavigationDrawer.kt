package com.example.todolist.utils

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReplyAll
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import com.example.compose.ToDoListTheme
import com.example.todolist.data.Collaboration
import com.example.todolist.ui.theme.navigation.NavRoutes
import com.example.todolist.ui.theme.viewmodels.CollabsState
import kotlinx.coroutines.launch

enum class Screens {
    HOME, COLLABORATION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(
    onJoin: (List<String>) -> Unit,
    collabs: CollabsState,
    isConnected: Boolean,
    isSignedIn: Boolean,
    screen: NavBackStackEntry? = null,
    drawerState: DrawerState,
    onHomeClick: () -> Unit = {},
    onCollaborationClick: (Collaboration) -> Unit,
    onCollaborationSave: (Collaboration) -> Unit = {},
    content: @Composable () -> Unit,
) {

    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxWidth(0.80f)
                    .clip(RoundedCornerShape(0, 20, 20, 0)),
                windowInsets = WindowInsets(0.dp)
            ) {
                DrawerTopTitle(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Column {
                    ModalDrawerContent(
                        isConnected = isConnected,
                        isSignedIn = isSignedIn,
                        currentScreen = screen,
                        onHomeClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            if (screen?.destination?.route != NavRoutes.Home.route) {
                                onHomeClick.invoke()
                            }

                        },
                        onCollaborationClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            onCollaborationClick.invoke(it)
                        },
                        onCollaborationSave = onCollaborationSave,
                        collabs = collabs,
                        onJoin = onJoin
                    )
                }
            }


        }
    ) {
        content()
    }
}


@Composable
fun DrawerTopTitle(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.background(Color(0xFF7713FF))) {
        Text(
            "To-Do App",
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun ModalDrawerContent(
    collabs: CollabsState,
    onJoin: (List<String>) -> Unit,
    isConnected: Boolean,
    isSignedIn: Boolean,
    onCollaborationSave: (Collaboration) -> Unit,
    currentScreen: NavBackStackEntry?,
    onHomeClick: () -> Unit,
    onCollaborationClick: (Collaboration) -> Unit
) {
    val context = LocalContext.current
    var collabDialogIsVisible by remember { mutableStateOf(false) }
    var joinCollabDialogVisible by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Your Tasks", fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(10.dp)
        )
        NavigationDrawerItem(
            modifier = Modifier.padding(vertical = 10.dp),
            icon = {
                Icon(
                    imageVector = if (currentScreen?.destination?.route == NavRoutes.Home.route)
                        Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") },
            selected = currentScreen?.destination?.route == NavRoutes.Home.route, // Change based on current screen
            onClick = onHomeClick
        )

        HorizontalDivider(Modifier.padding(vertical = 5.dp))
        Text(
            "Collaborations", fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(10.dp)
        )

        if (collabs.collabs.isNotEmpty()) {
            collabs.collabs.forEach {
                NavigationDrawerItem(
                    modifier = Modifier.padding(vertical = 10.dp),
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Group,
                            contentDescription = null
                        )
                    },
                    label = { Text(it?.username?: "No Title Collab") },
                    selected = (collabs.currentCollab == it && currentScreen?.destination?.route == NavRoutes.Collaboration.route),
                    onClick = { onCollaborationClick(it!!) }
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 5.dp))
        }

        // Collaboration Item
        NavigationDrawerItem(
            modifier = Modifier.padding(vertical = 10.dp),
            icon = {
                Row {
//                    Icon(
//                        imageVector = Icons.Filled.Add,
//                        contentDescription = null
//                    )
                    Icon(
                        imageVector = Icons.Filled.GroupAdd, contentDescription = "Collaboration"
                    )
                }
            },
            label = { Text("Create Collab") },
            selected = false, // Change based on current screen
            onClick = {
                if (isConnected && isSignedIn) {
                    collabDialogIsVisible = true
                } else if (!isSignedIn) {
                    Toast.makeText(
                        context,
                        "Sign in first to start collaborations",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "No Internet!, Check your internet connection",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
        NavigationDrawerItem(
            modifier = Modifier.padding(vertical = 10.dp),
            icon = {
                Row {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReplyAll,
                        contentDescription = null
                    )
                }
            },
            label = { Text("Join Collab") },
            selected = false, // Change based on current screen
            onClick = {
                if (isConnected && isSignedIn) {
                    joinCollabDialogVisible = true
                } else if (!isSignedIn) {
                    Toast.makeText(
                        context,
                        "Sign in first to join collaborations",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "No Internet!, Check your internet connection",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if (collabDialogIsVisible) {
        AddCollabDialog(
            onDismiss = {
                collabDialogIsVisible = false
                username = ""
                password = ""
            },
            onSave = {
                onCollaborationSave(it)
                collabDialogIsVisible = false
                username = ""
                password = ""
            },
            password = password,
            username = username,
            onPasswordChange = { password = it },
            onUsernameChange = { username = it }
        )
    }

    if (joinCollabDialogVisible) {
        JoinCollabDialog(
            onDismiss = {
                joinCollabDialogVisible = false
                username = ""
                password = ""
            },
            onSave = {
                onJoin(it)
                joinCollabDialogVisible = false
                username = ""
                password = ""
            },
            password = password,
            username = username,
            onPasswordChange = { password = it },
            onUsernameChange = { username = it }
        )
    }
}


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true,
    widthDp = 500
)
@Composable
private fun ModalDrawer() {
    ToDoListTheme {
        Surface {

        }
    }
}