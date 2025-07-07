package com.example.matricareog.repository

import android.util.Log
import com.example.matricareog.ChartData
import com.example.matricareog.HealthDataPoint
import com.example.matricareog.MedicalHistory
import com.example.matricareog.PersonalInformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class MatriCareRepository(
        private val auth: FirebaseAuth,
        private val firestore: FirebaseFirestore
         ) {
    private val TAG = "MatriCareRepository"
    suspend fun getChartData(): Flow<Result<ChartData>> = flow {
        try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                emit(Result.failure(Exception("User not authenticated")))
                return@flow
            }

            val medicalHistorySnapshot = firestore
                .collection("medical_history")
                .whereEqualTo("userId", currentUser.uid)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .await()

            val healthDataPoints = mutableListOf<HealthDataPoint>()
            val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

            medicalHistorySnapshot.documents.forEach { document ->
                val medicalHistory = document.toObject(MedicalHistory::class.java)
                medicalHistory?.let { history ->
                    val date = dateFormat.format(Date(history.createdAt))
                    val hemoglobin = history.personalInformation.hemoglobinLevel
                    val hba1c = calculateHba1cFromGlucose(history.personalInformation.glucose)

                    healthDataPoints.add(
                        HealthDataPoint(
                            date = date,
                            timestamp = history.createdAt,
                            hemoglobin = hemoglobin,
                            hba1c = hba1c
                        )
                    )
                }
            }

            val currentData = healthDataPoints.maxByOrNull { it.timestamp }

            val chartData = ChartData(
                hemoglobinData = healthDataPoints,
                hba1cData = healthDataPoints,
                currentHemoglobin = currentData?.hemoglobin ?: 0.0,
                currentHba1c = currentData?.hba1c ?: 0.0
            )

            emit(Result.success(chartData))
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching chart data: ${e.message}", e)
            emit(Result.failure(e))
        }
    }

    suspend fun addHealthData(
        hemoglobin: Double,
        hba1c: Double,
        glucose: Double
    ): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(Exception("User not authenticated"))

            val personalInfo = PersonalInformation(
                hemoglobinLevel = hemoglobin,
                glucose = glucose
            )

            val medicalHistory = MedicalHistory(
                userId = currentUser.uid,
                personalInformation = personalInfo,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            firestore.collection("medical_history")
                .add(medicalHistory)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding health data: ${e.message}", e)
            Result.failure(e)
        }
    }

    private fun calculateHba1cFromGlucose(glucose: Double): Double {
        return if (glucose > 0) {
            (glucose + 46.7) / 28.7
        } else {
            5.0
        }
    }
}
