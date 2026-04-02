package com.example.actionfiguresapp.data.repository

import com.example.actionfiguresapp.domain.model.UserProfile
import com.example.actionfiguresapp.domain.repository.AuthRepository
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUser: UserProfile?
        get() = firebaseAuth.currentUser?.toUserProfile()

    override val authState: Flow<UserProfile?> =
        firebaseAuth.authStateChanged.map { it?.toUserProfile() }

    override suspend fun signIn(email: String, password: String): Result<UserProfile> {
        return runCatching {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password)
            result.user?.toUserProfile() ?: error("Login fallito")
        }
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<UserProfile> {
        return runCatching {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
            val user = result.user ?: error("Registrazione fallita")
            user.updateProfile(displayName = displayName)
            user.toUserProfile().copy(displayName = displayName)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return runCatching {
            firebaseAuth.sendPasswordResetEmail(email)
        }
    }

    private fun dev.gitlive.firebase.auth.FirebaseUser.toUserProfile() = UserProfile(
        uid = uid,
        email = email ?: "",
        displayName = displayName,
        photoUrl = photoURL
    )
}
