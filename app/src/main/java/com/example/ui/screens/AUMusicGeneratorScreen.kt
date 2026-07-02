@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AuraViewModel

@Composable
fun AUMusicGeneratorFullScreen(
    viewModel: AuraViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Package name of Google Gemini App
    val geminiPackageName = "com.google.android.apps.bard"

    fun launchGeminiApp() {
        try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(geminiPackageName)
            if (launchIntent != null) {
                // Launch the installed Gemini app directly
                context.startActivity(launchIntent)
                Toast.makeText(context, "Opening Google Gemini AI...", Toast.LENGTH_SHORT).show()
            } else {
                // If not installed, redirect to Play Store
                val playStoreUri = Uri.parse("market://details?id=$geminiPackageName")
                val playStoreIntent = Intent(Intent.ACTION_VIEW, playStoreUri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(playStoreIntent)
                Toast.makeText(context, "Redirecting to Google Play Store...", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Fallback: Open Play Store in web browser
            try {
                val webUri = Uri.parse("https://play.google.com/store/apps/details?id=$geminiPackageName")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Unable to open Google Play Store", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun launchGeminiWeb() {
        try {
            val webUri = Uri.parse("https://gemini.google.com")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
            Toast.makeText(context, "Opening Gemini Web Portal...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Aura AI Music Guide",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1F2937)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1F2937)
                )
            )
        },
        containerColor = Color(0xFFF9FAFB)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero section with Google Gemini brand colored gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1E3A8A), // Deep Blue
                                Color(0xFF4F46E5), // Indigo
                                Color(0xFF7C3AED), // Violet
                                Color(0xFFDB2777)  // Deep Pink
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "Music",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Gemini Sparkling star
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Gemini Sparkle",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Aura AI & Gemini Hub",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Perfect Music & Story Songs",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main explanation text card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Use Gemini AI to Enhance Your Stories!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "To add the perfect music tune or custom song to your Aura Story, please use the official Google Gemini AI. Gemini is powered by Google's most advanced multimodal AI models, allowing you to create full, high-fidelity, and non-truncated 3-minute or 5-minute custom background tracks beautifully matched to your visual content.",
                        fontSize = 14.sp,
                        color = Color(0xFF4B5563),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step-by-step guidance card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "How to generate your song:",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Step 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFE0E7FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "1",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4F46E5)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Launch Gemini AI",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151)
                            )
                            Text(
                                text = "Click the 'Open Gemini AI' button below to open the official Gemini app or install it from the Play Store.",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Step 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFE0E7FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "2",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4F46E5)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Provide Your Story Details",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151)
                            )
                            Text(
                                text = "Upload your story photos/videos inside Gemini and ask: 'Analyze these files and suggest or compose a perfect matching 3-minute or 5-minute background music score or song.'",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Step 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFE0E7FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "3",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4F46E5)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Add it to Aura",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151)
                            )
                            Text(
                                text = "Save the generated soundtrack and select it when creating your Aura Story to share with your friends!",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Primary Call-To-Action (CTA) Button
            Button(
                onClick = { launchGeminiApp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4F46E5)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Gemini",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Open Gemini AI App",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Call-To-Action (CTA) Button for Web Portal fallback
            OutlinedButton(
                onClick = { launchGeminiWeb() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF4F46E5)
                ),
                border = BorderStroke(1.dp, Color(0xFF818CF8))
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Web",
                    tint = Color(0xFF4F46E5)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Use Gemini Web Portal",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4F46E5)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
