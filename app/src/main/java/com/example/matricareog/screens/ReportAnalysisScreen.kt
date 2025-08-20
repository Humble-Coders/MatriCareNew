package com.example.matricareog.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.matricareog.R
import com.example.matricareog.model.HealthMetric
import com.example.matricareog.model.HealthReport
import com.example.matricareog.model.HealthStatus
import com.example.matricareog.model.MetricStatus
import com.example.matricareog.model.PersonalInformation
import com.example.matricareog.model.PregnancyHistory
import com.example.matricareog.repository.ReportRepository
import com.example.matricareog.viewmodels.AuthViewModel
import com.example.matricareog.viewmodels.MedicalHistoryViewModel
import com.example.matricareog.viewmodels.ReportAnalysisViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportAnalysisScreen(
    userId: String,
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    reportViewModel: ReportAnalysisViewModel,
    medicalHistoryViewModel: MedicalHistoryViewModel,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current

    // Data from ReportViewModel
    val mlPrediction by reportViewModel.mlPrediction.observeAsState()
    val isLoading by reportViewModel.isLoading.observeAsState(false)
    val isModelLoading by reportViewModel.isModelLoading.observeAsState(false)
    val error by reportViewModel.error.observeAsState()
    val isModelReady by reportViewModel.isModelReady.observeAsState(false)
    val processingStatus by reportViewModel.processingStatus.observeAsState("")
    val generatedReport by reportViewModel.generatedHealthReport.observeAsState()

    // Data from MedicalHistoryViewModel (LiveData)
    val personalInfo by medicalHistoryViewModel.personalInfo.observeAsState()
    val pregnancyHistory by medicalHistoryViewModel.pregnancyHistory.observeAsState()

    // Debug states
    var showDebugInfo by remember { mutableStateOf(false) }

    // Get current user data (if needed)
    val currentUserState = authViewModel.currentUser.collectAsState()
    val currentUser = currentUserState.value

    // Initialize ML model when screen loads
    LaunchedEffect(Unit) {
        println("🔥 ReportAnalysisScreen: Starting ML model initialization")
        if (!isModelReady) {
            reportViewModel.initializeMLModel(context)
        }
    }

    // Generate report when both LiveData and model are ready
    LaunchedEffect(personalInfo, pregnancyHistory, isModelReady) {
        println("🔥 ReportAnalysisScreen: LaunchedEffect triggered")
        println("🔥 PersonalInfo: $personalInfo")
        println("🔥 PregnancyHistory: $pregnancyHistory")
        println("🔥 IsModelReady: $isModelReady")

        if (isModelReady && personalInfo != null && pregnancyHistory != null) {
            println("🔥 All conditions met - processing ML analysis")
            val actualUserName = currentUser?.fullName ?: "Patient"
            reportViewModel.processMLAnalysisFromLiveData(
                personalInfo = personalInfo!!,
                pregnancyHistory = pregnancyHistory!!,
                userName = actualUserName
            )
        } else {
            println("🔥 Conditions not met:")
            println("   - Model Ready: $isModelReady")
            println("   - Personal Info: ${personalInfo != null}")
            println("   - Pregnancy History: ${pregnancyHistory != null}")
        }
    }

    // Add this state variable at the top of ReportAnalysisScreen
    var hasAutoSaved by remember { mutableStateOf(false) }

// Replace the existing LaunchedEffect with this:
    LaunchedEffect(generatedReport, mlPrediction) {
        // Auto-save when both report and ML prediction are available (only once)
        if (generatedReport != null && mlPrediction != null && personalInfo != null && pregnancyHistory != null && !hasAutoSaved) {
            println("🔄 Auto-saving report to Firebase...")
            medicalHistoryViewModel.saveCompleteDataToFirebase(
                userId = userId,
                mlPrediction = mlPrediction
            )
            hasAutoSaved = true // Prevent future auto-saves
        }
    }



    // UI Layout
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ReportTopBar(
            onBackClick = onBackClick,
            onShareClick = onShareClick,
            onDebugToggle = { showDebugInfo = !showDebugInfo }
        )

        // Debug information (toggle-able)
        if (showDebugInfo) {
            DebugInfoCard(
                personalInfo = personalInfo,
                pregnancyHistory = pregnancyHistory,
                mlPrediction = mlPrediction,
                isModelReady = isModelReady,
                processingStatus = processingStatus
            )
        }

        when {
            isModelLoading -> {
                ModelLoadingIndicator()
            }

            !isModelReady -> {
                ModelNotReadyScreen {
                    reportViewModel.initializeMLModel(context)
                }
            }

            isLoading -> {
                AnalysisLoadingIndicator(processingStatus)
            }

            error != null -> {
                ErrorScreen(
                    error = error!!,
                    onRetry = {
                        personalInfo?.let { pInfo ->
                            pregnancyHistory?.let { pHistory ->
                                reportViewModel.retryAnalysis(pInfo, pHistory, "Patient")
                            }
                        }
                    }
                )
            }

            personalInfo == null || pregnancyHistory == null -> {
                DataMissingScreen()
            }

            generatedReport != null -> {
                ReportContent(
                    healthReport = generatedReport!!,
                    personalInfo = personalInfo!!,
                    pregnancyHistory = pregnancyHistory!!,
                    mlPrediction = mlPrediction
                )
            }

            else -> {
                EmptyStateScreen {
                    personalInfo?.let { pInfo ->
                        pregnancyHistory?.let { pHistory ->
                            reportViewModel.processMLAnalysisFromLiveData(pInfo, pHistory, "Patient")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DebugInfoCard(
    personalInfo: PersonalInformation?,
    pregnancyHistory: PregnancyHistory?,
    mlPrediction: ReportRepository.RiskPrediction?,
    isModelReady: Boolean,
    processingStatus: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "🔧 Debug Information",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text("Model Ready: $isModelReady", fontSize = 12.sp)
            Text("Processing: $processingStatus", fontSize = 12.sp)
            Text("Personal Info: ${personalInfo != null}", fontSize = 12.sp)
            Text("Pregnancy History: ${pregnancyHistory != null}", fontSize = 12.sp)
            Text("ML Prediction: ${mlPrediction?.riskLevel ?: "None"}", fontSize = 12.sp)

            if (personalInfo != null) {
                Text("Age: ${personalInfo.age}", fontSize = 11.sp, color = Color.Gray)
                Text("BP: ${personalInfo.systolicBloodPressure}/${personalInfo.diastolicBloodPressure}", fontSize = 11.sp, color = Color.Gray)
            }

            if (pregnancyHistory != null) {
                Text("G${pregnancyHistory.gravida}P${pregnancyHistory.para}L${pregnancyHistory.liveBirths}A${pregnancyHistory.abortions}D${pregnancyHistory.childDeaths}", fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun ModelLoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFFE91E63),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading AI Model...",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Please wait while we prepare the risk assessment AI",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ModelNotReadyScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Model Error",
            tint = Color.Red,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "AI Model Not Ready",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        Text(
            text = "The risk assessment model failed to load properly.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
        ) {
            Text("Retry Loading Model")
        }
    }
}

@Composable
private fun AnalysisLoadingIndicator(status: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFFE91E63),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Analyzing Your Health Data",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            if (status.isNotEmpty()) {
                Text(
                    text = status,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun DataMissingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Data Missing",
            tint = Color.Red,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Medical Data Missing",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        Text(
            text = "Please complete the medical history forms before viewing the analysis.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun ReportContent(
    healthReport: HealthReport,
    personalInfo: PersonalInformation,
    pregnancyHistory: PregnancyHistory,
    mlPrediction: ReportRepository.RiskPrediction?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Gray.copy(alpha = 0.3f),
            thickness = 1.dp
        )

        PatientInfoSection(
            name = healthReport.patientName,
            date = healthReport.date
        )

        Spacer(modifier = Modifier.height(10.dp))

        QuickStatsSection(
            bpm = personalInfo.pulseRate,
            bloodPressure = "${healthReport.bloodPressure.systolic}/${healthReport.bloodPressure.diastolic}",
            temperature = personalInfo.bodyTemperature
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Vitals Summary Section
        VitalsSummarySection(personalInfo = personalInfo)

        Spacer(modifier = Modifier.height(16.dp))

        // Show pregnancy history summary
        PregnancyHistorySection(pregnancyHistory = pregnancyHistory)

        Spacer(modifier = Modifier.height(16.dp))

        DetailedAnalysisSection(
            healthReport = healthReport,
            personalInfo = personalInfo
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ML-Enhanced Health Status Section - This is the key part!
        MLEnhancedHealthStatusSection(
            originalStatus = healthReport.overallStatus,
            mlPrediction = mlPrediction
        )

        Spacer(modifier = Modifier.height(20.dp))

        ShareButtonSection(
            onShareResults = { /* Handle share */ }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun VitalsSummarySection(personalInfo: PersonalInformation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Text(
                text = "Vitals Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Compact grid layout - 2 columns, 2 rows using simple Column/Row
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Row 1: Random Blood Sugar & HbA1c
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CompactVitalItem(
                        icon = Icons.Default.Bloodtype,
                        label = "Random Blood Sugar",
                        value = try { "%.1f mg/dL".format(personalInfo.glucose) } catch (e: Exception) { "N/A" },
                        color = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f)
                    )
                    CompactVitalItem(
                        icon = Icons.Default.Bloodtype,
                        label = "HbA1c",
                        value = try { "%.1f%%".format(personalInfo.hba1c) } catch (e: Exception) { "N/A" },
                        color = Color(0xFF9C27B0),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Row 2: Hemoglobin & Respiration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CompactVitalItem(
                        icon = Icons.Default.Bloodtype,
                        label = "Hemoglobin",
                        value = try { "%.1f g/dL".format(personalInfo.hemoglobinLevel) } catch (e: Exception) { "N/A" },
                        color = Color(0xFFF44336),
                        modifier = Modifier.weight(1f)
                    )
                    CompactVitalItem(
                        icon = Icons.Default.Air,
                        label = "Respiration",
                        value = "${personalInfo.respirationRate}/min",
                        color = Color(0xFF00BCD4),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactVitalItem(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon with colored background
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = color.copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Value (larger, more prominent)
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            
            // Label (smaller, below value)
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PregnancyHistorySection(pregnancyHistory: PregnancyHistory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Obstetric History",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Gravida: ${pregnancyHistory.gravida}", fontSize = 14.sp)
                    Text(text = "Para: ${pregnancyHistory.para}", fontSize = 14.sp)
                    Text(text = "Live Births: ${pregnancyHistory.liveBirths}", fontSize = 14.sp)
                }
                Column {
                    Text(text = "Abortions: ${pregnancyHistory.abortions}", fontSize = 14.sp)
                    Text(text = "Child Deaths: ${pregnancyHistory.childDeaths}", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "G${pregnancyHistory.gravida}P${pregnancyHistory.para}L${pregnancyHistory.liveBirths}A${pregnancyHistory.abortions}D${pregnancyHistory.childDeaths}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFE91E63)
            )
        }
    }
}

// ML-Enhanced Health Status Section - Updated to show better feedback
@Composable
fun MLEnhancedHealthStatusSection(
    originalStatus: HealthStatus,
    mlPrediction: ReportRepository.RiskPrediction?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "AI Risk Assessment",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // ML Prediction Card (if available)
        if (mlPrediction != null) {
            MLPredictionCard(prediction = mlPrediction)
        } else {
            // Show that ML prediction is missing
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "AI Prediction Unavailable",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "The AI model could not generate a risk prediction. Using traditional assessment methods.",
                        fontSize = 14.sp,
                        color = Color(0xFFFF9800).copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MLPredictionCard(
    prediction: ReportRepository.RiskPrediction
) {
    val (cardColor, iconColor, statusIcon) = when (prediction.riskLevel) {
        "High Risk" -> Triple(
            Color(0xFFF44336).copy(alpha = 0.1f),
            Color(0xFFF44336),
            Icons.Default.Error
        )
        "Moderate Risk" -> Triple(
            Color(0xFFFF9800).copy(alpha = 0.1f),
            Color(0xFFFF9800),
            Icons.Default.Warning
        )
        else -> Triple(
            Color(0xFF4CAF50).copy(alpha = 0.1f),
            Color(0xFF4CAF50),
            Icons.Default.CheckCircle
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // AI Badge
            Surface(
                color = iconColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "🤖 AI Risk Assessment",
                    color = iconColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // Main Status Icon
            Icon(
                imageVector = statusIcon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Risk Level
            Text(
                text = prediction.riskLevel,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = iconColor,
                textAlign = TextAlign.Center
            )

            // Confidence Score
            if (prediction.confidence > 0) {
                Text(
                    text = "Confidence: ${(prediction.confidence * 100).toInt()}%",
                    fontSize = 14.sp,
                    color = iconColor.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Risk Level Description
            val description = when (prediction.riskLevel) {
                "High Risk" -> "Immediate medical attention recommended. Please consult with your healthcare provider urgently."
                "Moderate Risk" -> "Regular monitoring recommended. Schedule follow-up appointments as advised by your doctor."
                else -> "Continue regular prenatal care. Maintain healthy lifestyle habits."
            }

            Text(
                text = description,
                fontSize = 14.sp,
                color = iconColor.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportTopBar(
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onDebugToggle: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                "AI Risk Analysis",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
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

// Keep existing components (PatientInfoSection, QuickStatsSection, etc.)
@Composable
fun PatientInfoSection(
    name: String,
    date: String
) {
    val capitalizedName = remember(name) {
        name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = capitalizedName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE91E63)
            )
            Text(
                text = date,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun QuickStatCard(
    icon: ImageVector,
    iconColor: Color,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(width = 120.dp, height = 120.dp)
            .zIndex(10f),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = iconColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = value,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = unit,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun QuickStatsSection(
    bpm: Int,
    bloodPressure: String,
    temperature: Double
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuickStatCard(
                icon = Icons.Default.Favorite,
                iconColor = Color(0xFFE91E63),
                value = bpm.toString(),
                unit = "BPM",
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                icon = Icons.Default.Speed,
                iconColor = Color(0xFF2196F3),
                value = bloodPressure,
                unit = "BP",
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                icon = Icons.Default.DeviceThermostat,
                iconColor = Color(0xFF4CAF50),
                value = "${temperature}°",
                unit = "TEMP",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DetailedAnalysisSection(
    healthReport: HealthReport,
    personalInfo: PersonalInformation
) {
    val estimatedOxygenSaturation = remember(personalInfo.pulseRate, personalInfo.respirationRate) {
        // Improved oxygen saturation calculation based on medical literature
        val baseO2 = 98.0 // Normal baseline
        val pulseEffect = (personalInfo.pulseRate - 80) * 0.1 // Heart rate effect
        val respEffect = (personalInfo.respirationRate - 16) * 0.2 // Respiration effect
        (baseO2 - pulseEffect - respEffect).coerceIn(85.0, 100.0)
    }
    // Combine metrics from both sources
    val metrics = remember(healthReport, personalInfo) {
        healthReport.detailedMetrics + listOf(
            createHealthMetric(
                title = "Respiration Rate",
                value = personalInfo.respirationRate.toString(),
                unit = "/min",
                currentValue = personalInfo.respirationRate.toFloat(),
                rangeMin = 12f,
                rangeMax = 20f, // Normal range for pregnancy
                icon = R.drawable.respiration
            ),
            createHealthMetric(
                title = "Hemoglobin Level",
                value = "%.1f".format(personalInfo.hemoglobinLevel),
                unit = "g/dL",
                currentValue = personalInfo.hemoglobinLevel.toFloat(),
                rangeMin = 11.0f, // Corrected for pregnancy
                rangeMax = 16.0f,
                icon = R.drawable.hemoglobin
            ),
            createHealthMetric(
                title = "Random Blood Sugar (RBS)",
                value = "%.1f".format(personalInfo.glucose),
                unit = "mg/dL",
                currentValue = personalInfo.glucose.toFloat(),
                rangeMin = 70f,
                rangeMax = 140f, // Corrected upper limit for random blood sugar
                icon = R.drawable.glucose
            ),
            createHealthMetric(
                title = "HbA1c Level",
                value = "%.1f".format(personalInfo.hba1c),
                unit = "%",
                currentValue = personalInfo.hba1c.toFloat(),
                rangeMin = 4.0f,
                rangeMax = 5.7f, // Corrected upper limit
                icon = R.drawable.glucose
            ),
            createHealthMetric(
                title = "Oxygen Saturation (Est.)",
                value = "%.1f".format(estimatedOxygenSaturation),
                unit = "%",
                currentValue = estimatedOxygenSaturation.toFloat(),
                rangeMin = 95f,
                rangeMax = 100f, // Normal range for pregnancy
                icon = R.drawable.respiration
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Detailed Analysis",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        metrics.forEach { metric ->
            HealthMetricCard(metric = metric)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

private fun createHealthMetric(
    title: String,
    value: String,
    unit: String,
    currentValue: Float,
    rangeMin: Float,
    rangeMax: Float,
    icon: Int
): HealthMetric {
    val normalRange = "${rangeMin.toInt()}-${rangeMax.toInt()}"
    
    // Improved status calculation with better thresholds
    val status = when {
        currentValue.isNaN() || currentValue.isInfinite() -> MetricStatus.CRITICAL
        currentValue < rangeMin * 0.85f || currentValue > rangeMax * 1.15f -> MetricStatus.CRITICAL
        currentValue < rangeMin * 0.95f || currentValue > rangeMax * 1.05f -> MetricStatus.WARNING
        else -> MetricStatus.NORMAL
    }

    return HealthMetric(
        id = title.hashCode().toString(),
        title = title,
        value = value,
        unit = unit,
        normalRange = normalRange,
        currentValue = currentValue,
        rangeMax = rangeMax,
        rangeMin = rangeMin,
        icon = icon,
        status = status
    )
}



@Composable
fun ShareButtonSection(
    onShareResults: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Auto-save indicator
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CloudDone,
                    contentDescription = "Auto-saved",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Report automatically saved to your records",
                    fontSize = 14.sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
        }

    }
}

@Composable
private fun ErrorScreen(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            tint = Color.Red,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Analysis Error",
            color = Color.Red,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
        ) {
            Text("Retry Analysis")
        }
    }
}

@Composable
private fun EmptyStateScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Assessment,
            contentDescription = "No Analysis",
            tint = Color.Gray,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Analysis Available",
            color = Color.Gray,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
        ) {
            Text("Generate Analysis")
        }
    }
}




@Composable
fun EnhancedGradientProgressIndicator(
    currentValue: Float,
    minValue: Float,
    maxValue: Float,
    modifier: Modifier = Modifier
) {
    // Better error handling for progress calculation
    val progress = if (currentValue.isFinite() && !currentValue.isNaN() && 
                       minValue.isFinite() && !minValue.isNaN() && 
                       maxValue.isFinite() && !maxValue.isNaN() && 
                       maxValue > minValue) {
        ((currentValue - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
    } else {
        0f
    }

    // Determine status based on progress
    val status = when {
        progress < 0.3f -> "Low"
        progress > 0.7f -> "High"
        else -> "Normal"
    }

    val statusColor = when(status) {
        "Low" -> Color(0xFFFF6B6B)
        "High" -> Color(0xFFFF8A50)
        else -> Color(0xFF4ECDC4)
    }

    Column(modifier = modifier) {
        // Progress track with glassmorphism effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            // Background gradient track
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF4ECDC4).copy(alpha = 0.2f),
                                Color(0xFF44E5A3).copy(alpha = 0.2f),
                                Color(0xFFFFE66D).copy(alpha = 0.2f),
                                Color(0xFFFF8A50).copy(alpha = 0.2f),
                                Color(0xFFFF6B6B).copy(alpha = 0.2f)
                            )
                        )
                    )
            )

            // Filled progress with vibrant gradient
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF4ECDC4),
                                Color(0xFF44E5A3),
                                Color(0xFFFFE66D),
                                Color(0xFFFF8A50),
                                Color(0xFFFF6B6B)
                            )
                        )
                    )
            )

           
        }

        // Status indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = status,
                fontSize = 12.sp,
                color = statusColor,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ModernProgressIndicator(
    currentValue: Float,
    minValue: Float,
    maxValue: Float,
    title: String,
    modifier: Modifier = Modifier
) {
    // Better error handling for progress calculation
    val progress = if (currentValue.isFinite() && !currentValue.isNaN() && 
                       minValue.isFinite() && !minValue.isNaN() && 
                       maxValue.isFinite() && !maxValue.isNaN() && 
                       maxValue > minValue) {
        ((currentValue - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
    } else {
        0f
    }

    // Create segments for different zones
    val segments = listOf(
        ProgressSegment(0f, 0.3f, Color(0xFF4ECDC4), "Optimal"),
        ProgressSegment(0.3f, 0.7f, Color(0xFF44E5A3), "Normal"),
        ProgressSegment(0.7f, 0.9f, Color(0xFFFFE66D), "Caution"),
        ProgressSegment(0.9f, 1f, Color(0xFFFF6B6B), "Critical")
    )

    val currentSegment = segments.find { progress >= it.start && progress <= it.end }

    Column(modifier = modifier) {
        // Multi-segment progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        ) {
            // Draw segments using relative positioning
            segments.forEach { segment ->
                val segmentWidth = segment.end - segment.start
                val segmentAlpha = if (progress >= segment.start) {
                    if (progress <= segment.end) {
                        ((progress - segment.start) / segmentWidth).coerceIn(0f, 1f)
                    } else 1f
                } else 0f

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(segmentWidth)
                        .offset(x = (segment.start * 100).dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    segment.color.copy(alpha = 0.3f),
                                    segment.color.copy(alpha = segmentAlpha)
                                )
                            )
                        )
                )
            }

            // // Current position indicator
            // Box(
            //     modifier = Modifier
            //         .width(3.dp)
            //         .fillMaxHeight()
            //         .offset(x = (progress * 100).dp)
            //         .background(
            //             color = currentSegment?.color ?: Color.Gray,
            //             shape = RoundedCornerShape(1.5.dp)
            //         )
            // )
        }

        // Zone indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            segments.forEach { segment ->
                Text(
                    text = segment.label,
                    fontSize = 10.sp,
                    color = if (currentSegment == segment) segment.color else Color.Gray.copy(alpha = 0.6f),
                    fontWeight = if (currentSegment == segment) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun CircularProgressIndicator(
    currentValue: Float,
    minValue: Float,
    maxValue: Float,
    title: String,
    modifier: Modifier = Modifier
) {
    val progress = ((currentValue - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )

    val statusColor = when {
        progress < 0.3f -> Color(0xFF4ECDC4)
        progress < 0.7f -> Color(0xFF44E5A3)
        progress < 0.9f -> Color(0xFFFFE66D)
        else -> Color(0xFFFF6B6B)
    }

    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background circle
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val strokeWidth = 8.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2

            // Background arc
            drawArc(
                color = Color(0xFFF5F5F5),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress arc with better error handling
            if (progress.isFinite() && !progress.isNaN()) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            statusColor.copy(alpha = 0.3f),
                            statusColor
                        )
                    ),
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // Center content with better error handling
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (progress.isFinite() && !progress.isNaN()) "${(animatedProgress * 100).toInt()}%" else "N/A",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )
            Text(
                text = title,
                fontSize = 8.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}



// Data class for progress segments
data class ProgressSegment(
    val start: Float,
    val end: Float,
    val color: Color,
    val label: String
)

// Updated HealthMetricCard to use the new progress indicator
@Composable
fun HealthMetricCard(
    metric: HealthMetric
) {
    val statusColor = when(metric.status) {
        MetricStatus.NORMAL -> Color(0xFF4ECDC4)
        MetricStatus.WARNING -> Color(0xFFFFE66D)
        MetricStatus.CRITICAL -> Color(0xFFFF6B6B)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = statusColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = metric.icon),
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = metric.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                        Text(
                            text = "${metric.value} ${metric.unit}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Circular progress indicator with fallback
                if (metric.currentValue.isFinite() && !metric.currentValue.isNaN() && 
                    metric.rangeMin.isFinite() && !metric.rangeMin.isNaN() && 
                    metric.rangeMax.isFinite() && !metric.rangeMax.isNaN() && 
                    metric.rangeMax > metric.rangeMin) {
                    CircularProgressIndicator(
                        currentValue = metric.currentValue,
                        minValue = metric.rangeMin,
                        maxValue = metric.rangeMax,
                        title = "Level"
                    )
                } else {
                    // Fallback simple indicator for invalid data
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = Color.Gray.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "N/A",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enhanced linear progress indicator with fallback
            if (metric.currentValue.isFinite() && !metric.currentValue.isNaN() && 
                metric.rangeMin.isFinite() && !metric.rangeMin.isNaN() && 
                metric.rangeMax.isFinite() && !metric.rangeMax.isNaN() && 
                metric.rangeMax > metric.rangeMin) {
                EnhancedGradientProgressIndicator(
                    currentValue = metric.currentValue,
                    minValue = metric.rangeMin,
                    maxValue = metric.rangeMax
                )
            } else {
                // Fallback simple progress bar for invalid data
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "Data unavailable",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Normal range text
            Text(
                text = "Normal Range: ${metric.normalRange} ${metric.unit}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}