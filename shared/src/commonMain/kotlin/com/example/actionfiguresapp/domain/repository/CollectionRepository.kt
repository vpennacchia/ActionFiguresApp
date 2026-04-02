package com.example.actionfiguresapp.domain.repository

import com.example.actionfiguresapp.domain.model.ActionFigure
import com.example.actionfiguresapp.domain.model.Collection
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {
    fun getCollections(userId: String): Flow<List<Collection>>

    suspend fun createCollection(collection: Collection): Result<Collection>
    suspend fun updateCollection(collection: Collection): Result<Unit>
    suspend fun deleteCollection(userId: String, collectionId: String): Result<Unit>

    suspend fun addFigure(userId: String, collectionId: String, figure: ActionFigure): Result<Unit>
    suspend fun removeFigure(userId: String, collectionId: String, figureId: String): Result<Unit>
}
