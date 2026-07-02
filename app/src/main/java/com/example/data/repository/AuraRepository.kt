package com.example.data.repository

import com.example.data.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AuraRepository(private val dao: AuraDao) {

    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    val currentUserFlow: Flow<UserEntity?> = dao.getCurrentUserFlow()
    val feedPosts: Flow<List<PostEntity>> = dao.getFeedPosts()
    val stories: Flow<List<StoryEntity>> = dao.getAllStories()
    val videos: Flow<List<VideoEntity>> = dao.getAllVideos()
    val reels: Flow<List<ReelEntity>> = dao.getAllReels()

    suspend fun getCurrentUser(): UserEntity? = withContext(Dispatchers.IO) {
        dao.getCurrentUserSync()
    }

    suspend fun getUserByEmail(email: String): UserEntity? = withContext(Dispatchers.IO) {
        dao.getUserByEmailSync(email)
    }

    suspend fun deleteUserByEmail(email: String) = withContext(Dispatchers.IO) {
        dao.deleteUserByEmailSync(email)
    }

    suspend fun deletePostsByAuthor(userId: Int, displayName: String) = withContext(Dispatchers.IO) {
        dao.deletePostsByAuthorSync(userId, displayName)
    }

    suspend fun deleteStoriesByAuthor(displayName: String) = withContext(Dispatchers.IO) {
        dao.deleteStoriesByAuthorSync(displayName)
    }

    suspend fun deleteCommentsByAuthor(displayName: String) = withContext(Dispatchers.IO) {
        dao.deleteCommentsByAuthorSync(displayName)
    }

    suspend fun insertUser(user: UserEntity): Long = withContext(Dispatchers.IO) {
        dao.insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) = withContext(Dispatchers.IO) {
        dao.updateUser(user)
    }

    suspend fun addPost(post: PostEntity): Long = withContext(Dispatchers.IO) {
        dao.insertPost(post)
    }

    suspend fun deletePost(postId: Int) = withContext(Dispatchers.IO) {
        dao.deletePost(postId)
    }

    suspend fun updatePost(post: PostEntity) = withContext(Dispatchers.IO) {
        dao.updatePost(post)
    }

    suspend fun toggleLike(post: PostEntity) = withContext(Dispatchers.IO) {
        val diff = if (post.isLikedByUser) -1 else 1
        dao.togglePostLike(post.postId, diff, !post.isLikedByUser)
    }

    fun getComments(postId: Int): Flow<List<CommentEntity>> {
        return dao.getCommentsForPost(postId)
    }

    suspend fun addComment(comment: CommentEntity) = withContext(Dispatchers.IO) {
        dao.insertComment(comment)
        dao.incrementCommentCount(comment.postId)
    }

    suspend fun addStory(story: StoryEntity): Long = withContext(Dispatchers.IO) {
        dao.insertStory(story)
    }

    suspend fun deleteStoryById(storyId: Int) = withContext(Dispatchers.IO) {
        dao.deleteStoryById(storyId)
    }

    suspend fun deleteExpiredStories(boundary: Long) = withContext(Dispatchers.IO) {
        dao.deleteExpiredStories(boundary)
    }

    fun getMessages(channelId: String): Flow<List<MessageEntity>> {
        return dao.getMessages(channelId)
    }

    suspend fun sendMessage(message: MessageEntity) = withContext(Dispatchers.IO) {
        dao.insertMessage(message)
    }

    fun getReactionsForPost(postId: Int): Flow<List<ReactionEntity>> = dao.getReactionsForPostFlow(postId)

    suspend fun addReaction(reaction: ReactionEntity) = withContext(Dispatchers.IO) {
        dao.insertReaction(reaction)
    }

    suspend fun removeReaction(postId: Int, userEmail: String) = withContext(Dispatchers.IO) {
        dao.deleteReaction(postId, userEmail)
    }

    suspend fun removeReactionsForPost(postId: Int) = withContext(Dispatchers.IO) {
        dao.deleteReactionsForPost(postId)
    }

    // --- Videos ---
    suspend fun addVideo(video: VideoEntity): Long = withContext(Dispatchers.IO) {
        dao.insertVideo(video)
    }

    suspend fun updateVideo(video: VideoEntity) = withContext(Dispatchers.IO) {
        dao.updateVideo(video)
    }

    suspend fun deleteVideo(videoId: Int) = withContext(Dispatchers.IO) {
        dao.deleteVideo(videoId)
    }

    suspend fun deleteVideosByAuthor(userId: Int, displayName: String) = withContext(Dispatchers.IO) {
        dao.deleteVideosByAuthorSync(userId, displayName)
    }

    fun getVideoComments(videoId: Int): Flow<List<VideoCommentEntity>> {
        return dao.getCommentsForVideo(videoId)
    }

    suspend fun addVideoComment(comment: VideoCommentEntity) = withContext(Dispatchers.IO) {
        dao.insertVideoComment(comment)
    }

    // --- Reels ---
    suspend fun addReel(reel: ReelEntity): Long = withContext(Dispatchers.IO) {
        dao.insertReel(reel)
    }

    suspend fun updateReel(reel: ReelEntity) = withContext(Dispatchers.IO) {
        dao.updateReel(reel)
    }

    suspend fun deleteReel(reelId: Int) = withContext(Dispatchers.IO) {
        dao.deleteReel(reelId)
    }

    suspend fun deleteReelsByAuthor(userId: Int, displayName: String) = withContext(Dispatchers.IO) {
        dao.deleteReelsByAuthorSync(userId, displayName)
    }

    fun getReelComments(reelId: Int): Flow<List<ReelCommentEntity>> {
        return dao.getCommentsForReel(reelId)
    }

    suspend fun addReelComment(comment: ReelCommentEntity) = withContext(Dispatchers.IO) {
        dao.insertReelComment(comment)
    }

    // --- Onboarding / Seeding Database (Aura Premium Default State) ---
    suspend fun seedMockDataIfEmpty() = withContext(Dispatchers.IO) {
        // No mock data to seed - keeping search list clean and real-time
    }

    suspend fun nukeLocalDatabaseAndWipe() = withContext(Dispatchers.IO) {
        dao.clearUsers()
        dao.clearPosts()
        dao.clearComments()
        dao.clearStories()
        dao.clearMessages()
        dao.clearReactions()
        dao.clearVideos()
        dao.clearReels()
        dao.clearVideoComments()
        dao.clearReelComments()
    }
}
