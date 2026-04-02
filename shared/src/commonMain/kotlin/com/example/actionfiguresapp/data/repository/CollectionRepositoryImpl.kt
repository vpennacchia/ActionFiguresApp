package com.example.actionfiguresapp.data.repository

import com.example.actionfiguresapp.domain.model.ActionFigure
import com.example.actionfiguresapp.domain.model.Collection
import com.example.actionfiguresapp.domain.repository.CollectionRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CollectionRepositoryImpl(
    private val firestore: FirebaseFirestore
) : CollectionRepository {

    private fun collectionsRef(userId: String) =
        firestore.collection("users").document(userId).collection("collections")

    private fun figuresRef(userId: String, collectionId: String) =
        collectionsRef(userId).document(collectionId).collection("figures")

    override fun getCollections(userId: String): Flow<List<Collection>> {
        return collectionsRef(userId).snapshots.map { snapshot ->
            snapshot.documents.map { doc ->
                val figures = figuresRef(userId, doc.id).get().documents.map { figDoc ->
                    ActionFigure(
                        id = figDoc.id,
                        name = figDoc.get("name"),
                        imageUrl = figDoc.get("imageUrl"),
                        price = figDoc.get("price"),
                        currency = figDoc.get<String?>("currency") ?: "EUR",
                        condition = figDoc.get("condition"),
                        ebayItemId = figDoc.get("ebayItemId"),
                        ebayUrl = figDoc.get("ebayUrl")
                    )
                }
                Collection(
                    id = doc.id,
                    userId = userId,
                    name = doc.get("name"),
                    description = doc.get<String?>("description") ?: "",
                    figures = figures,
                    createdAt = doc.get<Long?>("createdAt") ?: 0L
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createCollection(collection: Collection): Result<Collection> {
        return runCatching {
            val id = Uuid.random().toString()
            val now = Clock.System.now().epochSeconds
            collectionsRef(collection.userId).document(id).set(
                mapOf(
                    "name" to collection.name,
                    "description" to collection.description,
                    "createdAt" to now
                )
            )
            collection.copy(id = id, createdAt = now)
        }
    }

    override suspend fun updateCollection(collection: Collection): Result<Unit> {
        return runCatching {
            collectionsRef(collection.userId).document(collection.id).update(
                mapOf(
                    "name" to collection.name,
                    "description" to collection.description
                )
            )
        }
    }

    override suspend fun deleteCollection(userId: String, collectionId: String): Result<Unit> {
        return runCatching {
            figuresRef(userId, collectionId).get().documents.forEach { it.reference.delete() }
            collectionsRef(userId).document(collectionId).delete()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun addFigure(userId: String, collectionId: String, figure: ActionFigure): Result<Unit> {
        return runCatching {
            val figureId = Uuid.random().toString()
            figuresRef(userId, collectionId).document(figureId).set(
                mapOf(
                    "name" to figure.name,
                    "imageUrl" to figure.imageUrl,
                    "price" to figure.price,
                    "currency" to figure.currency,
                    "condition" to figure.condition,
                    "ebayItemId" to figure.ebayItemId,
                    "ebayUrl" to figure.ebayUrl
                )
            )
        }
    }

    override suspend fun removeFigure(userId: String, collectionId: String, figureId: String): Result<Unit> {
        return runCatching {
            figuresRef(userId, collectionId).document(figureId).delete()
        }
    }
}
