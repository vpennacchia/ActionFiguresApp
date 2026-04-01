package com.example.actionfiguresapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.actionfiguresapp.domain.model.ActionFigure
import com.example.actionfiguresapp.domain.repository.SearchRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<ActionFigure> = emptyList(),
    val error: String? = null
)

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _queryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _queryFlow
                .debounce(500)
                .distinctUntilChanged()
                .filter { it.length >= 2 }
                .collect { query -> performSearch(query) }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query, results = emptyList())
        _queryFlow.value = query
    }

    private suspend fun performSearch(query: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        searchRepository.searchFigures(query)
            .onSuccess { results ->
                _uiState.value = _uiState.value.copy(isLoading = false, results = results)
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
            }
    }

    fun clearSearch() {
        _uiState.value = SearchUiState()
        _queryFlow.value = ""
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
