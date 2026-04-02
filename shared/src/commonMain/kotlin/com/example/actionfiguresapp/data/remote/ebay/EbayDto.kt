package com.example.actionfiguresapp.data.remote.ebay

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EbayTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int
)

@Serializable
data class EbaySearchResponse(
    @SerialName("itemSummaries") val itemSummaries: List<EbayItemSummary> = emptyList()
)

@Serializable
data class EbayItemSummary(
    @SerialName("itemId") val itemId: String,
    @SerialName("title") val title: String,
    @SerialName("price") val price: EbayPrice? = null,
    @SerialName("image") val image: EbayImage? = null,
    @SerialName("condition") val condition: String? = null,
    @SerialName("itemWebUrl") val itemWebUrl: String? = null
)

@Serializable
data class EbayPrice(
    @SerialName("value") val value: String,
    @SerialName("currency") val currency: String
)

@Serializable
data class EbayImage(
    @SerialName("imageUrl") val imageUrl: String
)
