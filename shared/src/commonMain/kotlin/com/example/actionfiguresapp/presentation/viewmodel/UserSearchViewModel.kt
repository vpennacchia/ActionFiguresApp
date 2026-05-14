package com.example.actionfiguresapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.actionfiguresapp.domain.model.PublicUserProfile
import com.example.actionfiguresapp.domain.repository.SocialRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

data class UserSearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<PublicUserProfile> = emptyList(),
    val error: String? = null
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class UserSearchViewModel(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserSearchUiState())
    val uiState: StateFlow<UserSearchUiState> = _uiState.asStateFlow()

    private val _queryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _queryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) {
                        _uiState.value = _uiState.value.copy(results = emptyList(), isLoading = false)
                        return@collect
                    }
                    _uiState.value = _uiState.value.copy(isLoading = true)
                    socialRepository.searchUsers(query)
                        .onSuccess { results ->
                            _uiState.value = _uiState.value.copy(isLoading = false, results = results)
                        }
                        .onFailure {
                            _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
                        }
                }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        _queryFlow.value = query
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
