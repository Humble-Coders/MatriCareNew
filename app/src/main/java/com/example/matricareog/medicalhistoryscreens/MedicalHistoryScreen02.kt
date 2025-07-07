package com.example.matricareog.medicalhistoryscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matricareog.PersonalInformation
import com.example.matricareog.PregnancyHistory
import com.example.matricareog.viewmodels.MedicalHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun MedicalHistoryScreenTwo(
    userId: String,
    onBackPressed: () -> Unit = {},
    onContinuePressed: () -> Unit = {},
    viewModel: MedicalHistoryViewModel // Remove = viewModel()

) {
    val personalInfo by viewModel.personalInfo.observeAsState()
    val pinkColor = Color(0xFFEF5DA8)

    // Observe ViewModel state
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    // Simple float/int/string mutable states for form inputs
    var numberOfPregnancies by remember { mutableFloatStateOf(0f) }
    var numberOfLiveBirths by remember { mutableFloatStateOf(0f) }
    var numberOfAbortions by remember { mutableFloatStateOf(0f) }
    var numberOfChildDeaths by remember { mutableFloatStateOf(0f) }
    var numberOfDeliveries by remember { mutableFloatStateOf(0f) }
    var lastDeliveryDate by remember { mutableStateOf("") }

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
                    IconButton(onClick = onBackPressed) {
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
                            .background(Color(0xFFFFD6E5), RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(pinkColor, RoundedCornerShape(4.dp))
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
                            text = "PREGNANCY HISTORY",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Form fields with optimized state management
                    item {
                        OutlinedTextField(
                            value = if (numberOfPregnancies == 0f) "" else numberOfPregnancies.toInt().toString(),
                            onValueChange = { newValue ->
                                numberOfPregnancies = newValue.toFloatOrNull() ?: 0f
                            },
                            label = { Text("Number of Pregnancies") },
                            placeholder = { Text("Enter number") },
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
                            value = if (numberOfLiveBirths == 0f) "" else numberOfLiveBirths.toInt().toString(),
                            onValueChange = { newValue ->
                                numberOfLiveBirths = newValue.toFloatOrNull() ?: 0f
                            },
                            label = { Text("Number of Live Births") },
                            placeholder = { Text("Enter number") },
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
                            value = if (numberOfAbortions == 0f) "" else numberOfAbortions.toInt().toString(),
                            onValueChange = { newValue ->
                                numberOfAbortions = newValue.toFloatOrNull() ?: 0f
                            },
                            label = { Text("Number of Abortions") },
                            placeholder = { Text("Enter number") },
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
                            value = if (numberOfChildDeaths == 0f) "" else numberOfChildDeaths.toInt().toString(),
                            onValueChange = { newValue ->
                                numberOfChildDeaths = newValue.toFloatOrNull() ?: 0f
                            },
                            label = { Text("Number of Child Deaths") },
                            placeholder = { Text("Enter number") },
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
                            value = if (numberOfDeliveries == 0f) "" else numberOfDeliveries.toInt().toString(),
                            onValueChange = { newValue ->
                                numberOfDeliveries = newValue.toFloatOrNull() ?: 0f
                            },
                            label = { Text("Number of Deliveries") },
                            placeholder = { Text("Enter number") },
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
                            value = lastDeliveryDate,
                            onValueChange = { newValue ->
                                lastDeliveryDate = newValue
                            },
                            label = { Text("Last Delivery Date") },
                            placeholder = { Text("Enter date (e.g., DD/MM/YYYY)") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Select Date",
                                    tint = pinkColor
                                )
                            },
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
                        // Save and Continue button
                        Button(
                            onClick = {
                                // Create PregnancyHistory object from form inputs
                                val pregnancyHistoryObject = PregnancyHistory(
                                    numberOfPregnancies = numberOfPregnancies.toInt(),
                                    numberOfLiveBirths = numberOfLiveBirths.toInt(),
                                    numberOfAbortions = numberOfAbortions.toInt(),
                                    numberOfChildDeaths = numberOfChildDeaths.toInt(),
                                    numberOfDeliveries = numberOfDeliveries.toInt(),
                                    lastDeliveryDate = if (lastDeliveryDate.isBlank()) "N/A" else lastDeliveryDate
                                )
                                viewModel.updatePregnancyHistory(pregnancyHistoryObject)

                                if (personalInfo != null) {
                                    viewModel.storeMedicalHistoryInLiveData(
                                        personalInfo = personalInfo!!,
                                        pregnancyHistory = pregnancyHistoryObject
                                    )
                                    onContinuePressed()
                                }
                                else {
                                    println("Personal Info not available yet!")
                                }



                                println("Save & Continue button clicked")
                                println("Current pregnancyHistory: $pregnancyHistoryObject")

                                // Trigger saving to Firebase


                                // Navigate or do next

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
                                    "Save & Continue",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Home indicator line
                        Box(
                            modifier = Modifier
                                .padding(top = 16.dp, bottom = 8.dp)
                                .size(width = 40.dp, height = 4.dp)
                                .background(Color.Black, RoundedCornerShape(2.dp))
                                .align(Alignment.CenterHorizontally)
                        )

                        // Add extra space at the bottom
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Loading overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = pinkColor)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Saving your information...")
                        }
                    }
                }
            }
        }
    }
}