package com.example.lab8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.lab8.data.local.AppDatabase
import com.example.lab8.navigation.AppNavigation
import com.example.lab8.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database
        database = AppDatabase.getInstance(applicationContext)

        enableEdgeToEdge()
        setContent {
            var darkTheme by remember { mutableStateOf(false) }

            AppTheme(darkTheme = darkTheme) {
                AppNavigation(
                    database = database,
                    darkTheme = darkTheme,
                    onThemeToggle = { darkTheme = !darkTheme }
                )
            }
        }
    }
}