package com.example.vibedo.view.navigation

sealed class NavigationRoute(val route: String) {
    object Main : NavigationRoute("main")
    object AddTask : NavigationRoute("add_task")

    companion object {
        fun fromRoute(route: String?): NavigationRoute {
            return when(route?.substringBefore("/")) {
                "main" -> Main
                "add_task" -> AddTask
                null -> Main
                else -> throw IllegalArgumentException("Route $route is not recognized")
            }
        }
    }
}