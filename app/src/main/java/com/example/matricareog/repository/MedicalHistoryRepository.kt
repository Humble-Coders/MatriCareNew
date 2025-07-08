package com.example.matricareog.repository

import com.example.matricareog.model.MedicalHistory
import com.example.matricareog.model.PersonalInformation
import com.example.matricareog.model.PregnancyHistory
import com.google.firebase.firestore.FirebaseFirestore
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
                userId = userId,
                date = timestamp,
                personalInformation = personalInfo,
                pregnancyHistory = pregnancyHistory,
                mlRiskLevel = mlPrediction?.riskLevel,
                mlPredictionTimestamp = if (mlPrediction != null) timestamp else null
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
}