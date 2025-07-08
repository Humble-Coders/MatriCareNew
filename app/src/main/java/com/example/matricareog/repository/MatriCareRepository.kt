package com.example.matricareog.repository

import android.util.Log
import com.example.matricareog.model.ChartData
import com.example.matricareog.model.HealthDataPoint
import com.example.matricareog.model.MedicalHistory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    // Data classes for history
    data class PredictionHistoryItem(
        val id: String = "",
        val date: String = "",
        val timestamp: Long = 0L,
        val age: Int = 0,
        val systolicBP: Int = 0,
        val diastolicBP: Int = 0,
        val glucose: Double = 0.0,
        val bodyTemperature: Double = 0.0,
        val pulseRate: Int = 0,
        val hemoglobinLevel: Double = 0.0,
        val hba1c: Double = 0.0,
        val respirationRate: Int = 0,
        val gravida: Int = 0,
        val para: Int = 0,
        val liveBirths: Int = 0,
        val abortions: Int = 0,
        val childDeaths: Int = 0
    )

    data class RiskHistoryItem(
        val id: String = "",
        val date: String = "",
        val timestamp: Long = 0L,
        val riskLevel: String = "",
        val confidence: Float = 0f,
        val mlPredictionTimestamp: Long? = null
    )

    // Replace your getUserPredictionHistory() method with this:
    fun getUserPredictionHistory(): Flow<Result<List<PredictionHistoryItem>>> = flow {
        try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                emit(Result.failure(Exception("User not authenticated")))
                return@flow
            }

            Log.d(TAG, "Fetching prediction history for user: ${currentUser.uid}")

            // Remove the orderBy clause to avoid index requirement
            val medicalHistorySnapshot = firestore
                .collection("medical_history")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .await()

            val predictionHistory = mutableListOf<PredictionHistoryItem>()
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

            medicalHistorySnapshot.documents.forEach { document ->
                val medicalHistory = document.toObject(MedicalHistory::class.java)
                medicalHistory?.let { history ->
                    val formattedDate = dateFormat.format(Date(history.createdAt))

                    val item = PredictionHistoryItem(
                        id = document.id,
                        date = formattedDate,
                        timestamp = history.createdAt,
                        age = history.personalInformation.age,
                        systolicBP = history.personalInformation.systolicBloodPressure,
                        diastolicBP = history.personalInformation.diastolicBloodPressure,
                        glucose = history.personalInformation.glucose,
                        bodyTemperature = history.personalInformation.bodyTemperature,
                        pulseRate = history.personalInformation.pulseRate,
                        hemoglobinLevel = history.personalInformation.hemoglobinLevel,
                        hba1c = history.personalInformation.hba1c,
                        respirationRate = history.personalInformation.respirationRate,
                        gravida = history.pregnancyHistory.gravida,
                        para = history.pregnancyHistory.para,
                        liveBirths = history.pregnancyHistory.liveBirths,
                        abortions = history.pregnancyHistory.abortions,
                        childDeaths = history.pregnancyHistory.childDeaths
                    )
                    predictionHistory.add(item)
                }
            }

            // Sort in memory by timestamp (descending - most recent first)
            val sortedHistory = predictionHistory.sortedByDescending { it.timestamp }

            Log.d(TAG, "Found ${sortedHistory.size} prediction history records")
            emit(Result.success(sortedHistory))

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching prediction history: ${e.message}", e)
            emit(Result.failure(e))
        }
    }

    fun getUserRiskHistory(): Flow<Result<List<RiskHistoryItem>>> = flow {
        try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                emit(Result.failure(Exception("User not authenticated")))
                return@flow
            }

            Log.d(TAG, "Fetching risk history for user: ${currentUser.uid}")

            // Simplified query - remove orderBy and whereNotEqualTo to avoid index requirement
            val medicalHistorySnapshot = firestore
                .collection("medical_history")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .await()

            val riskHistory = mutableListOf<RiskHistoryItem>()
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

            medicalHistorySnapshot.documents.forEach { document ->
                val medicalHistory = document.toObject(MedicalHistory::class.java)
                medicalHistory?.let { history ->
                    // Filter for non-null and non-empty mlRiskLevel in code instead of query
                    if (!history.mlRiskLevel.isNullOrEmpty()) {
                        val predictionDate = history.mlPredictionTimestamp ?: history.createdAt
                        val formattedDate = dateFormat.format(Date(predictionDate))

                        val item = RiskHistoryItem(
                            id = document.id,
                            date = formattedDate,
                            timestamp = predictionDate,
                            riskLevel = history.mlRiskLevel ?: "Unknown",
                            confidence = 0f,
                            mlPredictionTimestamp = history.mlPredictionTimestamp
                        )
                        riskHistory.add(item)
                    }
                }
            }

            // Sort in memory by timestamp (descending - most recent first)
            val sortedRiskHistory = riskHistory.sortedByDescending { it.timestamp }

            Log.d(TAG, "Found ${sortedRiskHistory.size} risk history records")
            emit(Result.success(sortedRiskHistory))

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching risk history: ${e.message}", e)
            emit(Result.failure(e))
        }
    }



    // Get all available parameters for the user
    fun getAvailableParameters(): Flow<Result<List<String>>> = flow {
        try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                emit(Result.failure(Exception("User not authenticated")))
                return@flow
            }

            val medicalHistorySnapshot = firestore
                .collection("medical_history")
                .whereEqualTo("userId", currentUser.uid)
                .limit(1)
                .get()
                .await()

            val availableParameters = mutableListOf<String>()

            if (!medicalHistorySnapshot.isEmpty) {
                // Add all available parameters based on PersonalInformation structure
                availableParameters.addAll(
                    listOf(
                        "Hemoglobin",
                        "HBA1C",
                        "Blood Glucose",
                        "Blood Pressure",
                        "Heart Rate",
                        "Body Temperature",
                        "Respiration Rate"
                    )
                )
            }

            Log.d(TAG, "Available parameters: $availableParameters")
            emit(Result.success(availableParameters))

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching available parameters: ${e.message}", e)
            emit(Result.failure(e))
        }
    }


    fun getParameterChartData(parameter: String): Flow<Result<ChartData>> = flow {
        try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                emit(Result.failure(Exception("User not authenticated")))
                return@flow
            }

            Log.d(TAG, "Generating chart data for parameter: $parameter")

            val medicalHistorySnapshot = firestore
                .collection("medical_history")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .await()

            val healthDataPoints = mutableListOf<HealthDataPoint>()
            val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

            medicalHistorySnapshot.documents.forEach { document ->
                val medicalHistory = document.toObject(MedicalHistory::class.java)
                medicalHistory?.let { history ->
                    val date = dateFormat.format(Date(history.createdAt))

                    // Create a complete HealthDataPoint with all values
                    val dataPoint = HealthDataPoint(
                        date = date,
                        timestamp = history.createdAt,
                        hemoglobin = history.personalInformation.hemoglobinLevel,
                        hba1c = history.personalInformation.hba1c,
                        glucose = history.personalInformation.glucose,
                        systolicBP = history.personalInformation.systolicBloodPressure.toDouble(),
                        diastolicBP = history.personalInformation.diastolicBloodPressure.toDouble(),
                        pulseRate = history.personalInformation.pulseRate.toDouble(),
                        bodyTemperature = history.personalInformation.bodyTemperature,
                        respirationRate = history.personalInformation.respirationRate.toDouble()
                    )
                    healthDataPoints.add(dataPoint)
                }
            }

            // Sort in memory by timestamp (ascending for chart data)
            val sortedDataPoints = healthDataPoints.sortedBy { it.timestamp }
            val currentData = sortedDataPoints.maxByOrNull { it.timestamp }

            // Create ChartData with parameter-specific data
            val chartData = when (parameter.lowercase()) {
                "hemoglobin" -> {
                    ChartData(
                        hemoglobinData = sortedDataPoints,
                        currentHemoglobin = currentData?.hemoglobin ?: 0.0
                    )
                }
                "hba1c" -> {
                    ChartData(
                        hba1cData = sortedDataPoints,
                        currentHba1c = currentData?.hba1c ?: 0.0
                    )
                }
                "glucose", "blood glucose" -> {
                    ChartData(
                        glucoseData = sortedDataPoints,
                        currentGlucose = currentData?.glucose ?: 0.0
                    )
                }
                "bloodpressure", "blood pressure" -> {
                    ChartData(
                        bloodPressureData = sortedDataPoints,
                        currentSystolicBP = currentData?.systolicBP ?: 0.0,
                        currentDiastolicBP = currentData?.diastolicBP ?: 0.0
                    )
                }
                "pulse", "heart rate" -> {
                    ChartData(
                        pulseData = sortedDataPoints,
                        currentPulseRate = currentData?.pulseRate ?: 0.0
                    )
                }
                "temperature", "body temperature" -> {
                    ChartData(
                        temperatureData = sortedDataPoints,
                        currentBodyTemperature = currentData?.bodyTemperature ?: 0.0
                    )
                }
                "respiration", "respiration rate" -> {
                    ChartData(
                        respirationData = sortedDataPoints,
                        currentRespirationRate = currentData?.respirationRate ?: 0.0
                    )
                }
                else -> {
                    // Default to hemoglobin and hba1c
                    ChartData(
                        hemoglobinData = sortedDataPoints,
                        hba1cData = sortedDataPoints,
                        currentHemoglobin = currentData?.hemoglobin ?: 0.0,
                        currentHba1c = currentData?.hba1c ?: 0.0
                    )
                }
            }

            Log.d(TAG, "Generated chart data with ${sortedDataPoints.size} data points for parameter: $parameter")
            emit(Result.success(chartData))

        } catch (e: Exception) {
            Log.e(TAG, "Error generating chart data for $parameter: ${e.message}", e)
            emit(Result.failure(e))
        }
    }

    // Also update the original getChartData method to use complete data points
    fun getChartData(): Flow<Result<ChartData>> = flow {
        try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                emit(Result.failure(Exception("User not authenticated")))
                return@flow
            }

            val medicalHistorySnapshot = firestore
                .collection("medical_history")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .await()

            val healthDataPoints = mutableListOf<HealthDataPoint>()
            val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

            medicalHistorySnapshot.documents.forEach { document ->
                val medicalHistory = document.toObject(MedicalHistory::class.java)
                medicalHistory?.let { history ->
                    val date = dateFormat.format(Date(history.createdAt))

                    val dataPoint = HealthDataPoint(
                        date = date,
                        timestamp = history.createdAt,
                        hemoglobin = history.personalInformation.hemoglobinLevel,
                        hba1c = history.personalInformation.hba1c,
                        glucose = history.personalInformation.glucose,
                        systolicBP = history.personalInformation.systolicBloodPressure.toDouble(),
                        diastolicBP = history.personalInformation.diastolicBloodPressure.toDouble(),
                        pulseRate = history.personalInformation.pulseRate.toDouble(),
                        bodyTemperature = history.personalInformation.bodyTemperature,
                        respirationRate = history.personalInformation.respirationRate.toDouble()
                    )
                    healthDataPoints.add(dataPoint)
                }
            }

            val sortedDataPoints = healthDataPoints.sortedBy { it.timestamp }
            val currentData = sortedDataPoints.maxByOrNull { it.timestamp }

            val chartData = ChartData(
                hemoglobinData = sortedDataPoints,
                hba1cData = sortedDataPoints,
                glucoseData = sortedDataPoints,
                bloodPressureData = sortedDataPoints,
                pulseData = sortedDataPoints,
                temperatureData = sortedDataPoints,
                respirationData = sortedDataPoints,
                currentHemoglobin = currentData?.hemoglobin ?: 0.0,
                currentHba1c = currentData?.hba1c ?: 0.0,
                currentGlucose = currentData?.glucose ?: 0.0,
                currentSystolicBP = currentData?.systolicBP ?: 0.0,
                currentDiastolicBP = currentData?.diastolicBP ?: 0.0,
                currentPulseRate = currentData?.pulseRate ?: 0.0,
                currentBodyTemperature = currentData?.bodyTemperature ?: 0.0,
                currentRespirationRate = currentData?.respirationRate ?: 0.0
            )

            emit(Result.success(chartData))
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching chart data: ${e.message}", e)
            emit(Result.failure(e))
        }
    }

}