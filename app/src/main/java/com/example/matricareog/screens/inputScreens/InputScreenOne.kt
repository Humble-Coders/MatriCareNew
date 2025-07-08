package com.example.matricareog.screens.inputScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matricareog.model.PersonalInformation
import com.example.matricareog.viewmodels.MedicalHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreenOne(
    userId: String,
    navigateBack: () -> Unit = {},
    navigateToScreenTwo: () -> Unit = {},
    viewModel: MedicalHistoryViewModel
) {
    val isLoading by viewModel.isLoading.observeAsState(false)
    val pinkColor = Color(0xFFFF6B9B)

    // Form states with real-time validation
    var age by remember { mutableStateOf("") }
    var ageError by remember { mutableStateOf<String?>(null) }

    var systolicBP by remember { mutableStateOf("") }
    var systolicError by remember { mutableStateOf<String?>(null) }

    var diastolicBP by remember { mutableStateOf("") }
    var diastolicError by remember { mutableStateOf<String?>(null) }
    var bpRelationError by remember { mutableStateOf<String?>(null) }

    var glucose by remember { mutableStateOf("") }
    var glucoseError by remember { mutableStateOf<String?>(null) }

    var bodyTemperature by remember { mutableStateOf("") }
    var temperatureError by remember { mutableStateOf<String?>(null) }

    var pulseRate by remember { mutableStateOf("") }
    var pulseError by remember { mutableStateOf<String?>(null) }

    var hemoglobinLevel by remember { mutableStateOf("") }
    var hemoglobinError by remember { mutableStateOf<String?>(null) }

    var hba1c by remember { mutableStateOf("") }
    var hba1cError by remember { mutableStateOf<String?>(null) }

    var respirationRate by remember { mutableStateOf("") }
    var respirationError by remember { mutableStateOf<String?>(null) }

    // Real-time validation functions
    fun validateAge(value: String): String? {
        val intValue = value.toIntOrNull()
        return when {
            value.isEmpty() -> null
            intValue == null -> "Please enter a valid number"
            intValue < 15 -> "Age must be at least 15 years"
            intValue > 49 -> "Age must be 49 years or less"
            else -> null
        }
    }

    fun validateSystolic(value: String): String? {
        val intValue = value.toIntOrNull()
        return when {
            value.isEmpty() -> null
            intValue == null -> "Please enter a valid number"
            intValue < 70 -> "Too low (minimum: 70 mmHg)"
            intValue > 200 -> "Too high (maximum: 200 mmHg)"
            else -> null
        }
    }

    fun validateDiastolic(value: String): String? {
        val intValue = value.toIntOrNull()
        return when {
            value.isEmpty() -> null
            intValue == null -> "Please enter a valid number"
            intValue < 40 -> "Too low (minimum: 40 mmHg)"
            intValue > 120 -> "Too high (maximum: 120 mmHg)"
            else -> null
        }
    }

    fun validateBPRelation(systolic: String, diastolic: String): String? {
        val sys = systolic.toIntOrNull()
        val dia = diastolic.toIntOrNull()
        return if (sys != null && dia != null && sys <= dia) {
            "Systolic must be higher than Diastolic"
        } else null
    }

    fun validateGlucose(value: String): String? {
        val doubleValue = value.toDoubleOrNull()
        return when {
            value.isEmpty() -> null
            doubleValue == null -> "Please enter a valid number"
            doubleValue < 50 -> "Too low (minimum: 50 mg/dL)"
            doubleValue > 400 -> "Too high (maximum: 400 mg/dL)"
            else -> null
        }
    }

    fun validateTemperature(value: String): String? {
        val doubleValue = value.toDoubleOrNull()
        return when {
            value.isEmpty() -> null
            doubleValue == null -> "Please enter a valid number"
            doubleValue < 95.0 -> "Too low (minimum: 95°F)"
            doubleValue > 107.0 -> "Too high (maximum: 107°F)"
            else -> null
        }
    }

    fun validatePulse(value: String): String? {
        val intValue = value.toIntOrNull()
        return when {
            value.isEmpty() -> null
            intValue == null -> "Please enter a valid number"
            intValue < 40 -> "Too low (minimum: 40 BPM)"
            intValue > 180 -> "Too high (maximum: 180 BPM)"
            else -> null
        }
    }

    fun validateHemoglobin(value: String): String? {
        val doubleValue = value.toDoubleOrNull()
        return when {
            value.isEmpty() -> null
            doubleValue == null -> "Please enter a valid number"
            doubleValue < 5.0 -> "Too low (minimum: 5.0 g/dL)"
            doubleValue > 20.0 -> "Too high (maximum: 20.0 g/dL)"
            else -> null
        }
    }

    fun validateHBA1C(value: String): String? {
        val doubleValue = value.toDoubleOrNull()
        return when {
            value.isEmpty() -> null
            doubleValue == null -> "Please enter a valid number"
            doubleValue < 3.0 -> "Too low (minimum: 3.0%)"
            doubleValue > 15.0 -> "Too high (maximum: 15.0%)"
            else -> null
        }
    }

    fun validateRespiration(value: String): String? {
        val intValue = value.toIntOrNull()
        return when {
            value.isEmpty() -> null
            intValue == null -> "Please enter a valid number"
            intValue < 8 -> "Too low (minimum: 8 per minute)"
            intValue > 40 -> "Too high (maximum: 40 per minute)"
            else -> null
        }
    }

    // Check if all fields are valid
    val allFieldsValid = remember(age, systolicBP, diastolicBP, glucose, bodyTemperature,
        pulseRate, hemoglobinLevel, hba1c, respirationRate,
        ageError, systolicError, diastolicError, bpRelationError,
        glucoseError, temperatureError, pulseError,
        hemoglobinError, hba1cError, respirationError) {
        age.isNotEmpty() && systolicBP.isNotEmpty() && diastolicBP.isNotEmpty() &&
                glucose.isNotEmpty() && bodyTemperature.isNotEmpty() && pulseRate.isNotEmpty() &&
                hemoglobinLevel.isNotEmpty() && hba1c.isNotEmpty() && respirationRate.isNotEmpty() &&
                ageError == null && systolicError == null && diastolicError == null &&
                bpRelationError == null && glucoseError == null && temperatureError == null &&
                pulseError == null && hemoglobinError == null && hba1cError == null && respirationError == null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Personal Information",
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
                            text = "Enter Your Medical Information",
                            color = pinkColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "All fields are validated in real-time",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                        )
                    }

                    // Age Field
                    item {
                        RealTimeValidatedTextField(
                            value = age,
                            onValueChange = {
                                age = it
                                ageError = validateAge(it)
                            },
                            label = "Age",
                            placeholder = "Enter age (15-49 years)",
                            keyboardType = KeyboardType.Number,
                            isError = ageError != null,
                            errorMessage = ageError,
                            helperText = "Range: 15-49 years",
                            pinkColor = pinkColor
                        )
                    }

                    // Systolic BP Field
                    item {
                        RealTimeValidatedTextField(
                            value = systolicBP,
                            onValueChange = {
                                systolicBP = it
                                systolicError = validateSystolic(it)
                                bpRelationError = validateBPRelation(it, diastolicBP)
                            },
                            label = "Systolic Blood Pressure",
                            placeholder = "Enter systolic BP (70-200 mmHg)",
                            keyboardType = KeyboardType.Number,
                            isError = systolicError != null || bpRelationError != null,
                            errorMessage = systolicError ?: bpRelationError,
                            helperText = "Range: 70-200 mmHg",
                            pinkColor = pinkColor
                        )
                    }

                    // Diastolic BP Field
                    item {
                        RealTimeValidatedTextField(
                            value = diastolicBP,
                            onValueChange = {
                                diastolicBP = it
                                diastolicError = validateDiastolic(it)
                                bpRelationError = validateBPRelation(systolicBP, it)
                            },
                            label = "Diastolic Blood Pressure",
                            placeholder = "Enter diastolic BP (40-120 mmHg)",
                            keyboardType = KeyboardType.Number,
                            isError = diastolicError != null || bpRelationError != null,
                            errorMessage = diastolicError ?: bpRelationError,
                            helperText = "Range: 40-120 mmHg",
                            pinkColor = pinkColor
                        )
                    }

                    // Glucose Field
                    item {
                        RealTimeValidatedTextField(
                            value = glucose,
                            onValueChange = {
                                glucose = it
                                glucoseError = validateGlucose(it)
                            },
                            label = "Random Blood Sugar (RBS)",
                            placeholder = "Enter glucose level (50-400 mg/dL)",
                            keyboardType = KeyboardType.Decimal,
                            isError = glucoseError != null,
                            errorMessage = glucoseError,
                            helperText = "Range: 50-400 mg/dL",
                            pinkColor = pinkColor
                        )
                    }

                    // Body Temperature Field
                    item {
                        RealTimeValidatedTextField(
                            value = bodyTemperature,
                            onValueChange = {
                                bodyTemperature = it
                                temperatureError = validateTemperature(it)
                            },
                            label = "Body Temperature",
                            placeholder = "Enter temperature (95-107°F)",
                            keyboardType = KeyboardType.Decimal,
                            isError = temperatureError != null,
                            errorMessage = temperatureError,
                            helperText = "Range: 95-107°F",
                            pinkColor = pinkColor
                        )
                    }

                    // Heart Rate Field
                    item {
                        RealTimeValidatedTextField(
                            value = pulseRate,
                            onValueChange = {
                                pulseRate = it
                                pulseError = validatePulse(it)
                            },
                            label = "Heart Rate (HR)",
                            placeholder = "Enter pulse rate (40-180 BPM)",
                            keyboardType = KeyboardType.Number,
                            isError = pulseError != null,
                            errorMessage = pulseError,
                            helperText = "Range: 40-180 BPM",
                            pinkColor = pinkColor
                        )
                    }

                    // Hemoglobin Field
                    item {
                        RealTimeValidatedTextField(
                            value = hemoglobinLevel,
                            onValueChange = {
                                hemoglobinLevel = it
                                hemoglobinError = validateHemoglobin(it)
                            },
                            label = "Hemoglobin Level (Hb)",
                            placeholder = "Enter hemoglobin (5-20 g/dL)",
                            keyboardType = KeyboardType.Decimal,
                            isError = hemoglobinError != null,
                            errorMessage = hemoglobinError,
                            helperText = "Range: 5-20 g/dL",
                            pinkColor = pinkColor
                        )
                    }

                    // HBA1C Field
                    item {
                        RealTimeValidatedTextField(
                            value = hba1c,
                            onValueChange = {
                                hba1c = it
                                hba1cError = validateHBA1C(it)
                            },
                            label = "HBA1C Level",
                            placeholder = "Enter HBA1C (3-15%)",
                            keyboardType = KeyboardType.Decimal,
                            isError = hba1cError != null,
                            errorMessage = hba1cError,
                            helperText = "Range: 3-15%",
                            pinkColor = pinkColor
                        )
                    }

                    // Respiration Rate Field
                    item {
                        RealTimeValidatedTextField(
                            value = respirationRate,
                            onValueChange = {
                                respirationRate = it
                                respirationError = validateRespiration(it)
                            },
                            label = "Respiration Rate (RR)",
                            placeholder = "Enter respiration rate (8-40 /min)",
                            keyboardType = KeyboardType.Number,
                            isError = respirationError != null,
                            errorMessage = respirationError,
                            helperText = "Range: 8-40 per minute",
                            pinkColor = pinkColor
                        )
                    }

                    // Continue Button
                    item {
                        Button(
                            onClick = {
                                val personalInfoObject = PersonalInformation(
                                    age = age.toInt(),
                                    systolicBloodPressure = systolicBP.toInt(),
                                    diastolicBloodPressure = diastolicBP.toInt(),
                                    glucose = glucose.toDouble(),
                                    bodyTemperature = bodyTemperature.toDouble(),
                                    pulseRate = pulseRate.toInt(),
                                    hemoglobinLevel = hemoglobinLevel.toDouble(),
                                    hba1c = hba1c.toDouble(),
                                    respirationRate = respirationRate.toInt()
                                )

                                viewModel.updatePersonalInfo(personalInfoObject)
                                navigateToScreenTwo()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (allFieldsValid) pinkColor else Color.Gray
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = allFieldsValid && !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Continue to Pregnancy History",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

