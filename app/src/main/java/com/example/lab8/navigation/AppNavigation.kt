package com.example.lab8.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab8.data.local.AppDatabase
import com.example.lab8.views.DetailScreen
import com.example.lab8.views.HomeScreen
import com.example.lab8.views.ProfileScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Detail : Screen("detail/{photoId}") {
        fun createRoute(photoId: Long) = "detail/$photoId"
    }
}

@Composable
fun AppNavigation(
    database: AppDatabase,
    darkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                database = database,
                onPhotoClick = { photoId ->
                    navController.navigate(Screen.Detail.createRoute(photoId))
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                database = database,
                onBackClick = { navController.popBackStack() },
                onPhotoClick = { photoId ->
                    navController.navigate(Screen.Detail.createRoute(photoId))
                },
                darkTheme = darkTheme,
                onThemeToggle = onThemeToggle
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("photoId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getLong("photoId") ?: 0L
            DetailScreen(
                photoId = photoId,
                database = database,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}