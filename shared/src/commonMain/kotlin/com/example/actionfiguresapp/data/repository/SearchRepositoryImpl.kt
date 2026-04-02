package com.example.actionfiguresapp.data.repository

import com.example.actionfiguresapp.data.remote.ebay.EbayApiService
import com.example.actionfiguresapp.domain.model.ActionFigure
import com.example.actionfiguresapp.domain.repository.SearchRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SearchRepositoryImpl(
    private val ebayApiService: EbayApiService
) : SearchRepository {

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun searchFigures(query: String): Result<List<ActionFigure>> {
        return runCatching {
            ebayApiService.searchItems(query).map { item ->
                ActionFigure(
                    id = Uuid.random().toString(),
                    name = item.title,
                    imageUrl = item.image?.imageUrl,
                    price = item.price?.value?.toDoubleOrNull(),
                    currency = item.price?.currency ?: "EUR",
                    condition = item.condition,
                    ebayItemId = item.itemId,
                    ebayUrl = item.itemWebUrl
                )
            }
        }
    }
}
