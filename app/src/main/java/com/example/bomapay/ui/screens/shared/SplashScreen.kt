package com.example.bomapay.ui.screens.shared

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.bomapay.data.repository.AuthRepository
import com.example.bomapay.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val authRepository = AuthRepository()
    // Animatable float to handle a sleek brand alpha fade-in
    val fadeAlpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        // Animate from invisible (0) to fully visible (1)
        fadeAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200)
        )

        // Give the user a brief moment to experience the branding look
        delay(800)

        val currentUid = authRepository.getCurrentUid()
        if (currentUid != null) {
            val role = authRepository.getUserRole(currentUid)
            if (role == "landlord") {
                navController.navigate(Screen.LandlordHome.route) { popUpTo(0) }
            } else {
                navController.navigate(Screen.TenantHome.route) { popUpTo(0) }
            }
        } else {
            navController.navigate(Screen.Login.route) { popUpTo(0) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "BomaPay",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.alpha(fadeAlpha.value) // Plugs into the dynamic animation state
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Smart Apartment Management",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                modifier = Modifier.alpha(fadeAlpha.value)
            )
        }
    }
}