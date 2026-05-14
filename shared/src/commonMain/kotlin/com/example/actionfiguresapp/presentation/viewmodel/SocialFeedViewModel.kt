package com.example.actionfiguresapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.actionfiguresapp.domain.model.Post
import com.example.actionfiguresapp.domain.repository.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SocialFeedUiState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null
)

class SocialFeedViewModel(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SocialFeedUiState())
    val uiState: StateFlow<SocialFeedUiState> = _uiState.asStateFlow()

    fun loadFeed(currentUserId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            socialRepository.getFeed(currentUserId).collect { posts ->
                _uiState.value = _uiState.value.copy(isLoading = false, posts = posts)
            }
        }
    }

    fun toggleLike(postId: String, userId: String) {
        // Optimistic update: aggiorna subito la UI senza aspettare Firestore
        val updatedPosts = _uiState.value.posts.map { post ->
            if (post.id == postId) {
                val liked = !post.isLikedByCurrentUser
                post.copy(
                    isLikedByCurrentUser = liked,
                    likeCount = if (liked) post.likeCount + 1 else maxOf(0, post.likeCount - 1)
                )
            } else post
        }
        _uiState.value = _uiState.value.copy(posts = updatedPosts)

        viewModelScope.launch {
            socialRepository.toggleLike(postId, userId)
                .onFailure {
                    // Revert in caso di errore
                    val reverted = _uiState.value.posts.map { post ->
                        if (post.id == postId) {
                            val liked = !post.isLikedByCurrentUser
                            post.copy(
                                isLikedByCurrentUser = liked,
                                likeCount = if (liked) post.likeCount + 1 else maxOf(0, post.likeCount - 1)
                            )
                        } else post
                    }
                    _uiState.value = _uiState.value.copy(posts = reverted, error = it.message)
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
