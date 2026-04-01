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

    fun loadCollections(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            collectionRepository.getCollections(userId).collect { collections ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    collections = collections
                )
            }
        }
    }

    fun selectCollection(collection: Collection) {
        _uiState.value = _uiState.value.copy(selectedCollection = collection)
    }

    fun createCollection(userId: String, name: String, description: String) {
        viewModelScope.launch {
            val collection = Collection(
                userId = userId,
                name = name,
                description = description
            )
            collectionRepository.createCollection(collection)
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }

    fun deleteCollection(collectionId: String) {
        viewModelScope.launch {
            collectionRepository.deleteCollection(collectionId)
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }

    fun removeFigure(collectionId: String, figureId: String) {
        viewModelScope.launch {
            collectionRepository.removeFigure(collectionId, figureId)
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }

    fun addFigure(collectionId: String, figure: ActionFigure) {
        viewModelScope.launch {
            collectionRepository.addFigure(collectionId, figure)
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
