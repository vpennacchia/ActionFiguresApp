package com.example.actionfiguresapp.domain.repository

import com.example.actionfiguresapp.domain.model.ActionFigure
import com.example.actionfiguresapp.domain.model.Collection
import com.example.actionfiguresapp.domain.model.Post
import com.example.actionfiguresapp.domain.model.PublicUserProfile
import kotlinx.coroutines.flow.Flow

interface SocialRepository {
    fun getFeed(currentUserId: String): Flow<List<Post>>
    suspend fun createPost(
        authorId: String,
        authorName: String,
        authorPhotoUrl: String?,
        text: String,
        imageBytes: ByteArray?
    ): Result<Unit>
    suspend fun toggleLike(postId: String, userId: String): Result<Unit>
    suspend fun searchUsers(query: String): Result<List<PublicUserProfile>>
    fun getUserProfile(userId: String): Flow<PublicUserProfile?>
    fun getUserPublicCollections(userId: String): Flow<List<Collection>>
    fun getPublicCollectionFigures(userId: String, collectionId: String): Flow<List<ActionFigure>>
    suspend fun createOrUpdatePublicProfile(profile: PublicUserProfile): Result<Unit>
}
