package com.example.matricareog.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.matricareog.model.PersonalInformation
import com.example.matricareog.model.PregnancyHistory
import com.example.matricareog.repository.MedicalHistoryRepository
import com.example.matricareog.repository.ReportRepository
import kotlinx.coroutines.launch

class MedicalHistoryViewModel(
    private val repository: MedicalHistoryRepository
) : ViewModel() {

    // State for Personal Information Screen (8 parameters from research)
    private val _personalInfo = MutableLiveData(PersonalInformation())
    val personalInfo: LiveData<PersonalInformation> = _personalInfo

    // State for Pregnancy History Screen (5 parameters from research)
    private val _pregnancyHistory = MutableLiveData(PregnancyHistory())
    val pregnancyHistory: LiveData<PregnancyHistory> = _pregnancyHistory

    // Loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Success message with document ID
    private val _saveMessage = MutableLiveData<String?>()
    val saveMessage: LiveData<String?> = _saveMessage

    // State for complete data save (including ML predictions)
    private val _completeDataSaved = MutableLiveData<Boolean>()
    val completeDataSaved: LiveData<Boolean> = _completeDataSaved

    // Validation states for each screen
    private val _personalInfoValidation = MutableLiveData<Map<String, String>>()
    val personalInfoValidation: LiveData<Map<String, String>> = _personalInfoValidation

    private val _pregnancyHistoryValidation = MutableLiveData<Map<String, String>>()
    val pregnancyHistoryValidation: LiveData<Map<String, String>> = _pregnancyHistoryValidation

    // Update personal information with validation
    fun updatePersonalInfo(personalInfo: PersonalInformation) {
        val validationErrors = validatePersonalInfo(personalInfo)
        _personalInfoValidation.value = validationErrors

        if (validationErrors.isEmpty()) {
            _personalInfo.value = personalInfo
            _error.value = null
            println("PersonalInfo updated and validated in ViewModel: $personalInfo")
        } else {
            _error.value = "Please correct the validation errors"
            println("PersonalInfo validation failed: $validationErrors")
        }
    }

    // Update pregnancy history with validation
    fun updatePregnancyHistory(pregnancyHistory: PregnancyHistory) {
        val validationErrors = validatePregnancyHistory(pregnancyHistory)
        _pregnancyHistoryValidation.value = validationErrors

        if (validationErrors.isEmpty()) {
            _pregnancyHistory.value = pregnancyHistory
            _error.value = null
            println("PregnancyHistory updated and validated in ViewModel: $pregnancyHistory")
        } else {
            _error.value = "Please correct the validation errors"
            println("PregnancyHistory validation failed: $validationErrors")
        }
    }

    // Validate Personal Information based on research paper ranges
    private fun validatePersonalInfo(personalInfo: PersonalInformation): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        // Age validation (15-49 years as per research)
        if (personalInfo.age < 15 || personalInfo.age > 49) {
            errors["age"] = "Age must be between 15-49 years"
        }

        // Systolic Blood Pressure validation (70-200 mmHg)
        if (personalInfo.systolicBloodPressure < 70 || personalInfo.systolicBloodPressure > 200) {
            errors["systolicBP"] = "Systolic BP must be between 70-200 mmHg"
        }

        // Diastolic Blood Pressure validation (40-120 mmHg)
        if (personalInfo.diastolicBloodPressure < 40 || personalInfo.diastolicBloodPressure > 120) {
            errors["diastolicBP"] = "Diastolic BP must be between 40-120 mmHg"
        }

        // Blood Pressure relationship validation
        if (personalInfo.systolicBloodPressure <= personalInfo.diastolicBloodPressure) {
            errors["bloodPressure"] = "Systolic BP must be higher than Diastolic BP"
        }

        // Random Blood Sugar validation (50-400 mg/dL)
        if (personalInfo.glucose < 50 || personalInfo.glucose > 400) {
            errors["glucose"] = "Blood glucose must be between 50-400 mg/dL"
        }

        // Body Temperature validation (95-107°F)
        if (personalInfo.bodyTemperature < 95.0 || personalInfo.bodyTemperature > 107.0) {
            errors["bodyTemperature"] = "Body temperature must be between 95-107°F"
        }

        // Heart Rate validation (40-180 BPM)
        if (personalInfo.pulseRate < 40 || personalInfo.pulseRate > 180) {
            errors["pulseRate"] = "Heart rate must be between 40-180 BPM"
        }

        // Hemoglobin validation (5-20 g/dL)
        if (personalInfo.hemoglobinLevel < 5.0 || personalInfo.hemoglobinLevel > 20.0) {
            errors["hemoglobin"] = "Hemoglobin must be between 5-20 g/dL"
        }

        // Respiration Rate validation (8-40 per minute)
        if (personalInfo.respirationRate < 8 || personalInfo.respirationRate > 40) {
            errors["respirationRate"] = "Respiration rate must be between 8-40 per minute"
        }

        return errors
    }

    // Validate Pregnancy History based on research paper
    private fun validatePregnancyHistory(pregnancyHistory: PregnancyHistory): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        // Gravida validation (0-20, can be 0 for first pregnancy)
        if (pregnancyHistory.gravida < 0 || pregnancyHistory.gravida > 20) {
            errors["gravida"] = "Gravida must be between 0-20"
        }

        // Para validation (0-15, can be 0)
        if (pregnancyHistory.para < 0 || pregnancyHistory.para > 15) {
            errors["para"] = "Para must be between 0-15"
        }

        // Live births validation (0-15, can be 0)
        if (pregnancyHistory.liveBirths < 0 || pregnancyHistory.liveBirths > 15) {
            errors["liveBirths"] = "Live births must be between 0-15"
        }

        // Abortions validation (0-10, can be 0)
        if (pregnancyHistory.abortions < 0 || pregnancyHistory.abortions > 10) {
            errors["abortions"] = "Abortions must be between 0-10"
        }

        // Child deaths validation (0-10, can be 0)
        if (pregnancyHistory.childDeaths < 0 || pregnancyHistory.childDeaths > 10) {
            errors["childDeaths"] = "Child deaths must be between 0-10"
        }

        // Logical relationship validations
        if (pregnancyHistory.liveBirths > pregnancyHistory.para) {
            errors["liveBirthsPara"] = "Live births cannot exceed Para"
        }

        if (pregnancyHistory.childDeaths > pregnancyHistory.liveBirths) {
            errors["childDeathsLiveBirths"] = "Child deaths cannot exceed live births"
        }

        if ((pregnancyHistory.para + pregnancyHistory.abortions) > pregnancyHistory.gravida) {
            errors["totalPregnancies"] = "Para + Abortions cannot exceed Gravida"
        }

        return errors
    }

    // Store medical history in LiveData (NO Firebase save - just in-memory storage)
    fun storeMedicalHistoryInLiveData(personalInfo: PersonalInformation, pregnancyHistory: PregnancyHistory) {
        // Validate both before storing
        val personalInfoErrors = validatePersonalInfo(personalInfo)
        val pregnancyHistoryErrors = validatePregnancyHistory(pregnancyHistory)

        if (personalInfoErrors.isEmpty() && pregnancyHistoryErrors.isEmpty()) {
            _personalInfo.value = personalInfo
            _pregnancyHistory.value = pregnancyHistory
            _error.value = null
            _saveMessage.value = "Medical history ready for analysis"

            println("Medical History stored in LiveData and validated:")
            println("Personal Info: $personalInfo")
            println("Pregnancy History: $pregnancyHistory")
        } else {
            val allErrors = personalInfoErrors + pregnancyHistoryErrors
            _error.value = "Validation failed: ${allErrors.values.first()}"
            println("Validation failed: $allErrors")
        }
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

                // Final validation before saving
                val personalInfoErrors = validatePersonalInfo(currentPersonalInfo)
                val pregnancyHistoryErrors = validatePregnancyHistory(currentPregnancyHistory)

                if (personalInfoErrors.isNotEmpty() || pregnancyHistoryErrors.isNotEmpty()) {
                    val allErrors = personalInfoErrors + pregnancyHistoryErrors
                    _error.value = "Cannot save: ${allErrors.values.first()}"
                    return@launch
                }

                println("FINAL SAVE: Saving complete validated data to Firebase")
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

    // Clear error state
    fun clearError() {
        _error.value = null
    }

    // Clear validation errors
    fun clearValidationErrors() {
        _personalInfoValidation.value = emptyMap()
        _pregnancyHistoryValidation.value = emptyMap()
    }

    // Check if current data is valid for proceeding
    fun isPersonalInfoValid(): Boolean {
        val current = _personalInfo.value ?: PersonalInformation()
        return validatePersonalInfo(current).isEmpty()
    }

    fun isPregnancyHistoryValid(): Boolean {
        val current = _pregnancyHistory.value ?: PregnancyHistory()
        return validatePregnancyHistory(current).isEmpty()
    }
}