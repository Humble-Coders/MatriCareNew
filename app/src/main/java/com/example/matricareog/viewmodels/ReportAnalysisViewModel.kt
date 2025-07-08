package com.example.matricareog.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.matricareog.HealthReport
import com.example.matricareog.repository.ReportRepository
import kotlinx.coroutines.launch
import android.util.Log
import com.example.matricareog.PersonalInformation
import com.example.matricareog.PregnancyHistory
import com.example.matricareog.PregnancyInfo

class ReportViewModel(
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

    private val _isModelReady = MutableLiveData(false)
    val isModelReady: LiveData<Boolean> = _isModelReady

    // Error state
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val TAG = "ReportViewModel"

    // Initialize ML model
    fun initializeMLModel(context: Context) {
        viewModelScope.launch {
            _isModelLoading.value = true
            try {
                repository.initializeModel(context)
                _isModelReady.value = true
                Log.d(TAG, "ML Model initialized successfully")
            } catch (e: Exception) {
                _error.value = "Failed to load ML model: ${e.message}"
                _isModelReady.value = false
                Log.e(TAG, "ML Model initialization failed: ${e.message}")
            } finally {
                _isModelLoading.value = false
            }
        }
    }

    // Generate report from LiveData instances (received from MedicalHistoryViewModel)
    fun processMLAnalysisFromLiveData(
        personalInfo: PersonalInformation,
        pregnancyHistory: PregnancyHistory,
        userName: String = "Patient"
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                Log.d(TAG, "Processing ML analysis from LiveData instances")
                Log.d(TAG, "Personal Info received: $personalInfo")
                Log.d(TAG, "Pregnancy History received: $pregnancyHistory")

                // Create PregnancyInfo from PregnancyHistory
                val pregnancyInfo = PregnancyInfo(
                    numberOfPregnancies = pregnancyHistory.numberOfPregnancies,
                    numberOfLiveBirths = pregnancyHistory.numberOfLiveBirths,
                    numberOfAbortions = pregnancyHistory.numberOfAbortions
                )

                // Generate ML prediction
                val mlPrediction = repository.generateMLPrediction(personalInfo, pregnancyInfo)
                _mlPrediction.value = mlPrediction

                // Generate health report
                val healthReport = repository.generateHealthReportFromData(
                    personalInfo = personalInfo,
                    userName = userName,
                    pregnancyInfo = pregnancyInfo,
                    riskPrediction = mlPrediction
                )
                _generatedHealthReport.value = healthReport

                Log.d(TAG, "ML Analysis completed successfully")
                Log.d(TAG, "ML Prediction: $mlPrediction")
                Log.d(TAG, "Health Report generated for: $userName")

            } catch (e: Exception) {
                Log.e(TAG, "Error processing ML analysis: ${e.message}")
                _error.value = "Error processing ML analysis: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


}