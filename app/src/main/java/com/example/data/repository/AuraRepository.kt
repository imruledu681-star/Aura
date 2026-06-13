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

    suspend fun getCurrentUser(): UserEntity? = withContext(Dispatchers.IO) {
        dao.getCurrentUserSync()
    }

    suspend fun getUserByEmail(email: String): UserEntity? = withContext(Dispatchers.IO) {
        dao.getUserByEmailSync(email)
    }

    suspend fun deleteUserByEmail(email: String) = withContext(Dispatchers.IO) {
        dao.deleteUserByEmailSync(email)
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

    suspend fun addStory(story: StoryEntity) = withContext(Dispatchers.IO) {
        dao.insertStory(story)
    }

    fun getMessages(channelId: String): Flow<List<MessageEntity>> {
        return dao.getMessages(channelId)
    }

    suspend fun sendMessage(message: MessageEntity) = withContext(Dispatchers.IO) {
        dao.insertMessage(message)
    }

    // --- Onboarding / Seeding Database (Aura Premium Default State) ---
    suspend fun seedMockDataIfEmpty() = withContext(Dispatchers.IO) {
        // No pre-seeded default profiles are added. Everything starts is empty and operates on real-time data!
    }

    suspend fun nukeLocalDatabaseAndWipe() = withContext(Dispatchers.IO) {
        dao.clearUsers()
        dao.clearPosts()
        dao.clearComments()
        dao.clearStories()
        dao.clearMessages()
    }
}
