package com.example.todolist.utils

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ReplyAll
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoDisturbOn
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.todolist.data.CollabTask
import com.example.todolist.data.Collaboration
import com.example.todolist.data.Operation
import com.example.todolist.presentation.sign_in.AuthenticatedUserData
import com.example.todolist.ui.theme.viewmodels.TaskUiState
import java.text.SimpleDateFormat

@Composable
fun AddTaskDialog(
    title: String,
    isEdit: Boolean,
    task: TaskUiState?,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (TaskUiState) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column {
                // Title input field
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Title *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description input field
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isEdit) {
                        onSave(task!!.copy(title = title, description = description))
                    } else {
                        onSave(TaskUiState(title = title, description = description))
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF610FF4),
                    contentColor = Color.White,
                    disabledContentColor = Color.White.copy(alpha = 0.38f),
                    disabledContainerColor = Color(0xFF610FF4).copy(alpha = 0.38f)

                )// Enable the button only if the title is not empty
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernProfileDialog(
    fullName: String,
    email: String,
    onCancel: () -> Unit,
    onSignOut: () -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Title
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                // Full Name Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = "Full Name",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Full Name",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                        Text(
                            text = fullName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                // Divider
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                )

                // Email Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Email",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                // Buttons Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    // Cancel Button
                    TextButton(
                        onClick = onCancel,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    ) {
                        Text(text = "Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Sign Out Button
                    Button(
                        onClick = {
                            onSignOut()
                            onCancel()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 4.dp,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sign Out",
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Sign Out")
                    }
                }
            }
        }
    }
}


@Composable
fun ModernSignInDialog(
    onSignIn: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        )
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Title
                Text(
                    text = "Sign In Required",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                // Message
                Text(
                    text = "Sign in with Google to save your to-do lists online",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp),
                )

                GoogleButtonFab(
                    onClick = {
                        onSignIn()
                        onDismiss()
                    },
                    label = "Sign In With Google",
                    padding = 10.dp
                )

                // Cancel Button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.padding(top = 5.dp),
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
fun AddCollabDialog(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (Collaboration) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var showPassword by remember { mutableStateOf(false) }
    val passwordCriteria = listOf(
        "At least 6 characters" to (password.length >= 6),
        "Contains an uppercase letter" to password.any { it.isUpperCase() },
        "Contains a lowercase letter" to password.any { it.isLowerCase() },
        "Contains a number" to password.any { it.isDigit() },
        "Contains a special character" to password.any { it in "!@#\$%^&*()-_=+[]{}|;:'\",.<>?/" }
    )
    val progress by animateFloatAsState(
        targetValue = passwordCriteria.count { it.second } / passwordCriteria.size.toFloat(),
        animationSpec = tween(durationMillis = 1000)
    )

    AlertDialog(
        modifier = Modifier.clip(RoundedCornerShape(20.dp, 0.dp, 20.dp, 0.dp)),
        icon = {
            Icon(
                imageVector = Icons.Default.GroupAdd,
                contentDescription = "Add New Task",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        onDismissRequest = onDismiss,
        title = { Text("Create collaboration") },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    label = { Text("Collab name *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Password *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    color = Color.Green,
                    modifier = Modifier.fillMaxWidth(),
                )
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    passwordCriteria.forEach { (rule, met) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (met) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (met) Color.Green else Color.Red
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(rule, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        Collaboration(
                            username = username.trim(),
                            password = password
                        )
                    )
                },
                enabled = passwordCriteria.all { it.second } && username.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF610FF4),
                    contentColor = Color.White,
                    disabledContentColor = Color.White.copy(alpha = 0.38f),
                    disabledContainerColor = Color(0xFF610FF4).copy(alpha = 0.38f)
                )
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun JoinCollabDialog(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var showPassword by remember { mutableStateOf(false) }
    AlertDialog(
        modifier = Modifier.clip(RoundedCornerShape(20, 0, 20, 0)),
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ReplyAll,
                contentDescription = "Join collab",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        onDismissRequest = onDismiss,
        title = { Text("Join collaboration") },
        text = {
            Column {
                // Title input field
                OutlinedTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    label = { Text("Collab name *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description input field
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("password *") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(listOf(username.trim(), password))
                },
                enabled = username.isNotBlank() && password.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF610FF4),
                    contentColor = Color.White,
                    disabledContentColor = Color.White.copy(alpha = 0.38f),
                    disabledContainerColor = Color(0xFF610FF4).copy(alpha = 0.38f)

                ) // Enable the button only if the title is not empty
            ) {
                Text("Join")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}


@Composable
fun AddCollabTaskDialog(
    title: String,
    isEdit: Boolean,
    task: CollabTask? = null,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (CollabTask) -> Unit,
    userAccount: AuthenticatedUserData
) {
    val focusRequester = remember { FocusRequester() }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column {
                // Title input field
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Title *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description input field
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isEdit) {
                        onSave(task!!.copy(title = title, description = description))
                    } else {
                        onSave(
                            CollabTask(
                                title = title,
                                description = description,
                                assignedTo = mapOf(
                                    "id" to userAccount.userId,
                                    "username" to userAccount.username,
                                    "profilePic" to userAccount.profilePictureUrl
                                )
                            )
                        )
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF610FF4),
                    contentColor = Color.White,
                    disabledContentColor = Color.White.copy(alpha = 0.38f),
                    disabledContainerColor = Color(0xFF610FF4).copy(alpha = 0.38f)

                )// Enable the button only if the title is not empty
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun MemberItem(
    onExit: (String) -> Unit,
    member: Map<String, String?>,
    isCurrentUser: Boolean, // Whether the current user is the member
    isAdmin: Boolean, // Whether the current user is an admin
    isMemberAdmin: Boolean,
    onPromote: (id: String, username: String?) -> Unit, // Callback for promoting a member
    onRemove: (id: String, username: String?) -> Unit // Callback for removing a member
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Picture
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(member["profilePic"])
                    .build()
            ),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Name and Rank
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isCurrentUser) "You" else member["username"] as String,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isMemberAdmin) "admin" else "member",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // Actions (Only show for admins and non-current users)
        if (isAdmin) {
            // Current user is an admin
            if (isCurrentUser) {
                // Current user is an admin and the member is themselves
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { onExit(member["id"] as String) }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "exit",
                        tint = Color.Gray
                    )
                }
            } else if (!isMemberAdmin) {
                // Current user is an admin and the member is not an admin
                Row {
                    IconButton(onClick = {
                        onPromote(
                            (member["id"] as String),
                            (member["username"] as String)
                        )
                    }) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = "Promote")
                    }
                    IconButton(onClick = { onRemove((member["id"] as String), (member["username"] as String)) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove")
                    }
                }
            }
            // If the member is an admin and not the current user, do nothing (no UI changes)
        } else {
            // Current user is not an admin
            if (isCurrentUser) {
                // Current user is not an admin and the member is themselves
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { onExit(member["id"] as String) }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "exit",
                        tint = Color.Gray
                    )
                }
            }
            // If the current user is not an admin and the member is not themselves, do nothing (no UI changes)
        }
    }
}


@Composable
fun MembersDialog(
    members: List<Map<String, String?>?>,
    currentUser: Map<String, String?>,
    admins: List<Map<String, String?>?>, // Whether the current user is an admin
    onPromoteMember: (id: String, username: String?) -> Unit, // Callback for promoting a member
    onRemoveMember: (id: String, username: String?) -> Unit, // Callback for removing a member
    onDismiss: () -> Unit, // Callback for dismissing the dialog
    onExit: (String) -> Unit,
) {
    AlertDialog(
        modifier = Modifier.clip(
            RoundedCornerShape(20, 0, 20, 0)
        ),
        onDismissRequest = onDismiss,
        title = { Text(text = "Collab Members", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (members.isEmpty()) {
                    Text(text = "No members yet")
                } else {
                    members.forEach { member ->
                        MemberItem(
                            member = member!!,
                            isAdmin = currentUser in admins,
                            onPromote = onPromoteMember,
                            onRemove = onRemoveMember,
                            isCurrentUser = member["id"] == currentUser["id"],
                            isMemberAdmin = member in admins,
                            onExit = onExit
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}


@Composable
fun OperationsDialog(
    isAdmin: Boolean,
    operations: List<Operation?>,
    onClearClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false, dismissOnClickOutside = true,
            dismissOnBackPress = true
        ) // Make the dialog wider
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(1f) // Set width to 90% of the screen
                .fillMaxHeight(if (operations.isNotEmpty()) 0.8f else 0.3f) // Set height to 80% of the screen
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row {
                    // Dialog title
                    Text(
                        text = "Collaboration History",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.weight(1f))
                    if (isAdmin) {
                        IconButton(
                            onClick = onClearClick,
                            enabled = operations.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DoDisturbOn,
                                contentDescription = null
                            )
                        }
                    }
                }
                if (operations.isNotEmpty()) {
                    LazyColumn {
                        items(
                            items = operations,
                            key = { operation -> operation?.id ?: "" }) { operation ->
                            OperationItem(operation!!, isAdmin)
                            HorizontalDivider(
                                Modifier.padding(vertical = 5.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                } else {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EditNote,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(end = 0.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "No operations yet", textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun OperationItem(operation: Operation, isAdmin: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User profile picture
        AsyncImage(
            model = operation.userPic,
            contentDescription = "User Profile",
            modifier = Modifier
                .size(50.dp)
                .padding(5.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Operation details
        Column(modifier = Modifier.weight(0.80f)) {
            Text(
                text = operation.message ?: "Something Went Wrong!",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.padding(vertical = 5.dp))
            Text(
                text = SimpleDateFormat("hh:mm a, dd MMM yyyy").format(operation.timestamp!!), // Format the Date
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        if (isAdmin) {
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                    modifier = Modifier.weight(0.10f)
                )
            }
        }
    }
}
