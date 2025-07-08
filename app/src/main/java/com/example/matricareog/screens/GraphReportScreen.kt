package com.example.matricareog.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.matricareog.model.MatriCareState
import com.example.matricareog.repository.MatriCareRepository
import com.example.matricareog.viewmodels.MatriCareViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphReportScreen(
    onBackClick: () -> Unit,
    viewModel: MatriCareViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val predictionHistory by viewModel.predictionHistory.collectAsState()
    val riskHistory by viewModel.riskHistory.collectAsState()
    val availableParameters by viewModel.availableParameters.collectAsState()
    val selectedParameter by viewModel.selectedParameter.collectAsState()
    val isPredictionLoading by viewModel.isPredictionHistoryLoading.collectAsState()
    val isRiskLoading by viewModel.isRiskHistoryLoading.collectAsState()
    val predictionError by viewModel.predictionHistoryError.collectAsState()
    val riskError by viewModel.riskHistoryError.collectAsState()
    val totalRecords by viewModel.totalRecords.collectAsState()
    val lastUpdateDate by viewModel.lastUpdateDate.collectAsState()
    val riskSummary by viewModel.riskSummary.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    "Health History",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE91E63)
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFE91E63)
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.refreshCurrentTab() }) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color(0xFFE91E63)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Quick Stats Card
            QuickStatsCard(
                totalRecords = totalRecords,
                lastUpdate = lastUpdateDate,
                riskSummary = riskSummary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Row
            TabSection(
                selectedTab = selectedTab,
                onTabSelected = viewModel::selectTab
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Parameter Selection (only for Prediction History)
            if (selectedTab == 0 && availableParameters.isNotEmpty()) {
                ParameterSelectionRow(
                    parameters = availableParameters,
                    selectedParameter = selectedParameter,
                    onParameterSelected = viewModel::selectParameter
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Content based on selected tab
            when (selectedTab) {
                0 -> PredictionHistoryContent(
                    predictionHistory = predictionHistory,
                    isLoading = isPredictionLoading,
                    error = predictionError,
                    chartData = uiState,
                    selectedParameter = selectedParameter,
                    onRetry = { viewModel.refreshPredictionHistory() },
                    onClearError = { viewModel.clearPredictionHistoryError() }
                )
                1 -> RiskHistoryContent(
                    riskHistory = riskHistory,
                    isLoading = isRiskLoading,
                    error = riskError,
                    chartData = uiState,
                    onRetry = { viewModel.refreshRiskHistory() },
                    onClearError = { viewModel.clearRiskHistoryError() }
                )
            }
        }
    }
}

@Composable
private fun QuickStatsCard(
    totalRecords: Int,
    lastUpdate: String?,
    riskSummary: Map<String, Int>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Health Overview",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatsItem(
                    label = "Total Records",
                    value = totalRecords.toString(),
                    icon = Icons.Default.Assignment
                )
                StatsItem(
                    label = "Last Update",
                    value = lastUpdate ?: "No data",
                    icon = Icons.Default.Schedule
                )
            }

            if (riskSummary.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Risk Assessment Summary",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(riskSummary.entries.toList()) { (riskLevel, count) ->
                        RiskSummaryChip(riskLevel = riskLevel, count = count)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFE91E63),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun RiskSummaryChip(
    riskLevel: String,
    count: Int
) {
    val (backgroundColor, textColor) = when (riskLevel) {
        "High Risk" -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
        "Moderate Risk" -> Color(0xFFFFF3E0) to Color(0xFFFF6F00)
        "No Risk" -> Color(0xFFE8F5E8) to Color(0xFF2E7D32)
        else -> Color(0xFFF5F5F5) to Color(0xFF757575)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = riskLevel,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
                color = textColor,
                shape = CircleShape,
                modifier = Modifier.size(18.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = count.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun TabSection(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TabButton(
            text = "Prediction History",
            isSelected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            modifier = Modifier.weight(1f),
            icon = Icons.Default.History
        )
        TabButton(
            text = "Risk History",
            isSelected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Security
        )
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFFE91E63) else Color.White,
            contentColor = if (isSelected) Color.White else Color.Gray
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ParameterSelectionRow(
    parameters: List<String>,
    selectedParameter: String,
    onParameterSelected: (String) -> Unit
) {
    Column {
        Text(
            text = "Select Parameter",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(parameters) { parameter ->
                ParameterChip(
                    parameter = parameter,
                    isSelected = parameter == selectedParameter,
                    onClick = { onParameterSelected(parameter) }
                )
            }
        }
    }
}

@Composable
private fun ParameterChip(
    parameter: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) Color(0xFFE91E63) else Color.White,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .clickable { onClick() }
            .padding(2.dp),
        border = if (!isSelected) ButtonDefaults.outlinedButtonBorder else null
    ) {
        Text(
            text = parameter,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) Color.White else Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun PredictionHistoryContent(
    predictionHistory: List<MatriCareRepository.PredictionHistoryItem>,
    isLoading: Boolean,
    error: String?,
    chartData: MatriCareState,
    selectedParameter: String,
    onRetry: () -> Unit,
    onClearError: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Chart Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "$selectedParameter Trend",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    when (chartData) {
                        is MatriCareState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFFE91E63))
                            }
                        }
                        is MatriCareState.Success -> {
                            LineChartView(
                                data = chartData.chartData.hemoglobinData.map { it.hemoglobin },
                                labels = chartData.chartData.hemoglobinData.map { it.date },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                        is MatriCareState.Error -> {
                            ErrorMessage(
                                message = chartData.message,
                                onRetry = onRetry
                            )
                        }
                    }
                }
            }
        }

        // History List Section
        item {
            Text(
                text = "Medical History Records",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFE91E63))
                }
            }
        } else if (error != null) {
            item {
                ErrorMessage(
                    message = error,
                    onRetry = onRetry,
                    onDismiss = onClearError
                )
            }
        } else if (predictionHistory.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "No Medical History",
                    message = "Complete your medical history assessment to see data here.",
                    icon = Icons.Default.HistoryEdu
                )
            }
        } else {
            items(predictionHistory) { item ->
                PredictionHistoryCard(item = item)
            }
        }
    }
}

@Composable
private fun RiskHistoryContent(
    riskHistory: List<MatriCareRepository.RiskHistoryItem>,
    isLoading: Boolean,
    error: String?,
    chartData: MatriCareState,
    onRetry: () -> Unit,
    onClearError: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Chart Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Health Metrics Overview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    when (chartData) {
                        is MatriCareState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFFE91E63))
                            }
                        }
                        is MatriCareState.Success -> {
                            LineChartView(
                                data = chartData.chartData.hemoglobinData.map { it.hemoglobin },
                                labels = chartData.chartData.hemoglobinData.map { it.date },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                        is MatriCareState.Error -> {
                            ErrorMessage(
                                message = chartData.message,
                                onRetry = onRetry
                            )
                        }
                    }
                }
            }
        }

        // Risk History List Section
        item {
            Text(
                text = "AI Risk Assessments",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFE91E63))
                }
            }
        } else if (error != null) {
            item {
                ErrorMessage(
                    message = error,
                    onRetry = onRetry,
                    onDismiss = onClearError
                )
            }
        } else if (riskHistory.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "No Risk Assessments",
                    message = "Complete your medical assessment to get AI-powered risk predictions.",
                    icon = Icons.Default.Security
                )
            }
        } else {
            items(riskHistory) { item ->
                RiskHistoryCard(item = item)
            }
        }
    }
}

@Composable
private fun PredictionHistoryCard(
    item: MatriCareRepository.PredictionHistoryItem
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.date,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE91E63)
                )
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vital Signs
            Text(
                text = "Vital Signs",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VitalSignItem("BP", "${item.systolicBP}/${item.diastolicBP}")
                VitalSignItem("HR", "${item.pulseRate} BPM")
                VitalSignItem("Temp", "${item.bodyTemperature}°F")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lab Values
            Text(
                text = "Lab Values",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VitalSignItem("Glucose", "${item.glucose} mg/dL")
                VitalSignItem("Hemoglobin", "${item.hemoglobinLevel} g/dL")
                VitalSignItem("HBA1C", "${item.hba1c}%")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Obstetric History
            Text(
                text = "Obstetric History",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "G${item.gravida}P${item.para}L${item.liveBirths}A${item.abortions}D${item.childDeaths}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFE91E63)
            )
        }
    }
}

@Composable
private fun RiskHistoryCard(
    item: MatriCareRepository.RiskHistoryItem
) {
    val (cardColor, textColor, icon) = when (item.riskLevel) {
        "High Risk" -> Triple(
            Color(0xFFFFEBEE),
            Color(0xFFD32F2F),
            Icons.Default.Warning
        )
        "Moderate Risk" -> Triple(
            Color(0xFFFFF3E0),
            Color(0xFFFF6F00),
            Icons.Default.Info
        )
        "No Risk" -> Triple(
            Color(0xFFE8F5E8),
            Color(0xFF2E7D32),
            Icons.Default.CheckCircle
        )
        else -> Triple(
            Color(0xFFF5F5F5),
            Color(0xFF757575),
            Icons.Default.Help
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.riskLevel,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = item.date,
                    fontSize = 14.sp,
                    color = textColor.copy(alpha = 0.7f)
                )
                if (item.confidence > 0) {
                    Text(
                        text = "Confidence: ${(item.confidence * 100).toInt()}%",
                        fontSize = 12.sp,
                        color = textColor.copy(alpha = 0.6f)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = textColor.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun VitalSignItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun EmptyStateCard(
    title: String,
    message: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    onDismiss: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Error",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F)
            )
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color(0xFFD32F2F),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text("Retry")
                }
                if (onDismiss != null) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
}

@Composable
fun LineChartView(
    data: List<Double>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).apply {
                setTouchEnabled(true)
                setPinchZoom(true)
                description = Description().apply { text = "" }
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    valueFormatter = IndexAxisValueFormatter(labels)
                    granularity = 1f
                    isGranularityEnabled = true
                    setDrawGridLines(false)
                    textColor = android.graphics.Color.BLACK
                }
                axisLeft.textColor = android.graphics.Color.BLACK
                axisRight.isEnabled = false
                legend.isEnabled = false
            }
        },
        update = { chart ->
            val entries = data.mapIndexed { index, value ->
                Entry(index.toFloat(), value.toFloat())
            }

            val dataSet = LineDataSet(entries, "Data").apply {
                color = ColorTemplate.COLORFUL_COLORS[0]
                valueTextColor = android.graphics.Color.BLACK
                circleRadius = 4f
                setDrawValues(true)
                setDrawCircles(true)
                circleColors = listOf(android.graphics.Color.BLUE)
                lineWidth = 2f
            }

            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
}