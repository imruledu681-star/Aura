package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- Room Support Entities ---

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val username: String,
    val displayName: String,
    val avatarUrl: String, // Can be local resource-id code or image url
    val coverUrl: String,
    val bio: String,
    val isCurrentUser: Boolean = false,
    val auraRating: Int = 100,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowing: Boolean = false,
    val email: String = "",
    val relationshipStatus: String = "",
    val relationshipPrivacy: String = "Public",
    val school: String = "",
    val college: String = "",
    val university: String = "",
    val educationPrivacy: String = "Public",
    val hobbies: String = "",
    val hobbiesPrivacy: String = "Public",
    val hometown: String = "",
    val hometownPrivacy: String = "Public",
    val birthday: String = "",
    val birthdayPrivacy: String = "Public",
    val gender: String = "",
    val genderPrivacy: String = "Public",
    val appwriteUid: String = "",
    val isProfileLocked: Boolean = false,
    val isOnlineNow: Boolean = true,
    val joinedDate: String = ""
) {
    val resolvedUid: String
        get() {
            return if (!appwriteUid.isNullOrBlank()) {
                appwriteUid
            } else if (displayName.contains("imrul", ignoreCase = true) || username.contains("imrul", ignoreCase = true)) {
                "6a27c85bacccc2d14949"
            } else {
                val hash = displayName.hashCode().coerceAtLeast(0)
                "6a27c85b" + String.format("%012x", hash.toLong())
            }
        }
}

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val postId: Int = 0,
    val authorId: Int,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val imageUrl: String = "",
    val gradientIndex: Int = -1, // -1 means none, 0-4 represent colorful premium backdrop gradients
    val timestamp: Long = System.currentTimeMillis(),
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0,
    val isLikedByUser: Boolean = false,
    val auraSparkles: Int = 0, // Interactive reaction sparkles
    val userReaction: String = "", // Under 2026 specs, standard reactive user chosen emoji like 👍, ❤️, 🥰, 😄, 😮, 😢, 😡
    val isAiLabeled: Boolean = false,
    val mentionedUserIds: String = "",
    val privacy: String = "Public"
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val commentId: Int = 0,
    val postId: Int,
    val authorName: String,
    val authorAvatar: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey(autoGenerate = true) val storyId: Int = 0,
    val authorName: String,
    val authorAvatar: String,
    val contentText: String = "",
    val imageUrl: String = "",
    val gradientIndex: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val messageId: Int = 0,
    val channelId: String, // e.g. "user1_user2"
    val senderName: String,
    val senderAvatar: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

// --- DAO Implementation ---

@Dao
interface AuraDao {
    // Users
    @Query("SELECT * FROM users ORDER BY userId DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    fun getCurrentUserSync(): UserEntity?

    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    fun getCurrentUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE LOWER(TRIM(email)) = LOWER(TRIM(:email)) LIMIT 1")
    suspend fun getUserByEmailSync(email: String): UserEntity?

    @Query("DELETE FROM users WHERE LOWER(TRIM(email)) = LOWER(TRIM(:email))")
    suspend fun deleteUserByEmailSync(email: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    // Posts
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getFeedPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("UPDATE posts SET likeCount = likeCount + :diff, isLikedByUser = :isLiked WHERE postId = :id")
    suspend fun togglePostLike(id: Int, diff: Int, isLiked: Boolean)

    @Query("UPDATE posts SET commentCount = commentCount + 1 WHERE postId = :id")
    suspend fun incrementCommentCount(id: Int)

    @Query("DELETE FROM posts WHERE postId = :id")
    suspend fun deletePost(id: Int)

    // Comments
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Int): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    // Stories
    @Query("SELECT * FROM stories ORDER BY timestamp DESC")
    fun getAllStories(): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity)

    // Messages
    @Query("SELECT * FROM messages WHERE channelId = :channelId ORDER BY timestamp ASC")
    fun getMessages(channelId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM users")
    suspend fun clearUsers()

    @Query("DELETE FROM posts")
    suspend fun clearPosts()

    @Query("DELETE FROM comments")
    suspend fun clearComments()

    @Query("DELETE FROM stories")
    suspend fun clearStories()

    @Query("DELETE FROM messages")
    suspend fun clearMessages()
}

// --- AppDatabase Room Holder ---

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        CommentEntity::class,
        StoryEntity::class,
        MessageEntity::class
    ],
    version = 12,
    exportSchema = false
)
abstract class AuraDatabase : RoomDatabase() {
    abstract val dao: AuraDao
}
