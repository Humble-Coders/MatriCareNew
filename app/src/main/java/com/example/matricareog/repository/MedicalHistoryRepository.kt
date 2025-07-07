package com.example.matricareog.repository

// Required imports for MedicalHistoryRepository

// If you're using the newer Firebase BOM approach, use these imports instead:
import com.example.matricareog.MedicalHistory
import com.example.matricareog.PersonalInformation
import com.example.matricareog.PregnancyHistory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class MedicalHistoryRepository(
    private val firestore: FirebaseFirestore
) {



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