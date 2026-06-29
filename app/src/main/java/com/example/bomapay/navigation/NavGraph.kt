package com.example.bomapay.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.bomapay.ui.screens.landlord.AssignHouseScreen
import com.example.bomapay.ui.screens.landlord.IssueNoticeScreen
import com.example.bomapay.ui.screens.landlord.LandlordDashboard
import com.example.bomapay.ui.screens.shared.ForgotPasswordScreen
import com.example.bomapay.ui.screens.shared.LoginScreen
import com.example.bomapay.ui.screens.shared.RegisterScreen
import com.example.bomapay.ui.screens.shared.SplashScreen
import com.example.bomapay.ui.screens.tenant.TenantDashboard
import com.example.bomapay.ui.screens.tenant.TenantMaintenanceScreen
import com.example.bomapay.ui.screens.tenant.TenantPayScreen
import com.example.bomapay.ui.screens.tenant.TenantProfileScreen

object Graph {
    const val ROOT = "root_graph"
    const val SHARED = "shared_graph"
    const val TENANT = "tenant_graph"
    const val LANDLORD = "landlord_graph"
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")

    object TenantHome : Screen("tenant_home")
    object TenantPay : Screen("tenant_pay")
    object TenantMaintenance : Screen("tenant_maintenance")
    object TenantProfile : Screen("tenant_profile")

    object LandlordHome : Screen("landlord_home")
    object AssignHouse : Screen("assign_house")
    object IssueNotice : Screen("issue_notice")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.SHARED
    ) {
        // --- SHARED / AUTHENTICATION GRAPH ---
        navigation(route = Graph.SHARED, startDestination = Screen.Splash.route) {
            composable(route = Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(route = Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { userRole ->
                        navController.navigate(
                            if (userRole.equals("LANDLORD", ignoreCase = true)) Graph.LANDLORD else Graph.TENANT
                        ) {
                            popUpTo(route = Graph.SHARED) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(Screen.ForgotPassword.route)
                    }
                )
            }

            composable(route = Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(route = Screen.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }

            composable(route = Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    onNavigateBackToLogin = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // --- TENANT GRAPH ---
        navigation(route = Graph.TENANT, startDestination = Screen.TenantHome.route) {
            composable(route = Screen.TenantHome.route) {
                TenantDashboard(
                    onNavigateToPay = { navController.navigate(Screen.TenantPay.route) },
                    onNavigateToMaintenance = { navController.navigate(Screen.TenantMaintenance.route) },
                    onNavigateToProfile = { navController.navigate(Screen.TenantProfile.route) },
                    onLogout = { logoutAndReset(navController) }
                )
            }
            composable(route = Screen.TenantPay.route) {
                TenantPayScreen()
            }
            composable(route = Screen.TenantMaintenance.route) {
                TenantMaintenanceScreen()
            }
            composable(route = Screen.TenantProfile.route) {
                TenantProfileScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // --- LANDLORD GRAPH ---
        navigation(route = Graph.LANDLORD, startDestination = Screen.LandlordHome.route) {
            composable(route = Screen.LandlordHome.route) {
                LandlordDashboard(
                    onNavigateToAssign = { navController.navigate(Screen.AssignHouse.route) },
                    onNavigateToIssueNotice = { navController.navigate(Screen.IssueNotice.route) },
                    onLogout = { logoutAndReset(navController) }
                )
            }
            composable(route = Screen.AssignHouse.route) {
                AssignHouseScreen()
            }
            composable(route = Screen.IssueNotice.route) {
                IssueNoticeScreen()
            }
        }
    }
}

fun logoutAndReset(navController: NavHostController) {
    navController.navigate(Graph.SHARED) {
        popUpTo(0) { inclusive = true }
    }
}