package com.example.actionfiguresapp.domain.model

data class ActionFigure(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val price: Double? = null,               // prezzo listing specifico scelto dall'utente
    val averageMarketPrice: Double? = null,  // media di mercato eBay al momento dell'aggiunta/refresh
    val previousAveragePrice: Double? = null, // media precedente (per calcolo trend)
    val lastPriceCheck: Long? = null,        // timestamp Unix ultimo aggiornamento prezzi
    val currency: String = "EUR",
    val condition: String? = null,
    val ebayItemId: String? = null,
    val ebayUrl: String? = null
)
