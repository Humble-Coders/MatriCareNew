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
   }