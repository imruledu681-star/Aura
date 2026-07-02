package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.viewmodel.AuraViewModel

@Composable
fun SplashScreen(
    viewModel: AuraViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Explicit bold white background as requested
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Launcher Circle Icon (Styled round lavender button with stylized white A)
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(Color(0xFF7C4DFF), Color(0xFF4A148C))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Aura App Icon",
                    modifier = Modifier.size(110.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Brand name
            Text(
                text = "Aura",
                color = Color(0xFF7C4DFF), // Bold lavender title text
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // 6 Dots Loading Animation
            DotLoadingAnimation()
        }
    }
}

@Composable
fun DotLoadingAnimation() {
    val dotsCount = 6
    val dotSize = 12.dp
    val spacing = 8.dp
    val lavenderColor = Color(0xFF7C4DFF) // Bold lavender
    
    val infiniteTransition = rememberInfiniteTransition(label = "dots_loading_anim")
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until dotsCount) {
            val delayMillis = i * 120
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1.3f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1000
                        0.5f at delayMillis with FastOutSlowInEasing
                        1.3f at (delayMillis + 250) with FastOutSlowInEasing
                        0.5f at (delayMillis + 500) with FastOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "dot_scale_$i"
            )
            
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1000
                        0.3f at delayMillis with FastOutSlowInEasing
                        1.0f at (delayMillis + 250) with FastOutSlowInEasing
                        0.3f at (delayMillis + 500) with FastOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "dot_alpha_$i"
            )
            
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .background(
                        color = lavenderColor.copy(alpha = alpha),
                        shape = CircleShape
                    )
            )
        }
    }
}
