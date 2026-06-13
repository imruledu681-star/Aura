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
}

// Current Navigation Tab in Main Screen
enum class MainTab {
    FEEDS,
    FRIENDS,
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
    val privacy: String = "Public"
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
    val showNoteCreator: Boolean = false,
    val isViewingAsGuest: Boolean = false
)

class AuraViewModel(
    private val repository: AuraRepository,
    private val context: Context
) : ViewModel() {

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

        // 1. Restore Avatar from MediaStore backup if missing
        if (isLocalFileMissing(finalAvatar) || finalAvatar.startsWith("content://") || finalAvatar.startsWith("http") || finalAvatar == "avatar_user_main" || finalAvatar.isBlank()) {
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

        // 2. Restore Cover from MediaStore backup if missing
        if (isLocalFileMissing(finalCover) || finalCover.startsWith("content://") || finalCover.startsWith("http") || finalCover == "cover_main" || finalCover.isBlank()) {
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

    private val _uiState = MutableStateFlow(AuraUiState(currentScreen = initialNavigation.first))
    val uiState: StateFlow<AuraUiState> = _uiState.asStateFlow()

    private val _screenHistory = MutableStateFlow<List<Screen>>(initialNavigation.second)
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
            filteredPosts.filter {
                it.content.contains(query, ignoreCase = true) ||
                it.authorName.contains(query, ignoreCase = true)
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
        // Prepare database with default high-quality 2026 posts and configurations
        viewModelScope.launch {
            repository.seedMockDataIfEmpty()
            restoreNavigationState()
            checkAppwriteUserSync()
            scanAndImportAllUsersFromPublicStorage()
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

        if (currentStr != null) {
            val allCachedUsers = repository.allUsers.first()
            fun stringToScreen(route: String): Screen? {
                return when {
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
            }
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
        _uiState.update { it.copy(currentScreen = Screen.Main, currentTab = MainTab.FEEDS) }

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

    fun setReelCreatorVisible(visible: Boolean) {
        _uiState.update { it.copy(showReelCreator = visible) }
    }

    fun setLiveSimulatorVisible(visible: Boolean) {
        _uiState.update { it.copy(showLiveSimulator = visible) }
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

    fun updateCurrentUser(user: UserEntity) {
        viewModelScope.launch {
            repository.updateUser(user)
            saveProfileToPrefs(user)
            
            // Get credentials password if registered/logged in to perform updated backup
            val cleanEmail = user.email.lowercase().trim()
            if (cleanEmail.isNotBlank()) {
                val credentialsPrefs = context.getSharedPreferences("aura_credentials", android.content.Context.MODE_PRIVATE)
                val savedPassword = credentialsPrefs.getString(cleanEmail, "AuraDiscordSecurePass123!") ?: "AuraDiscordSecurePass123!"
                backupProfileToPublicStorage(user, savedPassword)
                backupProfileToImageMediaStore(user, savedPassword)
                
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
                val generatedId = if (existingLocal != null) {
                    // Update the existing entity instead of inserting a duplicate
                    val updatedUser = newUser.copy(userId = existingLocal.userId)
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
                        val isRealAppwrite = isRealAppwriteEnabled
                        if (isRealAppwrite) {
                            val cloudAvatarUrl = uploadUriToCloud(avatarSource)
                            val cloudCoverUrl = uploadUriToCloud(coverSource)
                            
                            val cloudUser = newUser.copy(
                                userId = generatedId,
                                avatarUrl = cloudAvatarUrl,
                                coverUrl = cloudCoverUrl
                            )
                            
                            // Save backup to cloud user store
                            repository.updateUser(cloudUser)
                            saveProfileToPrefs(cloudUser)

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

    fun isAiRelatedPhoto(url: String): Boolean {
        if (url.isBlank()) return false
        val urls = url.split(",")
        return urls.any { it.contains("unsplash") || it.contains("ai") || it.contains("synthetic") || it.contains("generated") }
    }

    fun publishPost() {
        val currUser = currentUser.value ?: return
        val state = _uiState.value.creationState
        if (state.content.isBlank() && state.imageInputUrl.isBlank()) return

        val isImageAiRelated = isAiRelatedPhoto(state.imageInputUrl)
        if (isImageAiRelated && !state.isAiLabeled) {
            android.widget.Toast.makeText(context, "⚠️ AI Content Detected! The Aura recognition engine has detected AI-generated imagery. Please turn on 'Add AI Label' to publish this post safely. 🤖💜", android.widget.Toast.LENGTH_LONG).show()
            return
        }

        viewModelScope.launch {
            val newPost = PostEntity(
                authorId = currUser.userId,
                authorName = currUser.displayName,
                authorAvatar = currUser.avatarUrl,
                content = state.content,
                imageUrl = state.imageInputUrl,
                gradientIndex = if (state.imageInputUrl.isNotBlank()) -1 else state.selectedGradientIdx,
                timestamp = System.currentTimeMillis(),
                isAiLabeled = state.isAiLabeled,
                mentionedUserIds = state.mentionedUserIds,
                privacy = state.privacy
            )
            repository.addPost(newPost)
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
        mentionedUserIds: String = ""
    ) {
        val currUser = currentUser.value ?: return
        if (content.isBlank() && imageUrl.isBlank()) return
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
                mentionedUserIds = mentionedUserIds
            )
            repository.addPost(newPost)
            backupUserPostsToPublicStorage(currUser.email)
        }
    }

    fun toggleLike(post: PostEntity) {
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

    fun selectReaction(post: PostEntity, emoji: String) {
        viewModelScope.launch {
            val alreadyLiked = post.isLikedByUser
            val updated = post.copy(
                isLikedByUser = true,
                userReaction = emoji,
                likeCount = if (alreadyLiked) post.likeCount else post.likeCount + 1
            )
            repository.updatePost(updated)
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

    // --- Stories Handler ---

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
            repository.addStory(newStory)
        }
    }

    fun showStory(story: StoryEntity?) {
        _uiState.update { it.copy(showStoryViewer = story) }
    }

    // --- Custom Comments System ---

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
                repository.deleteUserByEmail(cleanEmail)
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
                if (existingUser == null) {
                    val savedProfile = getSavedProfileFromPrefs(cleanEmail)
                    if (savedProfile != null) {
                        val finalSaved = if (remoteUid.isNotBlank()) savedProfile.copy(appwriteUid = remoteUid) else savedProfile
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
}

