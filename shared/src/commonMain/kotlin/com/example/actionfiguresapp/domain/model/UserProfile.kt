package com.example.actionfiguresapp.domain.model

data class UserProfile(
    val uid: String,
    val email: String,
    val displayName: String? = null,
    val photoUrl: String? = null
)
