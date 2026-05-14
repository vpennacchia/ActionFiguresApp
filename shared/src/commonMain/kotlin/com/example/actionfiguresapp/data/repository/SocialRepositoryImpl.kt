package com.example.actionfiguresapp.data.repository

import com.example.actionfiguresapp.domain.model.ActionFigure
import com.example.actionfiguresapp.domain.model.Collection
import com.example.actionfiguresapp.domain.model.Post
import com.example.actionfiguresapp.domain.model.PublicUserProfile
import com.example.actionfiguresapp.domain.repository.SocialRepository
import com.example.actionfiguresapp.domain.repository.StorageUploader
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SocialRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val storageUploader: StorageUploader
) : SocialRepository {

    override fun getFeed(currentUserId: String): Flow<List<Post>> {
        return firestore.collection("posts")
            .orderBy("createdAt", Direction.DESCENDING)
            .limit(50)
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    val isLiked = try {
                        firestore.collection("posts").document(doc.id)
                            .collection("likes").document(currentUserId).get().exists
                    } catch (e: Exception) { false }
                    Post(
                        id = doc.id,
                        authorId = doc.get<String?>("authorId") ?: "",
                        authorName = doc.get<String?>("authorName") ?: "",
                        authorPhotoUrl = doc.get("authorPhotoUrl"),
                        text = doc.get<String?>("text") ?: "",
                        imageUrl = doc.get("imageUrl"),
                        likeCount = doc.get<Int?>("likeCount") ?: 0,
                        createdAt = doc.get<Long?>("createdAt") ?: 0L,
                        isLikedByCurrentUser = isLiked
                    )
                }
            }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createPost(
        authorId: String,
        authorName: String,
        authorPhotoUrl: String?,
        text: String,
        imageBytes: ByteArray?
    ): Result<Unit> {
        return runCatching {
            var imageUrl: String? = null
            if (imageBytes != null) {
                val imagePath = "posts/$authorId/${Uuid.random()}.jpg"
                imageUrl = storageUploader.uploadImageBytes(imagePath, imageBytes)
            }
            val postId = Uuid.random().toString()
            val now = Clock.System.now().epochSeconds
            firestore.collection("posts").document(postId).set(
                mapOf(
                    "authorId" to authorId,
                    "authorName" to authorName,
                    "authorPhotoUrl" to authorPhotoUrl,
                    "text" to text,
                    "imageUrl" to imageUrl,
                    "likeCount" to 0,
                    "createdAt" to now
                )
            )
        }
    }

    override suspend fun toggleLike(postId: String, userId: String): Result<Unit> {
        return runCatching {
            val likeRef = firestore.collection("posts").document(postId)
                .collection("likes").document(userId)
            val likeDoc = likeRef.get()
            val postRef = firestore.collection("posts").document(postId)
            val postDoc = postRef.get()
            val currentCount = postDoc.get<Int?>("likeCount") ?: 0
            if (likeDoc.exists) {
                likeRef.delete()
                postRef.update(mapOf("likeCount" to maxOf(0, currentCount - 1)))
            } else {
                likeRef.set(mapOf("likedAt" to Clock.System.now().epochSeconds))
                postRef.update(mapOf("likeCount" to currentCount + 1))
            }
        }
    }

    override suspend fun searchUsers(query: String): Result<List<PublicUserProfile>> {
        return runCatching {
            if (query.isBlank()) return@runCatching emptyList()
            val lowerQuery = query.lowercase()
            val snapshot = firestore.collection("users")
                .orderBy("displayNameLower")
                .startAt(lowerQuery)
                .endAt("$lowerQuery\uf8ff")
                .limit(20)
                .get()
            snapshot.documents.map { doc -> doc.toPublicUserProfile() }
        }
    }

    override fun getUserProfile(userId: String): Flow<PublicUserProfile?> {
        return firestore.collection("users").document(userId).snapshots.map { doc ->
            if (!doc.exists) null else doc.toPublicUserProfile()
        }
    }

    override fun getUserPublicCollections(userId: String): Flow<List<Collection>> {
        return firestore.collection("users").document(userId).collection("collections").snapshots.map { snapshot ->
            snapshot.documents.map { doc ->
                val figureCount = try {
                    firestore.collection("users").document(userId)
                        .collection("collections").document(doc.id)
                        .collection("figures").get().documents.size
                } catch (e: Exception) { 0 }
                Collection(
                    id = doc.id,
                    userId = userId,
                    name = doc.get<String?>("name") ?: "",
                    description = doc.get<String?>("description") ?: "",
                    figures = List(figureCount) { ActionFigure(id = "", name = "") },
                    createdAt = doc.get<Long?>("createdAt") ?: 0L
                )
            }
        }
    }

    override fun getPublicCollectionFigures(userId: String, collectionId: String): Flow<List<ActionFigure>> {
        return firestore.collection("users").document(userId)
            .collection("collections").document(collectionId)
            .collection("figures").snapshots.map { snapshot ->
                snapshot.documents.map { doc ->
                    ActionFigure(
                        id = doc.id,
                        name = doc.get<String?>("name") ?: "",
                        imageUrl = doc.get("imageUrl"),
                        price = doc.get("price"),
                        currency = doc.get<String?>("currency") ?: "EUR",
                        condition = doc.get("condition"),
                        ebayItemId = doc.get("ebayItemId"),
                        ebayUrl = doc.get("ebayUrl")
                    )
                }
            }
    }

    override suspend fun createOrUpdatePublicProfile(profile: PublicUserProfile): Result<Unit> {
        return runCatching {
            firestore.collection("users").document(profile.uid).set(
                mapOf(
                    "displayName" to profile.displayName,
                    "displayNameLower" to profile.displayNameLower,
                    "email" to profile.email,
                    "bio" to profile.bio,
                    "photoUrl" to profile.photoUrl,
                    "createdAt" to profile.createdAt,
                    "collectionsCount" to profile.collectionsCount
                )
            )
        }
    }

    private suspend fun dev.gitlive.firebase.firestore.DocumentSnapshot.toPublicUserProfile() = PublicUserProfile(
        uid = id,
        displayName = get<String?>("displayName") ?: "",
        displayNameLower = get<String?>("displayNameLower") ?: "",
        email = get<String?>("email") ?: "",
        bio = get<String?>("bio") ?: "",
        photoUrl = get("photoUrl"),
        createdAt = get<Long?>("createdAt") ?: 0L,
        collectionsCount = firestore.collection("users").document(id).collection("collections").get().documents.size
    )
}
