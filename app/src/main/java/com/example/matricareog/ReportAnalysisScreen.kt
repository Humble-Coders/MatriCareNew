package com.example.matricareog

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items


import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp

import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.border



import com.example.matricareog.R


// Composable UI Implementation
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthReportScreen(
    // viewModel: HealthReportViewModel = hiltViewModel(),
    onBackPress: () -> Unit = {}
) {
    // val uiState by viewModel.uiState.collectAsState()

    // Dummy data for demonstration
    val dummyReport = HealthReport(
        patientName = "Anshika Goel",
        date = "March 15, 2024",
        heartRate = 72,
        bloodPressure = BloodPressure(120, 80),
        temperature = 98.6,
        detailedMetrics = listOf(
            HealthMetric(
                id = "systolic_bp",
                title = "Systolic Blood Pressure",
                value = "120",
                unit = "mmHg",
                normalRange = "95-100",
                currentValue = 75f,
                rangeMin = 90f,
                rangeMax = 140f,
                icon = R.drawable.heart,
                status = MetricStatus.NORMAL
            ),
            HealthMetric(
                id = "diastolic_bp",
                title = "Diastolic Blood Pressure",
                value = "80",
                unit = "mmHg",
                normalRange = "95-100",
                currentValue = 70f,
                rangeMin = 60f,
                rangeMax = 90f,
                icon = R.drawable.heart,
                status = MetricStatus.NORMAL
            ),
            HealthMetric(
                id = "blood_glucose",
                title = "Blood Glucose",
                value = "95",
                unit = "mg/dL",
                normalRange = "95-100",
                currentValue = 65f,
                rangeMin = 70f,
                rangeMax = 140f,
                icon = R.drawable.heart,
                status = MetricStatus.NORMAL
            ),
            HealthMetric(
                id = "respiration_rate",
                title = "Respiration Rate",
                value = "16",
                unit = "breaths/min",
                normalRange = "95-100",
                currentValue = 80f,
                rangeMin = 12f,
                rangeMax = 20f,
                icon = R.drawable.heart,
                status = MetricStatus.NORMAL
            ),
            HealthMetric(
                id = "pulse_rate",
                title = "Pulse Rate",
                value = "72",
                unit = "BPM",
                normalRange = "95-100",
                currentValue = 72f,
                rangeMin = 60f,
                rangeMax = 100f,
                icon = R.drawable.heart,
                status = MetricStatus.NORMAL
            ),
            HealthMetric(
                id = "oxygen_saturation",
                title = "Oxygen Saturation",
                value = "98",
                unit = "%",
                normalRange = "95-100",
                currentValue = 95f,
                rangeMin = 95f,
                rangeMax = 100f,
                icon = R.drawable.heart,
                status = MetricStatus.NORMAL
            ),
            HealthMetric(
                id = "body_temperature",
                title = "Body Temperature",
                value = "98.6",
                unit = "°F",
                normalRange = "95-100",
                currentValue = 85f,
                rangeMin = 97f,
                rangeMax = 100f,
                icon = R.drawable.heart,
                status = MetricStatus.NORMAL
            ),
            HealthMetric(
                id = "hemoglobin",
                title = "Hemoglobin Level",
                value = "14.5",
                unit = "g/dL",
                normalRange = "95-100",
                currentValue = 88f,
                rangeMin = 12f,
                rangeMax = 16f,
                icon = R.drawable.heart,
                status = MetricStatus.NORMAL
            )
        ),
        overallStatus = HealthStatus(
            status = "Excellent Health Status",
            description = "All metrics are within normal range",
            color = Color(0xFF4CAF50)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Analysis") },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share action */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Header Section
            item {
                HealthReportHeader(report = dummyReport)
            }

            // Detailed Analysis Title
            item {
                Text(
                    text = "Detailed Analysis",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Health Metrics Cards
            items(dummyReport.detailedMetrics) { metric ->
                HealthMetricCard(
                    metric = metric,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Overall Status
            item {
                OverallHealthStatusCard(
                    status = dummyReport.overallStatus,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Action Buttons
            item {
                ActionButtons(
                    onSaveClick = { /* viewModel.saveReport() */ },
                    onShareClick = { /* viewModel.shareReport() */ },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun HealthReportHeader(
    report: HealthReport,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = report.patientName,
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFE91E63),
                fontWeight = FontWeight.Bold
            )

            Text(
                text = report.date,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickMetricItem(
                    icon = Icons.Default.Favorite,
                    value = report.heartRate.toString(),
                    label = "BPM",
                    color = Color(0xFFE91E63)
                )

                QuickMetricItem(
                    icon = Icons.Default.MonitorHeart,
                    value = "${report.bloodPressure.systolic}/${report.bloodPressure.diastolic}",
                    label = "BP",
                    color = Color(0xFF2196F3)
                )

                QuickMetricItem(
                    icon = Icons.Default.Thermostat,
                    value = "${report.temperature}°",
                    label = "TEMP",
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
fun QuickMetricItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun HealthMetricCard(
    metric: HealthMetric,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status icon based on metric type
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            Color(0xFFE91E63), // Pink color from screenshot
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // You can add specific icons here based on metric type
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color.White, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = metric.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = "${metric.value} ${metric.unit}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Range: ${metric.normalRange}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Custom Progress Bar
            HealthMetricProgressBar(
                currentValue = metric.currentValue,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Better implementation of progress bar with proper positioning
@Composable
fun HealthMetricProgressBar(
    currentValue: Float,
    modifier: Modifier = Modifier
) {
    val progressBarWidth = 280.dp // Fixed width for consistent positioning

    Box(
        modifier = modifier
            .height(6.dp)
            .width(progressBarWidth)
    ) {
        // Background track
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0xFFE8E8E8),
                    shape = RoundedCornerShape(3.dp)
                )
        )

        // Green section (normal range) - typically 60-80% of the bar
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(progressBarWidth * 0.6f)
                .align(Alignment.CenterStart)
                .offset(x = progressBarWidth * 0.2f) // Start at 20% position
                .background(
                    Color(0xFF4CAF50),
                    shape = RoundedCornerShape(3.dp)
                )
        )

        // Current value indicator
        Box(
            modifier = Modifier
                .size(12.dp)
                .align(Alignment.CenterStart)
                .offset(x = progressBarWidth * (currentValue / 100f) - 6.dp)
                .background(
                    Color(0xFF2196F3),
                    CircleShape
                )
                .border(2.dp, Color.White, CircleShape)
        )
    }
}

@Composable
fun OverallHealthStatusCard(
    status: HealthStatus,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = status.color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = status.color,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = status.status,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = status.color
            )

            Text(
                text = status.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ActionButtons(
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE91E63)
            )
        ) {
            Text(
                text = "Save Report",
                color = Color.White,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        OutlinedButton(
            onClick = onShareClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFFE91E63)
            ),
            border = BorderStroke(1.dp, Color(0xFFE91E63))
        ) {
            Icon(
                Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Share Results",
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}