package com.example.todolist.ui.theme.navigation

sealed class NavRoutes(val route: String) {
    data object Home: NavRoutes("Home")
    data object Start: NavRoutes("AppStart")
    data object Collaboration: NavRoutes("Collaboration/{id}") {
        fun getRoute(id: String): String = "Collaboration/$id"
    }
}