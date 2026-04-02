package com.example.actionfiguresapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.actionfiguresapp.domain.model.ActionFigure
import com.example.actionfiguresapp.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WishlistUiState(
    val isLoading: Boolean = false,
    val items: List<ActionFigure> = emptyList(),
    val error: String? = null
)

class WishlistViewModel(
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WishlistUiState())
    val uiState: StateFlow<WishlistUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    fun loadWishlist(userId: String) {
        currentUserId = userId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            wishlistRepository.getWishlist(userId).collect { items ->
                _uiState.value = _uiState.value.copy(isLoading = false, items = items)
            }
        }
    }

    fun addToWishlist(figure: ActionFigure) {
        // Optimistic update
        _uiState.value = _uiState.value.copy(items = _uiState.value.items + figure)
        viewModelScope.launch {
            wishlistRepository.addToWishlist(currentUserId, figure)
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        items = _uiState.value.items - figure,
                        error = it.message
                    )
                }
        }
    }

    fun removeFromWishlist(figureId: String) {
        // Optimistic update
        val previous = _uiState.value.items
        _uiState.value = _uiState.value.copy(items = previous.filter { it.id != figureId })
        viewModelScope.launch {
            wishlistRepository.removeFromWishlist(currentUserId, figureId)
                .onFailure { _uiState.value = _uiState.value.copy(items = previous, error = it.message) }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
