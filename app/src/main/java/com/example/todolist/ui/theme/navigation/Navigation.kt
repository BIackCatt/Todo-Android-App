package com.example.todolist.ui.theme.navigation


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todolist.notifications.TasksNotificationsManager
import com.example.todolist.ui.theme.screens.HomeScreen
import com.example.todolist.ui.theme.screens.SplashScreen
import com.example.todolist.ui.theme.viewmodels.AppViewModelsProvider
import com.example.todolist.ui.theme.viewmodels.HomeScreenViewModel
import com.example.todolist.ui.theme.viewmodels.UserAccountViewModel
import com.example.todolist.utils.CoilImageFormatter
import com.example.todolist.utils.ModernProfileDialog
import com.example.todolist.utils.ModernSignInDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val DATABASE = "Tasks_database"

@Composable
fun TodoAppEntry(
    lifecycleScope: CoroutineScope,
    modifier: Modifier = Modifier,
    notificationsManager: TasksNotificationsManager,
) {
    val navController = rememberNavController()
    NavigationGraph(
        notificationsManager = notificationsManager,
        navHostController = navController,
    )
}

@SuppressLint(
    "StateFlowValueCalledInComposition", "MutableCollectionMutableState",
    "ContextCastToActivity"
)
@Composable
fun NavigationGraph(
    notificationsManager: TasksNotificationsManager,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: UserAccountViewModel = viewModel<UserAccountViewModel>(
        factory = AppViewModelsProvider.Factory
    ),
    homeViewModel: HomeScreenViewModel = viewModel(factory = AppViewModelsProvider.Factory)
) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appSyncState by homeViewModel.appSyncState.collectAsStateWithLifecycle()
    val userAccount by viewModel.userData.collectAsStateWithLifecycle()
    var start by rememberSaveable { mutableStateOf(true) }
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()

    LaunchedEffect(userAccount) {
        if (userAccount != null) {
            homeViewModel.updateDataSource(true)
        } else {
            homeViewModel.updateDataSource(false)
        }
    }

    state.signInErrorMessage?.let { error ->
        Toast.makeText(LocalContext.current, "SignIn: $error", Toast.LENGTH_LONG).show()
        viewModel.resetState()
    }

    // Handle successful sign-in and data import
    if (state.isSignInSuccessful && !start) {
        // Ensures it runs once when sign-in is successful
        homeViewModel.importData(
            userId = userAccount?.userId,
            onSuccessAction = {
            },
            onErrorAction = { error ->
                Toast.makeText(context, error ?: "Data import failed", Toast.LENGTH_LONG).show()
            },
            isConnected = isConnected
        )

    }

    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val snackBarScope = rememberCoroutineScope()
    var showProfileDialog by rememberSaveable { mutableStateOf(false) }
    var showNotSignedInDialog by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                snackBarScope.launch {
                    viewModel.onSignInResult(result.data ?: return@launch)
                }
            }
        }
    )

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
                        snackBarScope.launch {
                            homeViewModel.importData(
                                userId = userAccount?.userId,
                                onErrorAction = {
                                    snackBarScope.launch {
                                        snackBarHostState.showSnackbar(
                                            it ?: "Unknown Error",
                                            duration = SnackbarDuration.Indefinite,
                                            actionLabel = "ok",
                                            withDismissAction = true
                                        )
                                    }
                                },
                                onSuccessAction = {
                                    Toast.makeText(
                                        context,
                                        "Data fetched successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
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
                    isConnected = isConnected
                )
            }
        },

        ) { innerPadding ->
        BackHandler(enabled = navHostController.currentBackStackEntry?.destination?.route != NavRoutes.Home.route) {
            if (navHostController.currentBackStackEntry?.destination?.route == NavRoutes.Home.route) {
                activity.finish()
            } else {
                navHostController.popBackStack()
            }
        }
        NavHost(
            modifier = modifier.padding(top = innerPadding.calculateTopPadding()),
            navController = navHostController,
            startDestination = NavRoutes.Start.route,
        ) {
            composable(NavRoutes.Home.route) {
                HomeScreen(
                    navHostController = navHostController,
                    snackBarHost = snackBarHostState,
                    snackBarScope = snackBarScope,
                    onClick = { notificationsManager.groupSummary() },
                    viewModel = homeViewModel,
                    onGFCClicked = { showNotSignedInDialog = true },
                    userAccountViewModel = viewModel,
                    isConnected = isConnected
                )
            }
            composable(
                route = NavRoutes.Details.route,
                arguments = listOf(navArgument(name = "id") { type = NavType.IntType })
            ) {}
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
                    snackBarScope.launch {
                        viewModel.signOut()
                        Log.d("SignOut", "Signed out successfully $userAccount")
                        showProfileDialog = false
                        Toast.makeText(
                            context,
                            "Signed out successfully",
                            Toast.LENGTH_LONG
                        ).show()
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
                        snackBarScope.launch {
                            val signInIntent = viewModel.signinIntent()
                            launcher.launch(
                                signInIntent
                            )
                            showNotSignedInDialog = false
                        }
                    } else {
                        Toast.makeText(context, "No Internet Connection sign in failed", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTopAppBar(
    context: Context,
    onIconClick: () -> Unit,
    showDriveButtons: Boolean,
    title: String,
    navigationIcon: ImageVector? = null,
    onNavigationClick: () -> Unit = {},
    icon: @Composable (Modifier) -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    onImportClick: () -> Unit,
    isSync: Boolean,
    isConnected: Boolean,
) {
    var isImportEnabled by rememberSaveable { mutableStateOf(true) }
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
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

        navigationIcon = {
            if (navigationIcon != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = "Navigation Icon",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
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
}


@Composable
fun CustomSnackBarContainer(
    messages: List<String>,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        messages.forEach { message ->
            // Animate the appearance of each snackbar
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                CustomSnackBar(
                    message = message,
                    onDismiss = { onDismiss(message) },
                    modifier = modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}


@Composable
fun CustomSnackBar(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
