package com.example.todolist.ui.theme.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.RemoveDone
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.compose.ToDoListTheme
import com.example.todolist.data.CollabTask
import com.example.todolist.presentation.sign_in.AuthenticatedUserData
import com.example.todolist.ui.theme.navigation.NavRoutes
import com.example.todolist.ui.theme.viewmodels.CollaborationViewModel
import com.example.todolist.ui.theme.viewmodels.LoadingControl
import com.example.todolist.ui.theme.viewmodels.SnackBarViewModel
import com.example.todolist.ui.theme.viewmodels.TaskUiState
import com.example.todolist.ui.theme.viewmodels.UserAccountViewModel
import com.example.todolist.utils.AddCollabTaskDialog
import com.example.todolist.utils.BottomNavigation
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.util.Locale


@SuppressLint("ContextCastToActivity")
@Composable
fun CollaborationScreen(
    snackBarViewModel: SnackBarViewModel,
    navHostController: NavHostController,
    collaborationViewModel: CollaborationViewModel,
    onClick: () -> Unit,
    userAccountViewModel: UserAccountViewModel,
    isConnected: Boolean
) {
    val activity = LocalContext.current as Activity
    val loadingState by collaborationViewModel.loading.collectAsStateWithLifecycle()
    BackHandler {
        if (navHostController.previousBackStackEntry?.destination?.route == NavRoutes.Start.route) {
            activity.finish()
        } else {
            navHostController.navigate(NavRoutes.Home.route)
        }
    }


    when (loadingState) {
        is LoadingControl.FetchingData -> {
            FetchingDataScreen()
        }

        is LoadingControl.Success -> {
            SuccessCollaborationScreen(
                onClick = onClick,
                userAccountViewModel = userAccountViewModel,
                isConnected = isConnected,
                collaborationViewModel = collaborationViewModel,
                snackBarViewModel = snackBarViewModel
            )
        }

        is LoadingControl.Error -> {}
    }
}


@OptIn(ExperimentalAnimationApi::class, ExperimentalPagerApi::class)
@Composable
fun SuccessCollaborationScreen(
    snackBarViewModel: SnackBarViewModel,
    modifier: Modifier = Modifier,
    collaborationViewModel: CollaborationViewModel,
    userAccountViewModel: UserAccountViewModel,
    onClick: () -> Unit,
    isConnected: Boolean
) {
    val state by collaborationViewModel.userCollab.collectAsState()
    val context = LocalContext.current
    val userAccount by userAccountViewModel.userData.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var isEdit by rememberSaveable { mutableStateOf(false) }
    var task by remember { mutableStateOf<TaskUiState?>(null) }
    var pageCountIndex by rememberSaveable { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0)
    Scaffold(
        bottomBar = {
            BottomNavigation(
                pageIndex = pageCountIndex,
                onCompletedClick = {
                    scope.launch {
                        pagerState.scrollToPage(it)
                    }
                },
                onClick = {
                    scope.launch {
                        pagerState.scrollToPage(it)
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                AddTaskFAB(onClick = {
                    if (isConnected) {
                        isEdit = false
                        task = null
                        showDialog = true
                    } else {
                        scope.launch {
                            snackBarViewModel.show(
                                message = "No Internet Connection",
                                icon = Icons.Filled.WifiOff,
                            )
                        }
                    }
                })
            }
        }
    ) { innerPadding ->
        println(innerPadding)

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                pageCountIndex = page
            }
        }
        HorizontalPager(
            count = 2,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            verticalAlignment = Alignment.Top

        ) { pageIndex ->
            var title by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }
            val completedTasks = state.currentCollab?.completedTasks ?: emptyList()
            val uncompletedTasks = state.currentCollab?.tasks ?: emptyList()

            Box {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    CollaborationTasksList(
                        collaborationViewModel = collaborationViewModel,
                        userAccount = userAccount,
                        isEmpty = if (pageIndex == 1) completedTasks.isEmpty() else uncompletedTasks.isEmpty(),
                        label = if (pageIndex == 1) "Completed" else "Uncompleted",
                        tasksList = if (pageIndex == 1) completedTasks else uncompletedTasks,
                        onCheckedChange = {
                            if (isConnected) {
                                if (it in completedTasks) {
                                    collaborationViewModel.uncompleteTask(
                                        task = it,
                                        collabId = state.currentCollab!!.id,
                                        onSuccess = {
                                            scope.launch {
                                                snackBarViewModel.show(
                                                    message = "\"${it.title}\" uncompleted"
                                                )
                                            }
                                        },
                                        onError = {
                                            scope.launch {
                                                snackBarViewModel.show(
                                                    message = "Something went wrong: $it",
                                                    icon = Icons.Filled.Error,
                                                )
                                            }
                                        }
                                    )
                                } else if (it in uncompletedTasks) {
                                    collaborationViewModel.completeTask(
                                        task = it,
                                        collabId = state.currentCollab!!.id,
                                        onSuccess = {
                                            scope.launch {
                                                snackBarViewModel.show(
                                                    message = "\"${it.title}\" uncompleted"
                                                )
                                            }
                                        },
                                        onError = {
                                            scope.launch {
                                                snackBarViewModel.show(
                                                    message = "Something went wrong: $it",
                                                    icon = Icons.Filled.Error,
                                                )
                                            }
                                        }
                                    )
                                }
                            } else {
                                scope.launch {
                                    snackBarViewModel.show(
                                        message = "No Internet Connection",
                                        icon = Icons.Filled.WifiOff,
                                    )
                                }
                            }
                        },
                        onClick = {},
                        onSyncClick = {},
                        onDeleteClick = {
                            if (isConnected) {

                                scope.launch {
                                    collaborationViewModel.deleteTask(
                                        task = it,
                                        collab = state.currentCollab!!,
                                        onSuccess = {
                                            scope.launch {
                                                snackBarViewModel.show(
                                                    message = "Task deleted successfully!",
                                                    icon = Icons.Filled.Delete,
                                                )
                                            }
                                        },
                                        onError = {
                                            scope.launch {
                                                snackBarViewModel.show(
                                                    message = "Something went wrong: $it",
                                                    icon = Icons.Filled.Error,
                                                )
                                            }
                                        },
                                    )
                                }
                            } else {
                                scope.launch {
                                    snackBarViewModel.show(
                                        message = "No Internet Connection",
                                        icon = Icons.Filled.WifiOff,
                                    )
                                }
                            }
                        },
                        onEditClick = {},
                        completedTasks = completedTasks,
                        uncompletedTasks = uncompletedTasks
                    )
                }
            }
            if (showDialog) {
                AddCollabTaskDialog(
                    onDismiss = {
                        showDialog = false
                        title = ""
                        description = ""
                    },
                    onSave = {
                        collaborationViewModel.addTask(
                            it.copy(
                                assignedTo = mapOf(
                                    "id" to userAccount?.userId,
                                    "username" to userAccount?.username,
                                    "profilePic" to userAccount?.profilePictureUrl
                                )
                            ),
                            collab = state.currentCollab!!,
                            onSuccess = {
                                scope.launch {
                                    snackBarViewModel.show(
                                        message = "Task Added successfully!",
                                        icon = Icons.Filled.AddTask,
                                    )
                                }
                            },
                            onError = {
                                scope.launch {
                                    snackBarViewModel.show(
                                        message = "Something went wrong: $it",
                                        icon = Icons.Filled.Error,
                                    )
                                }
                            }
                        )
                        showDialog = false
                        title = ""
                        description = ""
                    },
                    title = title,
                    isEdit = false,
                    onTitleChange = { title = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    userAccount = userAccount!!
                )
            }
        }
    }
}


@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun CollaborationTasksList(
    completedTasks: List<CollabTask?>,
    uncompletedTasks: List<CollabTask?>,
    modifier: Modifier = Modifier,
    collaborationViewModel: CollaborationViewModel,
    label: String,
    onSyncClick: (CollabTask) -> Unit,
    isEmpty: Boolean = false,
    onClick: () -> Unit,
    tasksList: List<CollabTask?>,
    onCheckedChange: (CollabTask) -> Unit,
    onEditClick: (CollabTask) -> Unit,
    onDeleteClick: (CollabTask) -> Unit,
    userAccount: AuthenticatedUserData?
) {
    val state by collaborationViewModel.userCollab.collectAsStateWithLifecycle()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = CardDefaults.outlinedShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy, // Adjust bounciness
                        stiffness = Spring.StiffnessMediumLow // Adjust stiffness
                    )
                )
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(0, 0, 20, 20)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label, textAlign = TextAlign.Left,
                    modifier = Modifier
                        .padding(10.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.weight(1f))

            }
            if (tasksList.isNotEmpty()) {
                AnimatedVisibility(
                    true,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {

                    LazyColumn(
                        modifier = Modifier.padding(5.dp)
                    ) {
                        items(items = tasksList, key = { task -> task?.id ?: -1 }) { task ->

                            CollaborationTodoCard(
                                onClick = onClick,
                                onCheckedChange = onCheckedChange,
                                onDeleteClick = onDeleteClick,
                                onEditClick = onEditClick,
                                task = task ?: CollabTask(),
                                userAccount = userAccount,
                                creator = task!!.assignedTo,
                                admins = state.currentCollab!!.admins.map { it?.get("id") as String },
                                completedTasks = completedTasks,
                                uncompletedTasks = uncompletedTasks
                            )
                        }
                    }
                }
            } else {
                if (isEmpty) {
                    AnimatedVisibility(
                        true,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(200.dp)
                                        .padding(20.dp)
                                )
                                Text(
                                    text = "No ${
                                        label.replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase(
                                                Locale.getDefault()
                                            ) else it.toString()
                                        }
                                    } Tasks",
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CollaborationTodoCard(
    completedTasks: List<CollabTask?>,
    uncompletedTasks: List<CollabTask?>,
    creator: Map<String, String?>,
    admins: List<String?>,
    onClick: () -> Unit,
    userAccount: AuthenticatedUserData?,
    task: CollabTask,
    onCheckedChange: (CollabTask) -> Unit,
    onEditClick: (CollabTask) -> Unit,
    onDeleteClick: (CollabTask) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val hasAccess = (userAccount?.userId in admins || creator["id"] == userAccount?.userId)
    val isAdmin = creator["id"] in admins

    Card(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20))
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = if (userAccount?.userId == creator["id"]) {
                Color(0xB450008B)
            } else {
                if (task in completedTasks) {
                    Color(0xFF197900)
                } else {
                    CardDefaults.cardColors().containerColor
                }
            }
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .animateContentSize()
        ) {
            // Top Section (Same as Collapsed State)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context = context)
                            .data(creator["profilePic"])
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = "Creator Profile Picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = if (userAccount?.userId == (creator["id"] as String)) "You" else (creator["username"] as String),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isAdmin) "admin" else "member",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Column {
                    Text(text = task.title, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = task.date, fontSize = 12.sp, color = Color.LightGray)
                }
            }

            AnimatedVisibility(expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    Text(text = task.description, fontSize = 14.sp)
                    if (task.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Description: ${task.description}",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    } else {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Description: No description!",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Bottom Section (Access Controls)
                    if (hasAccess) {
                        Row {
                            Button(onClick = { onCheckedChange(task) }) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (task in completedTasks) Icons.Filled.RemoveDone else Icons.Filled.TaskAlt,
                                        contentDescription = "Check"
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { onDeleteClick(task) }) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete"
                                    )
                                    Text(" Delete")
                                }
                            }
                        }
                    }
                }

            }
        }
    }

}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "No Tasks", group = "")
@Composable
fun PreviewCollaborationScreen() {
    ToDoListTheme {
        Surface {
            CollaborationTodoCard(
                onCheckedChange = {},
                onEditClick = {},
                onDeleteClick = { CollabTask() },
                task = CollabTask(),
                onClick = {},
                userAccount = AuthenticatedUserData("", "", "", "", ""),
                creator = mapOf(),
                admins = emptyList(),
                completedTasks = listOf(),
                uncompletedTasks = listOf()
            )
        }
    }
}


