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
import com.example.matricareog.viewmodels.MedicalHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalHistoryScreenOne(
    userId: String,
    navigateBack: () -> Unit = {},
    navigateToScreenTwo: () -> Unit = {},
    viewModel: MedicalHistoryViewModel = viewModel()

) {
    val personalInfo by viewModel.personalInfo.observeAsState(PersonalInformation())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val pinkColor = Color(0xFFFF6B9B)
    val lightGrayBg = Color(0xFFF5F5F7)

    // Initialize form fields with existing data from ViewModel
    var age by remember { mutableStateOf(if (personalInfo.age == 0) "" else personalInfo.age.toString()) }
    var systolicBP by remember { mutableStateOf(if (personalInfo.systolicBloodPressure == 0) "" else personalInfo.systolicBloodPressure.toString()) }
    var diastolicBP by remember { mutableStateOf(if (personalInfo.diastolicBloodPressure == 0) "" else personalInfo.diastolicBloodPressure.toString()) }
    var glucose by remember { mutableStateOf(if (personalInfo.glucose == 0.0) "" else personalInfo.glucose.toString()) }
    var respirationRate by remember { mutableStateOf(if (personalInfo.respirationRate == 0) "" else personalInfo.respirationRate.toString()) }
    var bodyTemperature by remember { mutableStateOf(if (personalInfo.bodyTemperature == 0.0) "" else personalInfo.bodyTemperature.toString()) }
    var pulseRate by remember { mutableStateOf(if (personalInfo.pulseRate == 0) "" else personalInfo.pulseRate.toString()) }
    var hemoglobinLevel by remember { mutableStateOf(if (personalInfo.hemoglobinLevel == 0.0) "" else personalInfo.hemoglobinLevel.toString()) }

    // Update form fields when personalInfo changes
    LaunchedEffect(personalInfo) {
        age = if (personalInfo.age == 0) "" else personalInfo.age.toString()
        systolicBP = if (personalInfo.systolicBloodPressure == 0) "" else personalInfo.systolicBloodPressure.toString()
        diastolicBP = if (personalInfo.diastolicBloodPressure == 0) "" else personalInfo.diastolicBloodPressure.toString()
        glucose = if (personalInfo.glucose == 0.0) "" else personalInfo.glucose.toString()
        respirationRate = if (personalInfo.respirationRate == 0) "" else personalInfo.respirationRate.toString()
        bodyTemperature = if (personalInfo.bodyTemperature == 0.0) "" else personalInfo.bodyTemperature.toString()
        pulseRate = if (personalInfo.pulseRate == 0) "" else personalInfo.pulseRate.toString()
        hemoglobinLevel = if (personalInfo.hemoglobinLevel == 0.0) "" else personalInfo.hemoglobinLevel.toString()
    }

    // Show error message if any
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // You can show a snackbar or toast here
            // For now, we'll just print it (you can replace with proper error handling)
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
                            value = age,
                            onValueChange = { newValue ->
                                age = newValue
                                val ageInt = newValue.toIntOrNull() ?: 0
                                val updatedPersonalInfo = personalInfo.copy(age = ageInt)
                                viewModel.updatePersonalInfo(updatedPersonalInfo)
                                println("Age updated: $ageInt") // Debug log
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
                            value = systolicBP,
                            onValueChange = { newValue ->
                                systolicBP = newValue
                                val systolicInt = newValue.toIntOrNull() ?: 0
                                val updatedPersonalInfo = personalInfo.copy(systolicBloodPressure = systolicInt)
                                viewModel.updatePersonalInfo(updatedPersonalInfo)
                                println("Systolic BP updated: $systolicInt") // Debug log
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
                            value = diastolicBP,
                            onValueChange = { newValue ->
                                diastolicBP = newValue
                                val diastolicInt = newValue.toIntOrNull() ?: 0
                                val updatedPersonalInfo = personalInfo.copy(diastolicBloodPressure = diastolicInt)
                                viewModel.updatePersonalInfo(updatedPersonalInfo)
                                println("Diastolic BP updated: $diastolicInt") // Debug log
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
                            value = glucose,
                            onValueChange = { newValue ->
                                glucose = newValue
                                val glucoseDouble = newValue.toDoubleOrNull() ?: 0.0
                                val updatedPersonalInfo = personalInfo.copy(glucose = glucoseDouble)
                                viewModel.updatePersonalInfo(updatedPersonalInfo)
                                println("Glucose updated: $glucoseDouble") // Debug log
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
                            value = respirationRate,
                            onValueChange = { newValue ->
                                respirationRate = newValue
                                val respirationInt = newValue.toIntOrNull() ?: 0
                                val updatedPersonalInfo = personalInfo.copy(respirationRate = respirationInt)
                                viewModel.updatePersonalInfo(updatedPersonalInfo)
                                println("Respiration Rate updated: $respirationInt") // Debug log
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
                            value = bodyTemperature,
                            onValueChange = { newValue ->
                                bodyTemperature = newValue
                                val tempDouble = newValue.toDoubleOrNull() ?: 0.0
                                val updatedPersonalInfo = personalInfo.copy(bodyTemperature = tempDouble)
                                viewModel.updatePersonalInfo(updatedPersonalInfo)
                                println("Body Temperature updated: $tempDouble") // Debug log
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
                            value = pulseRate,
                            onValueChange = { newValue ->
                                pulseRate = newValue
                                val pulseInt = newValue.toIntOrNull() ?: 0
                                val updatedPersonalInfo = personalInfo.copy(pulseRate = pulseInt)
                                viewModel.updatePersonalInfo(updatedPersonalInfo)
                                println("Pulse Rate updated: $pulseInt") // Debug log
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
                            value = hemoglobinLevel,
                            onValueChange = { newValue ->
                                hemoglobinLevel = newValue
                                val hemoglobinDouble = newValue.toDoubleOrNull() ?: 0.0
                                val updatedPersonalInfo = personalInfo.copy(hemoglobinLevel = hemoglobinDouble)
                                viewModel.updatePersonalInfo(updatedPersonalInfo)
                                println("Hemoglobin Level updated: $hemoglobinDouble") // Debug log
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

                    // Button and bottom spacing
                    item {
                        // Save immediately and then navigate
                        Button(
                            onClick = {
                                println("Continue button clicked") // Debug log
                                println("Current personalInfo: $personalInfo") // Debug log

                                // Save personal information before navigating
                                if (viewModel.isPersonalInfoValid()) {
                                    viewModel.savePersonalInfo(userId) {
                                        // Navigate after successful save

                                        navigateToScreenTwo()
                                    }
                                } else {
                                    // Show validation error
                                    println("Personal info validation failed")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = pinkColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading // Disable button when loading
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

//            // Loading overlay
//            if (isLoading) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(Color.Black.copy(alpha = 0.3f)),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Card(
//                        modifier = Modifier.padding(32.dp),
//                        colors = CardDefaults.cardColors(containerColor = Color.White)
//                    ) {
//                        Column(
//                            modifier = Modifier.padding(24.dp),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            CircularProgressIndicator(color = pinkColor)
//                            Spacer(modifier = Modifier.height(16.dp))
//                            Text("Saving your information...")
//                        }
//                    }
//                }
//            }
        }
    }
}