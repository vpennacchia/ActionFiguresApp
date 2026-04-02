package com.example.actionfiguresapp.data.repository

import com.example.actionfiguresapp.domain.model.ActionFigure
import com.example.actionfiguresapp.domain.repository.WishlistRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class WishlistRepositoryImpl(
    private val firestore: FirebaseFirestore
) : WishlistRepository {

    private fun wishlistRef(userId: String) =
        firestore.collection("users").document(userId).collection("wishlist")

    override fun getWishlist(userId: String): Flow<List<ActionFigure>> {
        return wishlistRef(userId).snapshots.map { snapshot ->
            snapshot.documents.map { doc ->
                ActionFigure(
                    id = doc.id,
                    name = doc.get("name"),
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

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun addToWishlist(userId: String, figure: ActionFigure): Result<Unit> {
        return runCatching {
            val id = Uuid.random().toString()
            wishlistRef(userId).document(id).set(
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

    override suspend fun removeFromWishlist(userId: String, figureId: String): Result<Unit> {
        return runCatching {
            wishlistRef(userId).document(figureId).delete()
        }
    }
}
