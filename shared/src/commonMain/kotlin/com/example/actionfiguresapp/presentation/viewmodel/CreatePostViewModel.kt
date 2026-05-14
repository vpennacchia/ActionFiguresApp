package com.example.actionfiguresapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.actionfiguresapp.domain.repository.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreatePostUiState(
    val isLoading: Boolean = false,
    val text: String = "",
    val selectedImageBytes: ByteArray? = null,
    val postSuccess: Boolean = false,
    val error: String? = null
)

class CreatePostViewModel(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    fun onTextChange(text: String) {
        _uiState.value = _uiState.value.copy(text = text)
    }

    fun onImageSelected(bytes: ByteArray?) {
        _uiState.value = _uiState.value.copy(selectedImageBytes = bytes)
    }

    fun submitPost(authorId: String, authorName: String, authorPhotoUrl: String?) {
        val text = _uiState.value.text.trim()
        if (text.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            socialRepository.createPost(
                authorId = authorId,
                authorName = authorName,
                authorPhotoUrl = authorPhotoUrl,
                text = text,
                imageBytes = _uiState.value.selectedImageBytes
            ).onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, postSuccess = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
