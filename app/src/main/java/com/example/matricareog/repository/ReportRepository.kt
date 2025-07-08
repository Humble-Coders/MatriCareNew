package com.example.matricareog.repository

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.example.matricareog.model.BloodPressure
import com.example.matricareog.model.HealthReport
import com.example.matricareog.model.HealthStatus
import com.example.matricareog.model.PersonalInformation
import com.example.matricareog.model.PregnancyInfo
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportRepository {

    private val TAG = "ReportRepository"
    private var tfliteInterpreter: Interpreter? = null
    private var isModelLoaded = false

    fun initializeModel(context: Context) {
        try {
            val modelBuffer = loadModelFile(context, "medical_risk_model.tflite")
            tfliteInterpreter = Interpreter(modelBuffer)
            isModelLoaded = true
            Log.d(TAG, "✅ Model loaded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error loading TFLite model: ${e.message}", e)
            isModelLoaded = false
            throw e
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

    data class RiskPrediction(
        val riskLevel: String,
        val confidence: Float = 0f,
        val noRiskProb: Float = 0f,
        val highRiskProb: Float = 0f
    )

    private fun predictRisk(personalInfo: PersonalInformation, pregnancyInfo: PregnancyInfo): RiskPrediction? {
        if (!isModelLoaded || tfliteInterpreter == null) {
            Log.e(TAG, "❌ Model not loaded")
            return null
        }

        try {
            // Exact 14 features in Python model order
            val inputFeatures = floatArrayOf(
                personalInfo.age.toFloat(),                    // 1. Age
                pregnancyInfo.gravida.toFloat(),              // 2. G
                pregnancyInfo.para.toFloat(),                 // 3. P
                pregnancyInfo.liveBirths.toFloat(),           // 4. L
                pregnancyInfo.abortions.toFloat(),            // 5. A
                pregnancyInfo.childDeaths.toFloat(),          // 6. D
                personalInfo.systolicBloodPressure.toFloat(), // 7. SystolicBP
                personalInfo.diastolicBloodPressure.toFloat(),// 8. DiastolicBP
                personalInfo.glucose.toFloat(),               // 9. RBS
                personalInfo.bodyTemperature.toFloat(),       // 10. BodyTemp
                personalInfo.pulseRate.toFloat(),             // 11. HeartRate
                personalInfo.hemoglobinLevel.toFloat(),       // 12. HB
                personalInfo.hba1c.toFloat(),                 // 13. HBA1C (user input)
                personalInfo.respirationRate.toFloat()        // 14. RR
            )

            Log.d(TAG, "🧠 ML Input (14 features): ${inputFeatures.contentToString()}")

            val input = Array(1) { inputFeatures }
            val output = Array(1) { FloatArray(2) }

            tfliteInterpreter!!.run(input, output)

            val probabilities = output[0]
            val noRiskProb = probabilities[0]
            val highRiskProb = probabilities[1]

            Log.d(TAG, "📊 Raw model output: [No Risk: $noRiskProb, High Risk: $highRiskProb]")

            // CORRECTED PREDICTION LOGIC - matching your Python code
            val prediction = when {
                // If model outputs class directly (0 or 1)
                noRiskProb == 0.0f && highRiskProb == 1.0f -> "High Risk"
                noRiskProb == 1.0f && highRiskProb == 0.0f -> "No Risk"

                // If model outputs probabilities, use the higher one
                highRiskProb > noRiskProb -> "High Risk"
                noRiskProb > highRiskProb -> "No Risk"

                // If equal probabilities, default to No Risk
                else -> "No Risk"
            }

            val confidence = maxOf(noRiskProb, highRiskProb)

            Log.d(TAG, "🎯 Final Prediction: $prediction (confidence: ${(confidence * 100).toInt()}%)")

            return RiskPrediction(
                riskLevel = prediction,
                confidence = confidence,
                noRiskProb = noRiskProb,
                highRiskProb = highRiskProb
            )

        } catch (e: Exception) {
            Log.e(TAG, "💥 Prediction error: ${e.message}")
            return null
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
        val date = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())

        val overallStatus = riskPrediction?.let { prediction ->
            val description = if (prediction.riskLevel == "High Risk") {
                "🤖 AI indicates HIGH RISK - Seek immediate medical attention"
            } else {
                "🤖 AI indicates NO RISK - Continue regular care"
            }
            val color = if (prediction.riskLevel == "High Risk") Color(0xFFF44336) else Color(0xFF4CAF50)
            HealthStatus("AI: ${prediction.riskLevel}", description, color)
        } ?: HealthStatus("AI Unavailable", "Model failed to process data", Color(0xFF757575))

        return HealthReport(
            patientName = userName,
            date = date,
            heartRate = personalInfo.pulseRate,
            bloodPressure = BloodPressure(personalInfo.systolicBloodPressure, personalInfo.diastolicBloodPressure),
            temperature = personalInfo.bodyTemperature,
            detailedMetrics = emptyList(),
            overallStatus = overallStatus,
            pregnancyInfo = pregnancyInfo
        )
    }

    fun isModelReady(): Boolean = isModelLoaded
}