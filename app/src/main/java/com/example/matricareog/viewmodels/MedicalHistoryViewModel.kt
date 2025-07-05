package com.example.matricareog.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.matricareog.MedicalHistory
import com.example.matricareog.PersonalInformation
import com.example.matricareog.PregnancyHistory
import com.example.matricareog.repository.MedicalHistoryRepository
import kotlinx.coroutines.launch

class MedicalHistoryViewModel : ViewModel() {
    private val repository = MedicalHistoryRepository()

    // State for Personal Information Screen
    private val _personalInfo = MutableLiveData(PersonalInformation())
    val personalInfo: LiveData<PersonalInformation> = _personalInfo

    // State for Pregnancy History Screen
    private val _pregnancyHistory = MutableLiveData(PregnancyHistory())
    val pregnancyHistory: LiveData<PregnancyHistory> = _pregnancyHistory

    // State for medical history list (now we can have multiple records)
    private val _medicalHistoryList = MutableLiveData<List<MedicalHistory>>()
    val medicalHistoryList: LiveData<List<MedicalHistory>> = _medicalHistoryList

    // Current selected medical history record
    private val _currentMedicalHistory = MutableLiveData<MedicalHistory?>()
    val currentMedicalHistory: LiveData<MedicalHistory?> = _currentMedicalHistory

    // Loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Success state
    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    // Success message with document ID
    private val _saveMessage = MutableLiveData<String?>()
    val saveMessage: LiveData<String?> = _saveMessage

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

    // Save personal information only (for Screen One)
    fun savePersonalInfo(userId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val personalInfo = _personalInfo.value ?: PersonalInformation()
                val pregnancyHistory = _pregnancyHistory.value ?: PregnancyHistory()

                println("Saving personal info for user: $userId") // Debug log
                println("Personal Info to save: $personalInfo") // Debug log

                val result = repository.saveMedicalHistory(userId, personalInfo, pregnancyHistory)

                result.onSuccess { message ->
                    _error.value = null
                    _saveSuccess.value = true
                    _saveMessage.value = message
                    println("Personal info save successful: $message") // Debug log

                    // Refresh the medical history list after saving
                    loadMedicalHistoryList(userId)
                    onSuccess() // Call the success callback
                }.onFailure { exception ->
                    _error.value = exception.message
                    _saveSuccess.value = false
                    println("Personal info save failed: ${exception.message}") // Debug log
                }

            } catch (e: Exception) {
                _error.value = e.message
                _saveSuccess.value = false
                println("Exception during personal info save: ${e.message}") // Debug log
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Save complete medical history (for Screen Two)
    fun saveMedicalHistory(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Get current values from LiveData
                val currentPersonalInfo = _personalInfo.value ?: PersonalInformation()
                val currentPregnancyHistory = _pregnancyHistory.value ?: PregnancyHistory()

                // Debug logs
                println("Saving Personal Info: $currentPersonalInfo")
                println("Saving Pregnancy History: $currentPregnancyHistory")

                // Save both personal info and pregnancy history
                val result = repository.saveMedicalHistory(
                    userId = userId,
                    personalInfo = currentPersonalInfo,
                    pregnancyHistory = currentPregnancyHistory
                )

                result.fold(
                    onSuccess = { message ->
                        println("Save successful: $message")
                        _saveSuccess.value = true
                        _saveMessage.value = message
                        _error.value = null

                        // Refresh the medical history list after saving
                        loadMedicalHistoryList(userId)
                    },
                    onFailure = { exception ->
                        println("Save failed: ${exception.message}")
                        _error.value = exception.message
                        _saveSuccess.value = false
                    }
                )
            } catch (e: Exception) {
                println("Exception during save: ${e.message}")
                _error.value = e.message
                _saveSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Load existing medical history list for a user
    fun loadMedicalHistoryList(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val result = repository.getMedicalHistoryOrderedByDate(userId)

                result.onSuccess { medicalHistoryList ->
                    _medicalHistoryList.value = medicalHistoryList

                    // Set the most recent record as current
                    val latestRecord = medicalHistoryList.firstOrNull()
                    _currentMedicalHistory.value = latestRecord

                    // Update the form fields with the latest record
                    latestRecord?.let {
                        _personalInfo.value = it.personalInformation
                        _pregnancyHistory.value = it.pregnancyHistory
                        println("Loaded latest medical history: $it") // Debug log
                    }

                    _error.value = null
                }.onFailure { exception ->
                    _error.value = exception.message
                    _medicalHistoryList.value = emptyList()
                    println("Load failed: ${exception.message}") // Debug log
                }

            } catch (e: Exception) {
                _error.value = e.message
                _medicalHistoryList.value = emptyList()
                println("Exception during load: ${e.message}") // Debug log
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Load existing medical history (backward compatibility)
    fun loadMedicalHistory(userId: String) {
        loadMedicalHistoryList(userId)
    }

    // Load specific medical history by document ID
    fun loadMedicalHistoryById(documentId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val result = repository.getMedicalHistoryById(documentId)

                result.onSuccess { medicalHistory ->
                    _currentMedicalHistory.value = medicalHistory

                    medicalHistory?.let {
                        _personalInfo.value = it.personalInformation
                        _pregnancyHistory.value = it.pregnancyHistory
                        println("Loaded medical history by ID: $it") // Debug log
                    }
                    _error.value = null
                }.onFailure { exception ->
                    _error.value = exception.message
                    println("Load by ID failed: ${exception.message}") // Debug log
                }

            } catch (e: Exception) {
                _error.value = e.message
                println("Exception during load by ID: ${e.message}") // Debug log
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Update existing medical history record
    fun updateMedicalHistoryRecord(documentId: String, updates: Map<String, Any>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val result = repository.updateMedicalHistory(documentId, updates)

                result.onSuccess { message ->
                    _saveSuccess.value = true
                    _saveMessage.value = message
                    _error.value = null

                    // Refresh the specific record
                    loadMedicalHistoryById(documentId)
                    println("Update successful: $message") // Debug log
                }.onFailure { exception ->
                    _error.value = exception.message
                    _saveSuccess.value = false
                    println("Update failed: ${exception.message}") // Debug log
                }

            } catch (e: Exception) {
                _error.value = e.message
                _saveSuccess.value = false
                println("Exception during update: ${e.message}") // Debug log
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Delete medical history record
    fun deleteMedicalHistoryRecord(documentId: String, userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val result = repository.deleteMedicalHistory(documentId)

                result.onSuccess { message ->
                    _saveSuccess.value = true
                    _saveMessage.value = message
                    _error.value = null

                    // Refresh the list after deletion
                    loadMedicalHistoryList(userId)
                    println("Delete successful: $message") // Debug log
                }.onFailure { exception ->
                    _error.value = exception.message
                    _saveSuccess.value = false
                    println("Delete failed: ${exception.message}") // Debug log
                }

            } catch (e: Exception) {
                _error.value = e.message
                _saveSuccess.value = false
                println("Exception during delete: ${e.message}") // Debug log
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Setup real-time updates for medical history
    fun setupRealtimeUpdates(userId: String) {
        repository.getMedicalHistoryUpdates(userId) { medicalHistoryList ->
            _medicalHistoryList.value = medicalHistoryList

            // Update current record with the latest one
            val latestRecord = medicalHistoryList.firstOrNull()
            _currentMedicalHistory.value = latestRecord

            // Update form fields with latest record
            latestRecord?.let {
                _personalInfo.value = it.personalInformation
                _pregnancyHistory.value = it.pregnancyHistory
            }
        }
    }

    // Validate personal information
    fun isPersonalInfoValid(): Boolean {
        val personalInfo = _personalInfo.value ?: return false

        // Basic validation - at least one field should be filled
        return personalInfo.age > 0 ||
                personalInfo.systolicBloodPressure > 0 ||
                personalInfo.diastolicBloodPressure > 0 ||
                personalInfo.glucose > 0.0 ||
                personalInfo.respirationRate > 0 ||
                personalInfo.bodyTemperature > 0.0 ||
                personalInfo.pulseRate > 0 ||
                personalInfo.hemoglobinLevel > 0.0
    }

    // Validate pregnancy history
    fun isPregnancyHistoryValid(): Boolean {
        val pregnancyHistory = _pregnancyHistory.value ?: return false

        // Basic validation - at least one field should be filled
        return pregnancyHistory.numberOfPregnancies >= 0 ||
                pregnancyHistory.numberOfLiveBirths >= 0 ||
                pregnancyHistory.numberOfAbortions >= 0 ||
                pregnancyHistory.numberOfChildDeaths >= 0 ||
                pregnancyHistory.numberOfDeliveries >= 0 ||
                !pregnancyHistory.lastDeliveryDate.isNullOrEmpty()
    }

    // Validate complete data
    fun isDataValid(): Boolean {
        return isPersonalInfoValid() || isPregnancyHistoryValid()
    }

    // Clear error state
    fun clearError() {
        _error.value = null
    }

    // Clear success state
    fun clearSuccess() {
        _saveSuccess.value = false
        _saveMessage.value = null
    }

    // Get current personal info values (for debugging)
    fun getCurrentPersonalInfo(): PersonalInformation {
        return _personalInfo.value ?: PersonalInformation()
    }

    // Get current pregnancy history values (for debugging)
    fun getCurrentPregnancyHistory(): PregnancyHistory {
        return _pregnancyHistory.value ?: PregnancyHistory()
    }

    // Helper functions for medical history management
    fun getMedicalHistoryCount(): Int {
        return _medicalHistoryList.value?.size ?: 0
    }

    fun getLatestMedicalHistory(): MedicalHistory? {
        return _medicalHistoryList.value?.maxByOrNull { it.createdAt }
    }

    fun getAllMedicalHistory(): List<MedicalHistory> {
        return _medicalHistoryList.value ?: emptyList()
    }

    // Select a specific medical history record to edit
    fun selectMedicalHistoryRecord(medicalHistory: MedicalHistory) {
        _currentMedicalHistory.value = medicalHistory
        _personalInfo.value = medicalHistory.personalInformation
        _pregnancyHistory.value = medicalHistory.pregnancyHistory
    }

    // Clear form data
    fun clearFormData() {
        _personalInfo.value = PersonalInformation()
        _pregnancyHistory.value = PregnancyHistory()
        _currentMedicalHistory.value = null
    }
}