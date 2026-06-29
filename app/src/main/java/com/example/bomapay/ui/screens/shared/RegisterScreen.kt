package com.example.bomapay.ui.screens.shared

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bomapay.data.repository.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var adminCode by remember { mutableStateOf("") } // Tracks the secret landlord verification token
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepository = AuthRepository()

    val isEmailError = email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordError = password.isNotEmpty() && password.length < 6

    // Hardcoded secure token for your project presentation validation
    val SECRET_LANDLORD_CODE = "BomaAdmin2026"

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Create Account", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim() },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = isEmailError,
            supportingText = {
                if (isEmailError) {
                    Text("Please enter a valid email address", color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (6+ Characters)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = isPasswordError,
            supportingText = {
                if (isPasswordError) {
                    Text("Password must be at least 6 characters long", color = MaterialTheme.colorScheme.error)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle Visibility")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Protected text field input for admin role verification
        OutlinedTextField(
            value = adminCode,
            onValueChange = { adminCode = it },
            label = { Text("Landlord Admin Code (Leave blank if Tenant)") },
            placeholder = { Text("Enter property verification token") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty() && !isEmailError && !isPasswordError) {

                    // Security Logic: Verify if they are trying to gain landlord administrative access
                    val attemptsLandlordAccess = adminCode.isNotEmpty()
                    val isAuthorizedLandlord = adminCode == SECRET_LANDLORD_CODE

                    if (attemptsLandlordAccess && !isAuthorizedLandlord) {
                        Toast.makeText(context, "Invalid Admin Verification Code!", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        val result = authRepository.registerUser(email, password, isLandlord = attemptsLandlordAccess)
                        isLoading = false
                        if (result.isSuccess) {
                            // Tell the nav graph that registration succeeded
                            onRegisterSuccess()
                        } else {
                            Toast.makeText(context, "Registration Failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Please complete form inputs accurately.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text("Register", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Already have an account? Sign In",
            modifier = Modifier.clickable { onNavigateToLogin() },
            color = MaterialTheme.colorScheme.outline
        )
    }
}