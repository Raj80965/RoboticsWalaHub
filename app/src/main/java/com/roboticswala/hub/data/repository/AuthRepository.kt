package com.roboticswala.hub.data.repository

import com.roboticswala.hub.data.models.UserProfile

interface AuthRepository {
    val currentUserId: String?
    val isUserLoggedIn: Boolean

    suspend fun registerUser(fullName: String, email: String, password: String): Result<UserProfile>
    suspend fun loginUser(email: String, password: String): Result<UserProfile>
    suspend fun getCurrentUserProfile(): Result<UserProfile?>
    suspend fun fetchUserProfile(uid: String): Result<UserProfile>
    suspend fun checkUserStatus(uid: String): Result<String>
    fun logout()
}
