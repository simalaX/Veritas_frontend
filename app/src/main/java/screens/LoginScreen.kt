package com.example.veritas.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.veritas.ui.theme.*
import com.example.veritas.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // Admin credentials
    val adminEmail = "admin@veritas.com"
    val adminPassword = "Admin@123456"

    // Use the Web Client ID from your google-services.json
    val webClientId = "745737133293-dp2gf66715q3trl7nrpbopicij7i4gej.apps.googleusercontent.com"

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Matching the animated title from Registration
    var isMaroon by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            isMaroon = !isMaroon
            delay(1500)
        }
    }
    val animatedColor by animateColorAsState(
        targetValue = if (isMaroon) VeritasMaroon else VeritasGold,
        label = "TitleAnimation"
    )

    // Google Sign-In setup
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                android.util.Log.d("GoogleSignIn", "Account: ${account.email}")
                account.idToken?.let { idToken ->
                    android.util.Log.d("GoogleSignIn", "Got ID Token")
                    isLoading = true
                    authViewModel.signInWithGoogle(idToken) { success ->
                        isLoading = false
                        if (success) {
                            android.util.Log.d("GoogleSignIn", "Sign-in successful")
                            val isAdmin = auth.currentUser?.email?.contains("admin") == true
                            val route = if (isAdmin) "admindashboard" else "home"
                            navController.navigate(route) {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            android.util.Log.e("GoogleSignIn", "Sign-in failed")
                            Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } ?: run {
                    android.util.Log.e("GoogleSignIn", "ID Token is null")
                    Toast.makeText(context, "Failed to get credentials", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                android.util.Log.e("GoogleSignIn", "Error code: ${e.statusCode}, message: ${e.message}")
                Toast.makeText(context, "Google Sign-In failed (${e.statusCode})", Toast.LENGTH_LONG).show()
            }
        } else {
            android.util.Log.d("GoogleSignIn", "Result code: ${result.resultCode}")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(VeritasDarkest, VeritasDeepMaroon)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Veritas Generation",
                        color = animatedColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Welcome Back",
                        color = Color.White.copy(0.7f),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = VeritasIvory) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeritasGold,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = VeritasIvory) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeritasGold,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Please enter all details", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isLoading = true

                            // Check if admin credentials
                            if (email == adminEmail && password == adminPassword) {
                                // Admin login
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            Toast.makeText(context, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                                            navController.navigate("admindashboard") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } else {
                                            // If admin doesn't exist, create the account
                                            isLoading = true
                                            auth.createUserWithEmailAndPassword(adminEmail, adminPassword)
                                                .addOnCompleteListener { createTask ->
                                                    isLoading = false
                                                    if (createTask.isSuccessful) {
                                                        Toast.makeText(
                                                            context,
                                                            "Admin account created! Logging in...",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        navController.navigate("admindashboard") {
                                                            popUpTo("login") { inclusive = true }
                                                        }
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "Error: ${createTask.exception?.message}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }
                                        }
                                    }
                            } else {
                                // Regular user login
                                authViewModel.login(email, password) { success, errorMessage ->
                                    isLoading = false
                                    if (success) {
                                        val isAdmin = auth.currentUser?.email?.contains("admin") == true
                                        val route = if (isAdmin) "admindashboard" else "home"
                                        navController.navigate(route) {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Login failed: ${errorMessage ?: "Unknown error"}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VeritasMaroon),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Login", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            // Sign out any existing Google account first
                            googleSignInClient.signOut().addOnCompleteListener {
                                // Launch sign-in after sign out completes
                                googleSignInLauncher.launch(googleSignInClient.signInIntent)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = VeritasGold
                        )
                    ) {
                        Text("Sign in with Google", color = VeritasGold, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Toggle back to Registration
                    Text(
                        text = buildAnnotatedString {
                            append("Don't have an account? ")
                            withStyle(style = SpanStyle(color = VeritasGold, fontWeight = FontWeight.Bold)) {
                                append("Sign Up")
                            }
                        },
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("registration") {
                                    launchSingleTop = true
                                }
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}