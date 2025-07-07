package com.example.matricareog.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matricareog.MatriCareState
import com.example.matricareog.repository.MatriCareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MatriCareViewModel(
    private val repository: MatriCareRepository
) : ViewModel() { // Internal repository instance

    private val _uiState = MutableStateFlow<MatriCareState>(MatriCareState.Loading)
    val uiState: StateFlow<MatriCareState> = _uiState.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0 for Prediction History, 1 for Risk History
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    init {
        loadChartData()
    }

    fun loadChartData() {
        viewModelScope.launch {
            _uiState.value = MatriCareState.Loading

            repository.getChartData().collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { chartData ->
                        MatriCareState.Success(chartData)
                    },
                    onFailure = { exception ->
                        MatriCareState.Error(exception.message ?: "Unknown error occurred")
                    }
                )
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }



    fun refreshData() {
        loadChartData()
    }
}
