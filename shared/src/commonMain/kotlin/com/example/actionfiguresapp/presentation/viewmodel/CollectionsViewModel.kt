package com.example.actionfiguresapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.actionfiguresapp.domain.model.ActionFigure
import com.example.actionfiguresapp.domain.model.Collection
import com.example.actionfiguresapp.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CollectionsUiState(
    val isLoading: Boolean = false,
    val collections: List<Collection> = emptyList(),
    val selectedCollection: Collection? = null,
    val error: String? = null
)

class CollectionsViewModel(
    private val collectionRepository: CollectionRepository
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
