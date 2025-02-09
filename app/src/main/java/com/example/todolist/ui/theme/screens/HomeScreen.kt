package com.example.todolist.ui.theme.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.compose.ToDoListTheme
import com.example.todolist.R
import com.example.todolist.presentation.sign_in.AuthenticatedUserData
import com.example.todolist.ui.theme.navigation.NavRoutes
import com.example.todolist.ui.theme.viewmodels.HomeScreenViewModel
import com.example.todolist.ui.theme.viewmodels.LoadingControl
import com.example.todolist.ui.theme.viewmodels.TaskUiState
import com.example.todolist.ui.theme.viewmodels.UserAccountViewModel
import com.example.todolist.utils.AddTaskDialog
import com.example.todolist.utils.BottomNavigation
import com.example.todolist.utils.GoogleButtonIcon
import com.example.todolist.utils.getFormattedDate
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.util.Locale


@SuppressLint("ContextCastToActivity")
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    snackBarHost: SnackbarHostState,
    snackBarScope: CoroutineScope,
    onClick: () -> Unit,
    viewModel: HomeScreenViewModel,
    userAccountViewModel: UserAccountViewModel,
    onGFCClicked: () -> Unit,
    isConnected: Boolean
) {
    val activity = LocalContext.current as Activity

    BackHandler {
        if (navHostController.previousBackStackEntry?.destination?.route == NavRoutes.Start.route) {
            activity.finish()
        } else {
            navHostController.popBackStack()
        }
    }
    val loadingState by viewModel.isLoading.collectAsStateWithLifecycle()
    when (loadingState) {
        is LoadingControl.FetchingData -> {
            FetchingDataScreen()
        }

        is LoadingControl.Success -> {
            SuccessHomeScreen(
                homeScreenViewModel = viewModel,
                snackBarScope = snackBarScope,
                snackBarHost = snackBarHost,
                onClick = onClick,
                onGoogleFapClicked = onGFCClicked,
                userAccountViewModel = userAccountViewModel,
                isConnected = isConnected
            )
        }

        is LoadingControl.Error -> {}
    }
}


@OptIn(ExperimentalAnimationApi::class, ExperimentalPagerApi::class)
@Composable
fun SuccessHomeScreen(
    modifier: Modifier = Modifier,
    homeScreenViewModel: HomeScreenViewModel,
    userAccountViewModel: UserAccountViewModel,
    snackBarHost: SnackbarHostState,
    snackBarScope: CoroutineScope,
    onClick: () -> Unit,
    onGoogleFapClicked: () -> Unit,
    isConnected: Boolean
) {

    val context = LocalContext.current
    val homeUiState by homeScreenViewModel.homeUiState.collectAsState()
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
                if (userAccount == null) {
                    GoogleButtonIcon(
                        size = 50,
                        onClick = {
                            onGoogleFapClicked()
                        },
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(bottom = 10.dp)
                    )
                }
                AddTaskFAB(onClick = {
                    isEdit = false
                    task = null
                    showDialog = true
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
            val completedTasks = homeUiState.tasks.filter { taskUiState ->
                taskUiState?.isCompleted == true
            }
            val uncompletedTasks = homeUiState.tasks.filter { taskUiState ->
                taskUiState?.isCompleted == false
            }

            Box {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {

                    TasksList(
                        userAccount = userAccount,
                        isEmpty = if (pageIndex == 1) completedTasks.isEmpty() else uncompletedTasks.isEmpty(),
                        label = if (pageIndex == 1) "Completed" else "Uncompleted",
                        tasksList = if (pageIndex == 1) completedTasks else uncompletedTasks,
                        onCheckedChange = {
                            scope.launch {
                                if (userAccount != null) {
                                    homeScreenViewModel.updateTask(
                                        isConnected = isConnected,
                                        task = it.copy(isCompleted = !it.isCompleted),
                                        userId = userAccount!!.userId,
                                        onError = { error ->
                                            Toast.makeText(
                                                context,
                                                error,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    )
                                } else {
                                    homeScreenViewModel.updateOfflineTask(it.copy(isCompleted = !it.isCompleted))
                                }
                            }


                        },
                        onClick = onClick,
                        onEditClick = {
                            isEdit = true
                            task = it
                            showDialog = true
                            title = it.title
                            description = it.description
                        },
                        onDeleteClick = {
                            scope.launch {
                                if (userAccount != null) {
                                    homeScreenViewModel.deleteTask(
                                        isConnected = isConnected,
                                        task = it,
                                        userId = userAccount!!.userId,
                                        onError = { error ->
                                            Toast.makeText(
                                                context,
                                                error,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    )
                                } else {
                                    homeScreenViewModel.deleteOfflineTask(it)
                                }


                            }
                        },
                        onSyncClick = { task ->

                            homeScreenViewModel.updateTask(
                                task,
                                userAccount!!.userId,
                                { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                },
                                isConnected

                            )

                        }
                    )


                }
            }


            if (showDialog) {
                AddTaskDialog(
                    isEdit = isEdit,
                    task = task,
                    title = title,
                    onTitleChange = { title = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    onDismiss = {
                        showDialog = false
                        title = ""
                        description = ""
                    },
                    onSave = {
                        showDialog = false
                        if (!isEdit) {
                            scope.launch {
                                if (userAccount != null) {
                                    homeScreenViewModel.addTask(
                                        isConnected = isConnected,

                                        task = it,
                                        userId = userAccount!!.userId,
                                        onError = { error ->
                                            Toast.makeText(
                                                context,
                                                error,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    )
                                } else {
                                    homeScreenViewModel.addOfflineTask(it)
                                }
                                pagerState.scrollToPage(0)
                            }
                        } else {
                            scope.launch {
                                if (userAccount != null) {
                                    homeScreenViewModel.updateTask(
                                        isConnected = isConnected,
                                        userId = userAccount!!.userId,
                                        task = it,
                                        onError = { error ->
                                            Toast.makeText(
                                                context,
                                                error,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    )
                                } else {
                                    homeScreenViewModel.updateOfflineTask(it)
                                }
                            }
                        }
                        title = ""
                        description = ""
                    }

                )
            }
        }
    }
}


@Composable
fun AddTaskFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
        containerColor = Color(0xFF610FF4),
        contentColor = Color.White
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null)
    }
}


@Composable
fun FetchingDataScreen(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = modifier,
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
                text = "Getting Data Please Wait",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}


@Composable
fun TaskColumn(
    title: String,
    tasks: List<TaskUiState?>,
    backgroundColor: Color,
    homeScreenViewModel: HomeScreenViewModel,
    userAccount: AuthenticatedUserData?,
    scope: CoroutineScope,
    isConnected: Boolean,
    onClick: () -> Unit,
    snackBarHost: SnackbarHostState,
    snackBarScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        TasksList(
            userAccount = userAccount,
            isEmpty = tasks.isEmpty(),
            label = title,
            tasksList = tasks,
            onCheckedChange = {
                scope.launch {
                    homeScreenViewModel.updateTask(
                        isConnected = isConnected,
                        task = it.copy(isCompleted = !it.isCompleted),
                        userId = userAccount!!.userId,
                        onError = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            },
            onClick = onClick,
            onEditClick = {
                // Handle edit logic
            },
            onDeleteClick = {
                scope.launch {
                    homeScreenViewModel.deleteTask(
                        isConnected = isConnected,
                        task = it,
                        userId = userAccount!!.userId,
                        onError = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    )
                    snackBarScope.launch {
                        snackBarHost.showSnackbar(
                            message = "\"${it.title}\" Deleted Successfully!",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            },
            onSyncClick = { task ->
                homeScreenViewModel.updateTask(
                    task,
                    userAccount!!.userId,
                    { error -> Toast.makeText(context, error, Toast.LENGTH_SHORT).show() },
                    isConnected
                )
            }
        )
    }
}


@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun TasksList(
    label: String,
    onSyncClick: (TaskUiState) -> Unit,
    isEmpty: Boolean = false,
    onClick: () -> Unit,
    tasksList: List<TaskUiState?>,
    onCheckedChange: (TaskUiState) -> Unit,
    onEditClick: (TaskUiState) -> Unit,
    onDeleteClick: (TaskUiState) -> Unit,
    modifier: Modifier = Modifier,
    userAccount: AuthenticatedUserData?
) {
    var isVisible by rememberSaveable { mutableStateOf(true) }
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
                    .clip(RoundedCornerShape(0, 0, 20, 20))
                    .clickable(
                        onClick = { isVisible = !isVisible }
                    ),
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
                val animatedIcon = animateFloatAsState(
                    targetValue = if (isVisible) 0f else -90f
                )
                IconButton(
                    modifier = Modifier
                        .padding(10.dp),
                    onClick = { isVisible = !isVisible }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(animatedIcon.value),
                        )
                    }


                }
            }
            if (tasksList.isNotEmpty()) {
                AnimatedVisibility(
                    isVisible,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {

                    LazyColumn(
                        modifier = Modifier.padding(5.dp)
                    ) {
                        items(items = tasksList, key = { task -> task?.id ?: -1 }) { task ->
                            TodoCard(
                                onClick = onClick,
                                onCheckedChange = { onCheckedChange(task ?: TaskUiState()) },
                                onDeleteClick = onDeleteClick,
                                onEditClick = onEditClick,
                                task = task ?: TaskUiState(),
                                onSyncClick = onSyncClick,
                                userAccount = userAccount
                            )
                        }
                    }
                }
            } else {
                if (isEmpty) {
                    AnimatedVisibility(
                        isVisible,
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
fun TodoCard(
    onClick: () -> Unit,
    userAccount: AuthenticatedUserData?,
    task: TaskUiState,
    onSyncClick: (TaskUiState) -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    onEditClick: (TaskUiState) -> Unit,
    onDeleteClick: (TaskUiState) -> Unit,
    modifier: Modifier = Modifier
) {
    val completed = SwipeAction(
        onSwipe = { onCheckedChange(!task.isCompleted) },
        icon = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.White,

                    )
                Spacer(Modifier.padding(3.dp))
                Text(
                    text = if (task.isCompleted) "Undo" else "Complete",
                    color = Color.White
                )
            }
        },
        background = if (!task.isCompleted) Color(0xFF4CD453) else Color(0xFFD51A1A)
    )

    val delete = SwipeAction(
        onSwipe = { onDeleteClick(task) },
        icon = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.White,

                    )
                Spacer(Modifier.padding(3.dp))
                Text(
                    text = "Delete",
                    color = Color.White
                )
            }
        },
        background = Color(0xFFD51A1A)
    )
    SwipeableActionsBox(
        swipeThreshold = 100.dp,
        endActions = listOf(delete),
        modifier = Modifier.clip(RoundedCornerShape(20)),
        startActions = listOf(completed)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox for marking as complete
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Task title
                    Text(
                        text = if (task.description != "") task.description else "No description",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Task description
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (task.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val date = if (task.date == getFormattedDate()) "Today" else task.date
                    // Date and time
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Action buttons
                Row(horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = { onEditClick(task) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Task"
                        )
                    }
                    IconButton(onClick = { onDeleteClick(task) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Task"
                        )
                    }
                    AnimatedVisibility((!task.isSynced && userAccount != null)) {
                        IconButton(onClick = { onSyncClick(task) }) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Delete Task",
                                tint = Color.Red
                            )

                        }
                    }
                }
            }
        }
    }

}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "No Tasks", group = "")
@Composable
fun PreviewNoTasks() {
    ToDoListTheme {
        Surface {
            TodoCard(
                onCheckedChange = {},
                onEditClick = {},
                onDeleteClick = { TaskUiState() },
                task = TaskUiState(),
                onClick = {},
                onSyncClick = {},
                userAccount = AuthenticatedUserData("", "", "", "", "")
            )
        }
    }
}


