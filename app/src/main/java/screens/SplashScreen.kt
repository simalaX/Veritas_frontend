package com.example.veritas.ui.screens

import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.veritas.ui.theme.VeritasDarkest
import com.example.veritas.ui.theme.VeritasDeepMaroon
import com.example.veritas.ui.theme.VeritasGold
import com.example.veritas.ui.theme.VeritasIvory
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    // Navigation logic: Move to registration/login after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
        navController.navigate("registration") { // Updated to registration as start
            popUpTo("splash") { inclusive = true }
        }
    }

    // --- High-End Animations ---

    val infiniteTransition = rememberInfiniteTransition(label = "SplashInfinite")

    // Soft Breathing Glow
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )

    // Gentle Pulsing Scale
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    // Deep Radial Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        VeritasDeepMaroon,
                        VeritasDarkest,
                        Color(0xFF0D0000) // Pitch black edges for depth
                    ),
                    center = Offset.Unspecified,
                    radius = 1500f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // "Powered By" label
            Text(
                text = "POWERED BY",
                color = VeritasGold.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alphaAnim)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Main Brand Title with Cyber-Glow
            Text(
                text = "Xspace Security+",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .scale(scaleAnim)
                    .alpha(alphaAnim),
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF00FBFF), // Cyber Cyan
                            Color(0xFF007BFF), // Tech Blue
                            Color(0xFF00FBFF)
                        )
                    ),
                    shadow = Shadow(
                        color = Color(0xFF00FBFF).copy(alpha = 0.5f),
                        blurRadius = 30f
                    )
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tagline
            Text(
                text = "Where AI Meets Cyber Defense",
                color = VeritasIvory.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(50.dp))

            // Animated Loading Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val dotAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800, delayMillis = index * 150),
                            repeatMode = RepeatMode.Reverse
                        ), label = "dot$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .alpha(dotAlpha)
                            .background(VeritasGold, CircleShape)
                    )
                }
            }
        }
    }
}