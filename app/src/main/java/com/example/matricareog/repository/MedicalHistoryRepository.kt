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
    suspend fun saveMedicalHistoryWithServerTimestamp(
        userId: String,
        personalInfo: PersonalInformation,
        pregnancyHistory: PregnancyHistory
    ): Result<String> {
        return try {
            val medicalHistory = hashMapOf(
                "userId" to userId,
                "personalInformation" to personalInfo,
                "pregnancyHistory" to pregnancyHistory,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            // Use auto-generated document ID
            val documentRef = medicalHistoryCollection.document()
            documentRef.set(medicalHistory).await()

            Result.success("Medical history saved successfully with ID: ${documentRef.id}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get medical history by userId (since we're using auto-generated IDs)
    suspend fun getMedicalHistoryByUserId(userId: String): Result<List<MedicalHistory>> {
        return try {
            val querySnapshot = medicalHistoryCollection
                .whereEqualTo("userId", userId)
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

    // Get medical history by document ID
    suspend fun getMedicalHistoryById(documentId: String): Result<MedicalHistory?> {
        return try {
            val document = medicalHistoryCollection.document(documentId).get().await()
            val medicalHistory = document.toObject<MedicalHistory>()?.copy(documentId = document.id)
            Result.success(medicalHistory)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update medical history by document ID
    suspend fun updateMedicalHistory(
        documentId: String,
        updates: Map<String, Any>
    ): Result<String> {
        return try {
            val updatedData = updates.toMutableMap()
            updatedData["updatedAt"] = System.currentTimeMillis()

            medicalHistoryCollection.document(documentId)
                .update(updatedData)
                .await()

            Result.success("Medical history updated successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update with server timestamp
    suspend fun updateMedicalHistoryWithServerTimestamp(
        documentId: String,
        updates: Map<String, Any>
    ): Result<String> {
        return try {
            val updatedData = updates.toMutableMap()
            updatedData["updatedAt"] = FieldValue.serverTimestamp()

            medicalHistoryCollection.document(documentId)
                .update(updatedData)
                .await()

            Result.success("Medical history updated successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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
    fun getMedicalHistoryUpdatesByDocumentId(documentId: String, callback: (MedicalHistory?) -> Unit) {
        medicalHistoryCollection.document(documentId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    callback(null)
                    return@addSnapshotListener
                }

                val medicalHistory = snapshot?.toObject<MedicalHistory>()?.copy(documentId = snapshot.id)
                callback(medicalHistory)
            }
    }

    // Delete medical history by document ID
    suspend fun deleteMedicalHistory(documentId: String): Result<String> {
        return try {
            medicalHistoryCollection.document(documentId).delete().await()
            Result.success("Medical history deleted successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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