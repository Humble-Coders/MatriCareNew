package com.example.matricareog.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.matricareog.HealthReport
import com.example.matricareog.MedicalHistory
import com.example.matricareog.repository.ReportRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import android.util.Log

class ReportViewModel : ViewModel() {
    private val repository = ReportRepository() // Internal dependency (same as MedicalHistoryViewModel)

    // State for Health Report
    private val _healthReport = MutableLiveData<HealthReport?>()
    val healthReport: LiveData<HealthReport?> = _healthReport

    private val _medicalHistory = MutableLiveData<MedicalHistory?>()
    val medicalHistory: MutableLiveData<MedicalHistory?> get() = _medicalHistory

    // ML Prediction state
    private val _mlPrediction = MutableLiveData<ReportRepository.RiskPrediction?>()
    val mlPrediction: LiveData<ReportRepository.RiskPrediction?> = _mlPrediction

    // Loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // ML Model loading state
    private val _isModelLoading = MutableLiveData(false)
    val isModelLoading: LiveData<Boolean> = _isModelLoading

    private val _isModelReady = MutableLiveData(false)
    val isModelReady: LiveData<Boolean> = _isModelReady

    // Debug tag
    private val TAG = "ReportViewModel"

    // Initialize ML model
    fun initializeMLModel(context: Context) {
        viewModelScope.launch {
            _isModelLoading.value = true
            try {
                repository.initializeModel(context)
                _isModelReady.value = true
            } catch (e: Exception) {
                _error.value = "Failed to load ML model: ${e.message}"
                _isModelReady.value = false
            } finally {
                _isModelLoading.value = false
            }
        }
    }

    // Fetch health report (mirrors MedicalHistoryViewModel's loadMedicalHistory)
    fun fetchHealthData(userId: String) {
        viewModelScope.launch {
            Log.d(TAG, "Starting fetchHealthData for user: $userId")
            _isLoading.value = true
            _error.value = null

            try {
                Log.d(TAG, "Initiating parallel data fetching")
                val healthReportDeferred = async {
                    Log.d(TAG, "Starting health report fetch")
                    repository.getHealthReport(userId)
                }
                val medicalHistoryDeferred = async {
                    Log.d(TAG, "Starting medical history fetch")
                    repository.getMedicalHistory(userId)
                }

                Log.d(TAG, "Awaiting results...")
                val healthReportResult = healthReportDeferred.await()
                val medicalHistoryResult = medicalHistoryDeferred.await()

                Log.d(TAG, "Results received - HealthReport success: ${healthReportResult.isSuccess}, MedicalHistory success: ${medicalHistoryResult.isSuccess}")

                _healthReport.value = healthReportResult.getOrNull().also {
                    Log.d(TAG, "HealthReport value set: ${it != null}")
                }
                _medicalHistory.value = medicalHistoryResult.getOrNull().also {
                    Log.d(TAG, "MedicalHistory value set: ${it != null}")
                }

                if (_healthReport.value == null || _medicalHistory.value == null) {
                    val errorMsg = "Failed to load health data - " +
                            "HealthReport: ${healthReportResult.exceptionOrNull()?.message}, " +
                            "MedicalHistory: ${medicalHistoryResult.exceptionOrNull()?.message}"
                    Log.e(TAG, errorMsg)
                    _error.value = errorMsg
                } else {
                    Log.d(TAG, "Data loaded successfully, triggering UI update")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in fetchHealthData: ${e.message}", e)
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
                Log.d(TAG, "Fetch operation completed")
            }
        }
    }

    // Fetch ML prediction separately
    fun fetchMLPrediction(userId: String) {
        viewModelScope.launch {
            try {
                val result = repository.getMlPrediction(userId)
                _mlPrediction.value = result.getOrNull()

                if (result.isFailure) {
                    _error.value = "ML prediction failed: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Error getting ML prediction: ${e.message}"
            }
        }
    }

    // Fetch all data including ML prediction
    fun fetchAllData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Fetch health data first
                val healthReportDeferred = async { repository.getHealthReport(userId) }
                val medicalHistoryDeferred = async { repository.getMedicalHistory(userId) }
                val mlPredictionDeferred = async { repository.getMlPrediction(userId) }

                _healthReport.value = healthReportDeferred.await().getOrNull()
                _medicalHistory.value = medicalHistoryDeferred.await().getOrNull()
                _mlPrediction.value = mlPredictionDeferred.await().getOrNull()

                if (_healthReport.value == null || _medicalHistory.value == null) {
                    _error.value = "Failed to load health data"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Clear error state (matches MedicalHistoryViewModel's clearError)
    fun clearError() {
        _error.value = null
    }

    // Debug function to print current state (similar to getCurrentPersonalInfo)
    fun getCurrentReport(): HealthReport? {
        return _healthReport.value?.also { report ->
            println("$TAG: Current report state: $report")
        }
    }

    // Get current ML prediction
    fun getCurrentMLPrediction(): ReportRepository.RiskPrediction? {
        return _mlPrediction.value?.also { prediction ->
            println("$TAG: Current ML prediction: $prediction")
        }
    }

    // Check if ML features are available
    fun isMLReady(): Boolean {
        return _isModelReady.value == true
    }
}