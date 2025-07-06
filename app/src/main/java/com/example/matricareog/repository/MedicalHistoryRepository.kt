package com.example.matricareog.repository

// Required imports for MedicalHistoryRepository
import com.example.matricareog.MedicalHistory
import com.example.matricareog.PersonalInformation
import com.example.matricareog.PregnancyHistory
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

// If you're using the newer Firebase BOM approach, use these imports instead:
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore

class MedicalHistoryRepository {
    private val firestore = Firebase.firestore
    private val medicalHistoryCollection = firestore.collection("medical_history")

    // Save medical history to Firestore with auto-generated ID
    suspend fun saveMedicalHistory(
        userId: String,
        personalInfo: PersonalInformation,
        pregnancyHistory: PregnancyHistory
    ): Result<String> {
        return try {
            val currentTimestamp = System.currentTimeMillis()

            val medicalHistory = MedicalHistory(
                userId = userId,
                personalInformation = personalInfo,
                pregnancyHistory = pregnancyHistory,
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            )

            // Use auto-generated document ID
            val documentRef = medicalHistoryCollection.document()
            documentRef.set(medicalHistory).await()

            Result.success("Medical history saved successfully with ID: ${documentRef.id}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Alternative method to save with server timestamp (recommended for better consistency)

    // Get medical history by userId (since we're using auto-generated IDs)


    // Get medical history by document ID

    // Update medical history by document ID


    // Update with server timestamp


    // Get real-time updates for medical history by userId
    fun getMedicalHistoryUpdates(userId: String, callback: (List<MedicalHistory>) -> Unit) {
        medicalHistoryCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    callback(emptyList())
                    return@addSnapshotListener
                }

                val medicalHistoryList = snapshot?.documents?.mapNotNull { document ->
                    document.toObject<MedicalHistory>()?.copy(documentId = document.id)
                } ?: emptyList()

                callback(medicalHistoryList)
            }
    }

    // Get real-time updates for a specific document



    // Get all medical history records for a user, ordered by creation date
    suspend fun getMedicalHistoryOrderedByDate(userId: String): Result<List<MedicalHistory>> {
        return try {
            val querySnapshot = medicalHistoryCollection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val medicalHistoryList = querySnapshot.documents.mapNotNull { document ->
                document.toObject<MedicalHistory>()?.copy(documentId = document.id)
            }

            Result.success(medicalHistoryList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}