package com.example.matricareog.repository

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.example.matricareog.BloodPressure
import com.example.matricareog.HealthMetric
import com.example.matricareog.HealthReport
import com.example.matricareog.HealthStatus
import com.example.matricareog.MetricStatus
import com.example.matricareog.PersonalInformation
import com.example.matricareog.PregnancyInfo
import com.example.matricareog.R
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportRepository(
) {

    private val TAG = "ReportRepository"

    private var tfliteInterpreter: Interpreter? = null
    private var isModelLoaded = false

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
        val riskLevel: String
    )

    private fun predictRisk(personalInfo: PersonalInformation, pregnancyInfo: PregnancyInfo?): RiskPrediction? {
        return try {
            if (!isModelLoaded || tfliteInterpreter == null) {
                Log.w(TAG, "Model not loaded, skipping prediction")
                return null
            }

            val inputData = floatArrayOf(
                personalInfo.age.toFloat(),
                pregnancyInfo?.numberOfPregnancies?.toFloat() ?: 0f,
                pregnancyInfo?.numberOfLiveBirths?.toFloat() ?: 0f,
                personalInfo.lifestyle.toFloat(),
                if (personalInfo.alcoholConsumption) 1f else 0f,
                if (personalInfo.hasDiabetes) 1f else 0f,
                personalInfo.systolicBloodPressure.toFloat(),
                personalInfo.diastolicBloodPressure.toFloat(),
                personalInfo.glucose.toFloat(),
                personalInfo.bodyTemperature.toFloat(),
                personalInfo.pulseRate.toFloat(),
                personalInfo.hemoglobinLevel.toFloat(),
                personalInfo.hba1c.toFloat(),
                personalInfo.respirationRate.toFloat()
            )

            val input = Array(1) { inputData }
            val output = Array(1) { FloatArray(2) }

            tfliteInterpreter!!.run(input, output)
            val probabilities = output[0]
            val highRiskProbability = probabilities[1]

            val riskLevel = when {
                highRiskProbability > 0.7f -> "High Risk"
                highRiskProbability > 0.4f -> "Moderate Risk"
                else -> "Low Risk"
            }

            RiskPrediction(riskLevel = riskLevel)
        } catch (e: Exception) {
            Log.e(TAG, "Error during prediction: ${e.message}")
            null
        }
    }


    private fun convertToHealthReport(
        personalInfo: PersonalInformation,
        userName: String,
        pregnancyInfo: PregnancyInfo,
        riskPrediction: RiskPrediction?
    ): HealthReport {
        val date = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())

        val metrics = listOf(
            createHealthMetric("1", "Systolic Blood Pressure", personalInfo.systolicBloodPressure.toString(),
                "mmHg", "95-160", personalInfo.systolicBloodPressure.toFloat(), 95f, 160f, R.drawable.sbp),
            createHealthMetric("2", "Diastolic Blood Pressure", personalInfo.diastolicBloodPressure.toString(),
                "mmHg", "60-100", personalInfo.diastolicBloodPressure.toFloat(), 60f, 100f, R.drawable.sbp),
            createHealthMetric("3", "Pulse Rate", personalInfo.pulseRate.toString(),
                "BPM", "60-100", personalInfo.pulseRate.toFloat(), 60f, 100f, R.drawable.bp),
            createHealthMetric("4", "Body Temperature", "%.1f".format(personalInfo.bodyTemperature),
                "°F", "97.0-99.0", personalInfo.bodyTemperature.toFloat(), 97f, 99f, R.drawable.thermometer)
        )

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
        id: String, title: String, value: String, unit: String, normalRange: String,
        currentValue: Float, rangeMin: Float, rangeMax: Float, icon: Int
    ): HealthMetric {
        val status = when {
            currentValue < rangeMin * 0.9f || currentValue > rangeMax * 1.1f -> MetricStatus.CRITICAL
            currentValue < rangeMin * 0.95f || currentValue > rangeMax * 1.05f -> MetricStatus.WARNING
            else -> MetricStatus.NORMAL
        }
        return HealthMetric(id, title, value, unit, normalRange, currentValue, rangeMin, rangeMax, icon, status)
    }

    private fun determineOverallStatus(
        metrics: List<HealthMetric>,
        riskPrediction: RiskPrediction?
    ): HealthStatus {
        riskPrediction?.let {
            return when (it.riskLevel) {
                "High Risk" -> HealthStatus("High Risk Pregnancy",
                    "ML Analysis indicates high risk - Immediate medical attention recommended", Color(0xFFF44336))
                "Moderate Risk" -> HealthStatus("Moderate Risk Pregnancy",
                    "ML Analysis suggests moderate risk - Regular monitoring recommended", Color(0xFFFF9800))
                else -> HealthStatus("Low Risk Pregnancy",
                    "ML Analysis indicates low risk - Continue regular care", Color(0xFF4CAF50))
            }
        }
        return when {
            metrics.any { it.status == MetricStatus.CRITICAL } ->
                HealthStatus("Critical Health Status", "Some metrics are in critical range", Color(0xFFF44336))
            metrics.any { it.status == MetricStatus.WARNING } ->
                HealthStatus("Warning Health Status", "Some metrics are outside normal range", Color(0xFFFF9800))
            else -> HealthStatus("Excellent Health Status", "All metrics are within normal range", Color(0xFF4CAF50))
        }
    }

    fun generateMLPrediction(personalInfo: PersonalInformation, pregnancyInfo: PregnancyInfo): RiskPrediction? {
        return predictRisk(personalInfo, pregnancyInfo)
    }

    fun generateHealthReportFromData(
        personalInfo: PersonalInformation,
        userName: String,
        pregnancyInfo: PregnancyInfo,
        riskPrediction: RiskPrediction?
    ): HealthReport {
        return convertToHealthReport(personalInfo, userName, pregnancyInfo, riskPrediction)
    }


}
