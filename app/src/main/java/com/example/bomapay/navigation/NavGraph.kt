package com.example.bomapay.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bomapay.ui.screens.shared.SplashScreen
import com.example.bomapay.ui.screens.shared.LoginScreen
import com.example.bomapay.ui.screens.shared.RegisterScreen
import com.example.bomapay.ui.screens.shared.ForgotPasswordScreen
import com.example.bomapay.ui.screens.tenant.TenantDashboard
import com.example.bomapay.ui.screens.landlord.LandlordDashboard

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object TenantHome : Screen("tenant_home")
    object LandlordHome : Screen("landlord_home")
}

@Composable
fun BomaPayNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.ForgotPassword.route) { ForgotPasswordScreen(navController) }
        composable(Screen.TenantHome.route) { TenantDashboard(navController) }
        composable(Screen.LandlordHome.route) { LandlordDashboard(navController) }
    }
}