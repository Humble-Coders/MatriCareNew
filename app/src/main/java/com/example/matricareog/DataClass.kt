package com.example.matricareog


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




data class PersonalInformation(
    val age: Int = 0,
    val systolicBloodPressure: Int = 0,
    val diastolicBloodPressure: Int = 0,
    val glucose: Double = 0.0,
    val respirationRate: Int = 0,
    val bodyTemperature: Double = 0.0,
    val pulseRate: Int = 0,
    val hemoglobinLevel: Double = 0.0
)

data class PregnancyHistory(
    val numberOfPregnancies: Int = 0,
    val numberOfLiveBirths: Int = 0,
    val numberOfAbortions: Int = 0,
    val numberOfChildDeaths: Int = 0,
    val numberOfDeliveries: Int = 0,
    val lastDeliveryDate: String = "" // Format: "dd/MM/yyyy"
)

data class MedicalHistory(
    val userId: String = "",
    val personalInformation: PersonalInformation = PersonalInformation(),
    val pregnancyHistory: PregnancyHistory = PregnancyHistory(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)



// Data Classes
data class HealthReport(
    val patientName: String,
    val date: String,
    val heartRate: Int,
    val bloodPressure: BloodPressure,
    val temperature: Double,
    val detailedMetrics: List<HealthMetric>,
    val overallStatus: HealthStatus
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
    val currentValue: Float, // 0-100 for progress calculation
    val rangeMin: Float,
    val rangeMax: Float,
    val icon: Int,
    val status: MetricStatus
)

data class HealthStatus(
    val status: String,
    val description: String,
    val color: androidx.compose.ui.graphics.Color
)

enum class MetricStatus {
    NORMAL, WARNING, CRITICAL
}