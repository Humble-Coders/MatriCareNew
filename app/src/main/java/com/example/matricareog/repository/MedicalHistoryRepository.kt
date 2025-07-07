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
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import java.util.UUID

class MedicalHistoryRepository(
    private val firestore: FirebaseFirestore
) {
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
//    fun getMedicalHistoryUpdates(userId: String, callback: (List<MedicalHistory>) -> Unit) {
//        medicalHistoryCollection
//            .whereEqualTo("userId", userId)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    callback(emptyList())
//                    return@addSnapshotListener
//                }
//
//                val medicalHistoryList = snapshot?.documents?.mapNotNull { document ->
//                    document.toObject<MedicalHistory>()?.copy(documentId = document.id)
//                } ?: emptyList()
//
//                callback(medicalHistoryList)
//            }
//    }

    // Get real-time updates for a specific document



    // Get all medical history records for a user, ordered by creation date
//    suspend fun getMedicalHistoryOrderedByDate(userId: String): Result<List<MedicalHistory>> {
//        return try {
//            val querySnapshot = medicalHistoryCollection
//                .whereEqualTo("userId", userId)
//                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
//                .get()
//                .await()
//
//            val medicalHistoryList = querySnapshot.documents.mapNotNull { document ->
//                document.toObject<MedicalHistory>()?.copy(documentId = document.id)
//            }
//
//            Result.success(medicalHistoryList)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
    // Add these methods to your MedicalHistoryRepository class

    /**
     * Save complete data including ML predictions to Firebase
     */
    /**
     * Save complete data with ML predictions to Firebase
     * Now saves directly to medical_history collection with auto-generated ID
     */
    suspend fun saveCompleteDataWithMLPredictions(
        userId: String,
        personalInfo: PersonalInformation,
        pregnancyHistory: PregnancyHistory,
        mlPrediction: ReportRepository.RiskPrediction?
    ): Result<String> {
        return try {
            val timestamp = System.currentTimeMillis()

            // Create MedicalHistory object with ML prediction data
            val medicalHistory = MedicalHistory(
                id = "", // Will be set by Firestore auto-generated ID
                userId = userId,
                date = timestamp,
                personalInformation = personalInfo,
                pregnancyHistory = pregnancyHistory,
                timestamp = timestamp,
                // ML prediction fields (add these to your MedicalHistory data class)
                mlRiskLevel = mlPrediction?.riskLevel,
                mlPredictionTimestamp = if (mlPrediction != null) System.currentTimeMillis() else null
            )

            // Save to Firebase using add() for auto-generated ID
            val documentRef = firestore.collection("medical_history")
                .add(medicalHistory)
                .await()

            // Get the auto-generated document ID
            val generatedId = documentRef.id

            Result.success("Complete data with ML predictions saved successfully. Document ID: $generatedId")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get medical history list for a specific user, ordered by date (most recent first)
     */
    suspend fun getMedicalHistoryList(userId: String): Result<List<MedicalHistory>> {
        return try {
            val snapshot = firestore.collection("medical_history")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val medicalHistoryList = snapshot.documents.mapNotNull { document ->
                document.toObject(MedicalHistory::class.java)?.copy(id = document.id)
            }

            Result.success(medicalHistoryList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get specific medical history record by auto-generated ID
     */
    suspend fun getMedicalHistoryById(recordId: String): Result<MedicalHistory> {
        return try {
            val document = firestore.collection("medical_history")
                .document(recordId)
                .get()
                .await()

            if (document.exists()) {
                val medicalHistory = document.toObject(MedicalHistory::class.java)
                    ?.copy(id = document.id)

                if (medicalHistory != null) {
                    Result.success(medicalHistory)
                } else {
                    Result.failure(Exception("Failed to parse medical history data"))
                }
            } else {
                Result.failure(Exception("Medical history record not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}