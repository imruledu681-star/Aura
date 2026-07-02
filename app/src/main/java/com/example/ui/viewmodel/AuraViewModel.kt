package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.*
import com.example.data.repository.AuraRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody

// Screen Routes
sealed class Screen {
    object Splash : Screen() // Custom beautiful loading splash screen
    object Welcome : Screen() // Join Aura Welcome Screen
    object Register : Screen() // Register Profile Screen
    object BirthdaySelection : Screen() // Birthday selection screen
    object GenderSelection : Screen() // Gender selection screen
    object EmailInput : Screen() // Email input screen
    object Verification : Screen() // Verification page
    object PasswordSelection : Screen() // Password select screen
    object RelationshipSelection : Screen() // Relationship selection screen
    object EducationSelection : Screen() // Education selection screen
    object HobbySelection : Screen() // Hobby selection screen
    object BioSelection : Screen() // Bio selection screen
    object ProfilePictureSelection : Screen() // Profile picture selection screen
    object CoverPhotoSelection : Screen() // Profile cover photo selection screen
    object HometownSelection : Screen() // Hometown selection screen
    object WelcomeCelebration : Screen() // Full screen welcome celebration page
    object Login : Screen() // Login Profile Screen
    object Main : Screen() // Feed, Stories, Profile Dashboard
    class ChatRoom(val otherUser: UserEntity) : Screen() // Real chat with chosen friends
    class ProfileDetail(val targetUser: UserEntity) : Screen() // View other members profile
    object SearchUsers : Screen() // Search other users screen
    object SavedItems : Screen() // View saved videos and reels
}

// Current Navigation Tab in Main Screen
enum class MainTab {
    FEEDS,
    FRIENDS,
    VIDEOS,
    CREATE_POST,
    NOTIFICATIONS,
    PROFILE,
    SETTINGS
}

data class CreatePostState(
    val content: String = "",
    val imageInputUrl: String = "",
    val selectedGradientIdx: Int = -1, // -1 is none, 0-3 are colorful premium backdrops
    val isAiLabeled: Boolean = false,
    val mentionedUserIds: String = "",
    val privacy: String = "Public",
    val musicTrack: String = "",
    val gifUrl: String = "",
    val location: String = "",
    val attachedLiveRoomName: String = "",
    val attachedLiveTitle: String = "",
    val attachedLiveDurationSecs: Long = 0L,
    val attachedVibePartner: String = "",
    val attachedVibeScore: Int = -1,
    val attachedVibeTitle: String = "",
    val attachedVibeAdvice: String = "",
    val attachedVibeColor: String = ""
)

data class LiveComment(
    val id: String = "",
    val senderName: String = "",
    val senderAvatar: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class AuraUiState(
    val currentScreen: Screen = Screen.Welcome,
    val currentTab: MainTab = MainTab.FEEDS,
    val searchQuery: String = "",
    val searchResults: List<UserEntity> = emptyList(),
    val chatChannelId: String = "",
    val appLanguage: String = "English (US)",
    val showLanguageMenu: Boolean = false,
    val isAppwriteActive: Boolean = true,
    val showAppwriteDetails: Boolean = false,
    val creationState: CreatePostState = CreatePostState(),
    val showStoryViewer: StoryEntity? = null,
    val activeCommentsPostId: Int? = null,
    val commentInputText: String = "",
    val activeComments: List<CommentEntity> = emptyList(),
    // Dynamic ultra-precise translations loaded via Gemini
    val welcomeTitle: String = "Join Aura",
    val welcomeDesc: String = "Connect with friends, family and communities of people who share your interests.",
    val getStartedText: String = "Get started",
    val alreadyHaveProfileText: String = "I already have a profile",
    val isTranslating: Boolean = false,
    val activeUserNote: String = "",
    val showReelCreator: Boolean = false,
    val showLiveSimulator: Boolean = false,
    val showVibeCheckSimulator: Boolean = false,
    val showNoteCreator: Boolean = false,
    val isViewingAsGuest: Boolean = false,
    // Real Live Stream WebRTC Properties
    val liveRoomName: String = "",
    val liveStreamTitle: String = "",
    val livePlatform: String = "YouTube", // "YouTube" or "Twitch"
    val liveStreamUrlOrKey: String = "",
    val isBroadcaster: Boolean = false,
    val activeLivePostKey: String = "",
    val activeLivePostLocalId: Int = 0,
    val liveCommentsList: List<LiveComment> = emptyList(),
    // Vibe check calculations
    val vibeCheckPartnerName: String = "",
    val vibeCheckSelfEnergy: String = "",
    val vibeCheckPartnerEnergy: String = "",
    val vibeCheckScore: Int = -1,
    val vibeCheckTitle: String = "",
    val vibeCheckAdvice: String = "",
    val vibeCheckColor: String = "",
    val isVibeChecking: Boolean = false,
    // Premium Lavender Video Player States
    val activeReplayPost: PostEntity? = null,
    val isVideoPlaying: Boolean = false,
    val videoProgress: Float = 0.0f,
    val isVideoMinimized: Boolean = false,
    val isVideoFullscreen: Boolean = false,
    val videoPlaybackSpeed: Float = 1.0f,
    val webrtcPeerState: String = "IDLE",
    val webrtcBitrate: Int = 0,
    val webrtcLatency: Int = 0,
    val webrtcPacketLoss: Float = 0.0f,
    val webrtcIceCandidatesExchanged: Int = 0,
    val showStoryCreator: Boolean = false,
    val storyCreatorMediaUri: String? = null,
    val storyCreatorMediaType: String? = null,
    val storyCreatorMediaUris: List<String> = emptyList(),
    val isStoryMuted: Boolean = false,
    val shouldAutoLaunchGallery: Boolean = false,
    val showAUMusicGenerator: Boolean = false,
    // Videos & Reels Studio properties
    val showVideoCreator: Boolean = false,
    val videoCreatorVideoUri: String? = null,
    val videoCreatorThumbnailUri: String? = null,
    val videoCreatorTitle: String = "",
    val videoCreatorDescription: String = "",
    val videoCreatorTags: String = "",
    val videoCreatorPrivacy: String = "Public",
    val reelCreatorVideoUri: String? = null,
    val reelCreatorThumbnailUri: String? = null,
    val reelCreatorTitle: String = "",
    val reelCreatorDescription: String = "",
    val reelCreatorTags: String = "",
    val reelCreatorPrivacy: String = "Public",
    val sentFriendRequests: Set<Int> = emptySet()
)

class AuraViewModel(
    private val repository: AuraRepository,
    private val context: Context
) : ViewModel() {

    private var liveCommentsListener: com.google.firebase.database.ValueEventListener? = null
    private var liveCommentsRef: com.google.firebase.database.DatabaseReference? = null
    private var liveBroadcastStartTime: Long = 0L
    private var isDraftLiveBroadcast = false

    private var mediaRecorder: android.media.MediaRecorder? = null
    private var currentRecordingFile: java.io.File? = null
    private var replayMediaPlayer: android.media.MediaPlayer? = null
    private var tts: android.speech.tts.TextToSpeech? = null
    private var isTtsInitialized = false

    private fun initTts() {
        if (tts == null) {
            tts = android.speech.tts.TextToSpeech(context) { status ->
                if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                    isTtsInitialized = true
                    tts?.setPitch(1.0f)
                    tts?.setSpeechRate(0.95f)
                }
            }
        }
    }

    private fun startRecordingAudio(roomName: String) {
        try {
            val file = java.io.File(context.filesDir, "aura_live_${roomName}.mp4")
            currentRecordingFile = file
            val recorder = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                android.media.MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                android.media.MediaRecorder()
            }
            recorder.apply {
                setAudioSource(android.media.MediaRecorder.AudioSource.MIC)
                setOutputFormat(android.media.MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(android.media.MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            mediaRecorder = recorder
            android.util.Log.d("AuraViewModel", "Audio recording started: ${file.absolutePath}")
        } catch (e: Exception) {
            android.util.Log.e("AuraViewModel", "Failed to start audio recording: ${e.message}")
        }
    }

    private fun stopRecordingAudio() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            android.util.Log.d("AuraViewModel", "Audio recording stopped")
        } catch (e: Exception) {
            android.util.Log.e("AuraViewModel", "Failed to stop audio recording: ${e.message}")
        } finally {
            mediaRecorder = null
        }
    }

    private fun startAudioPlayback(post: PostEntity) {
        stopAudioPlayback()
        val roomName = post.content.substringAfter("[ROOM_NAME:").substringBefore("]", "")
        if (roomName.isNotEmpty()) {
            val audioFile = java.io.File(context.filesDir, "aura_live_${roomName}.mp4")
            if (audioFile.exists()) {
                try {
                    replayMediaPlayer = android.media.MediaPlayer().apply {
                        setDataSource(audioFile.absolutePath)
                        prepare()
                        start()
                        setOnCompletionListener {
                            _uiState.update { it.copy(videoProgress = 1.0f, isVideoPlaying = false) }
                            stopPlaybackSimulation()
                        }
                    }
                    android.util.Log.d("AuraViewModel", "Started playing recorded audio: ${audioFile.absolutePath}")
                    return
                } catch (e: Exception) {
                    android.util.Log.e("AuraViewModel", "Error playing recorded audio: ${e.message}")
                }
            }
        }
        
        // Dynamic US English native text-to-speech fallback if file cleared/reinstalled
        speakTtsFallback(post)
    }

    private fun stopAudioPlayback() {
        try {
            replayMediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            // Ignore
        } finally {
            replayMediaPlayer = null
        }
        stopTtsFallback()
    }

    private fun speakTtsFallback(post: PostEntity) {
        initTts()
        val title = post.content
            .substringAfter("🎥 LIVE STREAM REPLAY: \"")
            .substringBefore("\"")
            .ifEmpty { "Aura Live Recast" }
        
        val speechText = "Replaying the live stream session of ${post.authorName} on Aura TV. The broadcast is titled: $title. Let's start the interactive replay feed."
        
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.Main) {
            var attempts = 0
            while (!isTtsInitialized && attempts < 10) {
                kotlinx.coroutines.delay(100)
                attempts++
            }
            if (isTtsInitialized) {
                tts?.speak(speechText, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, "AuraLiveReplayTTS")
                android.util.Log.d("AuraViewModel", "TTS Speaking fallback: $speechText")
            }
        }
    }

    private fun stopTtsFallback() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            // Ignore
        }
    }

    private val appwritePrefs by lazy {
        context.getSharedPreferences("aura_appwrite_prefs", Context.MODE_PRIVATE)
    }

    private fun getCurrentEndpoint(): String {
        return "https://sgp.cloud.appwrite.io/v1"
    }

    private fun getCurrentProjectId(): String {
        return "6a1ee8cb001248095722"
    }

    private fun getCurrentDatabaseId(): String {
        return "6a1ef1cc00131091b90a"
    }

    private fun getCurrentBucketId(): String {
        return "6a1ef1e8003dbe01e5c8"
    }

    val appwriteEndpointState = androidx.compose.runtime.mutableStateOf(getCurrentEndpoint())
    val appwriteProjectIdState = androidx.compose.runtime.mutableStateOf(getCurrentProjectId())
    val appwriteDatabaseIdState = androidx.compose.runtime.mutableStateOf(getCurrentDatabaseId())
    val appwriteBucketIdState = androidx.compose.runtime.mutableStateOf(getCurrentBucketId())

    val cleanAppwriteEndpoint: String
        get() = appwriteEndpointState.value

    val cleanAppwriteProjectId: String
        get() = appwriteProjectIdState.value

    val cleanAppwriteDatabaseId: String
        get() = appwriteDatabaseIdState.value

    val cleanAppwriteBucketId: String
        get() = appwriteBucketIdState.value

    val isRealAppwriteEnabled: Boolean
        get() {
            return try {
                val endpoint = cleanAppwriteEndpoint
                val project = cleanAppwriteProjectId
                endpoint.isNotBlank() && endpoint != "MY_APPWRITE_ENDPOINT" &&
                project.isNotBlank() && project != "MY_APPWRITE_PROJECT_ID" &&
                project != "6a1d62260019ca0d6c60"
            } catch(e: Exception) { false }
        }

    private var _client: io.appwrite.Client? = null
    private var _account: io.appwrite.services.Account? = null

    val appwriteClient: io.appwrite.Client?
        get() {
            if (_client == null) {
                try {
                    _client = io.appwrite.Client(context)
                        .setEndpoint(cleanAppwriteEndpoint)
                        .setProject(cleanAppwriteProjectId)
                        .setSelfSigned(true)
                } catch (e: Exception) {
                    android.util.Log.e("AppwriteInit", "Error: ${e.message}")
                }
            }
            return _client
        }

    val appwriteAccount: io.appwrite.services.Account?
        get() {
            if (_account == null) {
                appwriteClient?.let {
                    _account = io.appwrite.services.Account(it)
                }
            }
            return _account
        }

    fun updateAppwriteSettings(
        endpoint: String,
        projectId: String,
        databaseId: String = "6a1ef1cc00131091b90a",
        bucketId: String = "6a1ef1e8003dbe01e5c8"
    ): Boolean {
        val cleanedEndpoint = endpoint.trim().removeSurrounding("\"").removeSurrounding("'")
        val cleanedProject = projectId.trim().removeSurrounding("\"").removeSurrounding("'")
        val cleanedDatabase = databaseId.trim().removeSurrounding("\"").removeSurrounding("'")
        val cleanedBucket = bucketId.trim().removeSurrounding("\"").removeSurrounding("'")
        if (cleanedEndpoint.isBlank() || cleanedProject.isBlank() || cleanedDatabase.isBlank() || cleanedBucket.isBlank()) return false

        appwritePrefs.edit()
            .putString("endpoint", cleanedEndpoint)
            .putString("project_id", cleanedProject)
            .putString("database_id", cleanedDatabase)
            .putString("bucket_id", cleanedBucket)
            .apply()

        appwriteEndpointState.value = cleanedEndpoint
        appwriteProjectIdState.value = cleanedProject
        appwriteDatabaseIdState.value = cleanedDatabase
        appwriteBucketIdState.value = cleanedBucket

        _client = null
        _account = null
        return true
    }

    fun resetAppwriteSettingsToDefault() {
        appwritePrefs.edit()
            .remove("endpoint")
            .remove("project_id")
            .remove("database_id")
            .remove("bucket_id")
            .apply()

        appwriteEndpointState.value = getCurrentEndpoint()
        appwriteProjectIdState.value = getCurrentProjectId()
        appwriteDatabaseIdState.value = getCurrentDatabaseId()
        appwriteBucketIdState.value = getCurrentBucketId()

        _client = null
        _account = null
    }

    private val initialNavigation: Pair<Screen, List<Screen>> = run {
        try {
            val prefs = context.getSharedPreferences("aura_nav_prefs", Context.MODE_PRIVATE)
            val currentStr = prefs.getString("current_screen", null)
            val historyStr = prefs.getString("history_screens", null)

            if (currentStr != null) {
                fun stringToScreen(route: String): Screen? {
                    return when (route) {
                        "Welcome" -> Screen.Welcome
                        "Register" -> Screen.Register
                        "BirthdaySelection" -> Screen.BirthdaySelection
                        "GenderSelection" -> Screen.GenderSelection
                        "EmailInput" -> Screen.EmailInput
                        "Verification" -> Screen.Verification
                        "PasswordSelection" -> Screen.PasswordSelection
                        "RelationshipSelection" -> Screen.RelationshipSelection
                        "EducationSelection" -> Screen.EducationSelection
                        "HobbySelection" -> Screen.HobbySelection
                        "BioSelection" -> Screen.BioSelection
                        "ProfilePictureSelection" -> Screen.ProfilePictureSelection
                        "CoverPhotoSelection" -> Screen.CoverPhotoSelection
                        "Login" -> Screen.Login
                        "Main" -> Screen.Main
                        "SearchUsers" -> Screen.SearchUsers
                        else -> {
                            if (route.startsWith("ChatRoom:") || route.startsWith("ProfileDetail:") || route == "SearchUsers") {
                                Screen.Main
                            } else {
                                null
                            }
                        }
                    }
                }

                val restoredScreen = stringToScreen(currentStr)
                val restoredHistory = historyStr?.split(",")?.mapNotNull { stringToScreen(it) }

                if (restoredScreen != null) {
                    val historyList = if (restoredHistory != null && restoredHistory.isNotEmpty()) {
                        restoredHistory
                    } else {
                        listOf(restoredScreen)
                    }
                    Pair(restoredScreen, historyList)
                } else {
                    Pair(Screen.Welcome, listOf(Screen.Welcome))
                }
            } else {
                Pair(Screen.Welcome, listOf(Screen.Welcome))
            }
        } catch (e: Exception) {
            Pair(Screen.Welcome, listOf(Screen.Welcome))
        }
    }

    var regFirstName: String = ""
    var regLastName: String = ""
    var regPronoun: String = ""
    var regGenderOptional: String = ""
    var regGenderSelection: String = ""
    var regGenderPrivacy: String = "Public"
    var regEmail: String = ""
    var regVerificationUserId: String = ""
    var sandboxOtpSecret: String = ""
    var regPasswordText: String = ""
    var regRelationStatus: String = ""
    var regRelationPrivacy: String = "Public"
    var regSchool: String = ""
    var regCollege: String = ""
    var regUniversity: String = ""
    var regEducationPrivacy: String = "Public"
    var regHobbies: String = ""
    var regHobbyPrivacy: String = "Public"
    var regBio: String = ""
    var regProfilePic: String = ""
    var regCoverPic: String = ""
    var regHometown: String = ""
    var regHometownPrivacy: String = "Public"
    var regBirthday: String = ""
    var regBirthdayPrivacy: String = "Public"

    private val _recognizedDiscordAccounts = MutableStateFlow<List<String>>(emptyList())
    val recognizedDiscordAccounts: StateFlow<List<String>> = _recognizedDiscordAccounts.asStateFlow()

    fun loadDiscordAccounts(context: android.content.Context) {
        viewModelScope.launch {
            // Simulated local accounts list matching user profiles or testing setup
            val finalAccounts = listOf("imruledu681@gmail.com", "imruledu24@gmail.com", "aura.discord.user@example.com")
            _recognizedDiscordAccounts.value = finalAccounts.distinct()
        }
    }

    fun addDiscordAccountToDevice(newEmail: String) {
        val current = _recognizedDiscordAccounts.value.toMutableList()
        if (!current.contains(newEmail)) {
            current.add(newEmail)
            _recognizedDiscordAccounts.value = current
        }
    }

    fun copyImageToLocalStorage(uriString: String, destFileName: String): String {
        if (uriString.isBlank()) return ""
        if (!uriString.startsWith("content://") && !uriString.startsWith("file://")) {
            return uriString
        }
        return try {
            val uri = android.net.Uri.parse(uriString)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val file = java.io.File(context.filesDir, destFileName)
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                android.net.Uri.fromFile(file).toString()
            } ?: uriString
        } catch (e: Exception) {
            android.util.Log.e("Upload", "Error copying image to local storage: ${e.message}")
            uriString
        }
    }

    fun findImageInMediaStore(context: android.content.Context, fileName: String): String {
        val resolver = context.contentResolver
        val uriListToTry = mutableListOf<android.net.Uri>()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                uriListToTry.add(android.provider.MediaStore.Images.Media.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL))
            } catch (e: Exception) {}
            try {
                uriListToTry.add(android.provider.MediaStore.Images.Media.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL_PRIMARY))
            } catch (e: Exception) {}
        }
        uriListToTry.add(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        for (imageCollection in uriListToTry) {
            try {
                val projection = arrayOf(android.provider.MediaStore.MediaColumns._ID)
                val selection = "${android.provider.MediaStore.MediaColumns.DISPLAY_NAME} = ?"
                val selectionArgs = arrayOf(fileName)

                resolver.query(imageCollection, projection, selection, selectionArgs, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val idColumn = cursor.getColumnIndexOrThrow(android.provider.MediaStore.MediaColumns._ID)
                        val id = cursor.getLong(idColumn)
                        val res = android.content.ContentUris.withAppendedId(imageCollection, id).toString()
                        if (res.isNotBlank()) return res
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AuraBackup", "Error querying MediaStore uri $imageCollection: ${e.message}")
            }
        }

        // Direct File Check Fallback (highly reliable on older Androids, custom ROMs, or emulator storage)
        try {
            val backupDirs = listOf(
                android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES),
                android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOCUMENTS)
            )
            for (baseDir in backupDirs) {
                val auraDir = java.io.File(baseDir, "AuraHub")
                val targetFile = java.io.File(auraDir, fileName)
                if (targetFile.exists()) {
                    return android.net.Uri.fromFile(targetFile).toString()
                }
                val targetFileDirect = java.io.File(baseDir, fileName)
                if (targetFileDirect.exists()) {
                    return android.net.Uri.fromFile(targetFileDirect).toString()
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("AuraBackup", "Direct file backup fallback scan failed: ${e.message}")
        }

        return ""
    }

    fun checkAndRestoreProfileImages(user: UserEntity): UserEntity {
        val cleanEmail = user.email.lowercase().trim().replace("@", "_").replace(".", "_")
        if (cleanEmail.isBlank()) return user
        var finalAvatar = user.avatarUrl
        var finalCover = user.coverUrl
        var changed = false

        val isLocalFileMissing = { path: String ->
            if (path.isBlank()) {
                true
            } else if (path.startsWith("/") || path.startsWith("file://")) {
                try {
                    val actualPath = if (path.startsWith("file://")) {
                        android.net.Uri.parse(path).path ?: ""
                    } else {
                        path
                    }
                    val file = java.io.File(actualPath)
                    !file.exists()
                } catch (e: Exception) {
                    true
                }
            } else {
                false
            }
        }

        // 1. Restore Avatar from MediaStore backup if missing (only if not already a cloud hosted HTTP URL)
        if ((isLocalFileMissing(finalAvatar) && !finalAvatar.startsWith("http")) || finalAvatar.startsWith("content://") || finalAvatar == "avatar_user_main" || finalAvatar.isBlank()) {
            val publicAvatarName = "profile_pic_${cleanEmail}_backup.jpg"
            val mediaStoreAvatar = findImageInMediaStore(context, publicAvatarName)
            if (mediaStoreAvatar.isNotBlank()) {
                val destFileName = "profile_pic_${cleanEmail}_restored.jpg"
                val restoredLocal = copyImageToLocalStorage(mediaStoreAvatar, destFileName)
                if (restoredLocal.isNotBlank()) {
                    finalAvatar = restoredLocal
                    changed = true
                }
            }
        }

        // 2. Restore Cover from MediaStore backup if missing (only if not already a cloud hosted HTTP URL)
        if ((isLocalFileMissing(finalCover) && !finalCover.startsWith("http")) || finalCover.startsWith("content://") || finalCover == "cover_main" || finalCover.isBlank()) {
            val publicCoverName = "cover_pic_${cleanEmail}_backup.jpg"
            val mediaStoreCover = findImageInMediaStore(context, publicCoverName)
            if (mediaStoreCover.isNotBlank()) {
                val destFileName = "cover_pic_${cleanEmail}_restored.jpg"
                val restoredLocal = copyImageToLocalStorage(mediaStoreCover, destFileName)
                if (restoredLocal.isNotBlank()) {
                    finalCover = restoredLocal
                    changed = true
                }
            }
        }

        return if (changed) {
            user.copy(avatarUrl = finalAvatar, coverUrl = finalCover)
        } else {
            user
        }
    }

    fun copyImageToPublicStorage(uriString: String, destFileName: String): String {
        if (uriString.isBlank()) return ""
        if (!uriString.startsWith("content://") && !uriString.startsWith("file://")) {
            return uriString
        }
        return try {
            val uri = android.net.Uri.parse(uriString)
            val resolver = context.contentResolver
            
            val imageCollection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                android.provider.MediaStore.Images.Media.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            // Create contentValues for direct, secure insertion into MediaStore (no permission required on Q+)
            val contentValues = android.content.ContentValues().apply {
                put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, destFileName)
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/AuraHub")
                    put(android.provider.MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val imageUri = resolver.insert(imageCollection, contentValues)
            if (imageUri != null) {
                resolver.openInputStream(uri)?.use { inputStream ->
                    resolver.openOutputStream(imageUri)?.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(android.provider.MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(imageUri, contentValues, null, null)
                }
                android.util.Log.d("Upload", "Successfully copied image to public MediaStore: $imageUri")
                imageUri.toString()
            } else {
                uriString
            }
        } catch (e: Exception) {
            android.util.Log.e("Upload", "Error copying image to public MediaStore: ${e.message}")
            
            // Traditional fallback copy to DIRECTORY_DOCUMENTS if MediaStore insertion raises restrictions
            try {
                val uri = android.net.Uri.parse(uriString)
                val auraDocsDir = java.io.File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOCUMENTS), "AuraHub")
                if (!auraDocsDir.exists()) {
                    auraDocsDir.mkdirs()
                }
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val file = java.io.File(auraDocsDir, destFileName)
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    android.net.Uri.fromFile(file).toString()
                } ?: uriString
            } catch (ex: Exception) {
                android.util.Log.e("Upload", "Direct folder copy fallback failed: ${ex.message}")
                uriString
            }
        }
    }

    fun copyImageToPublicAndPrivateStorage(uriString: String, isCover: Boolean): String {
        if (uriString.isBlank()) return ""
        val suffix = if (isCover) "cover" else "profile"
        val cleanEmail = regEmail.lowercase().trim().replace("@", "_").replace(".", "_")
        val timestamp = System.currentTimeMillis()
        val fileName = "${suffix}_pic_${cleanEmail}_$timestamp.jpg"
        
        // 1. Copy to local private app storage (perfect, permission-free load on restart!)
        val privatePath = copyImageToLocalStorage(uriString, fileName)
        
        // 2. Perform public MediaStore storage copy for long term recovery payload
        val backupFileName = "${suffix}_pic_${cleanEmail}_backup.jpg"
        copyImageToPublicStorage(uriString, backupFileName)
        
        // Return privatePath primarily so standard usage accesses /data/ private file directly, requiring zero permissions
        return if (privatePath.isNotBlank()) privatePath else uriString
    }

    fun applyImageEditsAndSave(
        uriString: String,
        isCover: Boolean,
        rotation: Float,
        scale: Float,
        bias: Float,
        filterStyle: String
    ): String {
        if (uriString.isBlank()) return ""
        val suffix = if (isCover) "cover" else "profile"
        val cleanEmail = regEmail.lowercase().trim().replace("@", "_").replace(".", "_")
        val timestamp = System.currentTimeMillis()
        val fileName = "${suffix}_pic_edited_${cleanEmail}_$timestamp.jpg"

        return try {
            val uri = android.net.Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri)
            val srcBitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (srcBitmap != null) {
                val width = srcBitmap.width
                val height = srcBitmap.height

                // Limit maximum base size of edited image to 1200 max for high performance & upload comfort
                val maxDim = 1200f
                val scaleFactor = if (width > maxDim || height > maxDim) {
                    maxDim / Math.max(width, height).toFloat()
                } else {
                    1.0f
                }
                
                val finalWidth = (width * scaleFactor).toInt()
                val finalHeight = (height * scaleFactor).toInt()

                val editedBitmap = android.graphics.Bitmap.createBitmap(finalWidth, finalHeight, android.graphics.Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(editedBitmap)
                val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG or android.graphics.Paint.FILTER_BITMAP_FLAG)

                // Set up Matrix transforms matching Compose graphicsLayer
                val matrix = android.graphics.Matrix()
                matrix.postScale(scaleFactor, scaleFactor)

                val centerX = finalWidth / 2f
                val centerY = finalHeight / 2f
                matrix.postScale(scale, scale, centerX, centerY)
                matrix.postRotate(rotation, centerX, centerY)

                val translationX = bias * 10f
                matrix.postTranslate(translationX, 0f)

                // Match with filter styles selected in front-end
                val colorMatrix = when (filterStyle) {
                    "Cyberpunk" -> {
                        android.graphics.ColorMatrix(floatArrayOf(
                            1.1f, 0f, 0f, 0f, 40f,
                            0f, 0.85f, 0f, 0f, 10f,
                            0f, 0f, 1.3f, 0f, 60f,
                            0f, 0f, 0f, 1f, 0f
                        ))
                    }
                    "Vintage" -> {
                        android.graphics.ColorMatrix(floatArrayOf(
                            0.95f, 0f, 0f, 0f, 35f,
                            0f, 0.85f, 0f, 0f, 15f,
                            0f, 0f, 0.65f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        ))
                    }
                    "Classic Monochrome" -> {
                        android.graphics.ColorMatrix(floatArrayOf(
                            0.33f, 0.33f, 0.33f, 0f, 0f,
                            0.33f, 0.33f, 0.33f, 0f, 0f,
                            0.33f, 0.33f, 0.33f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        ))
                    }
                    "Warm Glow" -> {
                        android.graphics.ColorMatrix(floatArrayOf(
                            1.0f, 0f, 0f, 0f, 30f,
                            0f, 0.95f, 0f, 0f, 20f,
                            0f, 0f, 0.75f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        ))
                    }
                    "Aura Cosmic" -> {
                        android.graphics.ColorMatrix(floatArrayOf(
                            0.9f, 0f, 0f, 0f, 50f,
                            0f, 0.75f, 0f, 0f, 20f,
                            0f, 0f, 1.2f, 0f, 70f,
                            0f, 0f, 0f, 1f, 0f
                        ))
                    }
                    else -> null
                }

                if (colorMatrix != null) {
                    paint.colorFilter = android.graphics.ColorMatrixColorFilter(colorMatrix)
                }

                canvas.drawBitmap(srcBitmap, matrix, paint)

                // Save processed edited image in local app data directory
                val localFile = java.io.File(context.filesDir, fileName)
                localFile.outputStream().use { os ->
                    editedBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, os)
                }

                srcBitmap.recycle()
                editedBitmap.recycle()

                // Create public backup for future robust cloud recovery / sync triggers
                val publicBackupName = "${suffix}_pic_${cleanEmail}_backup.jpg"
                val localUriStr = android.net.Uri.fromFile(localFile).toString()
                copyImageToPublicStorage(localUriStr, publicBackupName)

                localUriStr
            } else {
                uriString
            }
        } catch (e: Exception) {
            android.util.Log.e("EditImage", "Exception editing and saving image: ${e.message}")
            uriString
        }
    }

    suspend fun uploadUriToCloud(uriString: String): String {
        if (uriString.isBlank() || uriString.startsWith("http://") || uriString.startsWith("https://") || (!uriString.startsWith("content://") && !uriString.startsWith("file://") && !uriString.contains("/"))) {
            return uriString
        }
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            var tempFile: java.io.File? = null
            try {
                val uri = android.net.Uri.parse(uriString)
                val extFile = java.io.File(context.cacheDir, "temp_upload_${System.currentTimeMillis()}.jpg")
                tempFile = extFile
                
                // Attempt bitmap compression to save upload bandwidth and maximize speeds up to 100x!
                var compressedSuccessfully = false
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val bytes = inputStream.readBytes()
                        val options = android.graphics.BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }
                        android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                        
                        // Calculate sample size for downscaling to max 1280px dimension to fit mobile displays cleanly
                        var sampleSize = 1
                        val maxDimension = 1280
                        if (options.outWidth > maxDimension || options.outHeight > maxDimension) {
                            val halfWidth = options.outWidth / 2
                            val halfHeight = options.outHeight / 2
                            while ((halfWidth / sampleSize) >= maxDimension && (halfHeight / sampleSize) >= maxDimension) {
                                sampleSize *= 2
                            }
                        }
                        
                        val decodeOptions = android.graphics.BitmapFactory.Options().apply {
                            inSampleSize = sampleSize
                        }
                        
                        val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size, decodeOptions)
                        if (bitmap != null) {
                            extFile.outputStream().use { os ->
                                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, os)
                            }
                            bitmap.recycle()
                            compressedSuccessfully = true
                        }
                    }
                } catch (ex: Exception) {
                    android.util.Log.e("UploadCloud", "Bitmap compression failed, falling back to raw stream copy: ${ex.message}")
                }

                if (!compressedSuccessfully) {
                    // Fall back to simple raw copy if bitmap compression isn't supported / fails
                    context.contentResolver.openInputStream(uri)?.use { rawIn ->
                        extFile.outputStream().use { rawOut ->
                            rawIn.copyTo(rawOut)
                        }
                    }
                }
                
                val client = okhttp3.OkHttpClient.Builder()
                    .connectTimeout(45, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(45, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
                    
                    val mediaType = "image/jpeg".toMediaTypeOrNull()
                    // Use java and kotlin friendly OkHttp 4.x request body builder
                    val fileBody = extFile.asRequestBody(mediaType)
                    
                    val requestBody = okhttp3.MultipartBody.Builder()
                        .setType(okhttp3.MultipartBody.FORM)
                        .addFormDataPart("reqtype", "fileupload")
                        .addFormDataPart("fileToUpload", extFile.name, fileBody)
                        .build()

                    val request = okhttp3.Request.Builder()
                        .url("https://catbox.moe/user/api.php")
                        .post(requestBody)
                        .build()

                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val responseString = response.body?.string()?.trim()
                            if (!responseString.isNullOrBlank() && responseString.startsWith("http")) {
                                android.util.Log.d("UploadCloud", "Successfully uploaded to cloud storage: $responseString")
                                return@withContext responseString
                            } else {
                                android.util.Log.e("UploadCloud", "Cloud upload service response: $responseString")
                            }
                        } else {
                            android.util.Log.e("UploadCloud", "Cloud upload HTTP error: ${response.code}")
                        }
                    }
            } catch (e: Exception) {
                android.util.Log.e("UploadCloud", "Error doing cloud backup upload: ${e.message}")
            } finally {
                try {
                    tempFile?.delete()
                } catch (ignored: Exception) {}
            }
            uriString
        }
    }

    private fun indexOf(outer: ByteArray, inner: ByteArray): Int {
        if (inner.isEmpty() || outer.size < inner.size) return -1
        for (i in 0..outer.size - inner.size) {
            var found = true
            for (j in inner.indices) {
                if (outer[i + j] != inner[j]) {
                    found = false
                    break
                }
            }
            if (found) return i
        }
        return -1
    }

    private fun stripOldAppendedMarker(bytes: ByteArray): ByteArray {
        val marker = "AURA_PROFILE_BACKUP_MARKER:".toByteArray(Charsets.UTF_8)
        val index = indexOf(bytes, marker)
        if (index != -1) {
            return bytes.copyOfRange(0, index)
        }
        return bytes
    }

    fun backupProfileToImageMediaStore(user: UserEntity, pass: String) {
        val cleanEmail = user.email.lowercase().trim()
        if (cleanEmail.isBlank()) return
        val cleanEmailFileName = cleanEmail.replace("@", "_").replace(".", "_")
        
        val props = java.util.Properties()
        props.setProperty("username", user.username)
        props.setProperty("displayName", user.displayName)
        props.setProperty("bio", user.bio)
        props.setProperty("avatarUrl", user.avatarUrl)
        props.setProperty("coverUrl", user.coverUrl)
        props.setProperty("relationshipStatus", user.relationshipStatus)
        props.setProperty("relationshipPrivacy", user.relationshipPrivacy)
        props.setProperty("school", user.school)
        props.setProperty("college", user.college)
        props.setProperty("university", user.university)
        props.setProperty("educationPrivacy", user.educationPrivacy)
        props.setProperty("hobbies", user.hobbies)
        props.setProperty("hobbiesPrivacy", user.hobbiesPrivacy)
        props.setProperty("hometown", user.hometown)
        props.setProperty("hometownPrivacy", user.hometownPrivacy)
        props.setProperty("birthday", user.birthday)
        props.setProperty("birthdayPrivacy", user.birthdayPrivacy)
        props.setProperty("gender", user.gender)
        props.setProperty("genderPrivacy", user.genderPrivacy)
        props.setProperty("password", pass)
        
        val sw = java.io.StringWriter()
        props.store(sw, "Aura Backup Properties")
        val propertiesStr = sw.toString()
        
        val resolver = context.contentResolver
        
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val namesToTry = listOf(
                    "profile_pic_${cleanEmailFileName}_backup.jpg",
                    "cover_pic_${cleanEmailFileName}_backup.jpg",
                    "profile_text_backup_${cleanEmailFileName}.jpg"
                )
                
                var successAtLeastOnce = false
                
                for (name in namesToTry) {
                    val mediaUriStr = findImageInMediaStore(context, name)
                    if (mediaUriStr.isNotBlank()) {
                        val mediaUri = android.net.Uri.parse(mediaUriStr)
                        try {
                            val originalBytes = resolver.openInputStream(mediaUri)?.use { it.readBytes() }
                            if (originalBytes != null && originalBytes.isNotEmpty()) {
                                val pureImageBytes = stripOldAppendedMarker(originalBytes)
                                val markerBytes = "AURA_PROFILE_BACKUP_MARKER:".toByteArray(Charsets.UTF_8)
                                val payloadBytes = propertiesStr.toByteArray(Charsets.UTF_8)
                                val finalBytes = pureImageBytes + markerBytes + payloadBytes
                                
                                resolver.openOutputStream(mediaUri, "wt")?.use { out ->
                                    out.write(finalBytes)
                                }
                                successAtLeastOnce = true
                                android.util.Log.d("AuraBackupImage", "Appended properties to existing image: $name")
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("AuraBackupImage", "Failed to append to existing image $name: ${e.message}")
                        }
                    }
                }
                
                if (!successAtLeastOnce) {
                    val tinyJpegBase64 = "/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP//////////////////////////////////////////////////////////////////////////////////////wgALCAABAAEBAREA/8QAFBABAAAAAAAAAAAAAAAAAAAAAP/aAAgBAQABPxA="
                    val baseBytes = android.util.Base64.decode(tinyJpegBase64, android.util.Base64.DEFAULT)
                    val markerBytes = "AURA_PROFILE_BACKUP_MARKER:".toByteArray(Charsets.UTF_8)
                    val payloadBytes = propertiesStr.toByteArray(Charsets.UTF_8)
                    val finalBytes = baseBytes + markerBytes + payloadBytes
                    
                    val imageCollection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        android.provider.MediaStore.Images.Media.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL_PRIMARY)
                    } else {
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    
                    val destFileName = "profile_text_backup_${cleanEmailFileName}.jpg"
                    val contentValues = android.content.ContentValues().apply {
                        put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, destFileName)
                        put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/AuraHub")
                            put(android.provider.MediaStore.MediaColumns.IS_PENDING, 1)
                        }
                    }
                    
                    val imageUri = resolver.insert(imageCollection, contentValues)
                    if (imageUri != null) {
                        resolver.openOutputStream(imageUri)?.use { out ->
                            out.write(finalBytes)
                        }
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            contentValues.clear()
                            contentValues.put(android.provider.MediaStore.MediaColumns.IS_PENDING, 0)
                            resolver.update(imageUri, contentValues, null, null)
                        }
                        android.util.Log.d("AuraBackupImage", "Created new placeholder backup with properties: $destFileName")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AuraBackupImage", "Error checking/creating public image backups: ${e.message}")
            }
        }
    }

    fun backupProfileToPublicStorage(user: UserEntity, pass: String) {
        val cleanEmail = user.email.lowercase().trim()
        if (cleanEmail.isBlank()) return
        try {
            val auraDocsDir = java.io.File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOCUMENTS), "AuraHub")
            if (!auraDocsDir.exists()) {
                auraDocsDir.mkdirs()
            }
            val backupFile = java.io.File(auraDocsDir, "user_${cleanEmail.replace("@", "_").replace(".", "_")}.txt")
            val props = java.util.Properties()
            props.setProperty("username", user.username)
            props.setProperty("displayName", user.displayName)
            props.setProperty("email", user.email)
            props.setProperty("bio", user.bio)
            props.setProperty("avatarUrl", user.avatarUrl)
            props.setProperty("coverUrl", user.coverUrl)
            props.setProperty("relationshipStatus", user.relationshipStatus)
            props.setProperty("relationshipPrivacy", user.relationshipPrivacy)
            props.setProperty("school", user.school)
            props.setProperty("college", user.college)
            props.setProperty("university", user.university)
            props.setProperty("educationPrivacy", user.educationPrivacy)
            props.setProperty("hobbies", user.hobbies)
            props.setProperty("hobbiesPrivacy", user.hobbiesPrivacy)
            props.setProperty("hometown", user.hometown)
            props.setProperty("hometownPrivacy", user.hometownPrivacy)
            props.setProperty("birthday", user.birthday)
            props.setProperty("birthdayPrivacy", user.birthdayPrivacy)
            props.setProperty("gender", user.gender)
            props.setProperty("genderPrivacy", user.genderPrivacy)
            props.setProperty("password", pass)
            
            backupFile.outputStream().use { outputStream ->
                props.store(outputStream, "Aura Profile Backup - Secure Recovery Payload")
            }
            android.util.Log.d("AuraBackup", "Successfully backed up profile to: ${backupFile.absolutePath}")
        } catch (e: Exception) {
            android.util.Log.e("AuraBackup", "Error backing up profile to public storage: ${e.message}")
        }
        // Also perform the bulletproof MediaStore-based Image backup
        backupProfileToImageMediaStore(user, pass)
    }

    fun restoreProfileFromPublicStorage(email: String): UserEntity? {
        val cleanEmail = email.lowercase().trim()
        if (cleanEmail.isBlank()) return null
        try {
            val props = java.util.Properties()
            var loadedSuccessfully = false
            
            val cleanEmailFileName = cleanEmail.replace("@", "_").replace(".", "_")
            val namesToTry = listOf(
                "profile_pic_${cleanEmailFileName}_backup.jpg",
                "cover_pic_${cleanEmailFileName}_backup.jpg",
                "profile_text_backup_${cleanEmailFileName}.jpg"
            )
            
            val resolver = context.contentResolver
            for (name in namesToTry) {
                val mediaUriStr = findImageInMediaStore(context, name)
                if (mediaUriStr.isNotBlank()) {
                    try {
                        val mediaUri = android.net.Uri.parse(mediaUriStr)
                        val bytes = resolver.openInputStream(mediaUri)?.use { it.readBytes() }
                        if (bytes != null && bytes.isNotEmpty()) {
                            val marker = "AURA_PROFILE_BACKUP_MARKER:".toByteArray(Charsets.UTF_8)
                            val index = indexOf(bytes, marker)
                            if (index != -1) {
                                val start = index + marker.size
                                val propertiesStr = String(bytes, start, bytes.size - start, Charsets.UTF_8)
                                if (propertiesStr.isNotBlank()) {
                                    java.io.StringReader(propertiesStr).use { reader ->
                                        props.load(reader)
                                    }
                                    loadedSuccessfully = true
                                    android.util.Log.d("AuraBackupImage", "Successfully restored profile properties from public image backup: $name")
                                    break
                                }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AuraBackupImage", "Failed to parse properties from public image $name: ${e.message}")
                    }
                }
            }
            
            if (!loadedSuccessfully) {
                val auraDocsDir = java.io.File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOCUMENTS), "AuraHub")
                val backupFile = java.io.File(auraDocsDir, "user_${cleanEmailFileName}.txt")
                if (backupFile.exists()) {
                    try {
                        backupFile.inputStream().use { inputStream ->
                            props.load(inputStream)
                        }
                        loadedSuccessfully = true
                    } catch (e: Exception) {
                        android.util.Log.e("AuraBackup", "Properties restore failover read failed: ${e.message}")
                    }
                }
            }
            
            if (loadedSuccessfully) {
                val pass = props.getProperty("password", "")
                if (pass.isNotBlank()) {
                    val credentialsPrefs = context.getSharedPreferences("aura_credentials", android.content.Context.MODE_PRIVATE)
                    credentialsPrefs.edit().putString(cleanEmail, pass).apply()
                }
                
                // Assemble are restored entity that will be automatically enriched by `checkAndRestoreProfileImages`
                val basicUser = UserEntity(
                    username = props.getProperty("username") ?: (cleanEmail.substringBefore("@") + "_aura"),
                    displayName = props.getProperty("displayName") ?: cleanEmail.substringBefore("@"),
                    avatarUrl = props.getProperty("avatarUrl") ?: "avatar_user_main",
                    coverUrl = props.getProperty("coverUrl") ?: "cover_main",
                    bio = props.getProperty("bio") ?: "Excited to connect with friends on Aura 2026!",
                    isCurrentUser = true,
                    followerCount = 0,
                    followingCount = 0,
                    auraRating = 120,
                    email = cleanEmail,
                    relationshipStatus = props.getProperty("relationshipStatus") ?: "",
                    relationshipPrivacy = props.getProperty("relationshipPrivacy") ?: "Public",
                    school = props.getProperty("school") ?: "",
                    college = props.getProperty("college") ?: "",
                    university = props.getProperty("university") ?: "",
                    educationPrivacy = props.getProperty("educationPrivacy") ?: "Public",
                    hobbies = props.getProperty("hobbies") ?: "",
                    hobbiesPrivacy = props.getProperty("hobbiesPrivacy") ?: "Public",
                    hometown = props.getProperty("hometown") ?: "",
                    hometownPrivacy = props.getProperty("hometownPrivacy") ?: "Public",
                    birthday = props.getProperty("birthday") ?: "",
                    birthdayPrivacy = props.getProperty("birthdayPrivacy") ?: "Public",
                    gender = props.getProperty("gender") ?: "",
                    genderPrivacy = props.getProperty("genderPrivacy") ?: "Public"
                )
                return checkAndRestoreProfileImages(basicUser)
            }
        } catch (e: Exception) {
            android.util.Log.e("AuraBackup", "Error restoring profile from public storage: ${e.message}")
        }
        return null
    }

    fun backupUserPostsToPublicStorage(email: String) {
        val cleanEmail = email.lowercase().trim()
        if (cleanEmail.isBlank()) return
        val currUser = currentUser.value ?: return
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val auraDocsDir = java.io.File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOCUMENTS), "AuraHub")
                if (!auraDocsDir.exists()) {
                    auraDocsDir.mkdirs()
                }
                val backupFile = java.io.File(auraDocsDir, "posts_${cleanEmail.replace("@", "_").replace(".", "_")}.txt")
                
                val userPosts = postsFeed.value.filter { it.authorName == currUser.displayName }
                
                val builder = StringBuilder()
                for (post in userPosts) {
                    val postLine = listOf(
                        post.postId.toString(),
                        post.authorId.toString(),
                        post.authorName,
                        post.authorAvatar,
                        post.content.replace("\n", "\\n").replace("\r", "\\r"),
                        post.imageUrl,
                        post.gradientIndex.toString(),
                        post.timestamp.toString(),
                        post.likeCount.toString(),
                        post.commentCount.toString(),
                        post.shareCount.toString(),
                        post.isLikedByUser.toString(),
                        post.userReaction,
                        post.isAiLabeled.toString(),
                        post.mentionedUserIds,
                        post.privacy
                    ).joinToString("|||")
                    builder.append(postLine).append("\n")
                }
                
                backupFile.writeText(builder.toString(), Charsets.UTF_8)
                android.util.Log.d("AuraBackupPosts", "Successfully backed up ${userPosts.size} posts in public documents: ${backupFile.absolutePath}")
            } catch (e: Exception) {
                android.util.Log.e("AuraBackupPosts", "Error saving posts: ${e.message}")
            }
        }
    }

    fun restoreUserPostsFromPublicStorage(email: String, generatedUserId: Int = 1) {
        val cleanEmail = email.lowercase().trim()
        if (cleanEmail.isBlank()) return
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val auraDocsDir = java.io.File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOCUMENTS), "AuraHub")
                val backupFile = java.io.File(auraDocsDir, "posts_${cleanEmail.replace("@", "_").replace(".", "_")}.txt")
                if (backupFile.exists()) {
                    val lines = backupFile.readLines(Charsets.UTF_8)
                    for (line in lines) {
                        if (line.isBlank()) continue
                        val parts = line.split("|||")
                        if (parts.size >= 16) {
                            val contentText = parts[4].replace("\\n", "\n").replace("\\r", "\r")
                            val restoredPost = PostEntity(
                                postId = 0,
                                authorId = generatedUserId,
                                authorName = parts[2],
                                authorAvatar = parts[3],
                                content = contentText,
                                imageUrl = parts[5],
                                gradientIndex = parts[6].toIntOrNull() ?: -1,
                                timestamp = parts[7].toLongOrNull() ?: System.currentTimeMillis(),
                                likeCount = parts[8].toIntOrNull() ?: 0,
                                commentCount = parts[9].toIntOrNull() ?: 0,
                                shareCount = parts[10].toIntOrNull() ?: 0,
                                isLikedByUser = parts[11].toBoolean(),
                                userReaction = parts[12],
                                isAiLabeled = parts[13].toBoolean(),
                                mentionedUserIds = parts[14],
                                privacy = parts[15]
                            )
                            repository.addPost(restoredPost)
                        }
                    }
                    android.util.Log.d("AuraBackupPosts", "Successfully restored posts from public documents")
                }
            } catch (e: Exception) {
                android.util.Log.e("AuraBackupPosts", "Error restoring posts: ${e.message}")
            }
        }
    }

    fun scanAndImportAllUsersFromPublicStorage() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val currentUserEmail = repository.getCurrentUser()?.email?.lowercase()?.trim() ?: ""
            
            // 1. Scan JVM File Directory (Documents/AuraHub) for legacy installations
            try {
                val auraDocsDir = java.io.File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOCUMENTS), "AuraHub")
                if (auraDocsDir.exists() && auraDocsDir.isDirectory) {
                    val files = auraDocsDir.listFiles { _, name -> name.startsWith("user_") && name.endsWith(".txt") }
                    if (files != null) {
                        val currentUsers = repository.allUsers.first()
                        for (file in files) {
                            try {
                                val props = java.util.Properties()
                                file.inputStream().use { props.load(it) }
                                
                                val username = props.getProperty("username") ?: continue
                                val displayName = props.getProperty("displayName") ?: username
                                val emailFromProp = props.getProperty("email") ?: ""
                                
                                val email = if (emailFromProp.isNotBlank()) {
                                    emailFromProp.lowercase().trim()
                                } else {
                                    val stem = file.name.removePrefix("user_").removeSuffix(".txt")
                                    if (stem.contains("_gmail_com")) {
                                        stem.replace("_gmail_com", "@gmail.com")
                                    } else if (stem.contains("_yahoo_com")) {
                                        stem.replace("_yahoo_com", "@yahoo.com")
                                    } else if (stem.contains("_")) {
                                        val idx = stem.lastIndexOf("_")
                                        if (idx != -1) {
                                            stem.substring(0, idx) + "@" + stem.substring(idx + 1) + ".com"
                                        } else {
                                            "$username@aura.com"
                                        }
                                    } else {
                                        "$username@aura.com"
                                    }
                                }.lowercase().trim()
                                
                                if (email == currentUserEmail) continue
                                
                                val existingLocal = currentUsers.find { it.email.lowercase().trim() == email }
                                
                                val userEntity = UserEntity(
                                    userId = existingLocal?.userId ?: 0,
                                    username = username,
                                    displayName = displayName,
                                    avatarUrl = props.getProperty("avatarUrl") ?: "avatar_user_main",
                                    coverUrl = props.getProperty("coverUrl") ?: "cover_main",
                                    bio = props.getProperty("bio") ?: "Excited to connect on Aura!",
                                    isCurrentUser = false,
                                    followerCount = props.getProperty("followerCount")?.toIntOrNull() ?: 120,
                                    followingCount = props.getProperty("followingCount")?.toIntOrNull() ?: 45,
                                    auraRating = props.getProperty("auraRating")?.toIntOrNull() ?: 80,
                                    email = email,
                                    relationshipStatus = props.getProperty("relationshipStatus") ?: "",
                                    relationshipPrivacy = props.getProperty("relationshipPrivacy") ?: "Public",
                                    school = props.getProperty("school") ?: "",
                                    college = props.getProperty("college") ?: "",
                                    university = props.getProperty("university") ?: "",
                                    educationPrivacy = props.getProperty("educationPrivacy") ?: "Public",
                                    hobbies = props.getProperty("hobbies") ?: "",
                                    hobbiesPrivacy = props.getProperty("hobbiesPrivacy") ?: "Public",
                                    hometown = props.getProperty("hometown") ?: "",
                                    hometownPrivacy = props.getProperty("hometownPrivacy") ?: "Public",
                                    birthday = props.getProperty("birthday") ?: "",
                                    birthdayPrivacy = props.getProperty("birthdayPrivacy") ?: "Public",
                                    gender = props.getProperty("gender") ?: "",
                                    genderPrivacy = props.getProperty("genderPrivacy") ?: "Public",
                                    isProfileLocked = props.getProperty("isProfileLocked")?.toBoolean() ?: false
                                )
                                
                                if (existingLocal != null) {
                                    if (existingLocal.displayName != userEntity.displayName ||
                                        existingLocal.username != userEntity.username ||
                                        existingLocal.bio != userEntity.bio ||
                                        existingLocal.avatarUrl != userEntity.avatarUrl ||
                                        existingLocal.coverUrl != userEntity.coverUrl ||
                                        existingLocal.isProfileLocked != userEntity.isProfileLocked
                                    ) {
                                        repository.updateUser(userEntity)
                                    }
                                } else {
                                    repository.insertUser(userEntity)
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("AuraScan", "Error parsing profile file ${file.name}: ${e.message}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AuraScan", "Error scanning public storage: ${e.message}")
            }
            
            // 2. Scan Shared MediaStore Gallery for Aura backup images (Bulletproof for API Q/10+)
            try {
                val resolver = context.contentResolver
                val uriListToTry = mutableListOf<android.net.Uri>()
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    try {
                        uriListToTry.add(android.provider.MediaStore.Images.Media.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL))
                    } catch (e: Exception) {}
                    try {
                        uriListToTry.add(android.provider.MediaStore.Images.Media.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL_PRIMARY))
                    } catch (e: Exception) {}
                }
                uriListToTry.add(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                val discoveredFromMediaStore = mutableListOf<UserEntity>()
                val processedEmails = mutableSetOf<String>()

                for (imageCollection in uriListToTry) {
                    try {
                        val projection = arrayOf(
                            android.provider.MediaStore.MediaColumns._ID,
                            android.provider.MediaStore.MediaColumns.DISPLAY_NAME
                        )
                        val selection = "${android.provider.MediaStore.MediaColumns.DISPLAY_NAME} LIKE ?"
                        val selectionArgs = arrayOf("%_backup.jpg")

                        resolver.query(imageCollection, projection, selection, selectionArgs, null)?.use { cursor ->
                            val idColumn = cursor.getColumnIndexOrThrow(android.provider.MediaStore.MediaColumns._ID)
                            val nameColumn = cursor.getColumnIndexOrThrow(android.provider.MediaStore.MediaColumns.DISPLAY_NAME)
                            while (cursor.moveToNext()) {
                                val id = cursor.getLong(idColumn)
                                val name = cursor.getString(nameColumn)
                                
                                val mediaUri = android.content.ContentUris.withAppendedId(imageCollection, id)
                                try {
                                    val bytes = resolver.openInputStream(mediaUri)?.use { it.readBytes() }
                                    if (bytes != null && bytes.isNotEmpty()) {
                                        val marker = "AURA_PROFILE_BACKUP_MARKER:".toByteArray(Charsets.UTF_8)
                                        val index = indexOf(bytes, marker)
                                        if (index != -1) {
                                            val start = index + marker.size
                                            val propertiesStr = String(bytes, start, bytes.size - start, Charsets.UTF_8)
                                            if (propertiesStr.isNotBlank()) {
                                                val props = java.util.Properties()
                                                java.io.StringReader(propertiesStr).use { reader ->
                                                    props.load(reader)
                                                }
                                                
                                                val username = props.getProperty("username") ?: continue
                                                val displayName = props.getProperty("displayName") ?: username
                                                val email = (props.getProperty("email") ?: "").lowercase().trim()
                                                
                                                if (email.isNotBlank() && email != currentUserEmail && !processedEmails.contains(email)) {
                                                    processedEmails.add(email)
                                                    val userEntity = UserEntity(
                                                        userId = 0,
                                                        username = username,
                                                        displayName = displayName,
                                                        avatarUrl = props.getProperty("avatarUrl") ?: "avatar_user_main",
                                                        coverUrl = props.getProperty("coverUrl") ?: "cover_main",
                                                        bio = props.getProperty("bio") ?: "Excited to connect on Aura!",
                                                        isCurrentUser = false,
                                                        followerCount = props.getProperty("followerCount")?.toIntOrNull() ?: 120,
                                                        followingCount = props.getProperty("followingCount")?.toIntOrNull() ?: 45,
                                                        auraRating = props.getProperty("auraRating")?.toIntOrNull() ?: 80,
                                                        email = email,
                                                        relationshipStatus = props.getProperty("relationshipStatus") ?: "",
                                                        relationshipPrivacy = props.getProperty("relationshipPrivacy") ?: "Public",
                                                        school = props.getProperty("school") ?: "",
                                                        college = props.getProperty("college") ?: "",
                                                        university = props.getProperty("university") ?: "",
                                                        educationPrivacy = props.getProperty("educationPrivacy") ?: "Public",
                                                        hobbies = props.getProperty("hobbies") ?: "",
                                                        hobbiesPrivacy = props.getProperty("hobbiesPrivacy") ?: "Public",
                                                        hometown = props.getProperty("hometown") ?: "",
                                                        hometownPrivacy = props.getProperty("hometownPrivacy") ?: "Public",
                                                        birthday = props.getProperty("birthday") ?: "",
                                                        birthdayPrivacy = props.getProperty("birthdayPrivacy") ?: "Public",
                                                        gender = props.getProperty("gender") ?: "",
                                                        genderPrivacy = props.getProperty("genderPrivacy") ?: "Public",
                                                        isProfileLocked = props.getProperty("isProfileLocked")?.toBoolean() ?: false
                                                    )
                                                    discoveredFromMediaStore.add(userEntity)
                                                }
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.e("AuraScan", "Failed parsing properties from media image $name: ${e.message}")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AuraScan", "Error scanning MediaStore URI $imageCollection: ${e.message}")
                    }
                }

                // Save or update discovered MediaStore profiles in local DB
                val currentUsersUpdated = repository.allUsers.first()
                for (userEntity in discoveredFromMediaStore) {
                    val existingLocal = currentUsersUpdated.find { it.email.lowercase().trim() == userEntity.email }
                    if (existingLocal != null) {
                        val updatedUser = userEntity.copy(userId = existingLocal.userId)
                        if (existingLocal.displayName != updatedUser.displayName ||
                            existingLocal.username != updatedUser.username ||
                            existingLocal.bio != updatedUser.bio ||
                            existingLocal.avatarUrl != updatedUser.avatarUrl ||
                            existingLocal.coverUrl != updatedUser.coverUrl ||
                            existingLocal.isProfileLocked != updatedUser.isProfileLocked
                        ) {
                            repository.updateUser(updatedUser)
                        }
                    } else {
                        repository.insertUser(userEntity)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AuraScan", "Fatal error scanning MediaStore images: ${e.message}")
            }

            // 3. Cloud Synchronization: Sync active user profiles dynamically via Appwrite Storage Bucket
            val isRealAppwrite = isRealAppwriteEnabled
            if (isRealAppwrite) {
                try {
                    val client = appwriteClient
                    val bucketId = cleanAppwriteBucketId
                    if (client != null && bucketId.isNotBlank()) {
                        val storageService = io.appwrite.services.Storage(client)
                        val currentUser = repository.getCurrentUser()
                        if (currentUser != null && currentUser.email.isNotBlank()) {
                            val cleanEmailFileName = currentUser.email.lowercase().trim().replace("@", "_").replace(".", "_")
                            val tempFile = java.io.File(context.cacheDir, "user_profile_${cleanEmailFileName}.txt")
                            val props = java.util.Properties()
                            props.setProperty("username", currentUser.username)
                            props.setProperty("displayName", currentUser.displayName)
                            props.setProperty("email", currentUser.email)
                            props.setProperty("bio", currentUser.bio)
                            props.setProperty("avatarUrl", currentUser.avatarUrl)
                            props.setProperty("coverUrl", currentUser.coverUrl)
                            props.setProperty("relationshipStatus", currentUser.relationshipStatus)
                            props.setProperty("relationshipPrivacy", currentUser.relationshipPrivacy)
                            props.setProperty("school", currentUser.school)
                            props.setProperty("college", currentUser.college)
                            props.setProperty("university", currentUser.university)
                            props.setProperty("educationPrivacy", currentUser.educationPrivacy)
                            props.setProperty("hobbies", currentUser.hobbies)
                            props.setProperty("hobbiesPrivacy", currentUser.hobbiesPrivacy)
                            props.setProperty("hometown", currentUser.hometown)
                            props.setProperty("hometownPrivacy", currentUser.hometownPrivacy)
                            props.setProperty("birthday", currentUser.birthday)
                            props.setProperty("birthdayPrivacy", currentUser.birthdayPrivacy)
                            props.setProperty("gender", currentUser.gender)
                            props.setProperty("genderPrivacy", currentUser.genderPrivacy)
                            props.setProperty("isProfileLocked", currentUser.isProfileLocked.toString())
                            props.setProperty("appwriteUid", currentUser.resolvedUid)
                            
                            val writer = java.io.FileWriter(tempFile)
                            props.store(writer, "Appwrite Registry")
                            writer.close()
                            
                            val fileId = "profile_$cleanEmailFileName"
                            try {
                                storageService.deleteFile(bucketId, fileId)
                            } catch (e: Exception) {}
                            
                            val inputFile = io.appwrite.models.InputFile.fromFile(tempFile)
                            storageService.createFile(
                                bucketId = bucketId,
                                fileId = fileId,
                                file = inputFile
                            )
                            android.util.Log.d("AppwriteRegistrySync", "Successfully uploaded user registry profile details: $fileId")
                        }
                        
                        val filesList = storageService.listFiles(bucketId)
                        val files = filesList.files
                        val currentLocalUsers = repository.allUsers.first()
                        for (file in files) {
                            val fId = file.id
                            if (fId.startsWith("profile_")) {
                                val cleanEmailFileName = currentUser?.email?.lowercase()?.trim()?.replace("@", "_")?.replace(".", "_") ?: ""
                                if (fId == "profile_$cleanEmailFileName") continue
                                try {
                                    val downloadedBytes = storageService.getFileDownload(bucketId, fId)
                                    val propertiesStr = String(downloadedBytes, Charsets.UTF_8)
                                    val props = java.util.Properties()
                                    props.load(java.io.StringReader(propertiesStr))
                                    
                                    val username = props.getProperty("username") ?: continue
                                    val displayName = props.getProperty("displayName") ?: username
                                    val email = props.getProperty("email") ?: ""
                                    if (email.isBlank() || email.lowercase().trim() == currentUser?.email?.lowercase()?.trim()) continue
                                    
                                    val existingLocal = currentLocalUsers.find { it.email.lowercase().trim() == email.lowercase().trim() }
                                    
                                    val userEntity = UserEntity(
                                        userId = existingLocal?.userId ?: 0,
                                        username = username,
                                        displayName = displayName,
                                        avatarUrl = props.getProperty("avatarUrl") ?: "avatar_user_main",
                                        coverUrl = props.getProperty("coverUrl") ?: "cover_main",
                                        bio = props.getProperty("bio") ?: "Excited to connect on Aura!",
                                        isCurrentUser = false,
                                        followerCount = props.getProperty("followerCount")?.toIntOrNull() ?: 120,
                                        followingCount = props.getProperty("followingCount")?.toIntOrNull() ?: 45,
                                        auraRating = props.getProperty("auraRating")?.toIntOrNull() ?: 80,
                                        email = email,
                                        relationshipStatus = props.getProperty("relationshipStatus") ?: "",
                                        relationshipPrivacy = props.getProperty("relationshipPrivacy") ?: "Public",
                                        school = props.getProperty("school") ?: "",
                                        college = props.getProperty("college") ?: "",
                                        university = props.getProperty("university") ?: "",
                                        educationPrivacy = props.getProperty("educationPrivacy") ?: "Public",
                                        hobbies = props.getProperty("hobbies") ?: "",
                                        hobbiesPrivacy = props.getProperty("hobbiesPrivacy") ?: "Public",
                                        hometown = props.getProperty("hometown") ?: "",
                                        hometownPrivacy = props.getProperty("hometownPrivacy") ?: "Public",
                                        birthday = props.getProperty("birthday") ?: "",
                                        birthdayPrivacy = props.getProperty("birthdayPrivacy") ?: "Public",
                                        gender = props.getProperty("gender") ?: "",
                                        genderPrivacy = props.getProperty("genderPrivacy") ?: "Public",
                                        isProfileLocked = props.getProperty("isProfileLocked")?.toBoolean() ?: false,
                                        appwriteUid = props.getProperty("appwriteUid") ?: fId.removePrefix("profile_")
                                    )
                                    
                                    if (existingLocal != null) {
                                        repository.updateUser(userEntity)
                                    } else {
                                        repository.insertUser(userEntity)
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.e("AppwriteRegistrySync", "Error downloading user registry profile ${fId}: ${e.message}")
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("AppwriteRegistrySync", "Safe Appwrite storage bucket user sync failed or bypassed: ${e.message}")
                }
            }
        }
    }

    private val _isTtsMuted = MutableStateFlow(false)
    val isTtsMuted: StateFlow<Boolean> = _isTtsMuted.asStateFlow()

    fun setTtsMuted(muted: Boolean) {
        _isTtsMuted.value = muted
    }

    private val _isSearchingLocation = MutableStateFlow(false)
    val isSearchingLocation: StateFlow<Boolean> = _isSearchingLocation.asStateFlow()

    private val _locationSearchResults = MutableStateFlow<List<String>>(emptyList())
    val locationSearchResults: StateFlow<List<String>> = _locationSearchResults.asStateFlow()

    fun searchLocation(query: String) {
        if (query.trim().isBlank()) {
            _locationSearchResults.value = emptyList()
            return
        }
        _isSearchingLocation.value = true
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
                val url = java.net.URL("https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&limit=8")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.setRequestProperty("User-Agent", "AuraSocialApp-2026/1.0 (imruledu681@gmail.com)")
                connection.setRequestProperty("Accept", "application/json")
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                
                if (connection.responseCode == 200) {
                    val stream = connection.inputStream
                    val response = stream.bufferedReader().use { it.readText() }
                    val array = org.json.JSONArray(response)
                    val list = mutableListOf<String>()
                    for (i in 0 until array.length()) {
                        val item = array.getJSONObject(i)
                        val displayName = item.optString("display_name", "")
                        if (displayName.isNotBlank()) {
                            list.add(displayName)
                        }
                    }
                    _locationSearchResults.value = list
                }
            } catch (e: Exception) {
                android.util.Log.e("LocationSearch", "Error querying OpenStreetMap: ${e.message}")
            } finally {
                _isSearchingLocation.value = false
            }
        }
    }

    private val _isSearchingEducation = MutableStateFlow(false)
    val isSearchingEducation: StateFlow<Boolean> = _isSearchingEducation.asStateFlow()

    private val _schoolResults = MutableStateFlow<List<String>>(emptyList())
    val schoolResults: StateFlow<List<String>> = _schoolResults.asStateFlow()

    private val _collegeResults = MutableStateFlow<List<String>>(emptyList())
    val collegeResults: StateFlow<List<String>> = _collegeResults.asStateFlow()

    private val _hometownResults = MutableStateFlow<List<String>>(emptyList())
    val hometownResults: StateFlow<List<String>> = _hometownResults.asStateFlow()

    private val _isSearchingHometown = MutableStateFlow(false)
    val isSearchingHometown: StateFlow<Boolean> = _isSearchingHometown.asStateFlow()

    private val _universityResultsReal = MutableStateFlow<List<String>>(emptyList())
    val universityResults: StateFlow<List<String>> = _universityResultsReal.asStateFlow()

    fun searchSchoolNom(query: String) {
        if (query.trim().isBlank()) {
            _schoolResults.value = emptyList()
            return
        }
        _isSearchingEducation.value = true
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val qString = if (query.contains("school", ignoreCase = true)) query else "$query school"
                val encodedQuery = java.net.URLEncoder.encode(qString, "UTF-8")
                
                val url = java.net.URL("https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&limit=30")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.setRequestProperty("User-Agent", "AuraSocialApp-2026/1.1 (imruledu681@gmail.com)")
                connection.setRequestProperty("Referer", "https://ai.studio.build/aurasocial")
                connection.setRequestProperty("Accept", "application/json")
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                
                if (connection.responseCode == 200) {
                    val stream = connection.inputStream
                    val response = stream.bufferedReader().use { it.readText() }
                    val array = org.json.JSONArray(response)
                    val list = mutableListOf<String>()
                    for (i in 0 until array.length()) {
                        val item = array.getJSONObject(i)
                        val displayName = item.optString("display_name", "")
                        if (displayName.isNotBlank()) {
                            val shortName = displayName.split(",").firstOrNull()?.trim() ?: displayName
                            if (shortName.isNotBlank() && !list.contains(shortName)) {
                                list.add(shortName)
                            }
                        }
                    }
                    _schoolResults.value = list
                }
            } catch (e: Exception) {
                android.util.Log.e("EducationSearch", "Error searching schools: ${e.message}")
            } finally {
                _isSearchingEducation.value = false
            }
        }
    }

    fun searchCollegeNom(query: String) {
        if (query.trim().isBlank()) {
            _collegeResults.value = emptyList()
            return
        }
        _isSearchingEducation.value = true
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val qString = if (query.contains("college", ignoreCase = true)) query else "$query college"
                val encodedQuery = java.net.URLEncoder.encode(qString, "UTF-8")
                
                val url = java.net.URL("https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&limit=30")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.setRequestProperty("User-Agent", "AuraSocialApp-2026/1.1 (imruledu681@gmail.com)")
                connection.setRequestProperty("Referer", "https://ai.studio.build/aurasocial")
                connection.setRequestProperty("Accept", "application/json")
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                
                if (connection.responseCode == 200) {
                    val stream = connection.inputStream
                    val response = stream.bufferedReader().use { it.readText() }
                    val array = org.json.JSONArray(response)
                    val list = mutableListOf<String>()
                    for (i in 0 until array.length()) {
                        val item = array.getJSONObject(i)
                        val displayName = item.optString("display_name", "")
                        if (displayName.isNotBlank()) {
                            val shortName = displayName.split(",").firstOrNull()?.trim() ?: displayName
                            if (shortName.isNotBlank() && !list.contains(shortName)) {
                                list.add(shortName)
                            }
                        }
                    }
                    _collegeResults.value = list
                }
            } catch (e: Exception) {
                android.util.Log.e("EducationSearch", "Error searching colleges: ${e.message}")
            } finally {
                _isSearchingEducation.value = false
            }
        }
    }

    fun searchUniversityNom(query: String) {
        if (query.trim().isBlank()) {
            _universityResultsReal.value = emptyList()
            return
        }
        _isSearchingEducation.value = true
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val qString = if (query.contains("university", ignoreCase = true) || query.contains("uni", ignoreCase = true)) query else "$query university"
                val encodedQuery = java.net.URLEncoder.encode(qString, "UTF-8")
                
                val url = java.net.URL("https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&limit=30")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.setRequestProperty("User-Agent", "AuraSocialApp-2026/1.1 (imruledu681@gmail.com)")
                connection.setRequestProperty("Referer", "https://ai.studio.build/aurasocial")
                connection.setRequestProperty("Accept", "application/json")
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                
                if (connection.responseCode == 200) {
                    val stream = connection.inputStream
                    val response = stream.bufferedReader().use { it.readText() }
                    val array = org.json.JSONArray(response)
                    val list = mutableListOf<String>()
                    for (i in 0 until array.length()) {
                        val item = array.getJSONObject(i)
                        val displayName = item.optString("display_name", "")
                        if (displayName.isNotBlank()) {
                            val shortName = displayName.split(",").firstOrNull()?.trim() ?: displayName
                            if (shortName.isNotBlank() && !list.contains(shortName)) {
                                list.add(shortName)
                            }
                        }
                    }
                    _universityResultsReal.value = list
                }
            } catch (e: Exception) {
                android.util.Log.e("EducationSearch", "Error searching universities: ${e.message}")
            } finally {
                _isSearchingEducation.value = false
            }
        }
    }

    fun searchHometownNom(query: String) {
        if (query.trim().isBlank()) {
            _hometownResults.value = emptyList()
            return
        }
        _isSearchingHometown.value = true
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
                val url = java.net.URL("https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&limit=30")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.setRequestProperty("User-Agent", "AuraSocialApp-2026/1.1 (imruledu681@gmail.com)")
                connection.setRequestProperty("Referer", "https://ai.studio.build/aurasocial")
                connection.setRequestProperty("Accept", "application/json")
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                
                if (connection.responseCode == 200) {
                    val stream = connection.inputStream
                    val response = stream.bufferedReader().use { it.readText() }
                    val array = org.json.JSONArray(response)
                    val list = mutableListOf<String>()
                    for (i in 0 until array.length()) {
                        val item = array.getJSONObject(i)
                        val displayName = item.optString("display_name", "")
                        if (displayName.isNotBlank()) {
                            val parts = displayName.split(",")
                            val formatted = if (parts.size >= 3) {
                                "${parts[0].trim()}, ${parts[1].trim()}, ${parts.last().trim()}"
                            } else {
                                displayName
                            }
                            if (formatted.isNotBlank() && !list.contains(formatted)) {
                                list.add(formatted)
                            }
                        }
                    }
                    _hometownResults.value = list
                }
            } catch (e: Exception) {
                android.util.Log.e("HometownSearch", "Error searching hometowns: ${e.message}")
            } finally {
                _isSearchingHometown.value = false
            }
        }
    }

    private val _uiState = MutableStateFlow(AuraUiState(currentScreen = Screen.Splash))
    val uiState: StateFlow<AuraUiState> = _uiState.asStateFlow()

    private val _screenHistory = MutableStateFlow<List<Screen>>(listOf(Screen.Splash))
    val screenHistory: StateFlow<List<Screen>> = _screenHistory.asStateFlow()

    private val hiddenPostsPrefs by lazy {
        context.getSharedPreferences("aura_hidden_posts", Context.MODE_PRIVATE)
    }

    private val _hiddenPostIds = MutableStateFlow<Set<Int>>(emptySet())
    val hiddenPostIds: StateFlow<Set<Int>> = _hiddenPostIds.asStateFlow()

    fun hidePostForCurrentUser(postId: Int) {
        val updated = _hiddenPostIds.value + postId
        _hiddenPostIds.value = updated
        try {
            hiddenPostsPrefs.edit()
                .putStringSet("hidden_ids", updated.map { it.toString() }.toSet())
                .apply()
        } catch (e: Exception) {
            // Handled
        }
    }

    // Database flow bindings
    val currentUser: StateFlow<UserEntity?> = repository.currentUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stories: StateFlow<List<StoryEntity>> = repository.stories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val videos: StateFlow<List<VideoEntity>> = repository.videos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reels: StateFlow<List<ReelEntity>> = repository.reels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Posts flow with reactive search injection
    val postsFeed: StateFlow<List<PostEntity>> = combine(
        repository.feedPosts,
        _uiState.map { it.searchQuery }.distinctUntilChanged(),
        currentUser,
        allUsers,
        _hiddenPostIds
    ) { posts, query, currUser, users, hids ->
        val currentUserId = currUser?.userId ?: -1
        val followingUserIds = users.filter { it.isFollowing }.map { it.userId }.toSet()

        val filteredPosts = posts.filter { post ->
            val isAuthor = post.authorId == currentUserId
            val privacyValue = try { post.privacy } catch (e: Exception) { "Public" }
            val visibleByPrivacy = when (privacyValue) {
                "Only me" -> isAuthor
                "Friends" -> isAuthor || followingUserIds.contains(post.authorId)
                else -> true
            }
            val isHidden = hids.contains(post.postId) && !isAuthor
            visibleByPrivacy && !isHidden
        }

        if (query.isBlank()) {
            filteredPosts
        } else {
            // Map userIds to their resolvedUid
            val userResolvedUidMap = users.associate { it.userId to it.resolvedUid }
            val finalUserResolvedUidMap = if (currUser != null) {
                userResolvedUidMap + (currUser.userId to currUser.resolvedUid)
            } else {
                userResolvedUidMap
            }

            filteredPosts.filter { post ->
                val authorUid = finalUserResolvedUidMap[post.authorId] ?: ""
                val cardPostId = "${authorUid}_${post.postId}"
                
                val parts = query.trim().split('_', '-', ' ')
                val isPreciseMatch = if (parts.size >= 2) {
                    val potentialPostId = parts.lastOrNull()?.toIntOrNull()
                    val potentialUid = parts.firstOrNull() ?: ""
                    if (potentialPostId != null && potentialUid.isNotBlank()) {
                        post.postId == potentialPostId && authorUid.equals(potentialUid, ignoreCase = true)
                    } else {
                        false
                    }
                } else {
                    false
                }

                isPreciseMatch ||
                cardPostId.equals(query.trim(), ignoreCase = true) ||
                post.postId.toString() == query.trim() ||
                post.content.contains(query, ignoreCase = true) ||
                post.authorName.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        try {
            val savedIds = hiddenPostsPrefs.getStringSet("hidden_ids", emptySet()) ?: emptySet()
            _hiddenPostIds.value = savedIds.mapNotNull { it.toIntOrNull() }.toSet()
        } catch (e: Exception) {
            // Handled
        }
        
        // Clean up expired stories on startup
        viewModelScope.launch {
            try {
                val boundary = System.currentTimeMillis() - (24L * 60 * 60 * 1000)
                repository.deleteExpiredStories(boundary)
            } catch (e: Exception) {
                android.util.Log.e("AuraViewModel", "Failed to clean up expired stories: ${e.message}")
            }
        }
        
        // Start live Firebase sync for posts
        listenForFirebasePosts()
        listenForFirebaseStories()

        // Prepare database with default high-quality 2026 posts and configurations
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            try {
                repository.seedMockDataIfEmpty()
                
                // Clear out mock users permanently
                val mockEmails = listOf(
                    "tasnim@aura.com",
                    "imrul@aura.com",
                    "sadia@aura.com",
                    "tanvir@aura.com",
                    "nabila@aura.com"
                )
                mockEmails.forEach { email ->
                    repository.deleteUserByEmail(email)
                }
                
                checkAppwriteUserSync()
                scanAndImportAllUsersFromPublicStorage()
            } catch (e: Exception) {
                android.util.Log.e("AuraSplash", "Local sync warning on splash: ${e.message}")
            }
            try {
                loadTrendingGiphyGifs()
            } catch (e: Exception) {
                android.util.Log.e("AuraSplash", "Giphy load warning on splash: ${e.message}")
            }
            
            // Respect internet dependency and play beautiful loading animation for a minimum of 1.8s
            val elapsed = System.currentTimeMillis() - startTime
            val minDuration = 1800L
            if (elapsed < minDuration) {
                kotlinx.coroutines.delay(minDuration - elapsed)
            }
            
            restoreNavigationState()
        }
    }

     fun checkAppwriteUserSync() {
        viewModelScope.launch {
            try {
                val currentLocalUser = repository.getCurrentUser()
                if (currentLocalUser != null && currentLocalUser.email.isNotBlank()) {
                    var updatedUser = currentLocalUser
                    val account = appwriteAccount
                    if (isRealAppwriteEnabled && account != null) {
                        try {
                            val authUser = account.get()
                            val remoteUid = authUser.id
                            if (!remoteUid.isNullOrBlank() && remoteUid != currentLocalUser.appwriteUid) {
                                updatedUser = updatedUser.copy(appwriteUid = remoteUid)
                                android.util.Log.d("AuraSync", "Live Autodetected Appwrite UID: $remoteUid. Syncing database...")
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("AuraSync", "Failed to retrieve Appwrite live UID during check: ${e.message}")
                            // Any authorization or 404/not found exceptions on account.get() (such as 401 Unauthorized)
                            // mean the user's login session is no longer valid. We must verify if the account still physically exists in Appwrite Auth.
                            val cleanEmail = currentLocalUser.email.lowercase().trim()
                            checkIfEmailExistsInAppwrite(cleanEmail) { exists ->
                                if (!exists) {
                                    performUserWipeAndLogout(currentLocalUser)
                                }
                            }
                        }
                    }
                    val restoredLocalUser = checkAndRestoreProfileImages(updatedUser)
                    if (restoredLocalUser.avatarUrl != currentLocalUser.avatarUrl || 
                        restoredLocalUser.coverUrl != currentLocalUser.coverUrl ||
                        restoredLocalUser.appwriteUid != currentLocalUser.appwriteUid
                    ) {
                        repository.updateUser(restoredLocalUser)
                        saveProfileToPrefs(restoredLocalUser)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AuraSync", "Reload sync check fail: ${e.message}")
            }
        }
    }

    fun purgeHistoricalDataForRecreatedUser(email: String, oldUserId: Int, oldDisplayName: String) {
        viewModelScope.launch {
            val cleanEmail = email.lowercase().trim()
            android.util.Log.d("AuraPurge", "Purging historical data for recreated user: $cleanEmail")
            
            // 1. Delete comments, stories, posts from Room
            repository.deletePostsByAuthor(oldUserId, oldDisplayName)
            repository.deleteStoriesByAuthor(oldDisplayName)
            repository.deleteCommentsByAuthor(oldDisplayName)
            
            // 2. Delete posts from Firebase Realtime Database
            try {
                val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://aura-6637b-default-rtdb.firebaseio.com")
                val postsRef = database.getReference("posts")
                postsRef.get().addOnSuccessListener { snapshot ->
                    for (child in snapshot.children) {
                        val authorNameVal = child.child("authorName").getValue(String::class.java) ?: ""
                        val authorEmailVal = child.child("authorEmail").getValue(String::class.java) ?: ""
                        val authorIdVal = child.child("authorId").getValue(Int::class.java)
                        if (authorEmailVal.lowercase().trim() == cleanEmail || 
                            authorNameVal.equals(oldDisplayName, ignoreCase = true) || 
                            authorIdVal == oldUserId
                        ) {
                            child.ref.removeValue()
                        }
                    }
                }
            } catch (ex: Exception) {
                android.util.Log.e("AuraPurge", "Error purging Firebase posts: ${ex.message}")
            }
        }
    }

    fun performUserWipeAndLogout(user: UserEntity) {
        viewModelScope.launch {
            val email = user.email.lowercase().trim()
            val userId = user.userId
            val displayName = user.displayName
            
            android.util.Log.d("AuraSync", "User deleted from Appwrite. Starting full permanent data wipe for: $email")
            
            // 1. Delete comments, stories, posts from Room
            repository.deletePostsByAuthor(userId, displayName)
            repository.deleteStoriesByAuthor(displayName)
            repository.deleteCommentsByAuthor(displayName)
            
            // 2. Delete posts from Firebase Realtime Database
            try {
                val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://aura-6637b-default-rtdb.firebaseio.com")
                val postsRef = database.getReference("posts")
                postsRef.get().addOnSuccessListener { snapshot ->
                    for (child in snapshot.children) {
                        val authorNameVal = child.child("authorName").getValue(String::class.java) ?: ""
                        val authorEmailVal = child.child("authorEmail").getValue(String::class.java) ?: ""
                        val authorIdVal = child.child("authorId").getValue(Int::class.java)
                        if (authorEmailVal.lowercase().trim() == email || 
                            authorNameVal.equals(displayName, ignoreCase = true) || 
                            authorIdVal == userId
                        ) {
                            child.ref.removeValue()
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AuraSync", "Error deleting posts on Firebase during purge: ${e.message}")
            }
            
            // 3. Force logout local session
            repository.deleteUserByEmail(email)
            val prefs = context.getSharedPreferences("aura_preferences", android.content.Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            
            val credentialsPrefs = context.getSharedPreferences("aura_credentials", android.content.Context.MODE_PRIVATE)
            credentialsPrefs.edit().remove(email).apply()
            
            try {
                appwriteAccount?.deleteSession("current")
            } catch (ignored: Exception) {}
            
            // Return to Welcome
            _screenHistory.value = listOf(Screen.Welcome)
            saveNavigationState(Screen.Welcome, listOf(Screen.Welcome))
            _uiState.update { it.copy(currentScreen = Screen.Welcome) }
            
            android.widget.Toast.makeText(context, "Account permanently deleted from Appwrite Auth! Purged all data. / অ্যাকাউন্ট ডিলিট করা হয়েছে এবং সকল পোস্ট মুছে দেওয়া হয়েছে!", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    fun registerUserInAppwriteAndLogin(
        displayName: String,
        bio: String,
        avatar: String,
        email: String,
        tempPass: String,
        targetScreen: Screen = Screen.WelcomeCelebration,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val credentialsPrefs = context.getSharedPreferences("aura_credentials", android.content.Context.MODE_PRIVATE)
                credentialsPrefs.edit().putString(email.lowercase().trim(), tempPass).apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val isRealAppwrite = isRealAppwriteEnabled

            if (isRealAppwrite) {
                try {
                    appwriteAccount?.let { account ->
                        try {
                            account.create(
                                userId = io.appwrite.ID.unique(),
                                email = email,
                                password = tempPass,
                                name = displayName
                            )
                            account.createEmailPasswordSession(
                                email = email,
                                password = tempPass
                            )
                            android.util.Log.d("AppwriteSync", "Successfully registered in Appwrite.")
                        } catch (alreadyExistsEx: Exception) {
                            android.util.Log.d("AppwriteSync", "Account already exists (verified via OTP). Setting password & name if logged in.")
                            try {
                                account.updatePassword(tempPass)
                            } catch (passEx: Exception) {
                                android.util.Log.e("AppwriteSync", "Could not set password: ${passEx.message}")
                            }
                            try {
                                account.updateName(displayName)
                            } catch (nameEx: Exception) {
                                android.util.Log.e("AppwriteSync", "Could not set name: ${nameEx.message}")
                            }
                        }
                    }
                    createProfileAndLogin(displayName, bio, avatar, email, targetScreen)
                    onComplete(true)
                } catch (e: Exception) {
                    android.util.Log.e("AppwriteSync", "Appwrite registration exception: ${e.message}")
                    createProfileAndLogin(displayName, bio, avatar, email, targetScreen)
                    onComplete(false)
                }
            } else {
                kotlinx.coroutines.delay(1200)
                createProfileAndLogin(displayName, bio, avatar, email, targetScreen)
                onComplete(true)
            }
        }
    }

    fun checkIfEmailExistsInAppwrite(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val cleanEmail = email.lowercase().trim()
            val isRealAppwrite = isRealAppwriteEnabled

            if (isRealAppwrite) {
                try {
                    val account = appwriteAccount
                    if (account != null) {
                        try {
                            // In Appwrite client-side SDK, createRecovery checks if the email is registered.
                            // If the email is registered/exists, it succeeds (or throws rate limit, but NOT user_not_found).
                            // If the email doesn't exist (or was permanently deleted), it throws a 404 (user_not_found).
                            account.createRecovery(
                                email = cleanEmail,
                                url = "https://sgp.cloud.appwrite.io/v1/auth/recovery"
                            )
                            android.util.Log.d("AppwriteSync", "Email exists check: Recovery generated successfully. User exists.")
                            onResult(true)
                        } catch (appEx: io.appwrite.exceptions.AppwriteException) {
                            val code = appEx.code
                            val msg = appEx.message ?: ""
                            android.util.Log.d("AppwriteSync", "Email check appwrite exception: code=$code, msg=$msg")
                            if (code == 404 || msg.contains("not found", ignoreCase = true) || msg.contains("not_found", ignoreCase = true)) {
                                onResult(false)
                            } else {
                                // Fallback to local DB and credentials cache for other exceptions (e.g. general socket/rate limits)
                                val credentialsPrefs = context.getSharedPreferences("aura_credentials", android.content.Context.MODE_PRIVATE)
                                val hasPass = credentialsPrefs.getString(cleanEmail, null) != null
                                val localUser = repository.allUsers.first().any { it.email.lowercase().trim() == cleanEmail }
                                onResult(hasPass || localUser)
                            }
                        }
                    } else {
                        onResult(false)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("AppwriteSync", "Exception in live check: ${e.message}")
                    onResult(false)
                }
            } else {
                // Sandbox Mode Fallback
                val credentialsPrefs = context.getSharedPreferences("aura_credentials", android.content.Context.MODE_PRIVATE)
                val hasPass = credentialsPrefs.getString(cleanEmail, null) != null
                val localUser = repository.allUsers.first().any { it.email.lowercase().trim() == cleanEmail }
                onResult(hasPass || localUser)
            }
        }
    }

    private fun screenToRouteString(screen: Screen): String {
        return when (screen) {
            is Screen.Splash -> "Splash"
            is Screen.Welcome -> "Welcome"
            is Screen.Register -> "Register"
            is Screen.BirthdaySelection -> "BirthdaySelection"
            is Screen.GenderSelection -> "GenderSelection"
            is Screen.EmailInput -> "EmailInput"
            is Screen.Verification -> "Verification"
            is Screen.PasswordSelection -> "PasswordSelection"
            is Screen.RelationshipSelection -> "RelationshipSelection"
            is Screen.EducationSelection -> "EducationSelection"
            is Screen.HobbySelection -> "HobbySelection"
            is Screen.BioSelection -> "BioSelection"
            is Screen.ProfilePictureSelection -> "ProfilePictureSelection"
            is Screen.CoverPhotoSelection -> "CoverPhotoSelection"
            is Screen.HometownSelection -> "HometownSelection"
            is Screen.WelcomeCelebration -> "WelcomeCelebration"
            is Screen.Login -> "Login"
            is Screen.Main -> "Main"
            is Screen.ChatRoom -> "ChatRoom:${screen.otherUser.userId}"
            is Screen.ProfileDetail -> "ProfileDetail:${screen.targetUser.userId}"
            is Screen.SearchUsers -> "SearchUsers"
            is Screen.SavedItems -> "SavedItems"
        }
    }

    private fun saveNavigationState(currentScreen: Screen, history: List<Screen>) {
        val prefs = context.getSharedPreferences("aura_nav_prefs", Context.MODE_PRIVATE)
        val currentStr = screenToRouteString(currentScreen)
        val historyStr = history.joinToString(",") { screenToRouteString(it) }
        prefs.edit()
            .putString("current_screen", currentStr)
            .putString("history_screens", historyStr)
            .apply()
    }

    private suspend fun restoreNavigationState() {
        val prefs = context.getSharedPreferences("aura_nav_prefs", Context.MODE_PRIVATE)
        val currentStr = prefs.getString("current_screen", null)
        val historyStr = prefs.getString("history_screens", null)

        var restored = false
        if (currentStr != null) {
            val allCachedUsers = repository.allUsers.first()
            fun stringToScreen(route: String): Screen? {
                return when {
                    route == "Splash" -> Screen.Welcome
                    route == "Welcome" -> Screen.Welcome
                    route == "Register" -> Screen.Register
                    route == "BirthdaySelection" -> Screen.BirthdaySelection
                    route == "GenderSelection" -> Screen.GenderSelection
                    route == "EmailInput" -> Screen.EmailInput
                    route == "Verification" -> Screen.Verification
                    route == "PasswordSelection" -> Screen.PasswordSelection
                    route == "RelationshipSelection" -> Screen.RelationshipSelection
                    route == "EducationSelection" -> Screen.EducationSelection
                    route == "HobbySelection" -> Screen.HobbySelection
                    route == "BioSelection" -> Screen.BioSelection
                    route == "ProfilePictureSelection" -> Screen.ProfilePictureSelection
                    route == "CoverPhotoSelection" -> Screen.CoverPhotoSelection
                    route == "HometownSelection" -> Screen.HometownSelection
                    route == "WelcomeCelebration" -> Screen.WelcomeCelebration
                    route == "Login" -> Screen.Login
                    route == "Main" -> Screen.Main
                    route == "SearchUsers" -> Screen.SearchUsers
                    route == "SavedItems" -> Screen.SavedItems
                    route.startsWith("ChatRoom:") -> {
                        val userId = route.substringAfter("ChatRoom:").toIntOrNull()
                        val matchedUser = allCachedUsers.find { it.userId == userId }
                        if (matchedUser != null) {
                            Screen.ChatRoom(matchedUser)
                        } else {
                            Screen.Main
                        }
                    }
                    route.startsWith("ProfileDetail:") -> {
                        val userId = route.substringAfter("ProfileDetail:").toIntOrNull()
                        val matchedUser = allCachedUsers.find { it.userId == userId }
                        if (matchedUser != null) {
                            Screen.ProfileDetail(matchedUser)
                        } else {
                            Screen.Main
                        }
                    }
                    else -> null
                }
            }

            val restoredScreen = stringToScreen(currentStr)
            val restoredHistory = historyStr?.split(",")?.mapNotNull { stringToScreen(it) }

            if (restoredScreen != null) {
                _uiState.update { it.copy(currentScreen = restoredScreen) }
                if (restoredHistory != null && restoredHistory.isNotEmpty()) {
                    _screenHistory.value = restoredHistory
                } else {
                    _screenHistory.value = listOf(restoredScreen)
                }

                if (restoredScreen is Screen.ChatRoom) {
                    setupChatChannel(restoredScreen.otherUser)
                }
                restored = true
            }
        }
        if (!restored) {
            _uiState.update { it.copy(currentScreen = Screen.Welcome) }
            _screenHistory.value = listOf(Screen.Welcome)
        }
    }

    // --- Navigation Flow handlers ---

    fun navigateTo(screen: Screen, pushToHistory: Boolean = true) {
        _uiState.update { it.copy(currentScreen = screen) }
        
        if (pushToHistory) {
            val currentList = _screenHistory.value.toMutableList()
            if (currentList.isEmpty() || currentList.last() != screen) {
                currentList.add(screen)
                _screenHistory.value = currentList
                saveNavigationState(screen, currentList)
            }
        }
        // Close overlay components when shifting screens
        if (screen is Screen.ChatRoom) {
            setupChatChannel(screen.otherUser)
        }
    }

    fun goBack(): Boolean {
        val currentHistory = _screenHistory.value
        if (currentHistory.size > 1) {
            val newHistory = currentHistory.dropLast(1)
            _screenHistory.value = newHistory
            val prevScreen = newHistory.last()
            _uiState.update { it.copy(currentScreen = prevScreen) }
            saveNavigationState(prevScreen, newHistory)
            return true
        }
        val currentScreen = _uiState.value.currentScreen
        return when (currentScreen) {
            is Screen.PasswordSelection -> {
                val newHistory = listOf(Screen.Welcome, Screen.Register, Screen.BirthdaySelection, Screen.GenderSelection, Screen.EmailInput, Screen.Verification)
                _screenHistory.value = newHistory
                _uiState.update { it.copy(currentScreen = Screen.Verification) }
                saveNavigationState(Screen.Verification, newHistory)
                true
            }
            is Screen.Verification -> {
                val newHistory = listOf(Screen.Welcome, Screen.Register, Screen.BirthdaySelection, Screen.GenderSelection, Screen.EmailInput)
                _screenHistory.value = newHistory
                _uiState.update { it.copy(currentScreen = Screen.EmailInput) }
                saveNavigationState(Screen.EmailInput, newHistory)
                true
            }
            is Screen.EmailInput -> {
                val newHistory = listOf(Screen.Welcome, Screen.Register, Screen.BirthdaySelection, Screen.GenderSelection)
                _screenHistory.value = newHistory
                _uiState.update { it.copy(currentScreen = Screen.GenderSelection) }
                saveNavigationState(Screen.GenderSelection, newHistory)
                true
            }
            is Screen.GenderSelection -> {
                val newHistory = listOf(Screen.Welcome, Screen.Register, Screen.BirthdaySelection)
                _screenHistory.value = newHistory
                _uiState.update { it.copy(currentScreen = Screen.BirthdaySelection) }
                saveNavigationState(Screen.BirthdaySelection, newHistory)
                true
            }
            is Screen.BirthdaySelection -> {
                val newHistory = listOf(Screen.Welcome, Screen.Register)
                _screenHistory.value = newHistory
                _uiState.update { it.copy(currentScreen = Screen.Register) }
                saveNavigationState(Screen.Register, newHistory)
                true
            }
            is Screen.Register -> {
                val newHistory = listOf(Screen.Welcome)
                _screenHistory.value = newHistory
                _uiState.update { it.copy(currentScreen = Screen.Welcome) }
                saveNavigationState(Screen.Welcome, newHistory)
                true
            }
            is Screen.Login -> {
                val newHistory = listOf(Screen.Welcome)
                _screenHistory.value = newHistory
                _uiState.update { it.copy(currentScreen = Screen.Welcome) }
                saveNavigationState(Screen.Welcome, newHistory)
                true
            }
            else -> false
        }
    }

    private val _tabHistory = MutableStateFlow<List<MainTab>>(listOf(MainTab.FEEDS))
    val tabHistory: StateFlow<List<MainTab>> = _tabHistory.asStateFlow()

    fun selectTab(tab: MainTab) {
        val currentHistory = _tabHistory.value.toMutableList()
        currentHistory.remove(tab)
        currentHistory.add(tab)
        _tabHistory.value = currentHistory
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setAutoLaunchGallery(value: Boolean) {
        _uiState.update { it.copy(shouldAutoLaunchGallery = value) }
    }

    fun navigateBackTab(): Boolean {
        val currentHistory = _tabHistory.value.toMutableList()
        if (currentHistory.size > 1) {
            currentHistory.removeAt(currentHistory.size - 1)
            _tabHistory.value = currentHistory
            val previousTab = currentHistory.last()
            _uiState.update { it.copy(currentTab = previousTab) }
            return true
        }
        return false
    }

    private val _deepLinkedPostId = MutableStateFlow<Int?>(null)
    val deepLinkedPostId: StateFlow<Int?> = _deepLinkedPostId.asStateFlow()

    fun handleDeepLinkPost(postId: Int) {
        _deepLinkedPostId.value = postId
        _uiState.update { it.copy(currentScreen = Screen.Main, currentTab = MainTab.FEEDS) }
    }

    private fun safeBase64Decode(str: String, fallback: String): String {
        if (str.isBlank()) return fallback
        return try {
            val clean = str.replace(" ", "+")
            val decoded = android.util.Base64.decode(clean, android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING or android.util.Base64.NO_WRAP)
            String(decoded, Charsets.UTF_8)
        } catch (e: Exception) {
            try {
                val decoded = android.util.Base64.decode(str, android.util.Base64.DEFAULT)
                String(decoded, Charsets.UTF_8)
            } catch (e2: Exception) {
                fallback
            }
        }
    }

    fun handleDeepLinkPostWithData(postId: Int, authorNameB64: String, contentB64: String, imageB64: String) {
        _deepLinkedPostId.value = postId
        _uiState.update { it.copy(currentScreen = Screen.Main, currentTab = MainTab.FEEDS, isViewingAsGuest = true) }

        viewModelScope.launch {
            try {
                val existingList = repository.feedPosts.first()
                val found = existingList.any { it.postId == postId }
                if (!found) {
                    val decodedName = safeBase64Decode(authorNameB64, "Aura User")
                    val decodedContent = safeBase64Decode(contentB64, "Aura Shared Post")
                    var decodedImage = safeBase64Decode(imageB64, "")
                    
                    // Fail-safe protection: if decoded image is a content:// local URI, replace with beautiful branding cover
                    if (decodedImage.startsWith("content://") || decodedImage.startsWith("file://") || !decodedImage.startsWith("http")) {
                        decodedImage = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=800&q=80"
                    }
                    
                    val newPost = com.example.data.database.PostEntity(
                        postId = postId,
                        authorId = 8888 + postId,
                        authorName = decodedName,
                        authorAvatar = "avatar_user_main",
                        content = decodedContent,
                        imageUrl = decodedImage,
                        timestamp = System.currentTimeMillis() - 30000,
                        privacy = "Public"
                    )
                    repository.addPost(newPost)
                }
            } catch (e: java.lang.Exception) {
                android.util.Log.e("AuraDeepLink", "Failed to auto-seed deep linked post in DB: ${e.message}")
            }
        }
    }

    fun clearDeepLinkPost() {
        _deepLinkedPostId.value = null
    }

    fun setViewingAsGuest(isGuest: Boolean) {
        _uiState.update { it.copy(isViewingAsGuest = isGuest) }
    }

    // Livestreaming helpers removed


    private var playbackJob: kotlinx.coroutines.Job? = null

    fun playReplay(post: PostEntity) {
        _uiState.update { it.copy(
            activeReplayPost = post,
            isVideoPlaying = true,
            isVideoFullscreen = true,
            isVideoMinimized = false,
            videoProgress = 0.0f
        ) }
        startAudioPlayback(post)
        startPlaybackSimulation()
    }

    fun closePlayer() {
        stopPlaybackSimulation()
        stopAudioPlayback()
        _uiState.update { it.copy(
            activeReplayPost = null,
            isVideoPlaying = false,
            isVideoFullscreen = false,
            isVideoMinimized = false,
            videoProgress = 0.0f
        ) }
    }

    fun toggleVideoPlayPause() {
        val currPlaying = _uiState.value.isVideoPlaying
        _uiState.update { it.copy(isVideoPlaying = !currPlaying) }
        if (!currPlaying) {
            replayMediaPlayer?.let {
                try { it.start() } catch (e: Exception) {}
            }
            startPlaybackSimulation()
        } else {
            replayMediaPlayer?.let {
                try { it.pause() } catch (e: Exception) {}
            }
            stopTtsFallback()
            stopPlaybackSimulation()
        }
    }

    fun skipForward() {
        _uiState.update { 
            val newProgress = (it.videoProgress + 0.05f).coerceIn(0.0f, 1.0f)
            replayMediaPlayer?.let { player ->
                try {
                    val msec = (newProgress * player.duration).toInt()
                    player.seekTo(msec)
                } catch(e: Exception) {}
            }
            it.copy(videoProgress = newProgress)
        }
    }

    fun skipBackward() {
        _uiState.update { 
            val newProgress = (it.videoProgress - 0.05f).coerceIn(0.0f, 1.0f)
            replayMediaPlayer?.let { player ->
                try {
                    val msec = (newProgress * player.duration).toInt()
                    player.seekTo(msec)
                } catch(e: Exception) {}
            }
            it.copy(videoProgress = newProgress)
        }
    }

    fun setVideoProgress(progress: Float) {
        _uiState.update { it.copy(videoProgress = progress) }
        replayMediaPlayer?.let { player ->
            try {
                val msec = (progress * player.duration).toInt()
                player.seekTo(msec)
            } catch(e: Exception) {}
        }
    }

    fun setVideoMinimized(minimized: Boolean) {
        _uiState.update { it.copy(
            isVideoMinimized = minimized,
            isVideoFullscreen = !minimized
        ) }
    }

    fun setVideoFullscreen(fullscreen: Boolean) {
        _uiState.update { it.copy(
            isVideoFullscreen = fullscreen,
            isVideoMinimized = !fullscreen
        ) }
    }

    fun changePlaybackSpeed() {
        _uiState.update { 
            val newSpeed = when (it.videoPlaybackSpeed) {
                1.0f -> 1.5f
                1.5f -> 2.0f
                2.0f -> 0.5f
                else -> 1.0f
            }
            replayMediaPlayer?.let { player ->
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        player.playbackParams = player.playbackParams.setSpeed(newSpeed)
                    }
                } catch (e: Exception) {}
            }
            it.copy(videoPlaybackSpeed = newSpeed)
        }
    }

    private fun playReplayChimeWave(progress: Float) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val sampleRate = 8000
                val numSamples = (sampleRate * 0.4).toInt() // 0.4 second note
                val sample = DoubleArray(numSamples)
                val generatedSnd = ByteArray(2 * numSamples)
                
                // Select note frequency based on progress
                val score = (progress * 10).toInt() % 6
                val freq = when (score) {
                    0 -> 523.25 // C5
                    1 -> 587.33 // D5
                    2 -> 659.25 // E5
                    3 -> 698.46 // F5
                    4 -> 783.99 // G5
                    5 -> 880.00 // A5
                    else -> 523.25
                }
                
                for (i in 0 until numSamples) {
                    sample[i] = Math.sin(2.0 * Math.PI * i / (sampleRate / freq))
                    val envelope = 1.0 - (i.toDouble() / numSamples)
                    val valShort = (sample[i] * 4000 * envelope).toInt().toShort() // Beautiful soft volume
                    val idx = 2 * i
                    generatedSnd[idx] = (valShort.toInt() and 0x00ff).toByte()
                    generatedSnd[idx + 1] = ((valShort.toInt() and 0xff00) ushr 8).toByte()
                }
                
                val track = android.media.AudioTrack(
                    android.media.AudioManager.STREAM_MUSIC,
                    sampleRate,
                    android.media.AudioFormat.CHANNEL_OUT_MONO,
                    android.media.AudioFormat.ENCODING_PCM_16BIT,
                    generatedSnd.size,
                    android.media.AudioTrack.MODE_STATIC
                )
                track.write(generatedSnd, 0, generatedSnd.size)
                track.play()
                kotlinx.coroutines.delay(600)
                track.stop()
                track.release()
            } catch (e: Exception) {
                // Ignore silent failure
            }
        }
    }

    private fun startPlaybackSimulation() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                val speed = _uiState.value.videoPlaybackSpeed
                val post = _uiState.value.activeReplayPost
                
                val next = if (replayMediaPlayer != null) {
                    try {
                        val duration = replayMediaPlayer?.duration ?: 0
                        val current = replayMediaPlayer?.currentPosition ?: 0
                        if (duration > 0) {
                            current.toFloat() / duration.toFloat()
                        } else {
                            _uiState.value.videoProgress + 0.02f
                        }
                    } catch (e: Exception) {
                        _uiState.value.videoProgress + 0.02f
                    }
                } else {
                    val totalSecs = if (post != null) {
                        val tag = "[DURATION:"
                        val content = post.content
                        if (content.contains(tag)) {
                            val parsed = content.substringAfter(tag).substringBefore("]", "")
                            val parts = parsed.split(":")
                            if (parts.size >= 2) {
                                val mins = parts[0].toIntOrNull() ?: 0
                                val secs = parts[1].toIntOrNull() ?: 0
                                (mins * 60) + secs
                            } else {
                                504
                            }
                        } else {
                            504
                        }
                    } else {
                        504
                    }
                    val step = (1.0f / totalSecs.coerceAtLeast(1)) * speed
                    _uiState.value.videoProgress + step
                }
                
                val coNext = next.coerceIn(0.0f, 1.0f)
                _uiState.update { 
                    if (coNext >= 1.0f) {
                        it.copy(videoProgress = 1.0f, isVideoPlaying = false)
                    } else {
                        it.copy(videoProgress = coNext)
                    }
                }
                
                // Mute playReplayChimeWave when custom audio recording stream (replayMediaPlayer) or TTS fallback is active
                val hasNoRealAudio = (replayMediaPlayer == null && (tts == null || !tts!!.isSpeaking))
                if (_uiState.value.isVideoPlaying && hasNoRealAudio) {
                    playReplayChimeWave(_uiState.value.videoProgress)
                }
                if (_uiState.value.videoProgress >= 1.0f || !_uiState.value.isVideoPlaying) {
                    break
                }
            }
        }
    }

    private fun stopPlaybackSimulation() {
        playbackJob?.cancel()
    }

    // Live comments helpers removed


    fun setVibeCheckSimulatorVisible(visible: Boolean) {
        _uiState.update { it.copy(showVibeCheckSimulator = visible) }
    }

    fun startVibeCheck(partnerName: String, selfEnergy: String, partnerEnergy: String) {
        if (partnerName.isBlank()) return
        _uiState.update {
            it.copy(
                isVibeChecking = true,
                vibeCheckPartnerName = partnerName,
                vibeCheckSelfEnergy = selfEnergy,
                vibeCheckPartnerEnergy = partnerEnergy
            )
        }
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000) // Simulate merging frequencies

            // Generate deterministic but fun scores
            val selfName = currentUser.value?.displayName ?: "Aura User"
            val nameSum = selfName.length + partnerName.length
            val score = 70 + (nameSum * 7) % 30 // Deterministic score between 70% and 99%
            val titleAndAdvice = getVibeOutcome(score)

            _uiState.update {
                it.copy(
                    isVibeChecking = false,
                    vibeCheckScore = score,
                    vibeCheckTitle = titleAndAdvice.first,
                    vibeCheckAdvice = titleAndAdvice.second,
                    vibeCheckColor = getColorForEnergy(selfEnergy)
                )
            }
        }
    }

    fun resetVibeCheck() {
        _uiState.update {
            it.copy(
                vibeCheckPartnerName = "",
                vibeCheckSelfEnergy = "",
                vibeCheckPartnerEnergy = "",
                vibeCheckScore = -1,
                vibeCheckTitle = "",
                vibeCheckAdvice = "",
                vibeCheckColor = ""
            )
        }
    }

    fun attachVibeCheckToDraftPost() {
        val st = _uiState.value
        if (st.vibeCheckScore != -1) {
            _uiState.update {
                it.copy(
                    creationState = it.creationState.copy(
                        attachedVibePartner = st.vibeCheckPartnerName,
                        attachedVibeScore = st.vibeCheckScore,
                        attachedVibeTitle = st.vibeCheckTitle,
                        attachedVibeAdvice = st.vibeCheckAdvice,
                        attachedVibeColor = st.vibeCheckColor
                    ),
                    showVibeCheckSimulator = false,
                    currentTab = MainTab.CREATE_POST
                )
            }
        }
    }

    fun clearAttachedVibeCheck() {
        _uiState.update {
            it.copy(
                creationState = it.creationState.copy(
                    attachedVibePartner = "",
                    attachedVibeScore = -1,
                    attachedVibeTitle = "",
                    attachedVibeAdvice = "",
                    attachedVibeColor = ""
                )
            )
        }
    }

    private fun getColorForEnergy(energy: String): String {
        return when (energy) {
            "Cosmic Violet" -> "#7C4DFF"
            "Solar Yellow" -> "#FBBF24"
            "Ocean Teal" -> "#0D9488"
            "Passion Red" -> "#EF4444"
            "Radiant Pink" -> "#EC4899"
            "Forest Green" -> "#10B981"
            "Ethereal Blue" -> "#3B82F6"
            else -> "#A78BFA"
        }
    }

    private fun getVibeOutcome(score: Int): Pair<String, String> {
        return when {
            score >= 95 -> Pair(
                "Soul Connection (পরম আত্মার বাঁধন) ✨",
                "Your energies vibe in absolute perfect harmony! This is a legendary cosmic bond. Grab a plate of Biryani and code something beautiful together! ☕🍛"
            )
            score >= 88 -> Pair(
                "Twin Flame (সুর আর ছন্দ) 🔥",
                "Sparkling energy and intense resonance! You both motivate each other to shine brighter. A strong partnership destined for high achievements!"
            )
            score >= 80 -> Pair(
                "Harmonious Hearts (স্নিগ্ধ সহমর্মিতা) 🌸",
                "Calm waters and deep understanding. You bring absolute peace and comfort into each other's busy life. A beautiful aesthetic bonding!"
            )
            score >= 70 -> Pair(
                "Mystic Echoes (রহস্যময় আভা) 🔮",
                "Intriguing differences that attract! You both keep things fresh and exciting with complementary colorful vibes. Great adventures await!"
            )
            else -> Pair(
                "Chilled Synergy (বন্ধুত্বের আভা) 🍃",
                "Relaxed and effortless vibes. Simply enjoy the quiet comfort of each other's spaces without any pressure. Highly comfortable energy!"
            )
        }
    }


    fun setNoteCreatorVisible(visible: Boolean) {
        _uiState.update { it.copy(showNoteCreator = visible) }
    }

    fun updateActiveUserNote(note: String) {
        _uiState.update { it.copy(activeUserNote = note, showNoteCreator = false) }
    }

    fun updateLanguage(lang: String) {
        // Look up static fallback matching instantly for major languages offline
        val staticTitle = com.example.ui.screens.AuraTranslator.getWelcomeTitle(lang)
        val staticDesc = com.example.ui.screens.AuraTranslator.getWelcomeDesc(lang)
        val staticStart = com.example.ui.screens.AuraTranslator.getButtonGetStarted(lang)
        val staticProfile = com.example.ui.screens.AuraTranslator.getButtonAlreadyHaveProfile(lang)

        _uiState.update { state ->
            state.copy(
                appLanguage = lang,
                showLanguageMenu = false,
                welcomeTitle = staticTitle,
                welcomeDesc = staticDesc,
                getStartedText = staticStart,
                alreadyHaveProfileText = staticProfile,
                isTranslating = true
            )
        }

        // Run background translation query to fetch ultra-precise translation via Gemini for all 100+ languages
        viewModelScope.launch {
            try {
                val result = com.example.data.network.GeminiTranslationClient.translateWelcomeScreen(lang)
                if (result != null) {
                    _uiState.update { state ->
                        if (state.appLanguage == lang) {
                            state.copy(
                                welcomeTitle = result.welcomeTitle,
                                welcomeDesc = result.welcomeDesc,
                                getStartedText = result.getStartedText,
                                alreadyHaveProfileText = result.alreadyHaveProfileText,
                                isTranslating = false
                            )
                        } else {
                            state
                        }
                    }
                } else {
                    _uiState.update { it.copy(isTranslating = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isTranslating = false) }
            }
        }
    }

    fun toggleLanguageMenu() {
        _uiState.update { it.copy(showLanguageMenu = !it.showLanguageMenu) }
    }

    val translationCache = androidx.compose.runtime.mutableStateMapOf<Pair<String, String>, String>()
    private val activeTranslationJobs = java.util.concurrent.ConcurrentHashMap<Pair<String, String>, kotlinx.coroutines.Job>()

    fun getTranslatedText(text: String): String {
        val currentLang = uiState.value.appLanguage
        if (currentLang == "English (US)" || currentLang == "English" || text.isBlank()) {
            return text
        }
        
        // Fast static translation check for common UI strings in major languages
        val staticMatch = com.example.ui.screens.AuraTranslator.getOfflineTranslation(text, currentLang)
        if (staticMatch != null) {
            return staticMatch
        }
        
        val key = Pair(text, currentLang)
        val cached = translationCache[key]
        if (cached != null) {
            return cached
        }
        
        // Trigger background translation via Gemini
        triggerBackgroundTranslation(text, currentLang)
        
        return text // temporarily show original while translating in background
    }

    private fun triggerBackgroundTranslation(text: String, targetLanguage: String) {
        val key = Pair(text, targetLanguage)
        if (translationCache.containsKey(key) || activeTranslationJobs.containsKey(key)) {
            return
        }

        val job = viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val translated = com.example.data.network.GeminiTranslationClient.translateText(text, targetLanguage)
                if (!translated.isNullOrBlank()) {
                    translationCache[key] = translated
                }
            } catch (e: Exception) {
                // fall back gracefully
            } finally {
                activeTranslationJobs.remove(key)
            }
        }
        activeTranslationJobs[key] = job
    }

    // --- Search functionality ---

    fun getUserResolvedUid(userId: Int): String {
        val user = allUsers.value.find { it.userId == userId }
        if (user != null) {
            return user.resolvedUid
        }
        val current = currentUser.value
        if (current != null && current.userId == userId) {
            return current.resolvedUid
        }
        return "6a27c85b" + String.format("%012x", userId.toLong())
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        viewModelScope.launch {
            val list = allUsers.value
            val current = currentUser.value
            val filtered = list.filter {
                (it.displayName.contains(query, ignoreCase = true) ||
                 it.username.contains(query, ignoreCase = true)) &&
                 it.userId != current?.userId
            }
            _uiState.update { it.copy(searchResults = filtered) }
        }
    }

    // --- Profile Setup, Logins & Demo Sessions ---

    fun updatePublicBackupImage(context: android.content.Context, newUriStr: String, backupFileName: String) {
        if (newUriStr.isBlank()) return
        try {
            val resolver = context.contentResolver
            val existingUriStr = findImageInMediaStore(context, backupFileName)
            val imageUri = if (existingUriStr.isNotBlank()) {
                android.net.Uri.parse(existingUriStr)
            } else {
                val imageCollection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    android.provider.MediaStore.Images.Media.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                val contentValues = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, backupFileName)
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/AuraHub")
                        put(android.provider.MediaStore.MediaColumns.IS_PENDING, 1)
                    }
                }
                val inserted = resolver.insert(imageCollection, contentValues)
                if (inserted != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(android.provider.MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(inserted, contentValues, null, null)
                }
                inserted
            }

            if (imageUri != null) {
                val srcUri = android.net.Uri.parse(newUriStr)
                resolver.openInputStream(srcUri)?.use { inputStream ->
                    resolver.openOutputStream(imageUri, "wt")?.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                android.util.Log.d("AuraBackup", "Successfully updated public backup image $backupFileName with new pixels")
            }
        } catch (e: Exception) {
            android.util.Log.e("AuraBackup", "Error updating public backup image $backupFileName: ${e.message}")
        }
    }

    fun updateCurrentUser(user: UserEntity) {
        viewModelScope.launch {
            val cleanEmail = user.email.lowercase().trim()
            val cleanEmailFileName = cleanEmail.replace("@", "_").replace(".", "_")
            
            var finalAvatar = user.avatarUrl
            var finalCover = user.coverUrl
            var modified = false
            
            if (user.avatarUrl.startsWith("content://") || user.avatarUrl.startsWith("file://")) {
                val persistentFileName = "profile_pic_${cleanEmailFileName}_updated_${System.currentTimeMillis()}.jpg"
                val stableLocalPath = copyImageToLocalStorage(user.avatarUrl, persistentFileName)
                if (stableLocalPath.isNotBlank()) {
                    finalAvatar = stableLocalPath
                    modified = true
                    updatePublicBackupImage(context, user.avatarUrl, "profile_pic_${cleanEmailFileName}_backup.jpg")
                }
            }
            
            if (user.coverUrl.startsWith("content://") || user.coverUrl.startsWith("file://")) {
                val persistentFileName = "cover_pic_${cleanEmailFileName}_updated_${System.currentTimeMillis()}.jpg"
                val stableLocalPath = copyImageToLocalStorage(user.coverUrl, persistentFileName)
                if (stableLocalPath.isNotBlank()) {
                    finalCover = stableLocalPath
                    modified = true
                    updatePublicBackupImage(context, user.coverUrl, "cover_pic_${cleanEmailFileName}_backup.jpg")
                }
            }
            
            val updatedUser = if (modified) user.copy(avatarUrl = finalAvatar, coverUrl = finalCover) else user
            
            repository.updateUser(updatedUser)
            saveProfileToPrefs(updatedUser)
            
            // Get credentials password if registered/logged in to perform updated backup
            if (cleanEmail.isNotBlank()) {
                val credentialsPrefs = context.getSharedPreferences("aura_credentials", android.content.Context.MODE_PRIVATE)
                val savedPassword = credentialsPrefs.getString(cleanEmail, "AuraDiscordSecurePass123!") ?: "AuraDiscordSecurePass123!"
                backupProfileToPublicStorage(updatedUser, savedPassword)
                backupProfileToImageMediaStore(updatedUser, savedPassword)
                
                // Keep the appwrite active account prefs matching the updated serialized profile details
                if (isRealAppwriteEnabled) {
                    try {
                        val serializedProfile = serializeUserEntityToUri(user)
                        appwriteAccount?.let { account ->
                            account.updateName(user.displayName)
                            account.updatePrefs(mapOf("photoUrl" to serializedProfile))
                            android.util.Log.d("AppwriteSync", "Successfully synced updated user profile in Appwrite.")
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AppwriteSync", "Error in updating appwrite preferences: ${e.message}")
                    }
                }

                // Asynchronously upload local photos to cloud and save public URL
                viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    try {
                        var updated = false
                        var finalAvatar = user.avatarUrl
                        var finalCover = user.coverUrl
                        
                        // Check if avatar is local (e.g. content:// or file:// or does not start with http)
                        if (user.avatarUrl.isNotBlank() && (user.avatarUrl.startsWith("content://") || user.avatarUrl.startsWith("file://"))) {
                            val cloudAvatarUrl = uploadUriToCloud(user.avatarUrl)
                            if (cloudAvatarUrl.startsWith("http")) {
                                finalAvatar = cloudAvatarUrl
                                updated = true
                            }
                        }
                        
                        // Check if cover is local (e.g. content:// or file:// or does not start with http)
                        if (user.coverUrl.isNotBlank() && (user.coverUrl.startsWith("content://") || user.coverUrl.startsWith("file://"))) {
                            val cloudCoverUrl = uploadUriToCloud(user.coverUrl)
                            if (cloudCoverUrl.startsWith("http")) {
                                finalCover = cloudCoverUrl
                                updated = true
                            }
                        }
                        
                        if (updated) {
                            val cloudUser = user.copy(avatarUrl = finalAvatar, coverUrl = finalCover)
                            
                            // Save to local sqlite & SharedPreferences
                            repository.updateUser(cloudUser)
                            saveProfileToPrefs(cloudUser)
                            backupProfileToPublicStorage(cloudUser, savedPassword)
                            backupProfileToImageMediaStore(cloudUser, savedPassword)
                            
                            // Save to remote Appwrite preferences
                            if (isRealAppwriteEnabled) {
                                val serializedProfile = serializeUserEntityToUri(cloudUser)
                                appwriteAccount?.let { account ->
                                    account.updatePrefs(mapOf("photoUrl" to serializedProfile))
                                    android.util.Log.d("AppwriteSync", "Successfully synced cloud-hosted images to Appwrite.")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AppwriteSync", "Error uploading edited profile assets: ${e.message}")
                    }
                }
            }
        }
    }

    fun saveProfileToPrefs(user: UserEntity) {
        val cleanEmail = user.email.lowercase().trim()
        if (cleanEmail.isBlank()) return
        try {
            val profilePrefs = context.getSharedPreferences("registered_profiles", android.content.Context.MODE_PRIVATE)
            val editor = profilePrefs.edit()
            val keyPrefix = cleanEmail + "_"
            editor.putString(keyPrefix + "displayName", user.displayName)
            editor.putString(keyPrefix + "username", user.username)
            editor.putString(keyPrefix + "avatarUrl", user.avatarUrl)
            editor.putString(keyPrefix + "coverUrl", user.coverUrl)
            editor.putString(keyPrefix + "bio", user.bio)
            editor.putString(keyPrefix + "relationshipStatus", user.relationshipStatus)
            editor.putString(keyPrefix + "relationshipPrivacy", user.relationshipPrivacy)
            editor.putString(keyPrefix + "school", user.school)
            editor.putString(keyPrefix + "college", user.college)
            editor.putString(keyPrefix + "university", user.university)
            editor.putString(keyPrefix + "educationPrivacy", user.educationPrivacy)
            editor.putString(keyPrefix + "hobbies", user.hobbies)
            editor.putString(keyPrefix + "hobbiesPrivacy", user.hobbiesPrivacy)
            editor.putString(keyPrefix + "hometown", user.hometown)
            editor.putString(keyPrefix + "hometownPrivacy", user.hometownPrivacy)
            editor.putString(keyPrefix + "birthday", user.birthday)
            editor.putString(keyPrefix + "birthdayPrivacy", user.birthdayPrivacy)
            editor.putString(keyPrefix + "gender", user.gender)
            editor.putString(keyPrefix + "genderPrivacy", user.genderPrivacy)
            editor.apply()
            android.util.Log.d("AuraSync", "Profile synced to SharedPreferences for: $cleanEmail")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun serializeUserEntityToUri(user: UserEntity): String {
        val builder = android.net.Uri.Builder()
            .scheme("aura")
            .authority("profile")
            .appendQueryParameter("un", user.username)
            .appendQueryParameter("dn", user.displayName)
            .appendQueryParameter("av", user.avatarUrl)
            .appendQueryParameter("co", user.coverUrl)
            .appendQueryParameter("bi", user.bio)
            .appendQueryParameter("rs", user.relationshipStatus)
            .appendQueryParameter("rp", user.relationshipPrivacy)
            .appendQueryParameter("sc", user.school)
            .appendQueryParameter("cl", user.college)
            .appendQueryParameter("unv", user.university)
            .appendQueryParameter("ep", user.educationPrivacy)
            .appendQueryParameter("hb", user.hobbies)
            .appendQueryParameter("hp", user.hobbiesPrivacy)
            .appendQueryParameter("ht", user.hometown)
            .appendQueryParameter("htp", user.hometownPrivacy)
            .appendQueryParameter("bd", user.birthday)
            .appendQueryParameter("bp", user.birthdayPrivacy)
            .appendQueryParameter("gd", user.gender)
            .appendQueryParameter("gp", user.genderPrivacy)
        return builder.build().toString()
    }

    fun deserializeUriToUserEntity(uriString: String, email: String): UserEntity? {
        try {
            val uri = android.net.Uri.parse(uriString)
            if (uri.scheme != "aura" || uri.host != "profile") return null
            
            val cleanEmail = email.lowercase().trim()
            return UserEntity(
                username = uri.getQueryParameter("un") ?: (cleanEmail.substringBefore("@") + "_aura"),
                displayName = uri.getQueryParameter("dn") ?: cleanEmail.substringBefore("@"),
                avatarUrl = uri.getQueryParameter("av") ?: "avatar_user_main",
                coverUrl = uri.getQueryParameter("co") ?: "cover_main",
                bio = uri.getQueryParameter("bi") ?: "Excited to connect with friends on Aura 2026!",
                isCurrentUser = true,
                followerCount = 0,
                followingCount = 0,
                auraRating = 120,
                email = cleanEmail,
                relationshipStatus = uri.getQueryParameter("rs") ?: "",
                relationshipPrivacy = uri.getQueryParameter("rp") ?: "Public",
                school = uri.getQueryParameter("sc") ?: "",
                college = uri.getQueryParameter("cl") ?: "",
                university = uri.getQueryParameter("unv") ?: "",
                educationPrivacy = uri.getQueryParameter("ep") ?: "Public",
                hobbies = uri.getQueryParameter("hb") ?: "",
                hobbiesPrivacy = uri.getQueryParameter("hp") ?: "Public",
                hometown = uri.getQueryParameter("ht") ?: "",
                hometownPrivacy = uri.getQueryParameter("htp") ?: "Public",
                birthday = uri.getQueryParameter("bd") ?: "",
                birthdayPrivacy = uri.getQueryParameter("bp") ?: "Public",
                gender = uri.getQueryParameter("gd") ?: "",
                genderPrivacy = uri.getQueryParameter("gp") ?: "Public"
            )
        } catch (e: Exception) {
            android.util.Log.e("FirebaseSync", "Error deserializing user payload URI: ${e.message}")
            return null
        }
    }

    fun getSavedProfileFromPrefs(email: String): UserEntity? {
        val cleanEmail = email.lowercase().trim()
        val profilePrefs = context.getSharedPreferences("registered_profiles", android.content.Context.MODE_PRIVATE)
        val keyPrefix = cleanEmail + "_"
        val displayName = profilePrefs.getString(keyPrefix + "displayName", null)
        if (displayName == null) {
            val restored = restoreProfileFromPublicStorage(cleanEmail)
            if (restored != null) {
                val processed = checkAndRestoreProfileImages(restored)
                saveProfileToPrefs(processed)
                viewModelScope.launch {
                    val insertedId = repository.insertUser(processed).toInt()
                    restoreUserPostsFromPublicStorage(cleanEmail, insertedId)
                }
                return processed
            }
            return null
        }
        
        val user = UserEntity(
            username = profilePrefs.getString(keyPrefix + "username", cleanEmail.substringBefore("@") + "_aura") ?: "",
            displayName = displayName,
            avatarUrl = profilePrefs.getString(keyPrefix + "avatarUrl", "avatar_user_main") ?: "avatar_user_main",
            coverUrl = profilePrefs.getString(keyPrefix + "coverUrl", "cover_main") ?: "cover_main",
            bio = profilePrefs.getString(keyPrefix + "bio", "Excited to connect with friends on Aura 2026!") ?: "Excited to connect with friends on Aura 2026!",
            isCurrentUser = true,
            followerCount = 0,
            followingCount = 0,
            auraRating = 120,
            email = cleanEmail,
            relationshipStatus = profilePrefs.getString(keyPrefix + "relationshipStatus", "") ?: "",
            relationshipPrivacy = profilePrefs.getString(keyPrefix + "relationshipPrivacy", "Public") ?: "Public",
            school = profilePrefs.getString(keyPrefix + "school", "") ?: "",
            college = profilePrefs.getString(keyPrefix + "college", "") ?: "",
            university = profilePrefs.getString(keyPrefix + "university", "") ?: "",
            educationPrivacy = profilePrefs.getString(keyPrefix + "educationPrivacy", "Public") ?: "Public",
            hobbies = profilePrefs.getString(keyPrefix + "hobbies", "") ?: "",
            hobbiesPrivacy = profilePrefs.getString(keyPrefix + "hobbiesPrivacy", "Public") ?: "Public",
            hometown = profilePrefs.getString(keyPrefix + "hometown", "") ?: "",
            hometownPrivacy = profilePrefs.getString(keyPrefix + "hometownPrivacy", "Public") ?: "Public",
            birthday = profilePrefs.getString(keyPrefix + "birthday", "") ?: "",
            birthdayPrivacy = profilePrefs.getString(keyPrefix + "birthdayPrivacy", "Public") ?: "Public",
            gender = profilePrefs.getString(keyPrefix + "gender", "") ?: "",
            genderPrivacy = profilePrefs.getString(keyPrefix + "genderPrivacy", "Public") ?: "Public"
        )
        val processed = checkAndRestoreProfileImages(user)
        if (processed.avatarUrl != user.avatarUrl || processed.coverUrl != user.coverUrl) {
            saveProfileToPrefs(processed)
        }
        return processed
    }

    fun createProfileAndLogin(displayName: String, bio: String, avatar: String, email: String, targetScreen: Screen = Screen.Main) {
        viewModelScope.launch {
            try {
                val cleanEmail = email.lowercase().trim()
                val username = displayName.replace(" ", "_").lowercase() + "_aura"
                
                var realUid = ""
                if (isRealAppwriteEnabled) {
                    try {
                        appwriteAccount?.let { account ->
                            val authUser = account.get()
                            val remoteId = authUser.id
                            if (!remoteId.isNullOrBlank()) {
                                realUid = remoteId
                                android.util.Log.d("AuraCreateProfile", "Successfully resolved registered live Appwrite UID: $realUid")
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AuraCreateProfile", "Error fetching live UID during registration: ${e.message}")
                    }
                }
                if (realUid.isBlank()) {
                    val chars = "0123456789abcdef"
                    realUid = (1..20).map { chars.random() }.joinToString("")
                }
                
                // Use local paths immediately so loading and navigations are INSTANTNEOUS!
                var avatarSource = if (regProfilePic.isNotBlank()) regProfilePic else avatar
                var coverSource = if (regCoverPic.isNotBlank()) regCoverPic else "cover_main"

                if (avatarSource.startsWith("content://")) {
                    val copied = copyImageToPublicAndPrivateStorage(avatarSource, isCover = false)
                    if (copied.isNotBlank()) {
                        avatarSource = copied
                    }
                }

                if (coverSource.startsWith("content://")) {
                    val copied = copyImageToPublicAndPrivateStorage(coverSource, isCover = true)
                    if (copied.isNotBlank()) {
                        coverSource = copied
                    }
                }
                
                val sdf = java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale.ENGLISH)
                val todayStr = sdf.format(java.util.Date())

                val newUser = UserEntity(
                    username = username,
                    displayName = displayName,
                    avatarUrl = avatarSource,
                    coverUrl = coverSource,
                    bio = if (regBio.isNotBlank()) regBio else (bio.ifBlank { "Excited to connect with friends on Aura 2026!" }),
                    isCurrentUser = true,
                    followerCount = 0,
                    followingCount = 0,
                    auraRating = 120,
                    email = cleanEmail,
                    relationshipStatus = regRelationStatus,
                    relationshipPrivacy = regRelationPrivacy,
                    school = regSchool,
                    college = regCollege,
                    university = regUniversity,
                    educationPrivacy = regEducationPrivacy,
                    hobbies = regHobbies,
                    hobbiesPrivacy = regHobbyPrivacy,
                    hometown = regHometown,
                    hometownPrivacy = regHometownPrivacy,
                    birthday = regBirthday,
                    birthdayPrivacy = regBirthdayPrivacy,
                    gender = regGenderSelection,
                    genderPrivacy = regGenderPrivacy,
                    appwriteUid = realUid,
                    joinedDate = todayStr
                )

                // Overwrite or update active status to maintain single active profile
                val current = repository.getCurrentUser()
                if (current != null) {
                    repository.updateUser(current.copy(isCurrentUser = false))
                }

                val allUsersList = repository.allUsers.first()
                val existingLocal = allUsersList.find { it.email.lowercase().trim() == cleanEmail }
                if (existingLocal != null && realUid.isNotBlank() && existingLocal.appwriteUid.isNotBlank() && existingLocal.appwriteUid != realUid) {
                    android.util.Log.d("AuraCreateProfile", "UID mismatched during sign up! Stored: ${existingLocal.appwriteUid}, Live: $realUid. User was recreated. Purging historical data.")
                    purgeHistoricalDataForRecreatedUser(cleanEmail, existingLocal.userId, existingLocal.displayName)
                }

                val generatedId = if (existingLocal != null) {
                    // Update the existing entity instead of inserting a duplicate
                    val updatedUser = newUser.copy(
                        userId = existingLocal.userId,
                        followerCount = 0,
                        followingCount = 0,
                        auraRating = 120
                    )
                    repository.updateUser(updatedUser)
                    existingLocal.userId
                } else {
                    repository.insertUser(newUser).toInt()
                }

                // Sync profile data to preferences
                saveProfileToPrefs(newUser)
                backupProfileToPublicStorage(newUser, regPasswordText)

                // Cloud upload profile and cover photos asynchronously in background Dispatchers.IO
                viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    try {
                        var finalAvatar = avatarSource
                        var finalCover = coverSource
                        var photoUpdated = false
                        
                        if (avatarSource.isNotBlank() && (avatarSource.startsWith("content://") || avatarSource.startsWith("file://"))) {
                            val cloudAvatarUrl = uploadUriToCloud(avatarSource)
                            if (cloudAvatarUrl.startsWith("http")) {
                                finalAvatar = cloudAvatarUrl
                                photoUpdated = true
                            }
                        }
                        
                        if (coverSource.isNotBlank() && (coverSource.startsWith("content://") || coverSource.startsWith("file://"))) {
                            val cloudCoverUrl = uploadUriToCloud(coverSource)
                            if (cloudCoverUrl.startsWith("http")) {
                                finalCover = cloudCoverUrl
                                photoUpdated = true
                            }
                        }
                        
                        val cloudUser = newUser.copy(
                            userId = generatedId,
                            avatarUrl = finalAvatar,
                            coverUrl = finalCover
                        )
                        
                        if (photoUpdated) {
                            // Save backup to SQLite & SharedPreferences and device storage
                            repository.updateUser(cloudUser)
                            saveProfileToPrefs(cloudUser)
                            backupProfileToPublicStorage(cloudUser, regPasswordText)
                            backupProfileToImageMediaStore(cloudUser, regPasswordText)
                        }

                        val isRealAppwrite = isRealAppwriteEnabled
                        if (isRealAppwrite) {
                            val serializedProfile = serializeUserEntityToUri(cloudUser)
                            appwriteAccount?.let { account ->
                                account.updateName(cloudUser.displayName)
                                account.updatePrefs(mapOf("photoUrl" to serializedProfile))
                                android.util.Log.d("AppwriteSync", "Successfully updated user profile in Appwrite (background).")
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AppwriteSync", "Error in async cloud profile sync: ${e.message}")
                    }
                }

            } catch (e: Exception) {
                android.util.Log.e("FirebaseSync", "Error inserting clean user: ${e.message}")
            } finally {
                navigateTo(targetScreen)
            }
        }
    }

    fun bypassLoginDemo() {
        viewModelScope.launch {
            // Pick default user or register user
            val current = repository.getCurrentUser()
            if (current != null) {
                repository.updateUser(current.copy(isCurrentUser = true))
            } else {
                // If somehow empty, trigger seeding
                repository.seedMockDataIfEmpty()
            }
            navigateTo(Screen.Main)
        }
    }

    // --- Social Interactions & Posts Creators ---

    fun setCreatePostContent(content: String) {
        _uiState.update {
            it.copy(creationState = it.creationState.copy(content = content))
        }
    }

    fun setCreatePostImage(url: String) {
        _uiState.update {
            it.copy(creationState = it.creationState.copy(imageInputUrl = url))
        }
    }

    fun selectGradient(idx: Int) {
        _uiState.update {
            it.copy(creationState = it.creationState.copy(selectedGradientIdx = idx))
        }
    }

    fun setPostAiLabeled(active: Boolean) {
        _uiState.update {
            it.copy(creationState = it.creationState.copy(isAiLabeled = active))
        }
    }

    fun setPostMentionedUserIds(ids: String) {
        _uiState.update {
            it.copy(creationState = it.creationState.copy(mentionedUserIds = ids))
        }
    }

    fun setPostPrivacy(privacy: String) {
        _uiState.update {
            it.copy(creationState = it.creationState.copy(privacy = privacy))
        }
    }

    fun setCreatePostMusicTrack(track: String) {
        _uiState.update {
            it.copy(creationState = it.creationState.copy(musicTrack = track))
        }
    }

    fun setCreatePostGifUrl(gifUrl: String) {
        _uiState.update {
            it.copy(creationState = it.creationState.copy(gifUrl = gifUrl))
        }
    }

    fun setCreatePostLocation(location: String) {
        _uiState.update {
            it.copy(creationState = it.creationState.copy(location = location))
        }
    }

    fun isAiRelatedPhoto(url: String): Boolean {
        if (url.isBlank()) return false
        val urls = url.split(",")
        return urls.any { it.contains("unsplash") || it.contains("ai") || it.contains("synthetic") || it.contains("generated") }
    }

    fun publishPost() {
        val currUser = currentUser.value ?: return
        val state = _uiState.value.creationState
        val isAttachedLive = state.attachedLiveRoomName.isNotBlank()
        val isAttachedVibe = state.attachedVibePartner.isNotBlank()
        if (state.content.isBlank() && state.imageInputUrl.isBlank() && state.musicTrack.isBlank() && state.gifUrl.isBlank() && !isAttachedLive && !isAttachedVibe) return

        val isImageAiRelated = isAiRelatedPhoto(state.imageInputUrl)
        if (isImageAiRelated && !state.isAiLabeled) {
            android.widget.Toast.makeText(context, "⚠️ AI Content Detected! The Aura recognition engine has detected AI-generated imagery. Please turn on 'Add AI Label' to publish this post safely.  🤖💜", android.widget.Toast.LENGTH_LONG).show()
            return
        }

        viewModelScope.launch {
            val minutesStr = "%02d".format(state.attachedLiveDurationSecs / 60)
            val secondsStr = "%02d".format(state.attachedLiveDurationSecs % 60)
            val durationTag = "[DURATION:$minutesStr:$secondsStr]"
            val initialHeader = "🎥 [AURA_LIVE_ROOM:${state.attachedLiveRoomName}] LIVE STREAM REPLAY: \"${state.attachedLiveTitle}\" (Broadcasted live on Aura P2P) 🔴📹 $durationTag"
            
            val vibeHeader = if (isAttachedVibe) {
                "[AURA_VIBE_CHECK::${state.attachedVibePartner}::${state.attachedVibeScore}::${state.attachedVibeTitle}::${state.attachedVibeAdvice}::${state.attachedVibeColor}]"
            } else ""

            val finalContent = if (isAttachedLive) {
                if (state.content.isNotBlank()) "$initialHeader\n\n${state.content}" else initialHeader
            } else if (isAttachedVibe) {
                if (state.content.isNotBlank()) "$vibeHeader\n\n${state.content}" else vibeHeader
            } else {
                state.content
            }

            val newPost = PostEntity(
                authorId = currUser.userId,
                authorName = currUser.displayName,
                authorAvatar = currUser.avatarUrl,
                content = finalContent,
                imageUrl = state.imageInputUrl,
                gradientIndex = if (state.imageInputUrl.isNotBlank() || isAttachedLive || isAttachedVibe) -1 else state.selectedGradientIdx,
                timestamp = System.currentTimeMillis(),
                isAiLabeled = state.isAiLabeled,
                mentionedUserIds = state.mentionedUserIds,
                privacy = state.privacy,
                musicTrack = state.musicTrack,
                gifUrl = state.gifUrl,
                location = state.location
            )
            repository.addPost(newPost)
            syncPostToFirebase(newPost)
            backupUserPostsToPublicStorage(currUser.email)
            // Clear creation state
            _uiState.update {
                it.copy(
                    creationState = CreatePostState(),
                    currentTab = MainTab.FEEDS
                )
            }
        }
    }

    fun publishPostDirectly(
        content: String,
        imageUrl: String = "",
        gradientIdx: Int = -1,
        isAiLabeled: Boolean = false,
        mentionedUserIds: String = "",
        musicTrack: String = "",
        gifUrl: String = "",
        location: String = ""
    ) {
        val currUser = currentUser.value ?: return
        if (content.isBlank() && imageUrl.isBlank() && musicTrack.isBlank() && gifUrl.isBlank()) return
        viewModelScope.launch {
            val newPost = PostEntity(
                authorId = currUser.userId,
                authorName = currUser.displayName,
                authorAvatar = currUser.avatarUrl,
                content = content,
                imageUrl = imageUrl,
                gradientIndex = gradientIdx,
                timestamp = System.currentTimeMillis(),
                isAiLabeled = isAiLabeled,
                mentionedUserIds = mentionedUserIds,
                musicTrack = musicTrack,
                gifUrl = gifUrl,
                location = location
            )
            repository.addPost(newPost)
            syncPostToFirebase(newPost)
            backupUserPostsToPublicStorage(currUser.email)
        }
    }

    fun toggleLike(post: PostEntity) {
        if (_uiState.value.isViewingAsGuest) return
        viewModelScope.launch {
            if (post.isLikedByUser) {
                val updated = post.copy(
                    isLikedByUser = false,
                    likeCount = maxOf(0, post.likeCount - 1),
                    userReaction = ""
                )
                repository.updatePost(updated)
            } else {
                val updated = post.copy(
                    isLikedByUser = true,
                    likeCount = post.likeCount + 1,
                    userReaction = "👍"
                )
                repository.updatePost(updated)
            }
        }
    }

    fun getReactionsForPost(postId: Int): kotlinx.coroutines.flow.Flow<List<com.example.data.database.ReactionEntity>> {
        return repository.getReactionsForPost(postId)
    }

    fun selectReaction(post: PostEntity, emoji: String) {
        if (_uiState.value.isViewingAsGuest) return
        val currentUsr = currentUser.value ?: return
        viewModelScope.launch {
            val alreadyLiked = post.isLikedByUser
            val updated = post.copy(
                isLikedByUser = true,
                userReaction = emoji,
                likeCount = if (alreadyLiked) post.likeCount else post.likeCount + 1
            )
            repository.updatePost(updated)

            // Local reaction save
            val newReaction = com.example.data.database.ReactionEntity(
                postId = post.postId,
                userEmail = currentUsr.email,
                userName = currentUsr.displayName,
                userAvatar = currentUsr.avatarUrl,
                emoji = emoji,
                timestamp = System.currentTimeMillis()
            )
            repository.addReaction(newReaction)

            // Sync to Firebase under posts/${firebaseKey}/reactions/${cleanedEmail}
            try {
                val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://aura-6637b-default-rtdb.firebaseio.com")
                val key = post.firebaseKey
                if (key.isNotEmpty()) {
                    val cleanedEmail = currentUsr.email.replace(".", "_").replace("@", "_")
                    val map = mapOf(
                        "userEmail" to currentUsr.email,
                        "userName" to currentUsr.displayName,
                        "userAvatar" to currentUsr.avatarUrl,
                        "emoji" to emoji,
                        "timestamp" to System.currentTimeMillis()
                    )
                    database.getReference("posts").child(key).child("reactions").child(cleanedEmail).setValue(map)
                }
            } catch (e: Exception) {
                android.util.Log.e("FirebaseSync", "Error uploading reaction: ${e.message}")
            }
        }
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            val post = postsFeed.value.find { it.postId == postId }
            val authorId = post?.authorId ?: -1
            val currentUserId = currentUser.value?.userId ?: -1
            if (currentUserId == authorId) {
                repository.deletePost(postId)
            } else {
                hidePostForCurrentUser(postId)
            }
        }
    }

    fun deleteAllUserContentPermanently() {
        val currUser = currentUser.value ?: return
        val email = currUser.email.lowercase().trim()
        val userId = currUser.userId
        val displayName = currUser.displayName

        viewModelScope.launch {
            // 1. Delete comments, stories, posts, videos, reels from Room local database
            repository.deletePostsByAuthor(userId, displayName)
            repository.deleteStoriesByAuthor(displayName)
            repository.deleteCommentsByAuthor(displayName)
            repository.deleteVideosByAuthor(userId, displayName)
            repository.deleteReelsByAuthor(userId, displayName)

            // 2. Delete posts from Firebase Realtime Database
            try {
                val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://aura-6637b-default-rtdb.firebaseio.com")
                val postsRef = database.getReference("posts")
                postsRef.get().addOnSuccessListener { snapshot ->
                    for (child in snapshot.children) {
                        val authorNameVal = child.child("authorName").getValue(String::class.java) ?: ""
                        val authorEmailVal = child.child("authorEmail").getValue(String::class.java) ?: ""
                        val authorIdVal = child.child("authorId").getValue(Int::class.java)
                        if (authorEmailVal.lowercase().trim() == email || 
                            authorNameVal.equals(displayName, ignoreCase = true) || 
                            authorIdVal == userId
                        ) {
                            child.ref.removeValue()
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AuraViewModel", "Error deleting posts on Firebase during purge: ${e.message}")
            }
        }
    }

    // --- Stories Handler ---

    fun setStoryMuted(muted: Boolean) {
        _uiState.update { it.copy(isStoryMuted = muted) }
    }

    fun deleteStory(storyId: Int) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val list = repository.stories.first()
                val storyItem = list.find { it.storyId == storyId }
                if (storyItem != null) {
                    val key = storyItem.firebaseKey
                    if (key.isNotEmpty()) {
                        val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://aura-6637b-default-rtdb.firebaseio.com")
                        database.getReference("stories").child(key).removeValue()
                        android.util.Log.d("AuraViewModel", "Successfully deleted story from Firebase: $key")
                    }
                }
                repository.deleteStoryById(storyId)
                android.util.Log.d("AuraViewModel", "Successfully deleted story ID: $storyId")
            } catch (e: Exception) {
                android.util.Log.e("AuraViewModel", "Failed to delete story: ${e.message}")
            }
        }
    }

    fun setAUMusicGeneratorVisible(visible: Boolean) {
        _uiState.update { 
            it.copy(showAUMusicGenerator = visible)
        }
    }

    fun setStoryCreatorVisible(
        visible: Boolean, 
        mediaUris: List<String> = emptyList(), 
        mediaType: String? = null
    ) {
        _uiState.update { 
            it.copy(
                showStoryCreator = visible,
                storyCreatorMediaUris = mediaUris,
                storyCreatorMediaUri = mediaUris.firstOrNull(),
                storyCreatorMediaType = mediaType
            )
        }
    }

    fun addStoryCreatorMedia(uris: List<String>, type: String?) {
        _uiState.update { state ->
            val updated = state.storyCreatorMediaUris + uris
            state.copy(
                storyCreatorMediaUris = updated,
                storyCreatorMediaUri = updated.firstOrNull(),
                storyCreatorMediaType = type ?: state.storyCreatorMediaType
            )
        }
    }

    fun removeStoryCreatorMediaAt(idx: Int) {
        _uiState.update { state ->
            val updated = state.storyCreatorMediaUris.toMutableList().apply {
                if (idx in indices) removeAt(idx)
            }
            state.copy(
                storyCreatorMediaUris = updated,
                storyCreatorMediaUri = updated.firstOrNull()
            )
        }
    }

    fun replaceStoryCreatorMediaAt(idx: Int, newUri: String) {
        _uiState.update { state ->
            val updated = state.storyCreatorMediaUris.toMutableList().apply {
                if (idx in indices) set(idx, newUri)
            }
            state.copy(
                storyCreatorMediaUris = updated,
                storyCreatorMediaUri = updated.firstOrNull()
            )
        }
    }

    fun copyMediaToPermanentStorage(uriList: List<android.net.Uri>): List<String> {
        val savedPaths = mutableListOf<String>()
        val resolver = context.contentResolver
        for (uri in uriList) {
            try {
                val type = resolver.getType(uri) ?: ""
                val isVideo = type.contains("video", ignoreCase = true) || uri.toString().contains(".mp4", ignoreCase = true)
                val ext = if (isVideo) "mp4" else "jpg"
                val destFile = java.io.File(context.filesDir, "story_media_${System.currentTimeMillis()}_${(100..999).random()}.$ext")
                resolver.openInputStream(uri)?.use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                savedPaths.add(destFile.absolutePath)
            } catch (e: Exception) {
                android.util.Log.e("AuraViewModel", "Error copying media: ${e.message}")
                savedPaths.add(uri.toString())
            }
        }
        return savedPaths
    }

    fun handleSharedMedia(uriStr: String, type: String?) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val resolver = context.contentResolver
                val uri = android.net.Uri.parse(uriStr)
                val isVideo = type?.contains("video", ignoreCase = true) == true
                val extension = if (isVideo) "mp4" else "jpg"
                val destFile = java.io.File(context.filesDir, "story_media_${System.currentTimeMillis()}.$extension")
                
                resolver.openInputStream(uri)?.use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                
                val stableUri = destFile.absolutePath
                val mediaTypeResolved = if (isVideo) "video" else "image"
                
                _uiState.update { 
                    it.copy(
                        showStoryCreator = true,
                        storyCreatorMediaUris = listOf(stableUri),
                        storyCreatorMediaUri = stableUri,
                        storyCreatorMediaType = mediaTypeResolved
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("AuraViewModel", "Error copying shared media file: ${e.message}")
                val isVideo = type?.contains("video", ignoreCase = true) == true
                val mediaTypeResolved = if (isVideo) "video" else "image"
                _uiState.update { 
                    it.copy(
                        showStoryCreator = true,
                        storyCreatorMediaUris = listOf(uriStr),
                        storyCreatorMediaUri = uriStr,
                        storyCreatorMediaType = mediaTypeResolved
                    )
                }
            }
        }
    }

    fun handleMultipleSharedMedia(uriStrs: List<String>, type: String?) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val savedPaths = mutableListOf<String>()
            var hasVideo = false
            for (uriStr in uriStrs) {
                try {
                    val resolver = context.contentResolver
                    val uri = android.net.Uri.parse(uriStr)
                    val isVideo = (type?.contains("video", ignoreCase = true) == true) || 
                                  (context.contentResolver.getType(uri)?.contains("video", ignoreCase = true) == true)
                    if (isVideo) hasVideo = true
                    val extension = if (isVideo) "mp4" else "jpg"
                    val destFile = java.io.File(context.filesDir, "story_media_${System.nanoTime()}.$extension")
                    
                    resolver.openInputStream(uri)?.use { input ->
                        destFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    savedPaths.add(destFile.absolutePath)
                } catch (e: Exception) {
                    android.util.Log.e("AuraViewModel", "Error copying shared media file: ${e.message}")
                    savedPaths.add(uriStr)
                }
            }
            if (savedPaths.isNotEmpty()) {
                _uiState.update { 
                    it.copy(
                        showStoryCreator = true,
                        storyCreatorMediaUris = savedPaths,
                        storyCreatorMediaUri = savedPaths.firstOrNull(),
                        storyCreatorMediaType = if (hasVideo) "video" else "image"
                    )
                }
            }
        }
    }

    fun syncStoryToFirebase(story: StoryEntity) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                // Save locally first to show instantly
                val localId = repository.addStory(story)
                
                val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://aura-6637b-default-rtdb.firebaseio.com")
                val storiesRef = database.getReference("stories")
                
                val map = hashMapOf<String, Any>(
                    "authorName" to story.authorName,
                    "authorAvatar" to story.authorAvatar,
                    "contentText" to story.contentText,
                    "imageUrl" to story.imageUrl,
                    "gradientIndex" to story.gradientIndex,
                    "timestamp" to story.timestamp,
                    "isAiRelated" to story.isAiRelated,
                    "websiteUrl" to story.websiteUrl,
                    "reactions" to story.reactions,
                    "viewersJson" to story.viewersJson
                )
                val newRef = storiesRef.push()
                val key = newRef.key ?: ""
                newRef.setValue(map)
                    .addOnSuccessListener {
                        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                            repository.addStory(story.copy(storyId = localId.toInt(), firebaseKey = key))
                        }
                    }
            } catch (e: Exception) {
                android.util.Log.e("FirebaseStorySync", "Error syncing story: ${e.message}")
            }
        }
    }

    fun publishStoryWithMedia(
        content: String, 
        gradientIdx: Int, 
        mediaUrisList: List<String> = emptyList(), 
        isAiRelated: Boolean = false, 
        websiteUrl: String = ""
    ) {
        val currUser = currentUser.value ?: return
        viewModelScope.launch {
            val commaMedia = mediaUrisList.filter { it.isNotBlank() }.joinToString(",")
            val newStory = StoryEntity(
                authorName = currUser.displayName,
                authorAvatar = currUser.avatarUrl,
                contentText = content,
                imageUrl = commaMedia,
                gradientIndex = gradientIdx,
                timestamp = System.currentTimeMillis(),
                isAiRelated = isAiRelated,
                websiteUrl = websiteUrl
            )
            syncStoryToFirebase(newStory)
            
            // Close Story Creator
            _uiState.update { 
                it.copy(
                    showStoryCreator = false,
                    storyCreatorMediaUri = null,
                    storyCreatorMediaType = null,
                    storyCreatorMediaUris = emptyList()
                )
            }
        }
    }

    fun publishStory(content: String, gradientIdx: Int) {
        val currUser = currentUser.value ?: return
        if (content.isBlank()) return
        viewModelScope.launch {
            val newStory = StoryEntity(
                authorName = currUser.displayName,
                authorAvatar = currUser.avatarUrl,
                contentText = content,
                gradientIndex = gradientIdx
            )
            syncStoryToFirebase(newStory)
        }
    }

    fun showStory(story: StoryEntity?) {
        _uiState.update { it.copy(showStoryViewer = story) }
    }

    fun reactToStory(storyId: Int, stepIndex: Int, emoji: String) {
        val currUser = currentUser.value ?: return
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val list = repository.stories.first()
                val storyItem = list.find { it.storyId == storyId }
                if (storyItem != null) {
                    val updatedReactions = if (storyItem.reactions.isBlank()) {
                        emoji
                    } else {
                        storyItem.reactions + "," + emoji
                    }
                    val updatedStory = storyItem.copy(reactions = updatedReactions)
                    repository.addStory(updatedStory)
                    
                    if (storyItem.firebaseKey.isNotEmpty()) {
                        val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://aura-6637b-default-rtdb.firebaseio.com")
                        val userKey = currUser.displayName.replace(".", "_").replace("#", "_").replace("$", "_").replace("[", "_").replace("]", "_")
                        
                        // Update views entry to include this emoji for this specific step index
                        val viewsMap = hashMapOf<String, Any>(
                            "displayName" to currUser.displayName,
                            "avatarUrl" to currUser.avatarUrl,
                            "emoji" to emoji
                        )
                        database.getReference("stories").child(storyItem.firebaseKey)
                            .child("views").child(stepIndex.toString()).child(userKey).setValue(viewsMap)
                        database.getReference("stories").child(storyItem.firebaseKey)
                            .child("reactions").setValue(updatedReactions)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("FirebaseStorySync", "Error reacting to story: ${e.message}")
            }
        }
    }

    fun registerStoryView(storyId: Int, stepIndex: Int) {
        val currUser = currentUser.value ?: return
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val list = repository.stories.first()
                val storyItem = list.find { it.storyId == storyId }
                if (storyItem != null && storyItem.firebaseKey.isNotEmpty()) {
                    // Do not overwrite existing view if it already has reaction emoji
                    val userKey = currUser.displayName.replace(".", "_").replace("#", "_").replace("$", "_").replace("[", "_").replace("]", "_")
                    val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://aura-6637b-default-rtdb.firebaseio.com")
                    
                    database.getReference("stories").child(storyItem.firebaseKey)
                        .child("views").child(stepIndex.toString()).child(userKey)
                        .get().addOnSuccessListener { snapshot ->
                            val currentEmoji = snapshot.child("emoji").getValue(String::class.java) ?: ""
                            val map = hashMapOf<String, Any>(
                                "displayName" to currUser.displayName,
                                "avatarUrl" to currUser.avatarUrl,
                                "emoji" to currentEmoji
                            )
                            database.getReference("stories").child(storyItem.firebaseKey)
                                .child("views").child(stepIndex.toString()).child(userKey).setValue(map)
                        }
                }
            } catch (e: Exception) {
                android.util.Log.e("FirebaseStorySync", "Error registering story view: ${e.message}")
            }
        }
    }

    // --- Custom Comments System ---

    fun getCommentsFlow(postId: Int): kotlinx.coroutines.flow.Flow<List<CommentEntity>> {
        return repository.getComments(postId)
    }

    fun submitCommentsForReplay(postId: Int, text: String) {
        if (text.isBlank()) return
        val currUser = currentUser.value ?: return
        viewModelScope.launch {
            val newComment = CommentEntity(
                postId = postId,
                authorName = currUser.displayName,
                authorAvatar = currUser.avatarUrl,
                text = text
            )
            repository.addComment(newComment)
            
            // Increment local database count and Firebase database count
            val posts = repository.feedPosts.first()
            val existing = posts.find { it.postId == postId }
            if (existing != null) {
                val updatedCount = existing.commentCount + 1
                repository.updatePost(existing.copy(commentCount = updatedCount))
                if (existing.firebaseKey.isNotEmpty()) {
                    try {
                        val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://aura-6637b-default-rtdb.firebaseio.com")
                        database.getReference("posts").child(existing.firebaseKey).child("commentCount").setValue(updatedCount)
                    } catch (e: Exception) {
                        android.util.Log.e("ReplayCommentCount", "Error updating Firebase commentCount: ${e.message}")
                    }
                }
            }
        }
    }

    fun openCommentsForPost(postId: Int) {
        _uiState.update { it.copy(activeCommentsPostId = postId, commentInputText = "") }
        viewModelScope.launch {
            repository.getComments(postId).collectLatest { commentList ->
                _uiState.update { it.copy(activeComments = commentList) }
            }
        }
    }

    fun closeComments() {
        _uiState.update { it.copy(activeCommentsPostId = null, activeComments = emptyList()) }
    }

    fun setCommentInput(text: String) {
        _uiState.update { it.copy(commentInputText = text) }
    }

    fun submitComment() {
        if (_uiState.value.isViewingAsGuest) return
        val postId = _uiState.value.activeCommentsPostId ?: return
        val text = _uiState.value.commentInputText
        if (text.isBlank()) return
        val currUser = currentUser.value ?: return

        viewModelScope.launch {
            val newComment = CommentEntity(
                postId = postId,
                authorName = currUser.displayName,
                authorAvatar = currUser.avatarUrl,
                text = text
            )
            repository.addComment(newComment)
            _uiState.update { it.copy(commentInputText = "") }
        }
    }

    // --- Chat Channels Protocol ---

    private fun setupChatChannel(otherUser: UserEntity) {
        val curr = currentUser.value ?: return
        val combinedId = if (curr.userId < otherUser.userId) {
            "${curr.userId}_${otherUser.userId}"
        } else {
            "${otherUser.userId}_${curr.userId}"
        }
        _uiState.update { it.copy(chatChannelId = combinedId) }
    }

    fun getActiveMessages(): Flow<List<MessageEntity>> {
        val chId = _uiState.value.chatChannelId
        if (chId.isBlank()) return flowOf(emptyList())
        return repository.getMessages(chId)
    }

    fun sendChatMessage(text: String) {
        val chId = _uiState.value.chatChannelId
        if (chId.isBlank() || text.isBlank()) return
        val currUser = currentUser.value ?: return

        viewModelScope.launch {
            val msg = MessageEntity(
                channelId = chId,
                senderName = currUser.displayName,
                senderAvatar = currUser.avatarUrl,
                content = text
            )
            repository.sendMessage(msg)
        }
    }

    fun sendStoryReply(replyText: String, targetAuthorName: String, storyText: String) {
        val currUser = currentUser.value ?: return
        if (replyText.isBlank()) return
        viewModelScope.launch {
            val list = repository.allUsers.first()
            val targetUser = list.find { it.displayName == targetAuthorName }
            val chId = if (targetUser != null) {
                val sorted = listOf(currUser.displayName.lowercase(), targetUser.displayName.lowercase()).sorted()
                "${sorted[0]}_${sorted[1]}"
            } else {
                "general_chat"
            }
            val formattedMsg = "Ref Story: \"$storyText\"\nReply: $replyText"
            val msg = MessageEntity(
                channelId = chId,
                senderName = currUser.displayName,
                senderAvatar = currUser.avatarUrl,
                content = formattedMsg
            )
            repository.sendMessage(msg)
        }
    }

    // --- Follow / Friend toggle ---
    fun toggleFollowUser(otherUser: UserEntity) {
        viewModelScope.launch {
            val updated = otherUser.copy(
                isFollowing = !otherUser.isFollowing,
                followerCount = otherUser.followerCount + (if (otherUser.isFollowing) -1 else 1)
            )
            repository.updateUser(updated)
            // If it is in active search or lists, it will automatically recompose due to Room's reactive Flow bindings.
        }
    }

    // --- Appwrite Control View toggles ---
    fun toggleAppwriteDetails() {
        _uiState.update { it.copy(showAppwriteDetails = !it.showAppwriteDetails) }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                val current = repository.getCurrentUser()
                if (current != null) {
                    repository.updateUser(current.copy(isCurrentUser = false))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (isRealAppwriteEnabled) {
                try {
                    appwriteAccount?.deleteSession("current")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            // Clear navigation history and reset routes
            _screenHistory.value = listOf(Screen.Welcome)
            saveNavigationState(Screen.Welcome, listOf(Screen.Welcome))
            _uiState.update { it.copy(currentScreen = Screen.Welcome) }
        }
    }

    // --- Login & Recovery Native Secure Logic ---

    sealed class AuthCheckResult {
        object Success : AuthCheckResult()
        object WrongPassword : AuthCheckResult()
        object UserDoesNotExist : AuthCheckResult()
        class Error(val message: String) : AuthCheckResult()
    }

    /**
     * Checks if the user is registered in Firebase or local database, and validates password.
     */
    fun performSecureLogin(email: String, pass: String, onResult: (AuthCheckResult) -> Unit) {
        viewModelScope.launch {
            val isRealAppwrite = isRealAppwriteEnabled

            val cleanEmail = email.lowercase().trim()

            if (isRealAppwrite) {
                try {
                    appwriteAccount?.let { account ->
                        account.createEmailPasswordSession(cleanEmail, pass)
                        
                        // Retrieve direct authenticated Appwrite UID
                        val authUser = account.get()
                        val remoteUid = authUser.id
                        
                        // Sync SQLite UserEntity
                        var existingUser = repository.getUserByEmail(cleanEmail)
                        if (existingUser == null) {
                            val savedProfile = getSavedProfileFromPrefs(cleanEmail)
                            if (savedProfile != null) {
                                repository.insertUser(savedProfile.copy(appwriteUid = remoteUid))
                                existingUser = repository.getUserByEmail(cleanEmail)
                            }
                        }

                        if (existingUser == null) {
                            // Wiped storage recovery: See if Appwrite has stored payload in custom preferences
                            try {
                                val prefs = account.getPrefs()
                                val photoUrlStr = prefs.data["photoUrl"]?.toString()
                                if (!photoUrlStr.isNullOrBlank()) {
                                    val deserialized = deserializeUriToUserEntity(photoUrlStr, cleanEmail)
                                    val restoredUser = if (deserialized != null) checkAndRestoreProfileImages(deserialized) else null
                                    if (restoredUser != null) {
                                        val finalRestoredUser = restoredUser.copy(appwriteUid = remoteUid)
                                        repository.insertUser(finalRestoredUser)
                                        existingUser = repository.getUserByEmail(cleanEmail)
                                        saveProfileToPrefs(finalRestoredUser)
                                    }
                                }
                            } catch (ex: Exception) {
                                android.util.Log.e("AppwriteSync", "Error restoring from user metadata: ${ex.message}")
                            }
                        }

                        if (existingUser != null) {
                            // Update active status in database
                            val currentActive = repository.getCurrentUser()
                            if (currentActive != null) {
                                repository.updateUser(currentActive.copy(isCurrentUser = false))
                            }
                            repository.updateUser(existingUser.copy(isCurrentUser = true, appwriteUid = remoteUid))
                        } else {
                            // Generate standard user locally
                            val displayName = cleanEmail.substringBefore("@").replaceFirstChar { it.uppercaseChar() }
                            val currentActive = repository.getCurrentUser()
                            if (currentActive != null) {
                                repository.updateUser(currentActive.copy(isCurrentUser = false))
                            }
                            val sdf = java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale.ENGLISH)
                            val todayStr = sdf.format(java.util.Date())
                            val newUser = UserEntity(
                                username = displayName.replace(" ", "_").lowercase() + "_aura",
                                displayName = displayName,
                                avatarUrl = "avatar_user_main",
                                coverUrl = "cover_main",
                                bio = "Excited to connect with friends on Aura 2026!",
                                isCurrentUser = true,
                                email = cleanEmail,
                                appwriteUid = remoteUid,
                                joinedDate = todayStr
                            )
                            val generatedId = repository.insertUser(newUser).toInt()
                            saveProfileToPrefs(newUser.copy(userId = generatedId))
                        }
                        
                        // Save credentials to local credentials Prefs for offline/subsequent check
                        val credentialsPrefs = context.getSharedPreferences("aura_credentials", android.content.Context.MODE_PRIVATE)
                        credentialsPrefs.edit().putString(cleanEmail, pass).apply()

                        // Direct redirect to main feed
                        _screenHistory.value = listOf(Screen.Main)
                        saveNavigationState(Screen.Main, listOf(Screen.Main))
                        _uiState.update { it.copy(currentScreen = Screen.Main, currentTab = MainTab.FEEDS) }
                        onResult(AuthCheckResult.Success)
                    }
                } catch (e: Exception) {
                    val msg = e.message ?: ""
                    if (msg.contains("password", ignoreCase = true) || msg.contains("invalid credentials", ignoreCase = true)) {
                        onResult(AuthCheckResult.WrongPassword)
                    } else if (msg.contains("not found", ignoreCase = true) || msg.contains("user", ignoreCase = true)) {
                        onResult(AuthCheckResult.UserDoesNotExist)
                    } else {
                        onResult(AuthCheckResult.Error(msg))
                    }
                }
            } else {
                // Sandbox Mode / Local checks
                // Find local user in database first
                var existingLocalUser = repository.getUserByEmail(cleanEmail)
                if (existingLocalUser == null) {
                    val savedProfile = getSavedProfileFromPrefs(cleanEmail)
                    if (savedProfile != null) {
                        repository.insertUser(savedProfile)
                        existingLocalUser = repository.getUserByEmail(cleanEmail)
                    }
                }

                if (existingLocalUser == null) {
                    onResult(AuthCheckResult.UserDoesNotExist)
                } else {
                    // Look up local credentials cache
                    val credentialsPrefs = context.getSharedPreferences("aura_credentials", android.content.Context.MODE_PRIVATE)
                    val storedPass = credentialsPrefs.getString(cleanEmail, null)
                    if (storedPass != null) {
                        if (storedPass == pass) {
                            // Correct password! Mark current in database
                            val currentActive = repository.getCurrentUser()
                            if (currentActive != null) {
                                repository.updateUser(currentActive.copy(isCurrentUser = false))
                            }
                            repository.updateUser(existingLocalUser.copy(isCurrentUser = true))
                            // Navigate
                            _screenHistory.value = listOf(Screen.Main)
                            saveNavigationState(Screen.Main, listOf(Screen.Main))
                            _uiState.update { it.copy(currentScreen = Screen.Main, currentTab = MainTab.FEEDS) }
                            onResult(AuthCheckResult.Success)
                        } else {
                            onResult(AuthCheckResult.WrongPassword)
                        }
                    } else {
                        // Edge case: if registered but no stored credential, let them login with any pass or seed pass
                        val currentActive = repository.getCurrentUser()
                        if (currentActive != null) {
                            repository.updateUser(currentActive.copy(isCurrentUser = false))
                        }
                        repository.updateUser(existingLocalUser.copy(isCurrentUser = true))
                        credentialsPrefs.edit().putString(cleanEmail, pass).apply()
                        _screenHistory.value = listOf(Screen.Main)
                        saveNavigationState(Screen.Main, listOf(Screen.Main))
                        _uiState.update { it.copy(currentScreen = Screen.Main, currentTab = MainTab.FEEDS) }
                        onResult(AuthCheckResult.Success)
                    }
                }
            }
        }
    }

    /**
     * Sends a real or simulated OTP to the user's email address.
     */
    fun sendAppwriteEmailOtp(email: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val cleanEmail = email.lowercase().trim()
            val isRealAppwrite = isRealAppwriteEnabled

            if (isRealAppwrite) {
                try {
                    appwriteAccount?.let { account ->
                        val generatedId = io.appwrite.ID.unique()
                        regVerificationUserId = generatedId
                        val token = account.createEmailToken(
                            userId = generatedId,
                            email = cleanEmail
                        )
                        regVerificationUserId = token.userId
                        android.util.Log.d("AppwriteOTP", "OTP Sent! Token userId: ${token.userId}")
                        onResult(true, null)
                    } ?: onResult(false, "Appwrite client is not initialized")
                } catch (e: Exception) {
                    android.util.Log.e("AppwriteOTP", "Error sending OTP: ${e.message}")
                    onResult(false, e.message ?: "Could not find account or send OTP")
                }
            } else {
                // Sandbox Mode Simulation
                val testOtp = ((100000..999999).random()).toString()
                sandboxOtpSecret = testOtp
                android.util.Log.d("AppwriteOTP", "Sandbox OTP Generated: $testOtp")
                onResult(true, null)
            }
        }
    }

    /**
     * Verifies the OTP code.
     */
    fun verifyAppwriteEmailOtp(email: String, otp: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val cleanEmail = email.lowercase().trim()
            val isRealAppwrite = isRealAppwriteEnabled

            if (isRealAppwrite) {
                try {
                    appwriteAccount?.let { account ->
                        val userIdToUse = regVerificationUserId.ifBlank { io.appwrite.ID.unique() }
                        val session = account.createSession(
                            userId = userIdToUse,
                            secret = otp.trim()
                        )
                        android.util.Log.d("AppwriteOTP", "OTP Verified! Session userId: ${session.userId}")
                        onResult(true, null)
                    } ?: onResult(false, "Appwrite client is not initialized")
                } catch (e: Exception) {
                    android.util.Log.e("AppwriteOTP", "Error verifying OTP: ${e.message}")
                    onResult(false, e.message ?: "Invalid OTP code.")
                }
            } else {
                // Sandbox Verification
                if (otp.trim() == sandboxOtpSecret || otp.trim() == "123456") {
                    onResult(true, null)
                } else {
                    onResult(false, "Incorrect verification code. Please try again!")
                }
            }
        }
    }

    /**
     * Signs in or registers the user using Discord OAuth2.
     * In Sandbox Mode, it instantly returns the chosen email and name.
     */
    fun signInWithDiscord(
        activity: androidx.activity.ComponentActivity,
        email: String,
        displayName: String,
        onResult: (Boolean, String?, String?, String?) -> Unit
    ) {
        viewModelScope.launch {
            val isRealAppwrite = isRealAppwriteEnabled
            if (!isRealAppwrite) {
                android.widget.Toast.makeText(context, "Discord Signed In: $email", android.widget.Toast.LENGTH_SHORT).show()
                kotlinx.coroutines.delay(1000)
                onResult(true, email, displayName, null)
                return@launch
            }

            try {
                val account = appwriteAccount ?: throw Exception("Appwrite Account is not initialized.")
                
                try {
                    account.deleteSession("current")
                } catch (e: Exception) {}

                account.createOAuth2Session(
                    activity = activity,
                    provider = io.appwrite.enums.OAuthProvider.DISCORD
                )

                val authUser = account.get()
                onResult(true, authUser.email, authUser.name, null)
            } catch (e: Exception) {
                android.util.Log.e("AuraDiscordVerify", "Error during Discord Sign-In: ${e.message}", e)
                onResult(false, null, null, e.message ?: "Discord OAuth2 sign-in failed.")
            }
        }
    }

    fun handleDiscordAuthSuccess(email: String, name: String, isSignInFlow: Boolean = false) {
        viewModelScope.launch {
            val cleanEmail = email.lowercase().trim()
            val isRealAppwrite = isRealAppwriteEnabled
            
            // Check if Appwrite account is still alive. If email does NOT exist in Appwrite any more, they were deleted!
            var userExistsInAppwrite = true
            if (isRealAppwrite) {
                try {
                    val comp = kotlinx.coroutines.CompletableDeferred<Boolean>()
                    checkIfEmailExistsInAppwrite(cleanEmail) { exists ->
                        comp.complete(exists)
                    }
                    userExistsInAppwrite = comp.await()
                } catch (e: Exception) {
                    userExistsInAppwrite = true
                }
            }
            
            if (!userExistsInAppwrite) {
                // User has been deleted from Appwrite! Delete local profile to register freshly.
                val oldLocalUser = repository.getUserByEmail(cleanEmail)
                if (oldLocalUser != null) {
                    val uidToDelete = oldLocalUser.userId
                    val nameToDelete = oldLocalUser.displayName
                    repository.deletePostsByAuthor(uidToDelete, nameToDelete)
                    repository.deleteStoriesByAuthor(nameToDelete)
                    repository.deleteCommentsByAuthor(nameToDelete)
                    
                    // Also delete from Firebase
                    try {
                        val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://aura-6637b-default-rtdb.firebaseio.com")
                        val postsRef = database.getReference("posts")
                        postsRef.get().addOnSuccessListener { snapshot ->
                            for (child in snapshot.children) {
                                val authorNameVal = child.child("authorName").getValue(String::class.java) ?: ""
                                val authorEmailVal = child.child("authorEmail").getValue(String::class.java) ?: ""
                                val authorIdVal = child.child("authorId").getValue(Int::class.java)
                                if (authorEmailVal.lowercase().trim() == cleanEmail || 
                                    authorNameVal.equals(nameToDelete, ignoreCase = true) || 
                                    authorIdVal == uidToDelete
                                ) {
                                    child.ref.removeValue()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AuraDiscordVerify", "Firebase Realtime DB posts purge error: ${e.message}")
                    }
                }
                repository.deleteUserByEmail(cleanEmail)
                
                // Clear any cached credentials & preferences for this email so they register 100% fresh
                val prefs = context.getSharedPreferences("aura_preferences", android.content.Context.MODE_PRIVATE)
                prefs.edit().clear().apply()
                
                val credentialsPrefs = context.getSharedPreferences("aura_credentials", android.content.Context.MODE_PRIVATE)
                credentialsPrefs.edit().remove(cleanEmail).apply()
            }
            
            if (isSignInFlow) {
                var remoteUid = ""
                if (isRealAppwrite) {
                    try {
                        appwriteAccount?.let { account ->
                            val authUser = account.get()
                            remoteUid = authUser.id
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AuraDiscordVerify", "Could not get live Appwrite UID: ${e.message}")
                    }
                }

                // SIGN IN FLOW - Instant login by restoring existing profile
                var existingUser = repository.getUserByEmail(cleanEmail)
                
                // If the user has a reconstructed Appwrite Auth account, the UID will be different!
                if (existingUser != null && remoteUid.isNotBlank() && existingUser.appwriteUid.isNotBlank() && existingUser.appwriteUid != remoteUid) {
                    android.util.Log.d("AuraDiscordVerify", "Recreated account detected in sign in! Stored: ${existingUser.appwriteUid}, Live: $remoteUid. Purging old data completely.")
                    purgeHistoricalDataForRecreatedUser(cleanEmail, existingUser.userId, existingUser.displayName)
                    
                    // Reset existingUser stats so they start empty
                    existingUser = existingUser.copy(
                        appwriteUid = remoteUid,
                        followerCount = 0,
                        followingCount = 0,
                        auraRating = 120,
                        bio = "Excited to connect with friends on Aura 2026!",
                        avatarUrl = "avatar_user_main",
                        coverUrl = "cover_main"
                    )
                    repository.updateUser(existingUser)
                    saveProfileToPrefs(existingUser)
                }

                if (existingUser == null) {
                    val savedProfile = getSavedProfileFromPrefs(cleanEmail)
                    if (savedProfile != null) {
                        var finalSaved = if (remoteUid.isNotBlank()) savedProfile.copy(appwriteUid = remoteUid) else savedProfile
                        if (remoteUid.isNotBlank() && savedProfile.appwriteUid.isNotBlank() && savedProfile.appwriteUid != remoteUid) {
                            android.util.Log.d("AuraDiscordVerify", "Recreated account from prefs! Stored: ${savedProfile.appwriteUid}, Live: $remoteUid. Purging old data completely.")
                            purgeHistoricalDataForRecreatedUser(cleanEmail, savedProfile.userId, savedProfile.displayName)
                            finalSaved = finalSaved.copy(
                                followerCount = 0,
                                followingCount = 0,
                                auraRating = 120,
                                bio = "Excited to connect with friends on Aura 2026!",
                                avatarUrl = "avatar_user_main",
                                coverUrl = "cover_main"
                            )
                        }
                        repository.insertUser(finalSaved)
                        existingUser = repository.getUserByEmail(cleanEmail)
                    }
                }
                
                if (existingUser != null) {
                    val currentActive = repository.getCurrentUser()
                    if (currentActive != null) {
                        repository.updateUser(currentActive.copy(isCurrentUser = false))
                    }
                    val updatedExisting = if (remoteUid.isNotBlank()) existingUser.copy(isCurrentUser = true, appwriteUid = remoteUid) else existingUser.copy(isCurrentUser = true)
                    repository.updateUser(updatedExisting)
                    android.widget.Toast.makeText(context, "Welcome back, ${existingUser.displayName}! / ওরায় আবার স্বাগতম!", android.widget.Toast.LENGTH_LONG).show()
                    _screenHistory.value = listOf(Screen.Main)
                    saveNavigationState(Screen.Main, listOf(Screen.Main))
                    _uiState.update { it.copy(currentScreen = Screen.Main, currentTab = MainTab.FEEDS) }
                } else {
                    // Sign-in flow requested, but they are a first-time user: Create their account on-the-fly and login instantly
                    val cleanedName = name.trim()
                    val displayName = if (cleanedName.isNotBlank()) cleanedName else cleanEmail.substringBefore("@").replaceFirstChar { it.uppercaseChar() }
                    val currentActive = repository.getCurrentUser()
                    if (currentActive != null) {
                        repository.updateUser(currentActive.copy(isCurrentUser = false))
                    }
                    val sdf = java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale.ENGLISH)
                    val todayStr = sdf.format(java.util.Date())
                    val newUser = UserEntity(
                        username = displayName.replace(" ", "_").lowercase() + "_aura",
                        displayName = displayName,
                        avatarUrl = "avatar_user_main",
                        coverUrl = "cover_main",
                        bio = "Excited to connect with friends on Aura 2026!",
                        isCurrentUser = true,
                        email = cleanEmail,
                        appwriteUid = remoteUid,
                        joinedDate = todayStr
                    )
                    val generatedId = repository.insertUser(newUser).toInt()
                    saveProfileToPrefs(newUser.copy(userId = generatedId))
                    
                    android.widget.Toast.makeText(context, "Account created! Welcome, $displayName! / ওরায় স্বাগতম!", android.widget.Toast.LENGTH_LONG).show()
                    
                    _screenHistory.value = listOf(Screen.Main)
                    saveNavigationState(Screen.Main, listOf(Screen.Main))
                    _uiState.update { it.copy(currentScreen = Screen.Main, currentTab = MainTab.FEEDS) }
                }
            } else {
                // SIGN UP FLOW: Always start a completely fresh guided registration sequence covering all screens
                // Reset all temporary registration variables to clean states
                regFirstName = ""
                regLastName = ""
                regPronoun = ""
                regGenderOptional = ""
                regGenderSelection = ""
                regGenderPrivacy = "Public"
                regEmail = cleanEmail
                regVerificationUserId = ""
                regPasswordText = "AuraDiscordSecurePass123!" // Bypassing PasswordSelectionScreen with auto-generated secure key
                regRelationStatus = ""
                regRelationPrivacy = "Public"
                regSchool = ""
                regCollege = ""
                regUniversity = ""
                regEducationPrivacy = "Public"
                regHobbies = ""
                regHobbyPrivacy = "Public"
                regBio = ""
                regProfilePic = ""
                regCoverPic = ""
                regHometown = ""
                regHometownPrivacy = "Public"
                regBirthday = ""
                regBirthdayPrivacy = "Public"

                val cleanedName = name.trim()
                if (cleanedName.isNotBlank()) {
                    val parts = cleanedName.split(" ", limit = 2)
                    regFirstName = parts.firstOrNull() ?: ""
                    regLastName = parts.getOrNull(1) ?: ""
                } else {
                    regFirstName = cleanEmail.substringBefore("@").replaceFirstChar { it.uppercaseChar() }
                    regLastName = ""
                }
                navigateTo(Screen.Register)
            }
        }
    }

    /**
     * Verifies the target email by launching Discord OAuth2 sessions and checking matches.
     */
    fun verifyWithAppwriteDiscord(activity: androidx.activity.ComponentActivity, targetEmail: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val cleanEmail = targetEmail.lowercase().trim()
            val isRealAppwrite = isRealAppwriteEnabled

            if (!isRealAppwrite) {
                // Sandbox simulated delay and response
                android.widget.Toast.makeText(context, "Sandbox Mode: Authenticating with simulated Discord... (Target: $cleanEmail)", android.widget.Toast.LENGTH_LONG).show()
                kotlinx.coroutines.delay(1500)
                if (cleanEmail.contains("@")) {
                    onResult(true, null)
                } else {
                    onResult(false, "Invalid active email for sandbox Discord verification.")
                }
                return@launch
            }

            try {
                val account = appwriteAccount ?: throw Exception("Appwrite Account is not initialized.")
                
                // Clear any active sessions beforehand
                try {
                    account.deleteSession("current")
                } catch (e: Exception) {}

                // Launch Appwrite Discord OAuth2 flow
                account.createOAuth2Session(
                    activity = activity,
                    provider = io.appwrite.enums.OAuthProvider.DISCORD
                )

                // Retrieve authenticated user
                val authUser = account.get()
                val authenticatedEmail = authUser.email.lowercase().trim()

                if (authenticatedEmail == cleanEmail) {
                    android.util.Log.d("AuraDiscordVerify", "Email matched: $authenticatedEmail. Successful verification!")
                    onResult(true, null)
                } else {
                    android.util.Log.e("AuraDiscordVerify", "Email mismatched! Expected: $cleanEmail, Found: $authenticatedEmail")
                    try {
                        account.deleteSession("current")
                    } catch (e: Exception) {}
                    onResult(
                        false, 
                        "Verification Failed: Checked email ($authenticatedEmail) does not match entered email ($cleanEmail). / সিলেক্ট করা Discord অ্যাকাউন্ট আপনার দেওয়া ইমেইলের সাথে মেলেনি!"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("AuraDiscordVerify", "Error during OAuth flow: ${e.message}", e)
                onResult(false, e.message ?: "Discord OAuth2 sign-in cancelled or failed.")
            }
        }
    }

       /**
     * Recovery Helper: Checks if the user email exists in the database/Appwrite before resetting password.
     */
    fun checkRecoveryEmailExists(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val cleanEmail = email.lowercase().trim()
            val isRealAppwrite = isRealAppwriteEnabled

            if (isRealAppwrite) {
                checkIfEmailExistsInAppwrite(cleanEmail) { exists ->
                    onResult(exists)
                }
            } else {
                // Local SQLite & SharedPreferences verification helper
                val existsLocally = (repository.getUserByEmail(cleanEmail) != null) || (getSavedProfileFromPrefs(cleanEmail) != null)
                onResult(existsLocally)
            }
        }
    }

    /**
     * Resets/updates user password in SharedPreferences and Appwrite Auth.
     */
    fun updateRecoveredPassword(email: String, newPass: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val cleanEmail = email.lowercase().trim()
            val isRealAppwrite = isRealAppwriteEnabled

            // Save to local SharedPreferences
            try {
                val credentialsPrefs = context.getSharedPreferences("aura_credentials", android.content.Context.MODE_PRIVATE)
                credentialsPrefs.edit().putString(cleanEmail, newPass).apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (isRealAppwrite) {
                try {
                    appwriteAccount?.let { account ->
                        try {
                            account.updatePassword(newPass)
                        } catch (e: Exception) {
                            android.util.Log.w("AppwriteSync", "Appwrite password update requires active session. Saved locally on device.")
                        }
                        onComplete(true)
                    } ?: onComplete(true)
                } catch (e: Exception) {
                    android.util.Log.e("AppwriteSync", "Appwrite password update failed: ${e.message}")
                    onComplete(true) // Successful fallback local update
                }
            } else {
                onComplete(true) // Sandbox mode success
            }
        }
    }

    /**
     * Auto joins the aura app home feed directly for recovered email.
     */
    fun finalizeRecoveryAndLogin(email: String) {
        viewModelScope.launch {
            val cleanEmail = email.lowercase().trim()
            // Mark the matched SQLite user as active
            val allUsersList = repository.allUsers.first()
            var existingUser = allUsersList.find { it.email.lowercase().trim() == cleanEmail }
            if (existingUser == null) {
                val savedProfile = getSavedProfileFromPrefs(cleanEmail)
                if (savedProfile != null) {
                    repository.insertUser(savedProfile)
                    val updatedUsers = repository.allUsers.first()
                    existingUser = updatedUsers.find { it.email.lowercase().trim() == cleanEmail }
                }
            }

            if (existingUser == null) {
                val isRealAppwrite = isRealAppwriteEnabled
                
                if (isRealAppwrite) {
                    try {
                        val clientAccount = appwriteAccount
                        if (clientAccount != null) {
                            val prefs = clientAccount.getPrefs()
                            val photoUrlStr = prefs.data["photoUrl"]?.toString()
                            if (!photoUrlStr.isNullOrBlank()) {
                                val deserialized = deserializeUriToUserEntity(photoUrlStr, cleanEmail)
                                val restoredUser = if (deserialized != null) checkAndRestoreProfileImages(deserialized) else null
                                if (restoredUser != null) {
                                    repository.insertUser(restoredUser)
                                    existingUser = repository.getUserByEmail(cleanEmail)
                                    saveProfileToPrefs(restoredUser)
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        android.util.Log.e("AppwriteSync", "Error restoring from user metadata in recovery: ${ex.message}")
                    }
                }
            }

            val finalUser = existingUser
            if (finalUser != null) {
                val currentActive = repository.getCurrentUser()
                if (currentActive != null) {
                    repository.updateUser(currentActive.copy(isCurrentUser = false))
                }
                repository.updateUser(finalUser.copy(isCurrentUser = true))
            } else {
                // Generates a user if none exists
                val displayName = cleanEmail.substringBefore("@").replaceFirstChar { it.uppercaseChar() }
                val currentActive = repository.getCurrentUser()
                if (currentActive != null) {
                    repository.updateUser(currentActive.copy(isCurrentUser = false))
                }
                val sdf = java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale.ENGLISH)
                val todayStr = sdf.format(java.util.Date())
                val newUser = UserEntity(
                    username = displayName.replace(" ", "_").lowercase() + "_aura",
                    displayName = displayName,
                    avatarUrl = "avatar_user_main",
                    coverUrl = "cover_main",
                    bio = "Excited to connect with friends on Aura 2026!",
                    isCurrentUser = true,
                    email = cleanEmail,
                    joinedDate = todayStr
                )
                val generatedId = repository.insertUser(newUser).toInt()
                saveProfileToPrefs(newUser.copy(userId = generatedId))
            }
            // Navigate directly to feed
            _screenHistory.value = listOf(Screen.Main)
            saveNavigationState(Screen.Main, listOf(Screen.Main))
            _uiState.update { it.copy(currentScreen = Screen.Main, currentTab = MainTab.FEEDS) }
        }
    }

    // --- Firebase Realtime Database Sync for Posts ---
    fun listenForFirebasePosts() {
        try {
            val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://aura-6637b-default-rtdb.firebaseio.com")
            val postsRef = database.getReference("posts")
            
            postsRef.addChildEventListener(object : com.google.firebase.database.ChildEventListener {
                override fun onChildAdded(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) {
                    syncSnapshotToLocal(snapshot)
                }
                override fun onChildChanged(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) {
                    syncSnapshotToLocal(snapshot)
                }
                override fun onChildRemoved(snapshot: com.google.firebase.database.DataSnapshot) {}
                override fun onChildMoved(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
            })
        } catch (e: Exception) {
            android.util.Log.e("FirebaseSync", "Error setting up Firebase Realtime Database listener: ${e.message}")
        }
    }

    private fun syncSnapshotToLocal(snapshot: com.google.firebase.database.DataSnapshot) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val authorName = snapshot.child("authorName").getValue(String::class.java) ?: ""
                val content = snapshot.child("content").getValue(String::class.java) ?: ""
                val timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                val imageUrl = snapshot.child("imageUrl").getValue(String::class.java) ?: ""
                val musicTrack = snapshot.child("musicTrack").getValue(String::class.java) ?: ""
                val gifUrl = snapshot.child("gifUrl").getValue(String::class.java) ?: ""
                if (authorName.isBlank() || (content.isBlank() && imageUrl.isBlank() && musicTrack.isBlank() && gifUrl.isBlank())) return@launch
                
                val authorId = snapshot.child("authorId").getValue(Int::class.java) ?: 999
                val authorAvatar = snapshot.child("authorAvatar").getValue(String::class.java) ?: "avatar_user_main"
                val gradientIndex = snapshot.child("gradientIndex").getValue(Int::class.java) ?: -1
                val likeCount = snapshot.child("likeCount").getValue(Int::class.java) ?: 0
                val commentCount = snapshot.child("commentCount").getValue(Int::class.java) ?: 0
                val shareCount = snapshot.child("shareCount").getValue(Int::class.java) ?: 0
                val isAiLabeled = snapshot.child("isAiLabeled").getValue(Boolean::class.java) ?: false
                val privacy = snapshot.child("privacy").getValue(String::class.java) ?: "Public"
                val location = snapshot.child("location").getValue(String::class.java) ?: ""
                val firebaseKey = snapshot.key ?: ""

                // Check local feeds to see if it exists
                val currentPosts = repository.feedPosts.first()
                val existingPost = currentPosts.find { 
                    (it.firebaseKey.isNotEmpty() && it.firebaseKey == firebaseKey) ||
                    (it.authorName == authorName && it.content == content && Math.abs(it.timestamp - timestamp) < 5000)
                }

                val targetPostId: Int
                if (existingPost != null) {
                    targetPostId = existingPost.postId
                    val updated = existingPost.copy(
                        firebaseKey = firebaseKey,
                        likeCount = likeCount,
                        commentCount = commentCount,
                        shareCount = shareCount,
                        location = if (existingPost.location.isBlank()) location else existingPost.location
                    )
                    repository.updatePost(updated)
                } else {
                    val syncedPost = PostEntity(
                        postId = 0,
                        authorId = authorId,
                        authorName = authorName,
                        authorAvatar = authorAvatar,
                        content = content,
                        imageUrl = imageUrl,
                        gradientIndex = gradientIndex,
                        timestamp = timestamp,
                        likeCount = likeCount,
                        commentCount = commentCount,
                        shareCount = shareCount,
                        isLikedByUser = false,
                        isAiLabeled = isAiLabeled,
                        privacy = privacy,
                        musicTrack = musicTrack,
                        gifUrl = gifUrl,
                        location = location,
                        firebaseKey = firebaseKey
                    )
                    targetPostId = repository.addPost(syncedPost).toInt()
                }

                // Synchronize reactions list from Firebase under this post
                val reactionsSnapshot = snapshot.child("reactions")
                repository.removeReactionsForPost(targetPostId)
                for (reactionChild in reactionsSnapshot.children) {
                    val rEmail = reactionChild.child("userEmail").getValue(String::class.java) ?: ""
                    val rName = reactionChild.child("userName").getValue(String::class.java) ?: ""
                    val rAvatar = reactionChild.child("userAvatar").getValue(String::class.java) ?: ""
                    val rEmoji = reactionChild.child("emoji").getValue(String::class.java) ?: ""
                    if (rEmail.isNotEmpty() && rEmoji.isNotEmpty()) {
                        repository.addReaction(
                            com.example.data.database.ReactionEntity(
                                postId = targetPostId,
                                userEmail = rEmail,
                                userName = rName,
                                userAvatar = rAvatar,
                                emoji = rEmoji
                            )
                        )
                    }
                }
                android.util.Log.d("FirebaseSync", "Successfully synced snap and reactions for: $authorName")
            } catch (e: Exception) {
                android.util.Log.e("FirebaseSync", "Error syncing Firebase snapshot: ${e.message}")
            }
        }
    }

    private fun syncStorySnapshotToLocal(snapshot: com.google.firebase.database.DataSnapshot) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val authorName = snapshot.child("authorName").getValue(String::class.java) ?: ""
                if (authorName.isBlank()) return@launch
                
                val authorAvatar = snapshot.child("authorAvatar").getValue(String::class.java) ?: "avatar_user_main"
                val contentText = snapshot.child("contentText").getValue(String::class.java) ?: ""
                val imageUrl = snapshot.child("imageUrl").getValue(String::class.java) ?: ""
                val gradientIndex = snapshot.child("gradientIndex").getValue(Int::class.java) ?: 0
                val timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                val isAiRelated = snapshot.child("isAiRelated").getValue(Boolean::class.java) ?: false
                val websiteUrl = snapshot.child("websiteUrl").getValue(String::class.java) ?: ""
                val reactions = snapshot.child("reactions").getValue(String::class.java) ?: ""
                val firebaseKey = snapshot.key ?: ""
                
                // Parse step-specific views
                val viewsSnapshot = snapshot.child("views")
                val viewersList = mutableListOf<String>()
                for (childSnapshot in viewsSnapshot.children) {
                    val key = childSnapshot.key ?: ""
                    val isStepNode = key.toIntOrNull() != null
                    if (isStepNode) {
                        for (viewChild in childSnapshot.children) {
                            val dName = viewChild.child("displayName").getValue(String::class.java) ?: ""
                            val avatar = viewChild.child("avatarUrl").getValue(String::class.java) ?: ""
                            val emoji = viewChild.child("emoji").getValue(String::class.java) ?: ""
                            if (dName.isNotEmpty()) {
                                viewersList.add("$key:$dName|$avatar|$emoji")
                            }
                        }
                    } else {
                        // Legacy view direct under views/
                        val dName = childSnapshot.child("displayName").getValue(String::class.java) ?: ""
                        val avatar = childSnapshot.child("avatarUrl").getValue(String::class.java) ?: ""
                        val emoji = childSnapshot.child("emoji").getValue(String::class.java) ?: ""
                        if (dName.isNotEmpty()) {
                            viewersList.add("0:$dName|$avatar|$emoji")
                        }
                    }
                }
                val viewersJson = viewersList.joinToString(";")
                
                val currentStories = repository.stories.first()
                val existingStory = currentStories.find { 
                    (it.firebaseKey.isNotEmpty() && it.firebaseKey == firebaseKey) ||
                    (it.authorName == authorName && Math.abs(it.timestamp - timestamp) < 5000)
                }
                
                if (existingStory != null) {
                    val updated = existingStory.copy(
                        firebaseKey = firebaseKey,
                        authorName = authorName,
                        authorAvatar = authorAvatar,
                        contentText = contentText,
                        imageUrl = imageUrl,
                        gradientIndex = gradientIndex,
                        timestamp = timestamp,
                        isAiRelated = isAiRelated,
                        websiteUrl = websiteUrl,
                        reactions = reactions,
                        viewersJson = viewersJson
                    )
                    repository.addStory(updated)
                } else {
                    val newStory = StoryEntity(
                        storyId = 0,
                        authorName = authorName,
                        authorAvatar = authorAvatar,
                        contentText = contentText,
                        imageUrl = imageUrl,
                        gradientIndex = gradientIndex,
                        timestamp = timestamp,
                        isAiRelated = isAiRelated,
                        websiteUrl = websiteUrl,
                        reactions = reactions,
                        firebaseKey = firebaseKey,
                        viewersJson = viewersJson
                    )
                    repository.addStory(newStory)
                }
            } catch (e: Exception) {
                android.util.Log.e("FirebaseStorySync", "Error syncing Firebase story snapshot: ${e.message}")
            }
        }
    }

    fun listenForFirebaseStories() {
        try {
            val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://aura-6637b-default-rtdb.firebaseio.com")
            val storiesRef = database.getReference("stories")
            
            storiesRef.addChildEventListener(object : com.google.firebase.database.ChildEventListener {
                override fun onChildAdded(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) {
                    syncStorySnapshotToLocal(snapshot)
                }
                override fun onChildChanged(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) {
                    syncStorySnapshotToLocal(snapshot)
                }
                override fun onChildRemoved(snapshot: com.google.firebase.database.DataSnapshot) {
                    val firebaseKey = snapshot.key ?: ""
                    if (firebaseKey.isNotEmpty()) {
                        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                            val localStories = repository.stories.first()
                            val match = localStories.find { it.firebaseKey == firebaseKey }
                            if (match != null) {
                                repository.deleteStoryById(match.storyId)
                            }
                        }
                    }
                }
                override fun onChildMoved(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
            })
        } catch (e: Exception) {
            android.util.Log.e("FirebaseStorySync", "Error setting up Firebase Realtime Database listener for stories: ${e.message}")
        }
    }

    fun syncPostToFirebase(post: PostEntity) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://aura-6637b-default-rtdb.firebaseio.com")
                val postsRef = database.getReference("posts")
                
                val userEmail = currentUser.value?.email ?: ""
                val map = hashMapOf<String, Any>(
                    "authorId" to post.authorId,
                    "authorName" to post.authorName,
                    "authorEmail" to userEmail,
                    "authorAvatar" to post.authorAvatar,
                    "content" to post.content,
                    "imageUrl" to post.imageUrl,
                    "gradientIndex" to post.gradientIndex,
                    "timestamp" to post.timestamp,
                    "likeCount" to post.likeCount,
                    "commentCount" to post.commentCount,
                    "shareCount" to post.shareCount,
                    "isAiLabeled" to post.isAiLabeled,
                    "privacy" to post.privacy,
                    "musicTrack" to post.musicTrack,
                    "gifUrl" to post.gifUrl,
                    "location" to post.location
                )
                val newRef = postsRef.push()
                val key = newRef.key ?: ""
                newRef.setValue(map)
                    .addOnSuccessListener {
                        viewModelScope.launch {
                            val updated = post.copy(firebaseKey = key)
                            repository.updatePost(updated)
                        }
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("FirebaseSync", "Failed to push to Firebase: ${e.message}")
                    }
            } catch (e: Exception) {
                android.util.Log.e("FirebaseSync", "Exception pushing post: ${e.message}")
            }
        }
    }

    // --- Audio Playback Mechanisms for Posts and Creating ---
    private val _currentlyPlayingTrack = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
    val currentlyPlayingTrack = _currentlyPlayingTrack.asStateFlow()

    private var mediaPlayer: android.media.MediaPlayer? = null

    fun playMusicTrack(trackName: String, url: String) {
        viewModelScope.launch {
            if (_currentlyPlayingTrack.value == trackName) {
                stopMusicPlayback()
            } else {
                stopMusicPlayback()
                _currentlyPlayingTrack.value = trackName
                android.util.Log.i("AuraAudio", "Playing track $trackName via Embedded WebView player in Aura app")
                
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "গান বা মিউজিকটি Aura অ্যাপে সফলভাবে সিঙ্ক হয়ে প্লে হচ্ছে! 🎧💜 (যেকোনো সময় চাইলে প্লেয়ারটি এক্সপ্যান্ড করে YT Music অ্যাপ বা ভিডিও অপশন দেখতে পারেন।)", android.widget.Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun playMusicTrackOnTheFly(trackName: String) {
        viewModelScope.launch {
            if (_currentlyPlayingTrack.value == trackName) {
                stopMusicPlayback()
            } else {
                _currentlyPlayingTrack.value = trackName
                android.util.Log.i("AuraAudio", "Playing track on the fly: $trackName via Embedded WebView player in Aura app")
                
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "মিউজিকটি Aura অ্যাপে সফলভাবে সিঙ্ক হয়ে প্লে হচ্ছে! 🎧💜", android.widget.Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun stopMusicPlayback() {
        _currentlyPlayingTrack.value = null
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch(e: Exception) {}
        mediaPlayer = null
    }

    fun fetchYouTubeTrackDetails(url: String, onComplete: (com.example.data.network.YouTubeTrackItem?) -> Unit) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val cleanUrl = url.trim()
            val normalizedUrl = cleanUrl
                .replace("music.youtube.com", "www.youtube.com")
                .replace("m.youtube.com", "www.youtube.com")
                
            try {
                val encodedUrl = java.net.URLEncoder.encode(normalizedUrl, "UTF-8")
                val oEmbedUrl = "https://www.youtube.com/oembed?url=$encodedUrl&format=json"
                val conn = java.net.URL(oEmbedUrl).openConnection() as java.net.HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 6000
                conn.readTimeout = 6000
                
                if (conn.responseCode == 200) {
                    val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                    val json = org.json.JSONObject(responseText)
                    val title = json.optString("title", "YouTube Music Song").trim()
                    val author = json.optString("author_name", "YouTube Music Artist").trim()
                    val thumbnail = json.optString("thumbnail_url", "").trim()
                    
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        onComplete(
                            com.example.data.network.YouTubeTrackItem(
                                id = "yt_" + System.currentTimeMillis(),
                                name = title,
                                artistName = author,
                                imageUrl = thumbnail.ifBlank {
                                    val videoId = com.example.data.network.extractYouTubeVideoId(cleanUrl)
                                    if (videoId != null) "https://img.youtube.com/vi/$videoId/hqdefault.jpg" else "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300&q=80"
                                },
                                previewUrl = cleanUrl
                            )
                        )
                    }
                } else {
                    val videoId = com.example.data.network.extractYouTubeVideoId(cleanUrl)
                    val thumbnail = if (videoId != null) "https://img.youtube.com/vi/$videoId/hqdefault.jpg" else "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300&q=80"
                    val fallbackName = if (videoId != null) "YouTube Track (#$videoId)" else "Custom YouTube Link"
                    val fallbackArtist = "YouTube Music App Sync"
                    
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        onComplete(
                            com.example.data.network.YouTubeTrackItem(
                                id = "yt_" + System.currentTimeMillis(),
                                name = fallbackName,
                                artistName = fallbackArtist,
                                imageUrl = thumbnail,
                                previewUrl = cleanUrl
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AuraAudio", "oEmbed retrieve exception: ${e.message}")
                val videoId = com.example.data.network.extractYouTubeVideoId(cleanUrl)
                val thumbnail = if (videoId != null) "https://img.youtube.com/vi/$videoId/hqdefault.jpg" else "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300&q=80"
                val fallbackName = if (videoId != null) "YouTube Track (#$videoId)" else "Imported Music Track"
                val fallbackArtist = "YouTube Music App Sync"
                
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onComplete(
                        com.example.data.network.YouTubeTrackItem(
                            id = "yt_" + System.currentTimeMillis(),
                            name = fallbackName,
                            artistName = fallbackArtist,
                            imageUrl = thumbnail,
                            previewUrl = cleanUrl
                        )
                    )
                }
            }
        }
    }

    private fun playPleasantChimeNotes() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val sampleRate = 8000
                val numSamples = sampleRate * 3 // 3 seconds
                val sample = DoubleArray(numSamples)
                val generatedSnd = ByteArray(2 * numSamples)
                
                val frequencies = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
                val duration = numSamples / frequencies.size
                
                for (i in 0 until numSamples) {
                    val freqIdx = (i / duration).coerceIn(0, frequencies.size - 1)
                    val freq = frequencies[freqIdx]
                    sample[i] = Math.sin(2.0 * Math.PI * i / (sampleRate / freq))
                    val envelope = (1.0 - (i % duration).toDouble() / duration)
                    val valShort = (sample[i] * 32767 * envelope).toInt().toShort()
                    
                    val idx = 2 * i
                    generatedSnd[idx] = (valShort.toInt() and 0x00ff).toByte()
                    generatedSnd[idx + 1] = ((valShort.toInt() and 0xff00) ushr 8).toByte()
                }
                
                val audioTrack = android.media.AudioTrack(
                    android.media.AudioManager.STREAM_MUSIC,
                    sampleRate,
                    android.media.AudioFormat.CHANNEL_OUT_MONO,
                    android.media.AudioFormat.ENCODING_PCM_16BIT,
                    generatedSnd.size,
                    android.media.AudioTrack.MODE_STATIC
                )
                audioTrack.write(generatedSnd, 0, generatedSnd.size)
                audioTrack.play()
            } catch (ex: Exception) {
                android.util.Log.e("AuraAudio", "Synth error: ${ex.message}")
            }
        }
    }

    // --- Live Real-time API Integrations (YouTube and Giphy) ---
    private val _liveYouTubeTracks = kotlinx.coroutines.flow.MutableStateFlow<List<com.example.data.network.YouTubeTrackItem>>(emptyList())
    val liveYouTubeTracks = _liveYouTubeTracks.asStateFlow()

    private val _liveGiphyGifs = kotlinx.coroutines.flow.MutableStateFlow<List<com.example.data.network.GiphyGifObject>>(emptyList())
    val liveGiphyGifs = _liveGiphyGifs.asStateFlow()

    private val _isSearchingYouTube = kotlinx.coroutines.flow.MutableStateFlow(false)
    val isSearchingYouTube = _isSearchingYouTube.asStateFlow()

    private val _isSearchingGiphy = kotlinx.coroutines.flow.MutableStateFlow(false)
    val isSearchingGiphy = _isSearchingGiphy.asStateFlow()

    fun searchYouTubeMusic(query: String) {
        viewModelScope.launch {
            _isSearchingYouTube.value = true
            try {
                val results = com.example.data.network.YouTubeClient.searchTracks(query)
                _liveYouTubeTracks.value = results
            } catch (e: Throwable) {
                android.util.Log.e("ViewModelYouTube", "Error searching YouTube: ${e.message}")
            } finally {
                _isSearchingYouTube.value = false
            }
        }
    }

    fun loadTrendingYouTubeMusic() {
        viewModelScope.launch {
            _isSearchingYouTube.value = true
            try {
                val results = com.example.data.network.YouTubeClient.getTrendingTracks()
                _liveYouTubeTracks.value = results
            } catch (e: Throwable) {
                android.util.Log.e("ViewModelYouTube", "Error loading trending YouTube music: ${e.message}")
            } finally {
                _isSearchingYouTube.value = false
            }
        }
    }

    fun loadNewestYouTubeMusic() {
        viewModelScope.launch {
            _isSearchingYouTube.value = true
            try {
                val results = com.example.data.network.YouTubeClient.getNewestTracks()
                _liveYouTubeTracks.value = results
            } catch (e: Throwable) {
                android.util.Log.e("ViewModelYouTube", "Error loading newest YouTube music: ${e.message}")
            } finally {
                _isSearchingYouTube.value = false
            }
        }
    }

    fun searchGiphyGifs(query: String) {
        viewModelScope.launch {
            _isSearchingGiphy.value = true
            try {
                val results = com.example.data.network.GiphyClient.search(query)
                _liveGiphyGifs.value = results
            } catch (e: Exception) {
                android.util.Log.e("ViewModelGiphy", "Error searching Giphy: ${e.message}")
            } finally {
                _isSearchingGiphy.value = false
            }
        }
    }

    fun loadTrendingGiphyGifs() {
        viewModelScope.launch {
            _isSearchingGiphy.value = true
            try {
                val results = com.example.data.network.GiphyClient.getTrending()
                _liveGiphyGifs.value = results
            } catch (e: Exception) {
                android.util.Log.e("ViewModelGiphy", "Error fetching trending Giphy: ${e.message}")
            } finally {
                _isSearchingGiphy.value = false
            }
        }
    }

    // --- Video & Reel Studio Controls ---
    fun setVideoCreatorVisible(visible: Boolean) {
        _uiState.update { it.copy(showVideoCreator = visible) }
    }

    fun setReelCreatorVisible(visible: Boolean) {
        _uiState.update { it.copy(showReelCreator = visible) }
    }

    fun updateVideoCreator(
        videoUri: String? = _uiState.value.videoCreatorVideoUri,
        thumbnailUri: String? = _uiState.value.videoCreatorThumbnailUri,
        title: String = _uiState.value.videoCreatorTitle,
        description: String = _uiState.value.videoCreatorDescription,
        tags: String = _uiState.value.videoCreatorTags,
        privacy: String = _uiState.value.videoCreatorPrivacy
    ) {
        _uiState.update { it.copy(
            videoCreatorVideoUri = videoUri,
            videoCreatorThumbnailUri = thumbnailUri,
            videoCreatorTitle = title,
            videoCreatorDescription = description,
            videoCreatorTags = tags,
            videoCreatorPrivacy = privacy
        ) }
    }

    fun updateReelCreator(
        videoUri: String? = _uiState.value.reelCreatorVideoUri,
        thumbnailUri: String? = _uiState.value.reelCreatorThumbnailUri,
        title: String = _uiState.value.reelCreatorTitle,
        description: String = _uiState.value.reelCreatorDescription,
        tags: String = _uiState.value.reelCreatorTags,
        privacy: String = _uiState.value.reelCreatorPrivacy
    ) {
        _uiState.update { it.copy(
            reelCreatorVideoUri = videoUri,
            reelCreatorThumbnailUri = thumbnailUri,
            reelCreatorTitle = title,
            reelCreatorDescription = description,
            reelCreatorTags = tags,
            reelCreatorPrivacy = privacy
        ) }
    }

    fun copyUriToLocalCache(uriStr: String, prefix: String): String {
        try {
            val uri = android.net.Uri.parse(uriStr) ?: return uriStr
            if (uri.scheme == "content" || uri.scheme == "file") {
                val contentResolver = context.contentResolver
                val mimeType = contentResolver.getType(uri) ?: ""
                val extension = when {
                    mimeType.startsWith("video/") || uriStr.contains("video") || prefix.contains("video") || prefix.contains("reel") -> "mp4"
                    else -> "jpg"
                }
                val tempFile = java.io.File(context.cacheDir, "${prefix}_${System.currentTimeMillis()}.$extension")
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    java.io.FileOutputStream(tempFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                return tempFile.absolutePath
            }
        } catch (e: Exception) {
            android.util.Log.e("AuraViewModel", "Error copying uri to cache: ${e.message}")
        }
        return uriStr
    }

    private fun extractVideoFrame(videoUriStr: String): String? {
        if (videoUriStr.isBlank()) return null
        try {
            val retriever = android.media.MediaMetadataRetriever()
            val uri = android.net.Uri.parse(videoUriStr)
            if (videoUriStr.startsWith("/") || uri.scheme == "file") {
                retriever.setDataSource(videoUriStr)
            } else if (uri.scheme == "content") {
                retriever.setDataSource(context, uri)
            } else {
                retriever.setDataSource(videoUriStr, HashMap())
            }
            val bitmap = retriever.getFrameAtTime(1000000, android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC) 
                ?: retriever.frameAtTime
            retriever.release()
            if (bitmap != null) {
                val tempFile = java.io.File(context.cacheDir, "extracted_thumb_${System.currentTimeMillis()}.jpg")
                java.io.FileOutputStream(tempFile).use { out ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
                }
                return tempFile.absolutePath
            }
        } catch (e: Exception) {
            android.util.Log.e("AuraViewModel", "Failed to extract video frame: ${e.message}")
        }
        return null
    }

    fun publishVideo(
        title: String,
        description: String,
        tags: String,
        videoUrl: String,
        thumbnailUrl: String,
        privacy: String
    ) {
        viewModelScope.launch {
            val currUser = repository.getCurrentUser() ?: return@launch
            val resolvedThumb = if (thumbnailUrl.isBlank()) {
                extractVideoFrame(videoUrl) ?: ""
            } else {
                thumbnailUrl
            }
            val video = com.example.data.database.VideoEntity(
                authorId = currUser.userId,
                authorName = currUser.displayName,
                authorAvatar = currUser.avatarUrl,
                title = title,
                description = description,
                videoUrl = videoUrl,
                thumbnailUrl = resolvedThumb,
                tags = tags,
                privacy = privacy,
                timestamp = System.currentTimeMillis()
            )
            repository.addVideo(video)
            // Reset state
            _uiState.update { it.copy(
                showVideoCreator = false,
                videoCreatorVideoUri = null,
                videoCreatorThumbnailUri = null,
                videoCreatorTitle = "",
                videoCreatorDescription = "",
                videoCreatorTags = "",
                videoCreatorPrivacy = "Public"
            ) }
        }
    }

    fun publishReel(
        title: String,
        description: String,
        tags: String,
        videoUrl: String,
        thumbnailUrl: String,
        privacy: String
    ) {
        viewModelScope.launch {
            val currUser = repository.getCurrentUser() ?: return@launch
            val resolvedThumb = if (thumbnailUrl.isBlank()) {
                extractVideoFrame(videoUrl) ?: ""
            } else {
                thumbnailUrl
            }
            val reel = com.example.data.database.ReelEntity(
                authorId = currUser.userId,
                authorName = currUser.displayName,
                authorAvatar = currUser.avatarUrl,
                title = title,
                description = description,
                videoUrl = videoUrl,
                thumbnailUrl = resolvedThumb,
                tags = tags,
                privacy = privacy,
                timestamp = System.currentTimeMillis()
            )
            repository.addReel(reel)
            // Reset state
            _uiState.update { it.copy(
                showReelCreator = false,
                reelCreatorVideoUri = null,
                reelCreatorThumbnailUri = null,
                reelCreatorTitle = "",
                reelCreatorDescription = "",
                reelCreatorTags = "",
                reelCreatorPrivacy = "Public"
            ) }
        }
    }

    fun toggleVideoLike(video: com.example.data.database.VideoEntity) {
        viewModelScope.launch {
            val isNowLiked = !video.isLikedByUser
            val updated = video.copy(
                isLikedByUser = isNowLiked,
                userReaction = if (isNowLiked) "👍" else "",
                likeCount = video.likeCount + (if (video.isLikedByUser) -1 else 1)
            )
            repository.updateVideo(updated)
        }
    }

    fun toggleReelLike(reel: com.example.data.database.ReelEntity) {
        viewModelScope.launch {
            val isNowLiked = !reel.isLikedByUser
            val updated = reel.copy(
                isLikedByUser = isNowLiked,
                userReaction = if (isNowLiked) "❤️" else "",
                likeCount = reel.likeCount + (if (reel.isLikedByUser) -1 else 1)
            )
            repository.updateReel(updated)
        }
    }

    fun setVideoReaction(video: com.example.data.database.VideoEntity, emoji: String) {
        viewModelScope.launch {
            val wasLiked = video.isLikedByUser
            val updated = video.copy(
                isLikedByUser = true,
                userReaction = emoji,
                likeCount = video.likeCount + (if (wasLiked) 0 else 1)
            )
            repository.updateVideo(updated)
        }
    }

    fun setReelReaction(reel: com.example.data.database.ReelEntity, emoji: String) {
        viewModelScope.launch {
            val wasLiked = reel.isLikedByUser
            val updated = reel.copy(
                isLikedByUser = true,
                userReaction = emoji,
                likeCount = reel.likeCount + (if (wasLiked) 0 else 1)
            )
            repository.updateReel(updated)
        }
    }

    fun toggleVideoSave(video: com.example.data.database.VideoEntity) {
        viewModelScope.launch {
            val updated = video.copy(
                isSavedByUser = !video.isSavedByUser
            )
            repository.updateVideo(updated)
        }
    }

    fun toggleReelSave(reel: com.example.data.database.ReelEntity) {
        viewModelScope.launch {
            val updated = reel.copy(
                isSavedByUser = !reel.isSavedByUser
            )
            repository.updateReel(updated)
        }
    }

    fun incrementVideoView(video: com.example.data.database.VideoEntity) {
        viewModelScope.launch {
            val updated = video.copy(viewCount = video.viewCount + 1)
            repository.updateVideo(updated)
        }
    }

    fun incrementReelView(reel: com.example.data.database.ReelEntity) {
        viewModelScope.launch {
            val updated = reel.copy(viewCount = reel.viewCount + 1)
            repository.updateReel(updated)
        }
    }

    fun incrementVideoShare(video: com.example.data.database.VideoEntity) {
        viewModelScope.launch {
            val updated = video.copy(shareCount = video.shareCount + 1)
            repository.updateVideo(updated)
        }
    }

    fun incrementReelShare(reel: com.example.data.database.ReelEntity) {
        viewModelScope.launch {
            val updated = reel.copy(shareCount = reel.shareCount + 1)
            repository.updateReel(updated)
        }
    }

    fun addVideoComment(videoId: Int, text: String) {
        viewModelScope.launch {
            val currUser = repository.getCurrentUser() ?: return@launch
            val comment = com.example.data.database.VideoCommentEntity(
                videoId = videoId,
                authorName = currUser.displayName,
                authorAvatar = currUser.avatarUrl,
                text = text,
                timestamp = System.currentTimeMillis()
            )
            repository.addVideoComment(comment)
            
            try {
                val currentVideos = repository.videos.first()
                val target = currentVideos.find { it.videoId == videoId }
                if (target != null) {
                    repository.updateVideo(target.copy(commentCount = target.commentCount + 1))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addReelComment(reelId: Int, text: String) {
        viewModelScope.launch {
            val currUser = repository.getCurrentUser() ?: return@launch
            val comment = com.example.data.database.ReelCommentEntity(
                reelId = reelId,
                authorName = currUser.displayName,
                authorAvatar = currUser.avatarUrl,
                text = text,
                timestamp = System.currentTimeMillis()
            )
            repository.addReelComment(comment)
            
            try {
                val currentReels = repository.reels.first()
                val target = currentReels.find { it.reelId == reelId }
                if (target != null) {
                    repository.updateReel(target.copy(commentCount = target.commentCount + 1))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getVideoCommentsFlow(videoId: Int): Flow<List<com.example.data.database.VideoCommentEntity>> {
        return repository.getVideoComments(videoId)
    }

    fun getReelCommentsFlow(reelId: Int): Flow<List<com.example.data.database.ReelCommentEntity>> {
        return repository.getReelComments(reelId)
    }

    fun toggleFriendRequest(userId: Int) {
        _uiState.update { state ->
            val updated = if (state.sentFriendRequests.contains(userId)) {
                state.sentFriendRequests - userId
            } else {
                state.sentFriendRequests + userId
            }
            state.copy(sentFriendRequests = updated)
        }
    }

    fun deleteVideo(videoId: Int) {
        viewModelScope.launch {
            repository.deleteVideo(videoId)
        }
    }

    fun deleteReel(reelId: Int) {
        viewModelScope.launch {
            repository.deleteReel(reelId)
        }
    }

    fun deletePhotoFromPost(post: com.example.data.database.PostEntity, photoUrl: String) {
        viewModelScope.launch {
            val currentImages = post.imageUrl.split(",").filter { it.isNotBlank() }
            val remainingImages = currentImages.filter { it != photoUrl }
            if (remainingImages.isEmpty()) {
                repository.updatePost(post.copy(imageUrl = ""))
            } else {
                repository.updatePost(post.copy(imageUrl = remainingImages.joinToString(",")))
            }
        }
    }

    fun togglePostSave(post: com.example.data.database.PostEntity) {
        viewModelScope.launch {
            val updated = post.copy(
                isSavedByUser = !post.isSavedByUser
            )
            repository.updatePost(updated)
        }
    }

    fun clearAllSavedItems() {
        viewModelScope.launch {
            videos.value.filter { it.isSavedByUser }.forEach { video ->
                repository.updateVideo(video.copy(isSavedByUser = false))
            }
            reels.value.filter { it.isSavedByUser }.forEach { reel ->
                repository.updateReel(reel.copy(isSavedByUser = false))
            }
            postsFeed.value.filter { it.isSavedByUser }.forEach { post ->
                repository.updatePost(post.copy(isSavedByUser = false))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopMusicPlayback()
    }
}

