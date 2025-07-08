package com.example.matricareog.model

import androidx.compose.ui.graphics.Color


data class User(
    val fullName: String = "",
    val email: String = "",
    val uid: String = ""
)

sealed class AuthResult {
    object Loading : AuthResult()
    data class Success(val user: User? = null) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Idle : AuthResult()
}

// Updated PersonalInformation based on research paper Table 2
data class PersonalInformation(
    val age: Int = 0,
    val systolicBloodPressure: Int = 0,
    val diastolicBloodPressure: Int = 0,
    val glucose: Double = 0.0,
    val bodyTemperature: Double = 0.0,
    val pulseRate: Int = 0,
    val hemoglobinLevel: Double = 0.0,
    val hba1c: Double = 0.0,          // Add this field
    val respirationRate: Int = 0
)

// Updated PregnancyHistory based on research paper Table 2
data class PregnancyHistory(
    val gravida: Int = 0,           // G - Feature 2 (total pregnancies including current)
    val para: Int = 0,              // P - Feature 3 (deliveries after 20 weeks)
    val liveBirths: Int = 0,        // L - Feature 4 (total living children)
    val abortions: Int = 0,         // A - Feature 5 (termination of pregnancies)
    val childDeaths: Int = 0        // D - Feature 6 (number of children dead)
)

data class MedicalHistory(
    val id: String = "",
    val userId: String = "",
    val personalInformation: PersonalInformation = PersonalInformation(),
    val pregnancyHistory: PregnancyHistory = PregnancyHistory(),

    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val timestamp: Long = System.currentTimeMillis(),
    val date: Long = System.currentTimeMillis(),

    // ML prediction fields
    val mlRiskLevel: String? = null,
    val mlPredictionTimestamp: Long? = null,

    // Versioning
    val version: Int = 1
)

// Keep existing classes that don't need changes
data class HealthReport(
    val patientName: String,
    val date: String,
    val heartRate: Int,
    val bloodPressure: BloodPressure,
    val temperature: Double,
    val detailedMetrics: List<HealthMetric>,
    val overallStatus: HealthStatus,
    val pregnancyInfo: PregnancyInfo? = null
)

data class BloodPressure(
    val systolic: Int,
    val diastolic: Int
)

data class HealthMetric(
    val id: String,
    val title: String,
    val value: String,
    val unit: String,
    val normalRange: String,
    val currentValue: Float,
    val rangeMin: Float,
    val rangeMax: Float,
    val icon: Int,
    val status: MetricStatus
)

data class HealthStatus(
    val status: String,
    val description: String,
    val color: Color
)

enum class MetricStatus {
    NORMAL, WARNING, CRITICAL
}


sealed class MatriCareState {
    object Loading : MatriCareState()
    data class Success(val chartData: ChartData) : MatriCareState()
    data class Error(val message: String) : MatriCareState()
}

data class ChartRange(
    val min: Double,
    val max: Double,
    val unit: String,
    val label: String
)

data class HealthDataPoint(
    val date: String = "",
    val timestamp: Long = 0L,
    val hemoglobin: Double = 0.0,
    val hba1c: Double = 0.0,
    val glucose: Double = 0.0,
    val systolicBP: Double = 0.0,
    val diastolicBP: Double = 0.0,
    val pulseRate: Double = 0.0,
    val bodyTemperature: Double = 0.0,
    val respirationRate: Double = 0.0
)

// Updated ChartData class
data class ChartData(
    val hemoglobinData: List<HealthDataPoint> = emptyList(),
    val hba1cData: List<HealthDataPoint> = emptyList(),
    val glucoseData: List<HealthDataPoint> = emptyList(),
    val bloodPressureData: List<HealthDataPoint> = emptyList(),
    val pulseData: List<HealthDataPoint> = emptyList(),
    val temperatureData: List<HealthDataPoint> = emptyList(),
    val respirationData: List<HealthDataPoint> = emptyList(),
    val currentHemoglobin: Double = 0.0,
    val currentHba1c: Double = 0.0,
    val currentGlucose: Double = 0.0,
    val currentSystolicBP: Double = 0.0,
    val currentDiastolicBP: Double = 0.0,
    val currentPulseRate: Double = 0.0,
    val currentBodyTemperature: Double = 0.0,
    val currentRespirationRate: Double = 0.0
)

data class PregnancyInfo(
    val gravida: Int,      // G
    val para: Int,         // P
    val liveBirths: Int,   // L
    val abortions: Int,    // A
    val childDeaths: Int   // D
) {
    // Helper function for prediction input (13 features total)
    // Features in exact order from research paper Table 2
    fun toPredictionInput(personalInfo: PersonalInformation): FloatArray {
        return floatArrayOf(
            personalInfo.age.toFloat(),                    // Feature 1: Age
            gravida.toFloat(),                            // Feature 2: G (Gravida)
            para.toFloat(),                               // Feature 3: P (Para)
            liveBirths.toFloat(),                         // Feature 4: L (Live births)
            abortions.toFloat(),                          // Feature 5: A (Abortions)
            childDeaths.toFloat(),                        // Feature 6: D (Deaths)
            personalInfo.systolicBloodPressure.toFloat(), // Feature 7: SBP (Systolic BP)
            personalInfo.diastolicBloodPressure.toFloat(),// Feature 8: DSP (Diastolic BP)
            personalInfo.glucose.toFloat(),               // Feature 9: RBS (Random Blood Sugar)
            personalInfo.bodyTemperature.toFloat(),       // Feature 10: BT (Body Temperature)
            personalInfo.pulseRate.toFloat(),             // Feature 11: HR (Heart Rate)
            personalInfo.hemoglobinLevel.toFloat(),       // Feature 12: Hb (Hemoglobin)
            personalInfo.respirationRate.toFloat()        // Feature 13: RR (Respiration Rate)
        )
    }
}