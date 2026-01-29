package com.example.veritasgenerationministry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.veritas.ui.screens.AdminDashboard
import com.example.veritas.ui.screens.CategoryDetailScreen
import com.example.veritas.ui.screens.HomeScreen
import com.example.veritas.ui.screens.LoginScreen
import com.example.veritas.ui.screens.RegistrationScreen
import com.example.veritas.ui.screens.SplashScreen
import com.example.veritas.ui.theme.VeritasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VeritasTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
                    composable("splash") {
                        SplashScreen(navController)
                    }
                    composable("registration") {
                        RegistrationScreen(navController)
                    }
                    composable("login") {
                        LoginScreen(navController)
                    }
                    composable("home") {
                        HomeScreen(navController)
                    }
                    // FIXED: Changed from HomeScreen to AdminDashboard
                    composable("admindashboard") {
                        AdminDashboard(navController)
                    }

                    // Category detail route for PDF, AUDIO, IMAGE content
                    composable("category_detail/{categoryName}/{categoryType}") { backStackEntry ->
                        val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
                        val categoryType = backStackEntry.arguments?.getString("categoryType") ?: ""
                        CategoryDetailScreen(navController, categoryName, categoryType)
                    }
                }
            }
        }
    }
}