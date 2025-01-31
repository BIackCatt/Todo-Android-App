package com.example.todolist.ui.theme.navigation

sealed class NavRoutes(val route: String) {
    data object Home: NavRoutes("Home")
    data object Details: NavRoutes("details/{id}") {
        fun createRoute(id: Int) = "details/$id"
    }
    data object Start: NavRoutes("AppStart")
}