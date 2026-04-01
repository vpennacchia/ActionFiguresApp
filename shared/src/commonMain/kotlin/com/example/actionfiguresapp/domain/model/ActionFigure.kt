package com.example.actionfiguresapp.domain.model

data class ActionFigure(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val price: Double? = null,
    val currency: String = "EUR",
    val condition: String? = null,
    val ebayItemId: String? = null,
    val ebayUrl: String? = null
)
