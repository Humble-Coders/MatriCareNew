package com.example.matricareog.screens.maternalGuide

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data classes for the tips
data class TipItem(
    val icon: String,
    val title: String,
    val description: String,
    val iconColor: Color
)

data class TrimesterTips(
    val dos: List<TipItem>,
    val donts: List<TipItem>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosAndDontsScreen(
    onBackClick: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var selectedTrimester by remember { mutableStateOf(0) }

    val tabs = listOf("Do's", "Don'ts")
    val trimesters = listOf("1st Trimester", "2nd Trimester", "3rd Trimester")

    val trimesterData = mapOf(
        0 to TrimesterTips(
            dos = listOf(
                TipItem(
                    icon = "🍎",
                    title = "Nutrition First",
                    description = "Focus on balanced meals with protein, fruits, vegetables and whole grains daily",
                    iconColor = Color(0xFFFF6B6B)
                ),
                TipItem(
                    icon = "🏃‍♀️",
                    title = "Stay Active",
                    description = "30 minutes of gentle exercise like walking or swimming 3-5 times weekly",
                    iconColor = Color(0xFFFF6B6B)
                ),
                TipItem(
                    icon = "💧",
                    title = "Hydrate Well",
                    description = "Drink 8-10 glasses of water, herbal teas and fresh juices daily",
                    iconColor = Color(0xFF4ECDC4)
                ),
                TipItem(
                    icon = "🛌",
                    title = "Prioritize Sleep",
                    description = "Aim for 8 hours nightly, with afternoon rest periods",
                    iconColor = Color(0xFFFF6B6B)
                ),
                TipItem(
                    icon = "👩‍⚕️",
                    title = "Regular Prenatal Care",
                    description = "Follow your doctor's appointment schedule and screenings",
                    iconColor = Color(0xFFFF6B6B)
                ),
                TipItem(
                    icon = "💊",
                    title = "Take Supplements",
                    description = "Daily prenatal vitamins including folic acid as directed",
                    iconColor = Color(0xFFFF6B6B)
                )
            ),
            donts = listOf(
                TipItem(
                    icon = "🚫",
                    title = "Avoid Alcohol",
                    description = "No alcohol consumption during pregnancy",
                    iconColor = Color(0xFFFF4444)
                ),
                TipItem(
                    icon = "🚬",
                    title = "No Smoking",
                    description = "Avoid smoking and secondhand smoke",
                    iconColor = Color(0xFFFF4444)
                ),
                TipItem(
                    icon = "☕",
                    title = "Limit Caffeine",
                    description = "Maximum 200mg caffeine per day (1 cup coffee)",
                    iconColor = Color(0xFFFF8C00)
                ),
                TipItem(
                    icon = "🥩",
                    title = "No Raw Foods",
                    description = "Avoid raw meat, fish, eggs, and unpasteurized dairy",
                    iconColor = Color(0xFFFF4444)
                ),
                TipItem(
                    icon = "🏋️‍♀️",
                    title = "Avoid Heavy Lifting",
                    description = "No lifting objects heavier than 20 pounds",
                    iconColor = Color(0xFFFF4444)
                ),
                TipItem(
                    icon = "🧘‍♀️",
                    title = "Manage Stress",
                    description = "Avoid high-stress situations and practice relaxation",
                    iconColor = Color(0xFFFF8C00)
                )
            )
        ),
        1 to TrimesterTips(
            dos = listOf(
                TipItem(
                    icon = "🥗",
                    title = "Increase Calcium",
                    description = "Include dairy products, leafy greens, and fortified foods",
                    iconColor = Color(0xFF4ECDC4)
                ),
                TipItem(
                    icon = "🚶‍♀️",
                    title = "Continue Exercise",
                    description = "Swimming, prenatal yoga, and walking are excellent",
                    iconColor = Color(0xFFFF6B6B)
                ),
                TipItem(
                    icon = "💆‍♀️",
                    title = "Prenatal Massage",
                    description = "Get professional prenatal massages for relaxation",
                    iconColor = Color(0xFF9B59B6)
                ),
                TipItem(
                    icon = "📚",
                    title = "Educational Classes",
                    description = "Attend childbirth and parenting classes",
                    iconColor = Color(0xFF3498DB)
                ),
                TipItem(
                    icon = "🌡️",
                    title = "Monitor Temperature",
                    description = "Avoid overheating and stay in comfortable temperatures",
                    iconColor = Color(0xFFFF8C00)
                )
            ),
            donts = listOf(
                TipItem(
                    icon = "🐟",
                    title = "Limit High-Mercury Fish",
                    description = "Avoid shark, swordfish, king mackerel, and tilefish",
                    iconColor = Color(0xFFFF4444)
                ),
                TipItem(
                    icon = "🛁",
                    title = "No Hot Tubs",
                    description = "Avoid hot tubs, saunas, and very hot baths",
                    iconColor = Color(0xFFFF4444)
                ),
                TipItem(
                    icon = "💊",
                    title = "No Self-Medication",
                    description = "Don't take medications without doctor approval",
                    iconColor = Color(0xFFFF4444)
                ),
                TipItem(
                    icon = "🏃‍♀️",
                    title = "Avoid High-Impact Sports",
                    description = "No contact sports or activities with fall risk",
                    iconColor = Color(0xFFFF4444)
                ),
                TipItem(
                    icon = "🧽",
                    title = "Avoid Harsh Chemicals",
                    description = "Stay away from cleaning products with strong fumes",
                    iconColor = Color(0xFFFF8C00)
                )
            )
        ),
        2 to TrimesterTips(
            dos = listOf(
                TipItem(
                    icon = "🍽️",
                    title = "Eat Small, Frequent Meals",
                    description = "6 small meals instead of 3 large ones to avoid heartburn",
                    iconColor = Color(0xFF4ECDC4)
                ),
                TipItem(
                    icon = "🚶‍♀️",
                    title = "Gentle Walking",
                    description = "Continue light walking to prepare for labor",
                    iconColor = Color(0xFFFF6B6B)
                ),
                TipItem(
                    icon = "🛍️",
                    title = "Prepare Hospital Bag",
                    description = "Pack essentials for hospital stay by week 36",
                    iconColor = Color(0xFF9B59B6)
                ),
                TipItem(
                    icon = "🛏️",
                    title = "Sleep on Your Side",
                    description = "Use pillows for support, preferably left side",
                    iconColor = Color(0xFFFF6B6B)
                ),
                TipItem(
                    icon = "🤱",
                    title = "Practice Breathing",
                    description = "Learn breathing techniques for labor",
                    iconColor = Color(0xFF3498DB)
                )
            ),
            donts = listOf(
                TipItem(
                    icon = "🛌",
                    title = "Don't Sleep on Back",
                    description = "Avoid sleeping on your back after week 28",
                    iconColor = Color(0xFFFF4444)
                ),
                TipItem(
                    icon = "✈️",
                    title = "Avoid Long Travel",
                    description = "Limit long-distance travel after week 36",
                    iconColor = Color(0xFFFF8C00)
                ),
                TipItem(
                    icon = "🏋️‍♀️",
                    title = "No Strenuous Exercise",
                    description = "Avoid intense workouts and heavy lifting",
                    iconColor = Color(0xFFFF4444)
                ),
                TipItem(
                    icon = "⏰",
                    title = "Don't Ignore Signs",
                    description = "Don't ignore contractions, bleeding, or reduced movement",
                    iconColor = Color(0xFFFF4444)
                ),
                TipItem(
                    icon = "🧘‍♀️",
                    title = "Avoid Stress",
                    description = "Minimize stressful situations and get support",
                    iconColor = Color(0xFFFF8C00)
                )
            )
        )
    )

    Scaffold(
        topBar = {
            DosAndDontsTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Title section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White
            ) {
                Column {
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Gray.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Do's and Don'ts",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE91E63),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Subtitle
            Text(
                text = "Key tips for a healthy pregnancy",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Trimester selector
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(trimesters.size) { index ->
                    Card(
                        modifier = Modifier
                            .clickable { selectedTrimester = index },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedTrimester == index)
                                Color(0xFFE91E63) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = trimesters[index],
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = if (selectedTrimester == index) Color.White else Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.White,
                contentColor = Color(0xFFE91E63),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color(0xFFE91E63)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) Color(0xFFE91E63) else Color.Gray
                            )
                        }
                    )
                }
            }

            // Content
            val currentTips = trimesterData[selectedTrimester]
            val tipsToShow = if (selectedTabIndex == 0) currentTips?.dos else currentTips?.donts

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFF8F9FA)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                tipsToShow?.let { tips ->
                    items(tips) { tip ->
                        TipCard(tip = tip)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosAndDontsTopBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Matri",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = "Care",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE91E63)
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
fun TipCard(tip: TipItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(tip.iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tip.icon,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tip.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = tip.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}