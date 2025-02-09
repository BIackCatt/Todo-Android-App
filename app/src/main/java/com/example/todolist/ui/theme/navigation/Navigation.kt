package com.example.todolist.ui.theme.navigation


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FileDownloadDone
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todolist.data.network.toMemberData
import com.example.todolist.notifications.TasksNotificationsManager
import com.example.todolist.ui.theme.screens.CollaborationScreen
import com.example.todolist.ui.theme.screens.HomeScreen
import com.example.todolist.ui.theme.screens.SplashScreen
import com.example.todolist.ui.theme.viewmodels.AppViewModelsProvider
import com.example.todolist.ui.theme.viewmodels.CollaborationViewModel
import com.example.todolist.ui.theme.viewmodels.HomeScreenViewModel
import com.example.todolist.ui.theme.viewmodels.SnackBarViewModel
import com.example.todolist.ui.theme.viewmodels.UserAccountViewModel
import com.example.todolist.utils.CoilImageFormatter
import com.example.todolist.utils.CollaborationsTopAppBar
import com.example.todolist.utils.FloatingNotificationBar
import com.example.todolist.utils.MembersDialog
import com.example.todolist.utils.ModernProfileDialog
import com.example.todolist.utils.ModernSignInDialog
import com.example.todolist.utils.ModernTopAppBar
import com.example.todolist.utils.OperationsDialog
import com.example.todolist.utils.TodoApp
import kotlinx.coroutines.launch

const val DATABASE = "Tasks_database"

@Composable
fun TodoAppEntry(
    notificationsManager: TasksNotificationsManager,
) {
    val navController = rememberNavController()
    NavigationGraph(
        notificationsManager = notificationsManager,
        navHostController = navController,
    )
}

@SuppressLint("StateFlowValueCalledInComposition", "MutableCollectionMutableState", "ContextCastToActivity")
@Composable
fun NavigationGraph(
    notificationsManager: TasksNotificationsManager,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: UserAccountViewModel = viewModel<UserAccountViewModel>(
        factory = AppViewModelsProvider.Factory
    ),
    homeViewModel: HomeScreenViewModel = viewModel(factory = AppViewModelsProvider.Factory),
    collabsViewModel: CollaborationViewModel = viewModel(factory = AppViewModelsProvider.Factory),
    snackBarViewModel: SnackBarViewModel = viewModel()
) {

    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    val appSyncState by homeViewModel.appSyncState.collectAsStateWithLifecycle()
    val dataImported by homeViewModel.dataImported.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val userAccount by viewModel.userData.collectAsStateWithLifecycle()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val collabs by collabsViewModel.userCollab.collectAsStateWithLifecycle()
    var start by rememberSaveable { mutableStateOf(true) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val snackBarState by snackBarViewModel.currentMessage.collectAsStateWithLifecycle()
    val snackBarIsVisible by snackBarViewModel.isSnackbarVisible.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()



    LaunchedEffect(userAccount) {
        if (userAccount != null) {
            homeViewModel.updateDataSource(true)
        } else {
            homeViewModel.updateDataSource(false)
        }
    }
    state.signInErrorMessage?.let { error ->
        scope.launch {
            snackBarViewModel.show(
                message = "Something went wrong try again later",
                icon = Icons.Filled.Error,
            )
        }
        viewModel.resetState()
    }

    // Handle successful sign-in and data import
    if (state.isSignInSuccessful && !start && !dataImported) {
        // Ensures it runs once when sign-in is successful
        homeViewModel.importData(
            userId = userAccount?.userId,
            onSuccessAction = {
                scope.launch {
                    snackBarViewModel.show(
                        message = "Fetching data succeeded",
                        icon = Icons.Filled.DoneAll,
                    )
                }
            },
            onErrorAction = { error ->
                scope.launch {
                    snackBarViewModel.show(
                        message = "Fetching data failed",
                        icon = Icons.Filled.Error,
                    )
                }
            },
            isConnected = isConnected
        )
        collabsViewModel.getUserCollabs(
            userAccount?.userId,
            onError = {
                scope.launch {
                    snackBarViewModel.show(
                        message = it,
                        icon = Icons.Filled.Error,
                        duration = 3000
                    )
                }
            },
            onSuccess = {
                viewModel.updateUserCollabs(it)
                viewModel.updateFirestoreUserData { }
            },
            onTaskChange = {
                scope.launch {
                    snackBarViewModel.show(
                        message = it,
                        icon = Icons.Filled.Notifications,
                    )
                }
            }
        )
        homeViewModel.updateDataImported(true)
    }

    val snackBarHostState = remember {
        SnackbarHostState()
    }
    var showProfileDialog by rememberSaveable { mutableStateOf(false) }
    var showNotSignedInDialog by rememberSaveable { mutableStateOf(false) }
    var showMembersDialog by rememberSaveable { mutableStateOf(false) }
    var showOperationsDialog by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                scope.launch {
                    viewModel.onSignInResult(result.data ?: return@launch)
                }
            }
        }
    )

    TodoApp(
        collabs = collabs,
        isConnected = isConnected,
        isSignedIn = userAccount != null,
        screen = navBackStackEntry,
        onCollaborationSave = {
            scope.launch {
                drawerState.close()
            }
            if (isConnected) {
                collabsViewModel.addCollaboration(
                    userData = userAccount!!,
                    onIsExist = {
                        scope.launch {
                            snackBarViewModel.show(
                                message = "Collaboration already exists",
                                duration = 3000,
                                icon = Icons.Filled.Close,
                            )
                        }
                    },
                    onError = {
                        scope.launch {
                            snackBarViewModel.show(
                                message = it ?: "Unknown Error",
                                duration = 3000,
                                icon = Icons.Filled.Error,
                            )
                        }
                    },
                    collab = it,
                    onSuccess = {
                        scope.launch {
                            snackBarViewModel.show(
                                message = "Collab Created Successfully",
                                duration = 3000,
                                icon = Icons.Filled.Create,
                            )
                        }
                    }
                )
            } else {
                scope.launch {
                    snackBarViewModel.show(
                        message = "No internet connection",
                        icon = Icons.Filled.WifiOff,
                    )
                }
            }
        },
        onHomeClick = {
            scope.launch {
                drawerState.close()
            }
            navHostController.navigate(NavRoutes.Home.route)
        },
        drawerState = drawerState,
        onCollaborationClick = {
            scope.launch {
                drawerState.close()
                collabsViewModel.updateCurrentCollab(it)
                navHostController.navigate(NavRoutes.Collaboration.route)
            }
        },
        onJoin = {
            scope.launch {
                drawerState.close()
            }
            if (isConnected) {
                collabsViewModel.joinCollab(
                    userId = userAccount?.userId,
                    username = it[0],
                    password = it[1],
                    onError = {
                        scope.launch {
                            snackBarViewModel.show(
                                it,
                                duration = 3000,
                                icon = Icons.Filled.Error,
                            )
                        }
                    },
                    onSuccess = {
                        scope.launch {
                            snackBarViewModel.show(
                                message = "Joined Collab Successfully",
                                icon = Icons.Filled.DoneAll,
                                duration = 3000,
                            )
                        }
                    },
                    userData = userAccount
                )
            } else {
                scope.launch {
                    snackBarViewModel.show(
                        message = "No internet connection",
                        icon = Icons.Filled.WifiOff,
                    )
                }
            }
        }
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackBarHostState) { data ->
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Snackbar(
                            shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp),
                            containerColor = MaterialTheme.colorScheme.onSurface,
                            contentColor = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center // Center the text
                            ) {
                                Text(
                                    text = data.visuals.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            },
            topBar = {
                if (!start) {
                    if (currentRoute == NavRoutes.Collaboration.route) {
                        CollaborationsTopAppBar(
                            showDriveButtons = state.isSignInSuccessful,
                            icon = {
                                IconButton(
                                    onClick = {
                                        showMembersDialog = true
                                    },
                                    modifier = it.background(MaterialTheme.colorScheme.onBackground)
                                        .padding(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Group,
                                        contentDescription = "Group",
                                        tint = MaterialTheme.colorScheme.surface
                                    )
                                }
                            },
                            context = context,
                            onIconClick = {},
                            title = collabs.currentCollab?.username ?: "Collab",
                            onModelDrawerClicked = {
                                scope.launch {
                                    drawerState.open()
                                }
                            },
                            drawerState = drawerState,
                            onImportClick = {
                                showOperationsDialog = true
                            },
                            isSync = true,
                            isConnected = isConnected,
                        )
                    } else if (currentRoute == NavRoutes.Home.route) {
                        ModernTopAppBar(
                            showDriveButtons = state.isSignInSuccessful,
                            title = userAccount?.username ?: "ToDo App",
                            icon = {
                                CoilImageFormatter.ProfileImage(
                                    onNotSignedClick = {
                                        showNotSignedInDialog = true
                                    },
                                    image = userAccount?.profilePictureUrl ?: "",
                                    modifier = it,
                                    onClick = {
                                        showProfileDialog = true
                                    }
                                )
                            },
                            onImportClick = {
                                scope.launch {
                                    homeViewModel.importData(
                                        userId = userAccount?.userId,
                                        onErrorAction = {
                                            scope.launch {
                                                snackBarViewModel.show(
                                                    message = it ?: "Unknown Error",
                                                    duration = 3000,
                                                    icon = Icons.Filled.Error,
                                                )
                                            }
                                        },
                                        onSuccessAction = {
                                            scope.launch {
                                                snackBarViewModel.show(
                                                    message = "Data Fetched Successfully",
                                                    duration = 3000,
                                                    icon = Icons.Filled.FileDownloadDone,
                                                )
                                            }
                                        },
                                        isConnected = isConnected
                                    )
                                }
                            },
                            isSync = appSyncState.isSynced,
                            onIconClick = {
                                showProfileDialog = true
                            },
                            context = context,
                            isConnected = isConnected,
                            drawerState = drawerState,
                            onModelDrawerClicked = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {
                FloatingNotificationBar(
                    message = snackBarState?.message ?: "Notification",
                    isVisible = snackBarIsVisible,
                    onDismiss = {
                        snackBarViewModel.dismissCurrentMessage()
                    },
                    icon = snackBarState?.icon ?: Icons.Filled.Notifications,
                    modifier = Modifier
                        .padding(20.dp)
                        .clip(RoundedCornerShape(20))
                )
            }
        ) { innerPadding ->
            NavHost(
                modifier = modifier.padding(top = innerPadding.calculateTopPadding()),
                navController = navHostController,
                startDestination = NavRoutes.Start.route,
            ) {
                composable(NavRoutes.Home.route) {
                    HomeScreen(
                        navHostController = navHostController,
                        snackBarHost = snackBarHostState,
                        snackBarScope = scope,
                        onClick = { notificationsManager.groupSummary() },
                        viewModel = homeViewModel,
                        onGFCClicked = { showNotSignedInDialog = true },
                        userAccountViewModel = viewModel,
                        isConnected = isConnected
                    )
                }
                composable(
                    route = NavRoutes.Collaboration.route,
                    arguments = listOf(navArgument(name = "id") { type = NavType.StringType })
                ) {
                    CollaborationScreen(
                        navHostController = navHostController,
                        collaborationViewModel = collabsViewModel,
                        onClick = {},
                        userAccountViewModel = viewModel,
                        isConnected = isConnected,
                        snackBarViewModel = snackBarViewModel
                    )
                }
                composable(
                    route = NavRoutes.Start.route
                ) {
                    SplashScreen(
                        onLoadingComplete = {
                            start = false
                            navHostController.navigate(route = NavRoutes.Home.route)
                        },
                        homeViewModel = homeViewModel,
                        userAccountViewModel = viewModel,
                        isConnected = isConnected
                    )
                }
            }

            if (showProfileDialog) {
                ModernProfileDialog(
                    fullName = userAccount?.username ?: "",
                    email = userAccount?.email ?: "",
                    onSignOut = {
                        scope.launch {
                            navHostController.navigate(NavRoutes.Home.route)
                            viewModel.signOut {
                                scope.launch {
                                    homeViewModel.clearTasksList()
                                }
                            }
                            Log.d("SignOut", "Signed out successfully $userAccount")
                            collabsViewModel.signOut()
                            homeViewModel.updateDataImported(false)
                            showProfileDialog = false
                            snackBarViewModel.show(
                                message = "Signed out successfully!",
                                icon = Icons.Filled.CloudDone,
                            )
                        }
                    },
                    onCancel = {
                        showProfileDialog = false
                    }
                )
            }
            if (showNotSignedInDialog) {
                ModernSignInDialog(
                    onDismiss = {
                        showNotSignedInDialog = false
                    },
                    onSignIn = {
                        if (isConnected) {
                            scope.launch {
                                val signInIntent = viewModel.signinIntent()
                                launcher.launch(
                                    signInIntent
                                )
                                showNotSignedInDialog = false
                            }
                        } else {
                            scope.launch {
                                snackBarViewModel.show(
                                    "No Internet Connection sign in failed",
                                    icon = Icons.Filled.WifiOff
                                )
                            }
                        }
                    }
                )
            }
            if (showMembersDialog) {
                MembersDialog(
                    members = collabs.currentCollab!!.members,
                    currentUser = userAccount!!.toMemberData(),
                    admins = collabs.currentCollab!!.admins,
                    onPromoteMember = { id, username ->
                        if (isConnected) {
                            collabsViewModel.promoteUser(
                                user = userAccount,
                                memberUsername = username,
                                memberId = id,
                                collab = collabs.currentCollab!!,
                                onError = {
                                    scope.launch {
                                        snackBarViewModel.show(
                                            it,
                                            icon = Icons.Filled.Error
                                        )
                                    }
                                },
                                onSuccess = {
                                    scope.launch {
                                        snackBarViewModel.show(
                                            "Member promoted successfully",
                                            icon = Icons.Filled.DoneAll
                                        )
                                    }
                                }
                            )
                        } else {
                            scope.launch {
                                snackBarViewModel.show(
                                    message = "No internet connection",
                                    icon = Icons.Filled.WifiOff,
                                )
                            }
                        }
                        showMembersDialog = false
                    },
                    onRemoveMember = {id, username ->
                        if (isConnected) {
                            collabsViewModel.removeMember(
                                user = userAccount,
                                collab = collabs.currentCollab!!,
                                memberId = id,
                                memberUsername = username,
                                onSuccess = {
                                    scope.launch {
                                        snackBarViewModel.show(
                                            message = "Member removed successfully",
                                            icon = Icons.Filled.PersonOff,
                                        )
                                    }
                                },
                                onError = {
                                    scope.launch {
                                        snackBarViewModel.show(
                                            message = "Something went error",
                                            icon = Icons.Filled.Error,
                                        )
                                    }
                                }
                            )
                        } else {
                            scope.launch {
                                snackBarViewModel.show(
                                    message = "No internet connection",
                                    icon = Icons.Filled.WifiOff,
                                )
                            }
                        }
                    },
                    onExit = {
                        if (isConnected) {
                            collabsViewModel.exitCollab(
                                user = userAccount,
                                collab = collabs.currentCollab!!,
                                onError = {
                                    scope.launch {
                                        snackBarViewModel.show(
                                            it,
                                            icon = Icons.Filled.Error
                                        )
                                    }
                                },
                                onSuccess = {
                                    navHostController.navigate(NavRoutes.Home.route)
                                }
                            )
                        } else {
                            scope.launch {
                                snackBarViewModel.show(
                                    message = "No internet connection",
                                    icon = Icons.Filled.WifiOff,
                                )
                            }
                        }
                        showMembersDialog = false

                    },
                    onDismiss = { showMembersDialog = false }
                )
            }
            if (showOperationsDialog) {
                OperationsDialog(
                    isAdmin = collabs.currentCollab?.admins?.contains(userAccount?.toMemberData()) == true,
                    operations = collabs.currentCollab!!.operations,
                    onDismiss = {
                        showOperationsDialog = false
                    },
                    onClearClick = {
                        if (isConnected) {
                            collabsViewModel.clearOperationsHistory(
                                collabId = collabs.currentCollab!!.id,
                                onSuccess = {
                                    scope.launch {
                                        snackBarViewModel.show(
                                            message = "History cleared successfully",
                                            icon = Icons.Filled.ClearAll,
                                        )
                                    }
                                }, onError = {
                                    scope.launch {
                                        snackBarViewModel.show(
                                            message = "Something went wrong",
                                            icon = Icons.Filled.Error,
                                        )
                                    }
                                }
                            )
                        } else {
                            scope.launch {
                                snackBarViewModel.show(
                                    message = "No internet connection",
                                    icon = Icons.Filled.WifiOff,
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}





