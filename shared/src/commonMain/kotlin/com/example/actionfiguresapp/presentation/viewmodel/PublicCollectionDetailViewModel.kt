package com.example.actionfiguresapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.actionfiguresapp.domain.model.ActionFigure
import com.example.actionfiguresapp.domain.repository.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PublicCollectionDetailUiState(
    val isLoading: Boolean = true,
    val collectionName: String = "",
    val figures: List<ActionFigure> = emptyList(),
    val error: String? = null
)

class PublicCollectionDetailViewModel(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicCollectionDetailUiState())
    val uiState: StateFlow<PublicCollectionDetailUiState> = _uiState.asStateFlow()

    fun load(userId: String, collectionId: String, collectionName: String) {
        _uiState.value = PublicCollectionDetailUiState(isLoading = true, collectionName = collectionName)
        viewModelScope.launch {
            try {
                socialRepository.getPublicCollectionFigures(userId, collectionId).collect { figures ->
                    _uiState.value = _uiState.value.copy(isLoading = false, figures = figures)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
