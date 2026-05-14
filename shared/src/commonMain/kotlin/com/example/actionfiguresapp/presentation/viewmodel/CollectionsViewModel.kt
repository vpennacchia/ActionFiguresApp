package com.example.actionfiguresapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.actionfiguresapp.domain.model.ActionFigure
import com.example.actionfiguresapp.domain.model.Collection
import com.example.actionfiguresapp.domain.repository.CollectionRepository
import com.example.actionfiguresapp.domain.repository.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

private const val PRICE_CHANGE_THRESHOLD_PCT = 5.0
private const val PRICE_CHECK_COOLDOWN_SECONDS = 86400L // 24 ore

data class PriceAlert(
    val figureId: String,
    val figureName: String,
    val oldPrice: Double,
    val newPrice: Double
) {
    val isUp: Boolean = newPrice > oldPrice
    val changePercent: Double = ((newPrice - oldPrice) / oldPrice) * 100
}

data class CollectionsUiState(
    val isLoading: Boolean = false,
    val collections: List<Collection> = emptyList(),
    val selectedCollection: Collection? = null,
    val error: String? = null,
    val priceAlerts: List<PriceAlert> = emptyList(),
    val isRefreshingPrices: Boolean = false
)

class CollectionsViewModel(
    private val collectionRepository: CollectionRepository,
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionsUiState())
    val uiState: StateFlow<CollectionsUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    fun loadCollections(userId: String) {
        currentUserId = userId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            collectionRepository.getCollections(userId).collect { collections ->
                _uiState.value = _uiState.value.copy(isLoading = false, collections = collections)
            }
        }
    }

    fun selectCollection(collection: Collection) {
        _uiState.value = _uiState.value.copy(selectedCollection = collection)
    }

    fun createCollection(userId: String, name: String, description: String) {
        viewModelScope.launch {
            val collection = Collection(userId = userId, name = name, description = description)
            collectionRepository.createCollection(collection)
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }

    fun deleteCollection(collectionId: String) {
        viewModelScope.launch {
            collectionRepository.deleteCollection(currentUserId, collectionId)
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }

    fun addFigure(collectionId: String, figure: ActionFigure) {
        // Optimistic update: aggiorna subito la UI senza aspettare Firestore
        val updated = _uiState.value.collections.map { col ->
            if (col.id == collectionId) col.copy(figures = col.figures + figure) else col
        }
        _uiState.value = _uiState.value.copy(collections = updated)

        viewModelScope.launch {
            collectionRepository.addFigure(currentUserId, collectionId, figure)
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }

    fun removeFigure(collectionId: String, figureId: String) {
        // Optimistic update
        val updated = _uiState.value.collections.map { col ->
            if (col.id == collectionId) col.copy(figures = col.figures.filter { it.id != figureId }) else col
        }
        _uiState.value = _uiState.value.copy(collections = updated)

        viewModelScope.launch {
            collectionRepository.removeFigure(currentUserId, collectionId, figureId)
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }

    /**
     * Aggiorna i prezzi di mercato delle figure nella collezione ricercando su eBay.
     * Viene eseguito solo per figure non aggiornate nelle ultime 24 ore.
     * Se un prezzo cambia di più del 5%, genera un PriceAlert.
     */
    fun refreshCollectionPrices(collectionId: String) {
        val collection = _uiState.value.collections.find { it.id == collectionId } ?: return
        val now = Clock.System.now().epochSeconds
        val figuresToCheck = collection.figures.filter { figure ->
            figure.lastPriceCheck == null || now - figure.lastPriceCheck > PRICE_CHECK_COOLDOWN_SECONDS
        }
        if (figuresToCheck.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshingPrices = true)
            val alerts = mutableListOf<PriceAlert>()

            for (figure in figuresToCheck) {
                // Usa le prime 4 parole del titolo per una ricerca più generica
                val searchQuery = figure.name.split(" ").take(4).joinToString(" ")
                searchRepository.searchFigures(searchQuery)
                    .onSuccess { results ->
                        val newAvg = results.firstOrNull()?.averageMarketPrice ?: return@onSuccess
                        val oldAvg = figure.averageMarketPrice
                        if (oldAvg != null) {
                            val changePct = ((newAvg - oldAvg) / oldAvg) * 100
                            if (kotlin.math.abs(changePct) >= PRICE_CHANGE_THRESHOLD_PCT) {
                                alerts.add(PriceAlert(figure.id, figure.name, oldAvg, newAvg))
                            }
                        }
                        collectionRepository.updateFigurePrice(
                            userId = currentUserId,
                            collectionId = collectionId,
                            figureId = figure.id,
                            newAveragePrice = newAvg,
                            previousAveragePrice = oldAvg ?: newAvg,
                            checkedAt = now
                        )
                    }
            }

            _uiState.value = _uiState.value.copy(isRefreshingPrices = false, priceAlerts = alerts)
        }
    }

    fun dismissPriceAlerts() {
        _uiState.value = _uiState.value.copy(priceAlerts = emptyList())
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}