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
import com.example.matricareog.PregnancyHistory
import com.example.matricareog.viewmodels.MedicalHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalHistoryScreenTwo(
    userId: String,
    onBackPressed: () -> Unit = {},
    onContinuePressed: () -> Unit = {},
    viewModel: MedicalHistoryViewModel = viewModel()
) {
    val pinkColor = Color(0xFFEF5DA8)

    // Observe ViewModel state
    val pregnancyHistory by viewModel.pregnancyHistory.observeAsState(PregnancyHistory())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    // State for showing success message


    // Initialize form fields with existing data from ViewModel
    var numberOfPregnancies by remember { mutableStateOf(if (pregnancyHistory.numberOfPregnancies == 0) "" else pregnancyHistory.numberOfPregnancies.toString()) }
    var numberOfLiveBirths by remember { mutableStateOf(if (pregnancyHistory.numberOfLiveBirths == 0) "" else pregnancyHistory.numberOfLiveBirths.toString()) }
    var numberOfAbortions by remember { mutableStateOf(if (pregnancyHistory.numberOfAbortions == 0) "" else pregnancyHistory.numberOfAbortions.toString()) }
    var numberOfChildDeaths by remember { mutableStateOf(if (pregnancyHistory.numberOfChildDeaths == 0) "" else pregnancyHistory.numberOfChildDeaths.toString()) }
    var numberOfDeliveries by remember { mutableStateOf(if (pregnancyHistory.numberOfDeliveries == 0) "" else pregnancyHistory.numberOfDeliveries.toString()) }
    var lastDeliveryDate by remember { mutableStateOf(pregnancyHistory.lastDeliveryDate ?: "") }

    // Update form fields when pregnancyHistory changes
    LaunchedEffect(pregnancyHistory) {
        numberOfPregnancies = if (pregnancyHistory.numberOfPregnancies == 0) "" else pregnancyHistory.numberOfPregnancies.toString()
        numberOfLiveBirths = if (pregnancyHistory.numberOfLiveBirths == 0) "" else pregnancyHistory.numberOfLiveBirths.toString()
        numberOfAbortions = if (pregnancyHistory.numberOfAbortions == 0) "" else pregnancyHistory.numberOfAbortions.toString()
        numberOfChildDeaths = if (pregnancyHistory.numberOfChildDeaths == 0) "" else pregnancyHistory.numberOfChildDeaths.toString()
        numberOfDeliveries = if (pregnancyHistory.numberOfDeliveries == 0) "" else pregnancyHistory.numberOfDeliveries.toString()
        lastDeliveryDate = pregnancyHistory.lastDeliveryDate ?: ""
    }

    // Show error message if any
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // You can show a snackbar or toast here
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

                    // Form fields with ViewModel integration using OutlinedTextField
                    item {
                        OutlinedTextField(
                            value = numberOfPregnancies,
                            onValueChange = { newValue ->
                                numberOfPregnancies = newValue
                                val pregnanciesInt = newValue.toIntOrNull() ?: 0
                                val updatedPregnancyHistory = pregnancyHistory.copy(numberOfPregnancies = pregnanciesInt)
                                viewModel.updatePregnancyHistory(updatedPregnancyHistory)
                                println("Number of Pregnancies updated: $pregnanciesInt") // Debug log
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
                            value = numberOfLiveBirths,
                            onValueChange = { newValue ->
                                numberOfLiveBirths = newValue
                                val liveBirthsInt = newValue.toIntOrNull() ?: 0
                                val updatedPregnancyHistory = pregnancyHistory.copy(numberOfLiveBirths = liveBirthsInt)
                                viewModel.updatePregnancyHistory(updatedPregnancyHistory)
                                println("Number of Live Births updated: $liveBirthsInt") // Debug log
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
                            value = numberOfAbortions,
                            onValueChange = { newValue ->
                                numberOfAbortions = newValue
                                val abortionsInt = newValue.toIntOrNull() ?: 0
                                val updatedPregnancyHistory = pregnancyHistory.copy(numberOfAbortions = abortionsInt)
                                viewModel.updatePregnancyHistory(updatedPregnancyHistory)
                                println("Number of Abortions updated: $abortionsInt") // Debug log
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
                            value = numberOfChildDeaths,
                            onValueChange = { newValue ->
                                numberOfChildDeaths = newValue
                                val childDeathsInt = newValue.toIntOrNull() ?: 0
                                val updatedPregnancyHistory = pregnancyHistory.copy(numberOfChildDeaths = childDeathsInt)
                                viewModel.updatePregnancyHistory(updatedPregnancyHistory)
                                println("Number of Child Deaths updated: $childDeathsInt") // Debug log
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
                            value = numberOfDeliveries,
                            onValueChange = { newValue ->
                                numberOfDeliveries = newValue
                                val deliveriesInt = newValue.toIntOrNull() ?: 0
                                val updatedPregnancyHistory = pregnancyHistory.copy(numberOfDeliveries = deliveriesInt)
                                viewModel.updatePregnancyHistory(updatedPregnancyHistory)
                                println("Number of Deliveries updated: $deliveriesInt") // Debug log
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
                                val updatedPregnancyHistory = pregnancyHistory.copy(lastDeliveryDate = newValue)
                                viewModel.updatePregnancyHistory(updatedPregnancyHistory)
                                println("Last Delivery Date updated: $newValue") // Debug log
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
                                println("Save & Continue button clicked") // Debug log
                                println("Current pregnancyHistory: $pregnancyHistory") // Debug log

                                // Save the complete medical history
                                viewModel.saveMedicalHistory(userId)
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