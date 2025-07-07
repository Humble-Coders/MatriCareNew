package com.example.matricareog.medicalhistoryscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matricareog.PersonalInformation
import com.example.matricareog.Routes
import com.example.matricareog.viewmodels.MedicalHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun MedicalHistoryScreenOne(
    userId: String,
    navigateBack: () -> Unit = {},
    navigateToScreenTwo: () -> Unit = {},
    viewModel: MedicalHistoryViewModel // Remove = viewModel()
){
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()



    val pinkColor = Color(0xFFFF6B9B)
    val lightGrayBg = Color(0xFFF5F5F7)

    // Simple float/int mutable states for form inputs
    var age by remember { mutableFloatStateOf(0f) }
    var systolicBP by remember { mutableFloatStateOf(0f) }
    var diastolicBP by remember { mutableFloatStateOf(0f) }
    var glucose by remember { mutableFloatStateOf(0f) }
    var respirationRate by remember { mutableFloatStateOf(0f) }
    var bodyTemperature by remember { mutableFloatStateOf(0f) }
    var pulseRate by remember { mutableFloatStateOf(0f) }
    var hemoglobinLevel by remember { mutableFloatStateOf(0f) }
    var hba1cLevel by remember { mutableFloatStateOf(0f) }

    // Show error message if any
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            println("Error: $errorMessage")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Medical History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Progress indicator
                Row(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(pinkColor, RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFFFD6E5), RoundedCornerShape(4.dp))
                    )
                }

                // Form content
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Enter Values For Your Report",
                            color = pinkColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Please provide accurate information for your medical record",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                        )

                        Text(
                            text = "PERSONAL INFORMATION",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Medical input fields with OutlinedTextField
                    item {
                        OutlinedTextField(
                            value = if (age == 0f) "" else age.toInt().toString(),
                            onValueChange = { newValue ->
                                age = newValue.toFloatOrNull() ?: 0f
                            },
                            label = { Text("Age") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = pinkColor,
                                focusedLabelColor = pinkColor
                            ),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = if (systolicBP == 0f) "" else systolicBP.toInt().toString(),
                            onValueChange = { newValue ->
                                systolicBP = newValue.toFloatOrNull() ?: 0f
                            },
                            label = { Text("Systolic Blood Pressure") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = pinkColor,
                                focusedLabelColor = pinkColor
                            ),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = if (diastolicBP == 0f) "" else diastolicBP.toInt().toString(),
                            onValueChange = { newValue ->
                                diastolicBP = newValue.toFloatOrNull() ?: 0f
                            },
                            label = { Text("Diastolic Blood Pressure") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = pinkColor,
                                focusedLabelColor = pinkColor
                            ),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = if (glucose == 0f) "" else glucose.toString(),
                            onValueChange = { newValue ->
                                glucose = newValue.toFloatOrNull() ?: 0f
                            },
                            label = { Text("Glucose") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = pinkColor,
                                focusedLabelColor = pinkColor
                            ),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = if (respirationRate == 0f) "" else respirationRate.toInt().toString(),
                            onValueChange = { newValue ->
                                respirationRate = newValue.toFloatOrNull() ?: 0f
                            },
                            label = { Text("Respiration Rate") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = pinkColor,
                                focusedLabelColor = pinkColor
                            ),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = if (bodyTemperature == 0f) "" else bodyTemperature.toString(),
                            onValueChange = { newValue ->
                                bodyTemperature = newValue.toFloatOrNull() ?: 0f
                            },
                            label = { Text("Body Temperature (F)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = pinkColor,
                                focusedLabelColor = pinkColor
                            ),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = if (pulseRate == 0f) "" else pulseRate.toInt().toString(),
                            onValueChange = { newValue ->
                                pulseRate = newValue.toFloatOrNull() ?: 0f
                            },
                            label = { Text("Pulse Rate") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = pinkColor,
                                focusedLabelColor = pinkColor
                            ),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = if (hemoglobinLevel == 0f) "" else hemoglobinLevel.toString(),
                            onValueChange = { newValue ->
                                hemoglobinLevel = newValue.toFloatOrNull() ?: 0f
                            },
                            label = { Text("Hemoglobin Level") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = pinkColor,
                                focusedLabelColor = pinkColor
                            ),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = if (hba1cLevel == 0f) "" else hba1cLevel.toString(),
                            onValueChange = { newValue ->
                                hba1cLevel = newValue.toFloatOrNull() ?: 0f
                            },
                            label = { Text("HBA1C Level") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = pinkColor,
                                focusedLabelColor = pinkColor
                            ),
                            singleLine = true
                        )
                    }

                    // Button and bottom spacing
                    item {
                        Button(
                            onClick = {
                                // Create PersonalInformation object from form inputs
                                val personalInfoObject = PersonalInformation(
                                    age = age.toInt(),
                                    systolicBloodPressure = systolicBP.toInt(),
                                    diastolicBloodPressure = diastolicBP.toInt(),
                                    glucose = glucose.toDouble(),
                                    respirationRate = respirationRate.toInt(),
                                    bodyTemperature = bodyTemperature.toDouble(),
                                    pulseRate = pulseRate.toInt(),
                                    hemoglobinLevel = hemoglobinLevel.toDouble()
                                )

                                // Pass the object to viewModel to store in LiveData
                                viewModel.updatePersonalInfo(personalInfoObject)

                                // Navigate to next screen
                                navigateToScreenTwo()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = pinkColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Continue",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Add extra space at the bottom
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}