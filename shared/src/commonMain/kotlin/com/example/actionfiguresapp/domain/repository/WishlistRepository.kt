package com.example.actionfiguresapp.domain.repository

import com.example.actionfiguresapp.domain.model.ActionFigure
import kotlinx.coroutines.flow.Flow

interface WishlistRepository {
    fun getWishlist(userId: String): Flow<List<ActionFigure>>
    suspend fun addToWishlist(userId: String, figure: ActionFigure): Result<Unit>
    suspend fun removeFromWishlist(userId: String, figureId: String): Result<Unit>
}
