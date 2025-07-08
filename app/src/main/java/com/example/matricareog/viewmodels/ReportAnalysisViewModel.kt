package com.example.matricareog.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.matricareog.model.HealthReport
import com.example.matricareog.repository.ReportRepository
import kotlinx.coroutines.launch
import android.util.Log
import com.example.matricareog.model.PersonalInformation
import com.example.matricareog.model.PregnancyHistory
import com.example.matricareog.model.PregnancyInfo

class ReportAnalysisViewModel(
    private val repository: ReportRepository
) : ViewModel() {

    // ML Prediction state
    private val _mlPrediction = MutableLiveData<ReportRepository.RiskPrediction?>()
    val mlPrediction: LiveData<ReportRepository.RiskPrediction?> = _mlPrediction

    // Generated Health Report from LiveData
    private val _generatedHealthReport = MutableLiveData<HealthReport?>()
    val generatedHealthReport: LiveData<HealthReport?> = _generatedHealthReport

    // Loading states
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isModelLoading = MutableLiveData(false)
    val isModelLoading: LiveData<Boolean> = _isModelLoading

    private val _isModelReady = MutableLiveData(false)
    val isModelReady: LiveData<Boolean> = _isModelReady

    // Error state
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Processing state for debugging
    private val _processingStatus = MutableLiveData<String>()
    val processingStatus: LiveData<String> = _processingStatus

    private val TAG = "ReportViewModel"

    // Initialize ML model
    fun initializeMLModel(context: Context) {
        viewModelScope.launch {
            _isModelLoading.value = true
            _processingStatus.value = "Loading ML model..."

            try {
                repository.initializeModel(context)
                _isModelReady.value = true
                _processingStatus.value = "ML model loaded successfully"
                Log.d(TAG, "ML Model initialized successfully")
            } catch (e: Exception) {
                _error.value = "Failed to load ML model: ${e.message}"
                _isModelReady.value = false
                _processingStatus.value = "Failed to load ML model"
                Log.e(TAG, "ML Model initialization failed: ${e.message}")
            } finally {
                _isModelLoading.value = false
            }
        }
    }

    // Validate PersonalInformation data
    private fun isValidPersonalInfo(personalInfo: PersonalInformation): Boolean {
        return personalInfo.age > 0 &&
                personalInfo.systolicBloodPressure > 0 &&
                personalInfo.diastolicBloodPressure > 0 &&
                personalInfo.glucose > 0 &&
                personalInfo.bodyTemperature > 0 &&
                personalInfo.pulseRate > 0 &&
                personalInfo.hemoglobinLevel > 0 &&
                personalInfo.respirationRate > 0
    }

    // Validate PregnancyHistory data
    private fun isValidPregnancyHistory(pregnancyHistory: PregnancyHistory): Boolean {
        return pregnancyHistory.gravida >= 0 &&
                pregnancyHistory.para >= 0 &&
                pregnancyHistory.liveBirths >= 0 &&
                pregnancyHistory.abortions >= 0 &&
                pregnancyHistory.childDeaths >= 0
    }

    // Clear error state
    fun clearError() {
        _error.value = null
    }

    // Retry analysis
    fun retryAnalysis(personalInfo: PersonalInformation, pregnancyHistory: PregnancyHistory, userName: String = "Patient") {
        clearError()
        processMLAnalysisFromLiveData(personalInfo, pregnancyHistory, userName)
    }


    fun processMLAnalysisFromLiveData(
        personalInfo: PersonalInformation,
        pregnancyHistory: PregnancyHistory,
        userName: String = "Patient"
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _processingStatus.value = "Validating input data..."

            try {
                // Validate that model is ready
                if (!repository.isModelReady()) {
                    throw Exception("AI model is not ready. Please wait for model initialization to complete.")
                }

                // Validate input data
                if (!isValidPersonalInfo(personalInfo)) {
                    throw Exception("Invalid personal information. Please check all health parameters are filled correctly.")
                }

                if (!isValidPregnancyHistory(pregnancyHistory)) {
                    throw Exception("Invalid pregnancy history. Please verify all obstetric information.")
                }

                _processingStatus.value = "Converting data for AI analysis..."

                // Create PregnancyInfo from PregnancyHistory (matching research paper exactly)
                val pregnancyInfo = PregnancyInfo(
                    gravida = pregnancyHistory.gravida,
                    para = pregnancyHistory.para,
                    liveBirths = pregnancyHistory.liveBirths,
                    abortions = pregnancyHistory.abortions,
                    childDeaths = pregnancyHistory.childDeaths
                )

                _processingStatus.value = "Running AI risk assessment..."

                // Generate ML prediction - this is the ONLY source of risk assessment
                val mlPrediction = repository.generateMLPrediction(personalInfo, pregnancyInfo)

                if (mlPrediction == null) {
                    throw Exception("AI model failed to generate prediction. Please check your data and try again.")
                }

                _mlPrediction.value = mlPrediction
                _processingStatus.value = "Generating comprehensive health report..."

                // Generate health report
                val healthReport = repository.generateHealthReportFromData(
                    personalInfo = personalInfo,
                    userName = userName,
                    pregnancyInfo = pregnancyInfo,
                    riskPrediction = mlPrediction
                )
                _generatedHealthReport.value = healthReport

                _processingStatus.value = "✅ AI analysis completed successfully"
                Log.d(TAG, "✅ Pure ML Analysis completed - Risk: ${mlPrediction.riskLevel}")

            } catch (e: Exception) {
                Log.e(TAG, "❌ ML Analysis failed: ${e.message}")
                _error.value = "AI Analysis Failed: ${e.message}"
                _processingStatus.value = "❌ Analysis failed"
                _mlPrediction.value = null
                _generatedHealthReport.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

}