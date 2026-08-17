package com.roboticswala.hub.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.roboticswala.hub.data.models.UserProfile
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override val currentUserId: String?
        get() = auth.currentUser?.uid

    override val isUserLoggedIn: Boolean
        get() = auth.currentUser != null

    override suspend fun registerUser(
        fullName: String,
        email: String,
        password: String,
        phone: String,
        parentName: String,
        parentPhone: String
    ): Result<UserProfile> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = authResult.user ?: throw Exception("Failed to create Firebase user")

            val profile = UserProfile(
                uid = firebaseUser.uid,
                fullName = fullName.trim(),
                email = email.trim(),
                role = "Student",
                status = "Pending",
                phone = phone.trim(),
                parentName = parentName.trim(),
                parentPhone = parentPhone.trim(),
                createdAt = System.currentTimeMillis()
            )

            // Save user document in Cloud Firestore /users/{uid}
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(profile.toMap())
                .await()

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    override suspend fun loginUser(
        email: String,
        password: String
    ): Result<UserProfile> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = authResult.user ?: throw Exception("Failed to authenticate user")

            fetchUserProfile(firebaseUser.uid)
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    override suspend fun getCurrentUserProfile(): Result<UserProfile?> {
        val uid = currentUserId ?: return Result.success(null)
        return try {
            val profile = fetchUserProfile(uid).getOrNull()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchUserProfile(uid: String): Result<UserProfile> {
        return try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            if (snapshot.exists()) {
                val data = snapshot.data ?: emptyMap()
                val profile = UserProfile.fromMap(data)
                Result.success(profile)
            } else {
                // If user auth exists but document not yet created, create fallback
                val fallback = UserProfile(
                    uid = uid,
                    email = auth.currentUser?.email.orEmpty(),
                    fullName = auth.currentUser?.displayName ?: "Hub Member",
                    role = if (auth.currentUser?.email?.contains("admin", ignoreCase = true) == true) "Admin" else "Student",
                    status = "Pending"
                )
                firestore.collection("users").document(uid).set(fallback.toMap()).await()
                Result.success(fallback)
            }
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    override suspend fun checkUserStatus(uid: String): Result<String> {
        return try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .get()
                .await()
            val status = snapshot.getString("status") ?: "Pending"
            Result.success(status)
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    override fun logout() {
        try {
            auth.signOut()
        } catch (_: Exception) {}
    }

    private fun mapFirebaseException(e: Exception): Exception {
        val message = when (e) {
            is FirebaseAuthInvalidUserException -> "No account found with this email address."
            is FirebaseAuthInvalidCredentialsException -> "Invalid email or password. Please try again."
            is FirebaseAuthUserCollisionException -> "An account is already registered with this email address."
            is FirebaseAuthWeakPasswordException -> "Password is too weak. Please use at least 6 characters."
            is FirebaseNetworkException -> "Network error. Please check your internet connection."
            else -> e.localizedMessage ?: "Authentication failed. Please try again."
        }
        return Exception(message)
    }
}
