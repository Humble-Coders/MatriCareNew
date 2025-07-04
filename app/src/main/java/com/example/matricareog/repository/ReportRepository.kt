package com.example.matricareog.repository

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.example.matricareog.BloodPressure
import com.example.matricareog.HealthMetric
import com.example.matricareog.HealthReport
import com.example.matricareog.HealthStatus
import com.example.matricareog.MedicalHistory
import com.example.matricareog.MetricStatus
import com.example.matricareog.PersonalInformation
import com.example.matricareog.PregnancyInfo
import com.example.matricareog.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ReportRepository @Inject constructor() {
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val medicalHistoryCollection = firestore.collection("medical_history")
    private val usersCollection = firestore.collection("users")
    private val TAG = "ReportRepository"

    private var tfliteInterpreter: Interpreter? = null
    private var isModelLoaded = false

    // Initialize TFLite model
    fun initializeModel(context: Context) {
        try {
            val modelBuffer = loadModelFile(context, "medical_risk_model.tflite")
            tfliteInterpreter = Interpreter(modelBuffer)
            isModelLoaded = true
            Log.d(TAG, "TFLite model loaded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading TFLite model: ${e.message}")
            isModelLoaded = false
        }
    }

    private fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    // ML Model Prediction
    data class RiskPrediction(
        val riskLevel: String,
        val riskScore: Float,
        val riskPercentage: Float,
        val recommendations: List<String>
    )

    private fun predictRisk(personalInfo: PersonalInformation, pregnancyInfo: PregnancyInfo?): RiskPrediction? {
        return try {
            if (!isModelLoaded || tfliteInterpreter == null) {
                Log.w(TAG, "Model not loaded, skipping prediction")
                return null
            }

            // Prepare input data based on your model's expected format
            // [age, g, p, l, a, d, systolic, diastolic, rbs, bodytemp, heartrate, hb, hba1c, rr]
            val inputData = floatArrayOf(
                personalInfo.age.toFloat(),
                pregnancyInfo?.numberOfPregnancies?.toFloat() ?: 0f, // g
                pregnancyInfo?.numberOfLiveBirths?.toFloat() ?: 0f,  // p
                personalInfo.lifestyle.toFloat(), // l (0=Sedentary, 1=Active, 2=VeryActive)
                if (personalInfo.alcoholConsumption) 1f else 0f, // a
                if (personalInfo.hasDiabetes) 1f else 0f, // d
                personalInfo.systolicBloodPressure.toFloat(),
                personalInfo.diastolicBloodPressure.toFloat(),
                personalInfo.glucose.toFloat(), // rbs (Random Blood Sugar)
                personalInfo.bodyTemperature.toFloat(),
                personalInfo.pulseRate.toFloat(), // heartrate
                personalInfo.hemoglobinLevel.toFloat(), // hb
                personalInfo.hba1c.toFloat(),
                personalInfo.respirationRate.toFloat() // rr
            )

            // Prepare input and output arrays
            val input = Array(1) { inputData }
            val output = Array(1) { FloatArray(2) } // Assuming 2 classes: No Risk, High Risk

            // Run inference
            tfliteInterpreter!!.run(input, output)

            val probabilities = output[0]
            val highRiskProbability = probabilities[1]
            val lowRiskProbability = probabilities[0]

            val riskLevel = when {
                highRiskProbability > 0.7f -> "High Risk"
                highRiskProbability > 0.4f -> "Moderate Risk"
                else -> "Low Risk"
            }

            val recommendations = generateRecommendations(riskLevel, personalInfo, pregnancyInfo)

            RiskPrediction(
                riskLevel = riskLevel,
                riskScore = highRiskProbability,
                riskPercentage = highRiskProbability * 100,
                recommendations = recommendations
            )

        } catch (e: Exception) {
            Log.e(TAG, "Error during prediction: ${e.message}")
            null
        }
    }

    private fun generateRecommendations(
        riskLevel: String,
        personalInfo: PersonalInformation,
        pregnancyInfo: PregnancyInfo?
    ): List<String> {
        val recommendations = mutableListOf<String>()

        when (riskLevel) {
            "High Risk" -> {
                recommendations.add("Immediate medical consultation recommended")
                recommendations.add("Regular monitoring of vital signs")
                recommendations.add("Follow strict dietary guidelines")
                if (personalInfo.hasDiabetes) {
                    recommendations.add("Monitor blood glucose levels closely")
                }
            }
            "Moderate Risk" -> {
                recommendations.add("Schedule regular check-ups")
                recommendations.add("Maintain balanced diet and exercise")
                recommendations.add("Monitor blood pressure regularly")
            }
            else -> {
                recommendations.add("Continue current healthy lifestyle")
                recommendations.add("Regular prenatal check-ups")
                recommendations.add("Maintain balanced nutrition")
            }
        }

        // Add specific recommendations based on metrics
        if (personalInfo.systolicBloodPressure > 140 || personalInfo.diastolicBloodPressure > 90) {
            recommendations.add("Blood pressure management required")
        }

        if (personalInfo.hemoglobinLevel < 11.0) {
            recommendations.add("Iron supplementation may be needed")
        }

        if (personalInfo.alcoholConsumption) {
            recommendations.add("Avoid alcohol consumption during pregnancy")
        }

        return recommendations.take(4) // Limit to 4 recommendations
    }

    suspend fun getHealthReport(userId: String): Result<HealthReport> {
        Log.d(TAG, "getHealthReport called for user: $userId")
        return try {
            Log.d(TAG, "Fetching medical history for user: $userId")
            val medicalHistoryResult = getMedicalHistory(userId)

            if (medicalHistoryResult.isFailure) {
                Log.e(TAG, "Failed to get medical history: ${medicalHistoryResult.exceptionOrNull()?.message}")
                return Result.failure(medicalHistoryResult.exceptionOrNull()!!)
            }

            val medicalHistory = medicalHistoryResult.getOrNull().also {
                Log.d(TAG, "Medical history retrieved: ${it != null}")
            } ?: run {
                Log.e(TAG, "Medical history is null after successful retrieval")
                return Result.failure(Exception("No medical history found for user $userId"))
            }

            Log.d(TAG, "Fetching user name for: $userId")
            val userNameResult = getUserName(userId)
            val userName = if (userNameResult.isSuccess) {
                userNameResult.getOrNull() ?: run {
                    Log.w(TAG, "User name not found, using default")
                    "Patient"
                }
            } else {
                Log.w(TAG, "Couldn't fetch user name: ${userNameResult.exceptionOrNull()?.message}")
                "Patient"
            }

            Log.d(TAG, "Creating pregnancy info from history")
            val pregnancyInfo = PregnancyInfo(
                numberOfPregnancies = medicalHistory.pregnancyHistory.numberOfPregnancies,
                numberOfLiveBirths = medicalHistory.pregnancyHistory.numberOfLiveBirths,
                numberOfAbortions = medicalHistory.pregnancyHistory.numberOfAbortions
            )

            Log.d(TAG, "Converting to HealthReport")
            val report = convertToHealthReport(
                personalInfo = medicalHistory.personalInformation,
                userName = userName,
                pregnancyInfo = pregnancyInfo,
                riskPrediction = null // Skip prediction for basic fetch
            )

            Log.d(TAG, "Health report created successfully")
            Result.success(report)
        } catch (e: Exception) {
            Log.e(TAG, "Error in getHealthReport: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getMedicalHistory(userId: String): Result<MedicalHistory> {
        Log.d(TAG, "getMedicalHistory called for user: $userId")
        return try {
            Log.d(TAG, "Querying Firestore for medical history")
            val document = medicalHistoryCollection.document(userId).get().await()

            if (!document.exists()) {
                Log.e(TAG, "Document does not exist for user: $userId")
                return Result.failure(Exception("Medical history not found"))
            }

            val medicalHistory = document.toObject<MedicalHistory>().also {
                Log.d(TAG, "Document converted to MedicalHistory: ${it != null}")
            } ?: run {
                Log.e(TAG, "Failed to convert document to MedicalHistory")
                return Result.failure(Exception("Medical history data format error"))
            }

            Log.d(TAG, "Medical history retrieved successfully")
            Result.success(medicalHistory)
        } catch (e: Exception) {
            Log.e(TAG, "Exception in getMedicalHistory: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun getUserName(userId: String): Result<String> {
        Log.d(TAG, "getUserName called for user: $userId")
        return try {
            Log.d(TAG, "Querying Firestore for user name")
            val document = usersCollection.document(userId).get().await()

            val name = document.getString("fullName").also {
                Log.d(TAG, "User name retrieved: $it")
            } ?: run {
                Log.e(TAG, "User name field not found in document")
                return Result.failure(Exception("Name not found in user document"))
            }

            Log.d(TAG, "User name retrieved successfully")
            Result.success(name)
        } catch (e: Exception) {
            Log.e(TAG, "Exception in getUserName: ${e.message}", e)
            Result.failure(e)
        }
    }

    private fun convertToHealthReport(
        personalInfo: PersonalInformation,
        userName: String,
        pregnancyInfo: PregnancyInfo,
        riskPrediction: RiskPrediction?
    ): HealthReport {
        val date = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())

        // Create detailed metrics
        val metrics = listOf(
            createHealthMetric(
                id = "1",
                title = "Systolic Blood Pressure",
                value = personalInfo.systolicBloodPressure.toString(),
                unit = "mmHg",
                normalRange = "95-160",
                currentValue = personalInfo.systolicBloodPressure.toFloat(),
                rangeMin = 95f,
                rangeMax = 160f,
                icon = R.drawable.sbp
            ),
            createHealthMetric(
                id = "2",
                title = "Diastolic Blood Pressure",
                value = personalInfo.diastolicBloodPressure.toString(),
                unit = "mmHg",
                normalRange = "60-100",
                currentValue = personalInfo.diastolicBloodPressure.toFloat(),
                rangeMin = 60f,
                rangeMax = 100f,
                icon = R.drawable.sbp
            ),
            createHealthMetric(
                id = "3",
                title = "Pulse Rate",
                value = personalInfo.pulseRate.toString(),
                unit = "BPM",
                normalRange = "60-100",
                currentValue = personalInfo.pulseRate.toFloat(),
                rangeMin = 60f,
                rangeMax = 100f,
                icon = R.drawable.bp
            ),
            createHealthMetric(
                id = "4",
                title = "Body Temperature",
                value = "%.1f".format(personalInfo.bodyTemperature),
                unit = "°F",
                normalRange = "97.0-99.0",
                currentValue = personalInfo.bodyTemperature.toFloat(),
                rangeMin = 97f,
                rangeMax = 99f,
                icon = R.drawable.thermometer
            )
        )

        // Determine overall status (now considering ML prediction)
        val overallStatus = determineOverallStatus(metrics, riskPrediction)

        return HealthReport(
            patientName = userName,
            date = date,
            heartRate = personalInfo.pulseRate,
            bloodPressure = BloodPressure(
                systolic = personalInfo.systolicBloodPressure,
                diastolic = personalInfo.diastolicBloodPressure
            ),
            temperature = personalInfo.bodyTemperature,
            detailedMetrics = metrics,
            overallStatus = overallStatus,
            pregnancyInfo = pregnancyInfo
        )
    }

    private fun createHealthMetric(
        id: String,
        title: String,
        value: String,
        unit: String,
        normalRange: String,
        currentValue: Float,
        rangeMin: Float,
        rangeMax: Float,
        icon: Int
    ): HealthMetric {
        val status = when {
            currentValue < rangeMin * 0.9f || currentValue > rangeMax * 1.1f -> MetricStatus.CRITICAL
            currentValue < rangeMin * 0.95f || currentValue > rangeMax * 1.05f -> MetricStatus.WARNING
            else -> MetricStatus.NORMAL
        }

        return HealthMetric(
            id = id,
            title = title,
            value = value,
            unit = unit,
            normalRange = normalRange,
            currentValue = currentValue,
            rangeMin = rangeMin,
            rangeMax = rangeMax,
            icon = icon,
            status = status
        )
    }

    private fun determineOverallStatus(
        metrics: List<HealthMetric>,
        riskPrediction: RiskPrediction?
    ): HealthStatus {
        // First check ML prediction if available
        riskPrediction?.let { prediction ->
            return when (prediction.riskLevel) {
                "High Risk" -> HealthStatus(
                    status = "High Risk Pregnancy",
                    description = "ML Analysis indicates high risk - Immediate medical attention recommended",
                    color = Color(0xFFF44336)
                )
                "Moderate Risk" -> HealthStatus(
                    status = "Moderate Risk Pregnancy",
                    description = "ML Analysis suggests moderate risk - Regular monitoring recommended",
                    color = Color(0xFFFF9800)
                )
                else -> HealthStatus(
                    status = "Low Risk Pregnancy",
                    description = "ML Analysis indicates low risk - Continue regular care",
                    color = Color(0xFF4CAF50)
                )
            }
        }

        // Fallback to traditional metric-based assessment
        return when {
            metrics.any { it.status == MetricStatus.CRITICAL } ->
                HealthStatus(
                    status = "Critical Health Status",
                    description = "Some metrics are in critical range",
                    color = Color(0xFFF44336)
                )
            metrics.any { it.status == MetricStatus.WARNING } ->
                HealthStatus(
                    status = "Warning Health Status",
                    description = "Some metrics are outside normal range",
                    color = Color(0xFFFF9800))
            else ->
                HealthStatus(
                    status = "Excellent Health Status",
                    description = "All metrics are within normal range",
                    color = Color(0xFF4CAF50))
        }
    }

    // Get ML prediction separately for UI display
    suspend fun getMlPrediction(userId: String): Result<RiskPrediction> {
        return try {
            val medicalHistoryResult = getMedicalHistory(userId)
            if (medicalHistoryResult.isFailure) {
                return Result.failure(medicalHistoryResult.exceptionOrNull()!!)
            }

            val medicalHistory = medicalHistoryResult.getOrNull() ?: return Result.failure(
                Exception("No medical history found for user $userId")
            )

            val pregnancyInfo = PregnancyInfo(
                numberOfPregnancies = medicalHistory.pregnancyHistory.numberOfPregnancies,
                numberOfLiveBirths = medicalHistory.pregnancyHistory.numberOfLiveBirths,
                numberOfAbortions = medicalHistory.pregnancyHistory.numberOfAbortions
            )

            val prediction = predictRisk(medicalHistory.personalInformation, pregnancyInfo)
                ?: return Result.failure(Exception("Unable to generate ML prediction"))

            Result.success(prediction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}