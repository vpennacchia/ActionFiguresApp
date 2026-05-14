package com.example.actionfiguresapp.domain.model

data class PublicUserProfile(
    val uid: String = "",
    val displayName: String = "",
    val displayNameLower: String = "",
    val email: String = "",
    val bio: String = "",
    val photoUrl: String? = null,
    val createdAt: Long = 0L,
    val collectionsCount: Int = 0
)
