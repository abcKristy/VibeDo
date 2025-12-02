package com.example.vibedo.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.vibedo.view.navigation.MainNavGraph
import com.example.vibedo.view.theme.VibeDoTheme
import com.example.vibedo.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VibeDoTheme {
                val navController = rememberNavController()

                MainNavGraph(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}