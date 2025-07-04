package com.example.matricareog.repository


// Required imports for MedicalHistoryRepository
import com.example.matricareog.MedicalHistory
import com.example.matricareog.PersonalInformation
import com.example.matricareog.PregnancyHistory
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

// If you're using the newer Firebase BOM approach, use these imports instead:
 import com.google.firebase.firestore.FirebaseFirestore
 import com.google.firebase.firestore.ktx.firestore


class MedicalHistoryRepository {
    private val firestore = Firebase.firestore
    private val medicalHistoryCollection = firestore.collection("medical_history")


    // Save medical history to Firestore
    suspend fun saveMedicalHistory(
        userId: String,
        personalInfo: PersonalInformation,
        pregnancyHistory: PregnancyHistory
    ): Result<String> {
        return try {
            val medicalHistory = MedicalHistory(
                userId = userId,
                personalInformation = personalInfo,
                pregnancyHistory = pregnancyHistory
            )

            val documentRef = medicalHistoryCollection.document(userId)
            documentRef.set(medicalHistory).await()

            Result.success("Medical history saved successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get medical history from Firestore
    suspend fun getMedicalHistory(userId: String): Result<MedicalHistory?> {
        return try {
            val document = medicalHistoryCollection.document(userId).get().await()
            val medicalHistory = document.toObject<MedicalHistory>()
            Result.success(medicalHistory)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update medical history
    suspend fun updateMedicalHistory(
        userId: String,
        updates: Map<String, Any>
    ): Result<String> {
        return try {
            val updatedData = updates.toMutableMap()
            updatedData["updatedAt"] = System.currentTimeMillis()

            medicalHistoryCollection.document(userId)
                .update(updatedData)
                .await()

            Result.success("Medical history updated successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Add to your existing MedicalHistoryRepository.kt
    fun getMedicalHistoryUpdates(userId: String, callback: (MedicalHistory?) -> Unit) {
        firestore.collection("medical_history")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    callback(null)
                    return@addSnapshotListener
                }

                snapshot?.documents?.firstOrNull()?.let { document ->
                    callback(document.toObject(MedicalHistory::class.java))
                } ?: callback(null)
            }
    }
}