package com.example.matricareog.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.matricareog.MedicalHistory
import com.example.matricareog.PersonalInformation
import com.example.matricareog.PregnancyHistory
import com.example.matricareog.repository.MedicalHistoryRepository
import com.example.matricareog.repository.ReportRepository
import kotlinx.coroutines.launch

class MedicalHistoryViewModel(
    private val repository: MedicalHistoryRepository
) : ViewModel() {

    // State for Personal Information Screen
    private val _personalInfo = MutableLiveData(PersonalInformation())
    val personalInfo: LiveData<PersonalInformation> = _personalInfo

    // State for Pregnancy History Screen
    private val _pregnancyHistory = MutableLiveData(PregnancyHistory())
    val pregnancyHistory: LiveData<PregnancyHistory> = _pregnancyHistory

    // State for medical history list (now we can have multiple records)
    private val _medicalHistoryList = MutableLiveData<List<MedicalHistory>>()

    // Current selected medical history record
    private val _currentMedicalHistory = MutableLiveData<MedicalHistory?>()

    // Loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Success state
    private val _saveSuccess = MutableLiveData<Boolean>()

    // Success message with document ID
    private val _saveMessage = MutableLiveData<String?>()

    // State for complete data save (including ML predictions)
    private val _completeDataSaved = MutableLiveData<Boolean>()

    // Update personal information
    fun updatePersonalInfo(personalInfo: PersonalInformation) {
        _personalInfo.value = personalInfo
        println("PersonalInfo updated in ViewModel: $personalInfo") // Debug log
    }

    // Update pregnancy history
    fun updatePregnancyHistory(pregnancyHistory: PregnancyHistory) {
        _pregnancyHistory.value = pregnancyHistory
        println("PregnancyHistory updated in ViewModel: $pregnancyHistory") // Debug log
    }

    // Store medical history in LiveData (NO Firebase save - just in-memory storage)
    fun storeMedicalHistoryInLiveData(personalInfo: PersonalInformation, pregnancyHistory: PregnancyHistory) {
        _personalInfo.value = personalInfo
        _pregnancyHistory.value = pregnancyHistory

        // Debug logs
        println("Medical History stored in LiveData:")
        println("Personal Info: $personalInfo")
        println("Pregnancy History: $pregnancyHistory")

        // Set success state to indicate data is ready for ML processing
        _saveSuccess.value = true
        _saveMessage.value = "Medical history ready for analysis"
    }

    // SINGLE COMPLETE SAVE: Save everything to Firebase (PersonalInfo + PregnancyHistory + ML Predictions)
    fun saveCompleteDataToFirebase(
        userId: String,
        mlPrediction: ReportRepository.RiskPrediction?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _completeDataSaved.value = false

            try {
                // Get current values from LiveData
                val currentPersonalInfo = _personalInfo.value ?: PersonalInformation()
                val currentPregnancyHistory = _pregnancyHistory.value ?: PregnancyHistory()

                // Debug logs
                println("FINAL SAVE: Saving complete data to Firebase")
                println("Personal Info: $currentPersonalInfo")
                println("Pregnancy History: $currentPregnancyHistory")
                println("ML Prediction: $mlPrediction")

                // Save complete data including ML predictions to Firebase
                val result = repository.saveCompleteDataWithMLPredictions(
                    userId = userId,
                    personalInfo = currentPersonalInfo,
                    pregnancyHistory = currentPregnancyHistory,
                    mlPrediction = mlPrediction
                )

                result.fold(
                    onSuccess = { message ->
                        println("FINAL SAVE successful: $message")
                        _completeDataSaved.value = true
                        _saveMessage.value = message
                        _error.value = null

                        // Refresh the medical history list after saving
                    },
                    onFailure = { exception ->
                        println("FINAL SAVE failed: ${exception.message}")
                        _error.value = exception.message
                        _completeDataSaved.value = false
                    }
                )
            } catch (e: Exception) {
                println("Exception during FINAL SAVE: ${e.message}")
                _error.value = e.message
                _completeDataSaved.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }



}