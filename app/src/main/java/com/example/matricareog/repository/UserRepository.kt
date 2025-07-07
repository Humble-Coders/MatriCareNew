package com.example.matricareog.repository

import android.util.Log
import com.example.matricareog.AuthResult
import com.example.matricareog.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun signUp(
        email: String,
        password: String,
        fullName: String
    ): AuthResult {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val user = User(
                    fullName = fullName,
                    email = email,
                    uid = firebaseUser.uid
                )

                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(user)
                    .await()

                Log.d("UserRepository", "✅ User created successfully: $user")
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Failed to create user account")
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Sign up error: ${e.message}", e)
            AuthResult.Error(getErrorMessage(e))
        }
    }

    suspend fun login(email: String, password: String): AuthResult {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val userDoc = firestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .await()

                val user = userDoc.toObject(User::class.java)
                if (user != null) {
                    Log.d("UserRepository", "✅ Login successful: $user")
                    AuthResult.Success(user)
                } else {
                    val newUser = User(
                        fullName = "",
                        email = email,
                        uid = firebaseUser.uid
                    )
                    firestore.collection("users")
                        .document(firebaseUser.uid)
                        .set(newUser)
                        .await()

                    AuthResult.Success(newUser)
                }
            } else {
                AuthResult.Error("Login failed")
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Login error: ${e.message}", e)
            AuthResult.Error(getErrorMessage(e))
        }
    }



    private fun getErrorMessage(exception: Exception): String {
        return when {
            exception.message?.contains("network error", true) == true ->
                "Network error. Please check your connection."
            exception.message?.contains("email", true) == true ->
                "Invalid email address format."
            exception.message?.contains("password", true) == true ->
                "Password should be at least 6 characters."
            exception.message?.contains("user-not-found", true) == true ->
                "No account found with this email."
            exception.message?.contains("wrong-password", true) == true ->
                "Incorrect password."
            exception.message?.contains("email-already-in-use", true) == true ->
                "An account with this email already exists."
            else -> exception.message ?: "An unexpected error occurred."
        }
    }
}
