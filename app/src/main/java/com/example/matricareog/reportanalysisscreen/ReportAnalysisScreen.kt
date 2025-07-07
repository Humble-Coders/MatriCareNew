package com.example.matricareog.reportscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matricareog.HealthMetric
import com.example.matricareog.HealthReport
import com.example.matricareog.HealthStatus
import com.example.matricareog.MetricStatus
import com.example.matricareog.viewmodels.ReportViewModel
import com.example.matricareog.viewmodels.MedicalHistoryViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalContext
import com.example.matricareog.PersonalInformation
import com.example.matricareog.R
import com.example.matricareog.repository.ReportRepository
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportAnalysisScreen(
    userId: String,
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    reportViewModel: ReportViewModel ,
    medicalHistoryViewModel: MedicalHistoryViewModel
) {
    val context = LocalContext.current

    // Data from ReportViewModel
    val mlPrediction by reportViewModel.mlPrediction.observeAsState()
    val isLoading by reportViewModel.isLoading.observeAsState(false)
    val error by reportViewModel.error.observeAsState()
    val isModelReady by reportViewModel.isModelReady.observeAsState(false)

    val generatedReport by reportViewModel.generatedHealthReport.observeAsState()

    // Data from MedicalHistoryViewModel (LiveData)
    val personalInfo by medicalHistoryViewModel.personalInfo.observeAsState()
    val pregnancyHistory by medicalHistoryViewModel.pregnancyHistory.observeAsState()

    // Initialize ML model when screen loads
    LaunchedEffect(Unit) {
        if (!isModelReady) {
            reportViewModel.initializeMLModel(context)
        }
    }

    // Generate report when LiveData changes
    LaunchedEffect(personalInfo, pregnancyHistory) {
        personalInfo?.let { pInfo ->
            pregnancyHistory?.let { pHistory ->
                reportViewModel.processMLAnalysisFromLiveData(
                    personalInfo = pInfo,
                    pregnancyHistory = pHistory,
                    userName = "Patient" // Or fetch from user data
                )
            }
        }
    }

    // Save button click handler
    fun onSaveClick() {
        personalInfo?.let { pInfo ->
            pregnancyHistory?.let { pHistory ->
                medicalHistoryViewModel.saveCompleteDataToFirebase(
                    userId = userId,
                    mlPrediction = mlPrediction
                )
            }
        }
    }




    // UI Layout
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ReportTopBar(
            onBackClick = onBackClick,
            onShareClick = onShareClick
        )

        when {
            isLoading -> {
                LoadingIndicator()
            }

            error != null -> {
                val localError = error
                ErrorScreen(
                    error = localError ?: "Unknown error occurred",
                    onRetry = {
                        personalInfo?.let { pInfo ->
                            pregnancyHistory?.let { pHistory ->
                                reportViewModel.processMLAnalysisFromLiveData(

                                    personalInfo = pInfo,
                                    pregnancyHistory = pHistory,
                                    userName = "Patient"
                                )
                            }
                        }
                    }
                )
            }

            generatedReport != null && personalInfo != null -> {
                ReportContent(
                    healthReport = generatedReport!!,
                    personalInfo = personalInfo!!,
                    mlPrediction = mlPrediction,
                    onSaveClick = {
                        onSaveClick()
                    }
                )
            }

            else -> {
                EmptyStateScreen(
                    onRetry = {
                        medicalHistoryViewModel.loadMedicalHistoryList(userId ?: "")
                    }
                )
            }
        }

    }
}

@Composable
private fun ReportContent(
    healthReport: HealthReport,
    personalInfo: PersonalInformation,
    mlPrediction: ReportRepository.RiskPrediction?,
    onSaveClick: () -> Unit
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

        DetailedAnalysisSection(
            healthReport = healthReport,
            personalInfo = personalInfo
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Updated HealthStatusSection with ML prediction
        MLEnhancedHealthStatusSection(
            originalStatus = healthReport.overallStatus,
            mlPrediction = mlPrediction
        )

        Spacer(modifier = Modifier.height(20.dp))

        ActionButtonsSection(
            onSaveReport = onSaveClick,
            onShareResults = { /* Handle share */ }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// NEW: ML-Enhanced Health Status Section
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
        // ML Prediction Card (if available)
        mlPrediction?.let { prediction ->
            MLPredictionCard(prediction = prediction)
        }

        // Original Health Status Card (keeping as fallback or additional info)
        //OriginalHealthStatusCard(status = originalStatus, showAsSecondary = mlPrediction != null)
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
        //elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ML Badge
            Surface(
                color = iconColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "AI Risk Assessment",
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
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = iconColor,
                textAlign = TextAlign.Center
            )

            // Risk Percentage


            // Recommendations
//            if (prediction.recommendations.isNotEmpty()) {
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Text(
//                    text = "AI Recommendations:",
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    color = iconColor,
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                prediction.recommendations.take(3).forEach { recommendation ->
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 2.dp),
//                        verticalAlignment = Alignment.Top
//                    ) {
//                        Text(
//                            text = "•",
//                            color = iconColor,
//                            fontSize = 14.sp,
//                            modifier = Modifier.padding(end = 6.dp)
//                        )
//                        Text(
//                            text = recommendation,
//                            fontSize = 13.sp,
//                            color = iconColor.copy(alpha = 0.9f),
//                            modifier = Modifier.weight(1f)
//                        )
//                    }
//                }
//            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportTopBar(
    onBackClick: () -> Unit,
    onShareClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                "Report Analysis",
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
        actions = {
            IconButton(onClick = onShareClick) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

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
            .size(width = 110.dp, height = 110.dp)
            .zIndex(10f),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = unit,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
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
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuickStatCard(
                icon = Icons.Default.Favorite,
                iconColor = Color(0xFFE91E63),
                value = bpm.toString(),
                unit = "BPM"
            )
            QuickStatCard(
                icon = Icons.Default.Speed,
                iconColor = Color(0xFF2196F3),
                value = bloodPressure,
                unit = "BP"
            )
            QuickStatCard(
                icon = Icons.Default.DeviceThermostat,
                iconColor = Color(0xFF4CAF50),
                value = "${temperature}°",
                unit = "TEMP"
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
        (100 - (personalInfo.respirationRate * 0.5 + personalInfo.pulseRate * 0.1) / 2).coerceIn(85.0, 100.0)
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
                rangeMax = 20f,
                icon = R.drawable.respiration
            ),
            createHealthMetric(
                title = "Hemoglobin Level",
                value = "%.1f".format(personalInfo.hemoglobinLevel),
                unit = "g/dL",
                currentValue = personalInfo.hemoglobinLevel.toFloat(),
                rangeMin = 12f,
                rangeMax = 16f,
                icon = R.drawable.hemoglobin
            ),
            createHealthMetric(
                title = "Blood Glucose",
                value = "%.1f".format(personalInfo.glucose),
                unit = "mg/dL",
                currentValue = personalInfo.glucose.toFloat(),
                rangeMin = 70f,
                rangeMax = 100f,
                icon = R.drawable.glucose
            ),
            createHealthMetric(
                title = "Oxygen Saturation (Est.)",
                value = "%.1f".format(estimatedOxygenSaturation),
                unit = "%",
                currentValue = estimatedOxygenSaturation.toFloat(),
                rangeMin = 95f,
                rangeMax = 100f,
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
    val status = when {
        currentValue < rangeMin * 0.9f || currentValue > rangeMax * 1.1f -> MetricStatus.CRITICAL
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
        rangeMin = rangeMin,
        rangeMax = rangeMax,
        icon = icon,
        status = status
    )
}

@Composable
fun HealthMetricCard(
    metric: HealthMetric
) {
    val statusColor = when(metric.status) {
        MetricStatus.NORMAL -> Color(0xFF4CAF50)
        MetricStatus.WARNING -> Color(0xFFFF9800)
        MetricStatus.CRITICAL -> Color(0xFFF44336)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = metric.icon),
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = metric.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
                IconButton(
                    onClick = { /* Handle info click */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Color(0xFFE91E63),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Text(
                text = "${metric.value} ${metric.unit}",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 28.dp, top = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            GradientProgressIndicator(
                currentValue = metric.currentValue,
                minValue = metric.rangeMin,
                maxValue = metric.rangeMax
            )

            Text(
                text = "Range: ${metric.normalRange}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun GradientProgressIndicator(
    currentValue: Float,
    minValue: Float,
    maxValue: Float
) {
    val progress = ((currentValue - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF4CAF50), // Green
                        Color(0xFFFFEB3B), // Yellow
                        Color(0xFFFF9800), // Orange
                        Color(0xFFF44336)  // Red
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
                .offset(x = (progress * (maxOf(0f, 1f - 2.dp.value / 300.dp.value)) * 300.dp))
                .background(Color.Black)
        )
    }
}

@Composable
fun ActionButtonsSection(
    onSaveReport: () -> Unit,
    onShareResults: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onSaveReport,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE91E63)
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(
                text = "Save Report",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        OutlinedButton(
            onClick = onShareResults,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFFE91E63)
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp,
                brush = Brush.horizontalGradient(listOf(Color(0xFFE91E63), Color(0xFFE91E63)))
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Share Results",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
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
        Text(
            text = "Error loading report",
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
        Button(onClick = onRetry) {
            Text("Retry")
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
        Text(
            text = "No report available",
            color = Color.Gray,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Load Report")
        }
    }
}