package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import com.example.data.database.AuraDatabase
import com.example.data.repository.AuraRepository
import com.example.ui.screens.*
import androidx.compose.runtime.CompositionLocalProvider
import com.example.ui.theme.AuraTheme
import com.example.ui.viewmodel.AuraViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

    // Initialize Database holding our social threads natively (Lifetime Free SQLite store)
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AuraDatabase::class.java,
            "aura_social_database"
        )
        .fallbackToDestructiveMigration() // Gracefully clear storage on changes
        .build()
    }

    private val repository by lazy {
        AuraRepository(database.dao)
    }

    private val viewModel by lazy {
        AuraViewModel(repository, applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Mandatory modern immersive draw borders rule

        // Proactively request image/external storage read permissions to ensure robust profile backup/restore functionality across reinstalls
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES), 101)
                }
            } else {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 101)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("AuraPermissions", "Failed to pro-actively request media permissions on start: ${e.message}")
        }

        val intentUri = intent?.data
        if (intentUri != null) {
            val isAuraCom = (intentUri.host == "aura.com" && intentUri.path?.contains("/shared_post/") == true)
            val isAuraScheme = (intentUri.scheme == "aura")
            val isPlatformDomain = (intentUri.host?.contains("run.app") == true || intentUri.host?.contains("appwrite") == true)
            if (isAuraCom || isAuraScheme || isPlatformDomain) {
                val postIdStr = intentUri.getQueryParameter("postId")?.toIntOrNull()
                    ?: intentUri.lastPathSegment?.toIntOrNull()
                    ?: intentUri.host?.toIntOrNull()
                if (postIdStr != null) {
                    val authorNameB64 = intentUri.getQueryParameter("n") ?: intentUri.getQueryParameter("t") ?: ""
                    val contentB64 = intentUri.getQueryParameter("d") ?: ""
                    val imageB64 = intentUri.getQueryParameter("i") ?: ""
                    viewModel.handleDeepLinkPostWithData(postIdStr, authorNameB64, contentB64, imageB64)
                }
            }
        }
        
        handleIncomingIntent(intent)

        setContent {
            AuraTheme {
                CompositionLocalProvider(LocalAuraViewModel provides viewModel) {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    // Root-level system Back button processor
                    BackHandler(enabled = true) {
                        if (!viewModel.goBack()) {
                            finish()
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Type-safe dynamic transition between screen routes with smooth slide cards
                        AnimatedContent(
                            targetState = uiState.currentScreen,
                            transitionSpec = {
                                val initialOrder = getScreenOrder(initialState)
                                val targetOrder = getScreenOrder(targetState)
                                if (targetOrder >= initialOrder) {
                                    // Slide in from right (forward)
                                    (slideInHorizontally(animationSpec = tween(400)) { it } + fadeIn(animationSpec = tween(400)))
                                        .togetherWith(slideOutHorizontally(animationSpec = tween(400)) { -it } + fadeOut(animationSpec = tween(400)))
                                } else {
                                    // Slide in from left (backward)
                                    (slideInHorizontally(animationSpec = tween(400)) { -it } + fadeIn(animationSpec = tween(400)))
                                        .togetherWith(slideOutHorizontally(animationSpec = tween(400)) { it } + fadeOut(animationSpec = tween(400)))
                                }
                            },
                            label = "aura_screen_router"
                        ) { screen ->
                            when (screen) {
                                is Screen.Splash -> SplashScreen(
                                    viewModel = viewModel
                                )
                                is Screen.Welcome -> WelcomeScreen(
                                    state = uiState,
                                    viewModel = viewModel
                                )
                                is Screen.Register -> RegisterScreen(
                                    viewModel = viewModel
                                )
                                is Screen.BirthdaySelection -> BirthdaySelectionScreen(
                                    viewModel = viewModel
                                )
                                is Screen.GenderSelection -> GenderSelectionScreen(
                                    viewModel = viewModel
                                )
                                is Screen.EmailInput -> EmailInputScreen(
                                    viewModel = viewModel
                                )
                                is Screen.Verification -> VerificationScreen(
                                    viewModel = viewModel
                                )
                                is Screen.PasswordSelection -> PasswordSelectionScreen(
                                    viewModel = viewModel
                                )
                                is Screen.RelationshipSelection -> RelationshipSelectionScreen(
                                    viewModel = viewModel
                                )
                                is Screen.EducationSelection -> EducationSelectionScreen(
                                    viewModel = viewModel
                                )
                                is Screen.HobbySelection -> HobbySelectionScreen(
                                    viewModel = viewModel
                                )
                                is Screen.BioSelection -> BioSelectionScreen(
                                    viewModel = viewModel
                                )
                                is Screen.ProfilePictureSelection -> ProfilePictureSelectionScreen(
                                    viewModel = viewModel
                                )
                                is Screen.CoverPhotoSelection -> CoverPhotoSelectionScreen(
                                    viewModel = viewModel
                                )
                                is Screen.HometownSelection -> HometownSelectionScreen(
                                    viewModel = viewModel
                                )
                                is Screen.WelcomeCelebration -> WelcomeCelebrationScreen(
                                    viewModel = viewModel
                                )
                                is Screen.Login -> LoginScreen(
                                    viewModel = viewModel
                                )
                                is Screen.Main -> MainScreen(
                                    state = uiState,
                                    viewModel = viewModel
                                )
                                is Screen.ChatRoom -> ChatRoomScreen(
                                    state = uiState,
                                    viewModel = viewModel,
                                    otherUser = screen.otherUser
                                )
                                is Screen.ProfileDetail -> ProfileDetailScreen(
                                    viewModel = viewModel,
                                    targetUser = screen.targetUser,
                                    onBackClick = { if (!viewModel.goBack()) viewModel.navigateTo(Screen.Main) }
                                )
                                is Screen.SavedItems -> com.example.ui.screens.SavedItemsScreen(
                                     viewModel = viewModel,
                                     onBackClick = { if (!viewModel.goBack()) viewModel.navigateTo(Screen.Main) }
                                 )
                                 is Screen.SearchUsers -> SearchUsersScreen(
                                    viewModel = viewModel,
                                    onBackClick = { if (!viewModel.goBack()) viewModel.navigateTo(Screen.Main) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
        val intentUri = intent.data
        if (intentUri != null) {
            val isAuraCom = (intentUri.host == "aura.com" && intentUri.path?.contains("/shared_post/") == true)
            val isAuraScheme = (intentUri.scheme == "aura")
            val isPlatformDomain = (intentUri.host?.contains("run.app") == true || intentUri.host?.contains("appwrite") == true)
            if (isAuraCom || isAuraScheme || isPlatformDomain) {
                val postIdStr = intentUri.getQueryParameter("postId")?.toIntOrNull()
                    ?: intentUri.lastPathSegment?.toIntOrNull()
                    ?: intentUri.host?.toIntOrNull()
                if (postIdStr != null) {
                    val authorNameB64 = intentUri.getQueryParameter("n") ?: intentUri.getQueryParameter("t") ?: ""
                    val contentB64 = intentUri.getQueryParameter("d") ?: ""
                    val imageB64 = intentUri.getQueryParameter("i") ?: ""
                    viewModel.handleDeepLinkPostWithData(postIdStr, authorNameB64, contentB64, imageB64)
                }
            }
        }
    }

    private fun handleIncomingIntent(intent: android.content.Intent?) {
        if (intent == null) return
        val action = intent.action
        val type = intent.type
        if (android.content.Intent.ACTION_SEND == action && type != null) {
            if (type.startsWith("image/") || type.startsWith("video/")) {
                val mediaUri = intent.getParcelableExtra<android.net.Uri>(android.content.Intent.EXTRA_STREAM)
                if (mediaUri != null) {
                    viewModel.handleSharedMedia(mediaUri.toString(), type)
                }
            }
        } else if (android.content.Intent.ACTION_SEND_MULTIPLE == action && type != null) {
            if (type.startsWith("image/") || type.startsWith("video/") || type.startsWith("*/*")) {
                val mediaUris = intent.getParcelableArrayListExtra<android.net.Uri>(android.content.Intent.EXTRA_STREAM)
                if (mediaUris != null && mediaUris.isNotEmpty()) {
                    viewModel.handleMultipleSharedMedia(mediaUris.map { it.toString() }, type)
                }
            }
        }
    }

    private fun getScreenOrder(screen: Screen): Int {
        return when (screen) {
            is Screen.Splash -> -1
            is Screen.Welcome -> 0
            is Screen.Register -> 1
            is Screen.BirthdaySelection -> 2
            is Screen.GenderSelection -> 3
            is Screen.EmailInput -> 4
            is Screen.Verification -> 5
            is Screen.PasswordSelection -> 6
            is Screen.RelationshipSelection -> 7
            is Screen.EducationSelection -> 8
            is Screen.HobbySelection -> 9
            is Screen.BioSelection -> 10
            is Screen.ProfilePictureSelection -> 11
            is Screen.CoverPhotoSelection -> 12
            is Screen.HometownSelection -> 13
            is Screen.WelcomeCelebration -> 14
            is Screen.Login -> 1
            is Screen.Main -> 15
            is Screen.ChatRoom -> 16
            is Screen.ProfileDetail -> 17
            is Screen.SearchUsers -> 18
            is Screen.SavedItems -> 19
        }
    }
}
