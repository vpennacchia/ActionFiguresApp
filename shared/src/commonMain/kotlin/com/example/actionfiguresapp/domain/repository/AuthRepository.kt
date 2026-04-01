package com.example.actionfiguresapp.domain.repository

import com.example.actionfiguresapp.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: UserProfile?
    val authState: Flow<UserProfile?>

    suspend fun signIn(email: String, password: String): Result<UserProfile>
    suspend fun signUp(email: String, password: String, displayName: String): Result<UserProfile>
    suspend fun signOut()
    suspend fun resetPassword(email: String): Result<Unit>
}
