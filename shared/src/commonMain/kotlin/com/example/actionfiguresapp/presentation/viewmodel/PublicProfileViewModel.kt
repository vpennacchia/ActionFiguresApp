package com.example.actionfiguresapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.actionfiguresapp.domain.model.Collection
import com.example.actionfiguresapp.domain.model.PublicUserProfile
import com.example.actionfiguresapp.domain.repository.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PublicProfileUiState(
    val isLoading: Boolean = false,
    val profile: PublicUserProfile? = null,
    val collections: List<Collection> = emptyList(),
    val error: String? = null
)

class PublicProfileViewModel(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicProfileUiState())
    val uiState: StateFlow<PublicProfileUiState> = _uiState.asStateFlow()

    fun loadProfile(userId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            launch {
                socialRepository.getUserProfile(userId).collect { profile ->
                    _uiState.value = _uiState.value.copy(profile = profile)
                }
            }
            socialRepository.getUserPublicCollections(userId).collect { collections ->
                _uiState.value = _uiState.value.copy(isLoading = false, collections = collections)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
