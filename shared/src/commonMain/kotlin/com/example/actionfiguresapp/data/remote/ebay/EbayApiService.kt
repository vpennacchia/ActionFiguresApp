package com.example.actionfiguresapp.data.remote.ebay

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.Parameters
import kotlinx.datetime.Clock
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private const val BASE_URL = "https://api.ebay.com"
private const val TOKEN_URL = "$BASE_URL/identity/v1/oauth2/token"
private const val SEARCH_URL = "$BASE_URL/buy/browse/v1/item_summary/search"
private const val MARKETPLACE_ID = "EBAY_IT"
private const val SEARCH_LIMIT = 20

class EbayApiService(
    private val httpClient: HttpClient,
    private val clientId: String,
    private val clientSecret: String
) {
    private var cachedToken: String? = null
    private var tokenExpiresAt: Long = 0L

    @OptIn(ExperimentalEncodingApi::class)
    private suspend fun getToken(): String {
        val now = Clock.System.now().epochSeconds
        if (cachedToken != null && now < tokenExpiresAt) {
            return cachedToken!!
        }

        val credentials = Base64.encode("$clientId:$clientSecret".encodeToByteArray())
        val response: EbayTokenResponse = httpClient.submitForm(
            url = TOKEN_URL,
            formParameters = Parameters.build {
                append("grant_type", "client_credentials")
                append("scope", "https://api.ebay.com/oauth/api_scope")
            }
        ) {
            header("Authorization", "Basic $credentials")
        }.body()

        cachedToken = response.accessToken
        tokenExpiresAt = now + response.expiresIn - 60 // rinnova 60s prima della scadenza
        return response.accessToken
    }

    suspend fun searchItems(query: String): List<EbayItemSummary> {
        val token = getToken()
        val response: EbaySearchResponse = httpClient.get(SEARCH_URL) {
            header("Authorization", "Bearer $token")
            header("X-EBAY-C-MARKETPLACE-ID", MARKETPLACE_ID)
            parameter("q", query)
            parameter("limit", SEARCH_LIMIT)
            parameter("category_ids", "246")
        }.body()
        return response.itemSummaries
    }
}
