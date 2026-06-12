package com.example.bomapay.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.bomapay.data.model.UserProfile
import kotlinx.coroutines.tasks.await
import kotlin.Result

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun registerUser(email: String, password: String, isLandlord: Boolean = false): Result<UserProfile> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                // Fixed: Matches all properties defined in UserProfile.kt
                val profile = UserProfile(
                    uid = firebaseUser.uid,
                    email = email,
                    role = if (isLandlord) "landlord" else "tenant",
                    houseNumber = "Unassigned",
                    rentBalance = 0L,
                    createdAt = System.currentTimeMillis()
                )

                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(profile.toMap())
                    .await()

                Result.success(profile)
            } else {
                Result.failure(Exception("Registration failed to create profile."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success("Success")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserRole(uid: String): String {
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            snapshot.getString("role") ?: "tenant"
        } catch (e: Exception) {
            "tenant"
        }
    }

    fun getCurrentUid(): String? = auth.currentUser?.uid
}