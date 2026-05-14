package com.example.actionfiguresapp.data.repository

import com.example.actionfiguresapp.domain.model.UserProfile
import com.example.actionfiguresapp.domain.repository.AuthRepository
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: UserProfile?
        get() = firebaseAuth.currentUser?.toUserProfile()

    override val authState: Flow<UserProfile?> =
        firebaseAuth.authStateChanged.map { it?.toUserProfile() }

    override suspend fun signIn(email: String, password: String): Result<UserProfile> {
        return runCatching {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password)
            val user = result.user ?: error("Login fallito")
            // Crea il profilo pubblico se non esiste (utenti registrati prima della feature social)
            runCatching {
                val userDoc = firestore.collection("users").document(user.uid).get()
                if (!userDoc.exists) {
                    val displayName = user.displayName ?: email.substringBefore("@")
                    val now = Clock.System.now().epochSeconds
                    firestore.collection("users").document(user.uid).set(
                        mapOf(
                            "displayName" to displayName,
                            "displayNameLower" to displayName.lowercase(),
                            "email" to email,
                            "bio" to "",
                            "photoUrl" to null,
                            "createdAt" to now,
                            "collectionsCount" to 0
                        )
                    )
                }
            }
            user.toUserProfile()
        }
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<UserProfile> {
        return runCatching {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
            val user = result.user ?: error("Registrazione fallita")
            user.updateProfile(displayName = displayName)
            val now = Clock.System.now().epochSeconds
            firestore.collection("users").document(user.uid).set(
                mapOf(
                    "displayName" to displayName,
                    "displayNameLower" to displayName.lowercase(),
                    "email" to email,
                    "bio" to "",
                    "photoUrl" to null,
                    "createdAt" to now,
                    "collectionsCount" to 0
                )
            )
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
