package com.example.matricareog.graphscreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.matricareog.ChartData
import com.example.matricareog.HealthDataPoint
import com.example.matricareog.MatriCareState
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
fun MatriCareScreen(
    onBackClick: () -> Unit,
    viewModel: MatriCareViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    "Matri Care",
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
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Tab Row
            TabSection(
                selectedTab = selectedTab,
                onTabSelected = viewModel::selectTab
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Content based on state
            when (uiState) {
                is MatriCareState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFE91E63))
                    }
                }
                is MatriCareState.Success -> {
                    ChartContent(chartData = (uiState as MatriCareState.Success).chartData)
                }
                is MatriCareState.Error -> {
                    ErrorMessage(
                        message = (uiState as MatriCareState.Error).message,
                        onRetry = viewModel::refreshData
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
            modifier = Modifier.weight(1f)
        )
        TabButton(
            text = "Risk History",
            isSelected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFFE91E63) else Color.White,
            contentColor = if (isSelected) Color.White else Color.Gray
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun ChartContent(chartData: ChartData) {
    Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
        // Hemoglobin Chart
        ChartCard(
            title = "Hemoglobin (HB)",
            data = chartData.hemoglobinData.map { it.hemoglobin },
            labels = chartData.hemoglobinData.map { it.date },
            currentValue = chartData.currentHemoglobin,
            currentValueText = "${chartData.currentHemoglobin} g/dL",
            referenceRange = "13.5-17.5 g/dL",
            referenceLabel = "Reference Range",
            isNormal = chartData.currentHemoglobin in 13.5..17.5
        )

        // HBA1C Chart
        ChartCard(
            title = "HBA1C Level",
            data = chartData.hba1cData.map { it.hba1c },
            labels = chartData.hba1cData.map { it.date },
            currentValue = chartData.currentHba1c,
            currentValueText = "${String.format("%.1f", chartData.currentHba1c)}%",
            referenceRange = "4.0-5.7%",
            referenceLabel = "Target Range",
            isNormal = chartData.currentHba1c in 4.0..5.7
        )
    }
}

@Composable
private fun ChartCard(
    title: String,
    data: List<Double>,
    labels: List<String>,
    currentValue: Double,
    currentValueText: String,
    referenceRange: String,
    referenceLabel: String,
    isNormal: Boolean
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
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Simple Line Chart Representation
//            LineChartView(
//                data = data,
//                labels = labels,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(200.dp)
//            )
            LineChartScreen()



            Spacer(modifier = Modifier.height(16.dp))

            // Bottom info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = referenceLabel,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = referenceRange,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Current",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = currentValueText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isNormal) Color(0xFF4CAF50) else Color(0xFFE91E63)
                    )
                }
            }
        }
    }
}

@Composable
fun LineChartScreen() {
    val hemoglobinValues = listOf(13.5, 14.0, 12.8, 13.2, 14.5)
    val dateLabels = listOf("01 Jun", "05 Jun", "10 Jun", "15 Jun", "18 Jun")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Hemoglobin Over Time", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        LineChartView(
            data = hemoglobinValues,
            labels = dateLabels,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
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
                description = Description().apply {
                    text = ""
                }
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

            val dataSet = LineDataSet(entries, "Hemoglobin").apply {
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



@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error loading data",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFD32F2F)
            )
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color(0xFFD32F2F)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63)
                )
            ) {
                Text("Retry")
            }
        }
    }
}