package com.example.vibedo.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vibedo.view.screens.AddTaskScreen
import com.example.vibedo.view.screens.MainScreen
import com.example.vibedo.viewmodel.TaskViewModel

@Composable
fun MainNavGraph(
    navController: NavHostController,
    viewModel: TaskViewModel
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Main.route
    ) {
        composable(NavigationRoute.Main.route) {
            MainScreen(
                viewModel = viewModel,
                onNavigateToAddTask = {
                    navController.navigate(NavigationRoute.AddTask.route)
                }
            )
        }

        composable(NavigationRoute.AddTask.route) {
            AddTaskScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}