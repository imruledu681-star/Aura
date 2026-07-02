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
    val privacy: String = "Public",
    val musicTrack: String = "",
    val gifUrl: String = "",
    val location: String = "",
    val firebaseKey: String = "",
    val isSavedByUser: Boolean = false
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
    val timestamp: Long = System.currentTimeMillis(),
    val isAiRelated: Boolean = false,
    val websiteUrl: String = "",
    val reactions: String = "",
    val firebaseKey: String = "",
    val viewersJson: String = ""
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

@Entity(tableName = "reactions")
data class ReactionEntity(
    @PrimaryKey(autoGenerate = true) val reactionId: Int = 0,
    val postId: Int,
    val userEmail: String,
    val userName: String,
    val userAvatar: String,
    val emoji: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true) val videoId: Int = 0,
    val authorId: Int,
    val authorName: String,
    val authorAvatar: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val tags: String,
    val privacy: String = "Public",
    val timestamp: Long = System.currentTimeMillis(),
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0,
    val isLikedByUser: Boolean = false,
    val isSavedByUser: Boolean = false,
    val viewCount: Int = 0,
    val userReaction: String = ""
)

@Entity(tableName = "reels")
data class ReelEntity(
    @PrimaryKey(autoGenerate = true) val reelId: Int = 0,
    val authorId: Int,
    val authorName: String,
    val authorAvatar: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String = "",
    val tags: String,
    val privacy: String = "Public",
    val timestamp: Long = System.currentTimeMillis(),
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0,
    val isLikedByUser: Boolean = false,
    val isSavedByUser: Boolean = false,
    val viewCount: Int = 0,
    val userReaction: String = ""
)

@Entity(tableName = "video_comments")
data class VideoCommentEntity(
    @PrimaryKey(autoGenerate = true) val commentId: Int = 0,
    val videoId: Int,
    val authorName: String,
    val authorAvatar: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reel_comments")
data class ReelCommentEntity(
    @PrimaryKey(autoGenerate = true) val commentId: Int = 0,
    val reelId: Int,
    val authorName: String,
    val authorAvatar: String,
    val text: String,
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
    suspend fun insertStory(story: StoryEntity): Long

    @Query("DELETE FROM stories WHERE storyId = :storyId")
    suspend fun deleteStoryById(storyId: Int)

    @Query("DELETE FROM stories WHERE timestamp < :boundary")
    suspend fun deleteExpiredStories(boundary: Long)

    // Messages
    @Query("SELECT * FROM messages WHERE channelId = :channelId ORDER BY timestamp ASC")
    fun getMessages(channelId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM posts WHERE authorId = :userId OR LOWER(TRIM(authorName)) = LOWER(TRIM(:displayName))")
    suspend fun deletePostsByAuthorSync(userId: Int, displayName: String)

    @Query("DELETE FROM stories WHERE LOWER(TRIM(authorName)) = LOWER(TRIM(:displayName))")
    suspend fun deleteStoriesByAuthorSync(displayName: String)

    @Query("DELETE FROM comments WHERE LOWER(TRIM(authorName)) = LOWER(TRIM(:displayName))")
    suspend fun deleteCommentsByAuthorSync(displayName: String)

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

    // Reactions
    @Query("SELECT * FROM reactions WHERE postId = :postId ORDER BY timestamp DESC")
    fun getReactionsForPostFlow(postId: Int): Flow<List<ReactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReaction(reaction: ReactionEntity)

    @Query("DELETE FROM reactions WHERE postId = :postId AND LOWER(TRIM(userEmail)) = LOWER(TRIM(:userEmail))")
    suspend fun deleteReaction(postId: Int, userEmail: String)

    @Query("DELETE FROM reactions WHERE postId = :postId")
    suspend fun deleteReactionsForPost(postId: Int)

    @Query("DELETE FROM reactions")
    suspend fun clearReactions()

    // --- Video DAO operations ---
    @Query("SELECT * FROM videos ORDER BY timestamp DESC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity): Long

    @Update
    suspend fun updateVideo(video: VideoEntity)

    @Query("DELETE FROM videos WHERE videoId = :id")
    suspend fun deleteVideo(id: Int)

    @Query("DELETE FROM videos WHERE authorId = :userId OR LOWER(TRIM(authorName)) = LOWER(TRIM(:displayName))")
    suspend fun deleteVideosByAuthorSync(userId: Int, displayName: String)

    @Query("DELETE FROM videos")
    suspend fun clearVideos()

    // --- Reel DAO operations ---
    @Query("SELECT * FROM reels ORDER BY timestamp DESC")
    fun getAllReels(): Flow<List<ReelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReel(reel: ReelEntity): Long

    @Update
    suspend fun updateReel(reel: ReelEntity)

    @Query("DELETE FROM reels WHERE reelId = :id")
    suspend fun deleteReel(id: Int)

    @Query("DELETE FROM reels WHERE authorId = :userId OR LOWER(TRIM(authorName)) = LOWER(TRIM(:displayName))")
    suspend fun deleteReelsByAuthorSync(userId: Int, displayName: String)

    @Query("DELETE FROM reels")
    suspend fun clearReels()

    // --- Video Comments DAO ---
    @Query("SELECT * FROM video_comments WHERE videoId = :videoId ORDER BY timestamp ASC")
    fun getCommentsForVideo(videoId: Int): Flow<List<VideoCommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideoComment(comment: VideoCommentEntity)

    @Query("DELETE FROM video_comments")
    suspend fun clearVideoComments()

    // --- Reel Comments DAO ---
    @Query("SELECT * FROM reel_comments WHERE reelId = :reelId ORDER BY timestamp ASC")
    fun getCommentsForReel(reelId: Int): Flow<List<ReelCommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReelComment(comment: ReelCommentEntity)

    @Query("DELETE FROM reel_comments")
    suspend fun clearReelComments()
}

// --- AppDatabase Room Holder ---

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        CommentEntity::class,
        StoryEntity::class,
        MessageEntity::class,
        ReactionEntity::class,
        VideoEntity::class,
        ReelEntity::class,
        VideoCommentEntity::class,
        ReelCommentEntity::class
    ],
    version = 19,
    exportSchema = false
)
abstract class AuraDatabase : RoomDatabase() {
    abstract val dao: AuraDao
}
