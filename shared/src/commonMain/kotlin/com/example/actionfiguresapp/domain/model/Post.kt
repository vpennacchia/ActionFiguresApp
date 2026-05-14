package com.example.actionfiguresapp.domain.model

data class Post(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorPhotoUrl: String? = null,
    val text: String = "",
    val imageUrl: String? = null,
    val likeCount: Int = 0,
    val createdAt: Long = 0L,
    val isLikedByCurrentUser: Boolean = false
)
