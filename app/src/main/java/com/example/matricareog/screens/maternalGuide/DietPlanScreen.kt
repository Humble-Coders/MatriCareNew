package com.example.matricareog.screens.maternalGuide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data classes for diet recommendations
data class DietRecommendation(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val borderColor: Color,
    val backgroundColor: Color
)

enum class Trimester(val displayName: String) {
    FIRST("1st Trimester"),
    SECOND("2nd Trimester"),
    THIRD("3rd Trimester")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietPlanScreen(
    onBackClick: () -> Unit = {}
) {
    var selectedTrimester by remember { mutableStateOf(Trimester.FIRST) }
    var isRecommendationsExpanded by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            DietPlanTopBar(onBackClick = onBackClick)
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
                            text = "Diet Plan",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE91E63),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trimester Tabs
            TrimesterTabs(
                selectedTrimester = selectedTrimester,
                onTrimesterSelected = { selectedTrimester = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Recommendations Section
            RecommendationsSection(
                isExpanded = isRecommendationsExpanded,
                onExpandClick = { isRecommendationsExpanded = !isRecommendationsExpanded },
                selectedTrimester = selectedTrimester
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietPlanTopBar(
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
                    tint = Color(0xFFE91E63)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
fun TrimesterTabs(
    selectedTrimester: Trimester,
    onTrimesterSelected: (Trimester) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Trimester.values().forEach { trimester ->
            val isSelected = selectedTrimester == trimester

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                TextButton(
                    onClick = { onTrimesterSelected(trimester) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (isSelected) Color(0xFFE91E63) else Color.Gray
                    )
                ) {
                    Text(
                        text = trimester.displayName,
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(3.dp)
                            .background(
                                color = Color(0xFFE91E63),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendationsSection(
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    selectedTrimester: Trimester
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Recommendations Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE91E63)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recommendations",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )


            }
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(16.dp))

            // Diet Recommendations List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(getDietRecommendations(selectedTrimester)) { recommendation ->
                    DietRecommendationCard(recommendation = recommendation)
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                }
            }

        }
    }
}

@Composable
fun DietRecommendationCard(
    recommendation: DietRecommendation
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left colored border
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(80.dp)
                    .background(
                        color = recommendation.borderColor,
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            bottomStart = 12.dp,
                            topEnd = 3.dp,
                            bottomEnd = 3.dp
                        )
                    )
            )

            // Content
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
                        .background(
                            color = recommendation.backgroundColor,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = recommendation.icon,
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Text content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = recommendation.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = recommendation.description,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// Mock data for diet recommendations
private fun getDietRecommendations(trimester: Trimester): List<DietRecommendation> {
    return when (trimester) {
        Trimester.FIRST -> listOf(
            DietRecommendation(
                id = "healthy_foods",
                title = "Eat Healthy Foods",
                description = "Include Fruits, Vegetables, Whole Grains And Dairy Products",
                icon = "🥗",
                borderColor = Color(0xFF4CAF50),
                backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
            ),
            DietRecommendation(
                id = "prenatal_vitamins",
                title = "Take Prenatal Vitamins",
                description = "Take Folic Acid Supplements Daily",
                icon = "💊",
                borderColor = Color(0xFF9C27B0),
                backgroundColor = Color(0xFF9C27B0).copy(alpha = 0.2f)
            ),
            DietRecommendation(
                id = "stay_hydrated",
                title = "Stay Hydrated",
                description = "Drink Plenty Of Water, Coconut Water And Fresh Juices",
                icon = "💧",
                borderColor = Color(0xFFFF9800),
                backgroundColor = Color(0xFFFF9800).copy(alpha = 0.2f)
            ),
            DietRecommendation(
                id = "exercise_gently",
                title = "Exercise Gently",
                description = "Walk, Do Prenatal Yoga Or Light Stretching",
                icon = "💙",
                borderColor = Color(0xFF2196F3),
                backgroundColor = Color(0xFF2196F3).copy(alpha = 0.2f)
            ),
            DietRecommendation(
                id = "plenty_rest",
                title = "Get Plenty Of Rest",
                description = "Take Naps And Ensure 8 Hours Of Sleep At Night",
                icon = "🛏️",
                borderColor = Color(0xFFFFEB3B),
                backgroundColor = Color(0xFFFFEB3B).copy(alpha = 0.2f)
            ),
            DietRecommendation(
                id = "doctor_visits",
                title = "Regular Doctor Visits",
                description = "Visit Doctor Regularly",
                icon = "👨‍⚕️",
                borderColor = Color(0xFF9C27B0),
                backgroundColor = Color(0xFF9C27B0).copy(alpha = 0.2f)
            )
        )

        Trimester.SECOND -> listOf(
            DietRecommendation(
                id = "calcium_rich",
                title = "Increase Calcium Intake",
                description = "Include Dairy Products, Leafy Greens And Fortified Foods",
                icon = "🥛",
                borderColor = Color(0xFF4CAF50),
                backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
            ),
            DietRecommendation(
                id = "protein_foods",
                title = "Eat Protein-Rich Foods",
                description = "Include Lean Meats, Fish, Eggs, Beans And Nuts",
                icon = "🥩",
                borderColor = Color(0xFF9C27B0),
                backgroundColor = Color(0xFF9C27B0).copy(alpha = 0.2f)
            ),
            DietRecommendation(
                id = "iron_rich",
                title = "Iron-Rich Foods",
                description = "Include Spinach, Red Meat, And Iron Supplements",
                icon = "🥬",
                borderColor = Color(0xFFFF9800),
                backgroundColor = Color(0xFFFF9800).copy(alpha = 0.2f)
            ),
            DietRecommendation(
                id = "moderate_exercise",
                title = "Moderate Exercise",
                description = "Swimming, Walking And Prenatal Classes",
                icon = "🏊‍♀️",
                borderColor = Color(0xFF2196F3),
                backgroundColor = Color(0xFF2196F3).copy(alpha = 0.2f)
            )
        )

        Trimester.THIRD -> listOf(
            DietRecommendation(
                id = "small_meals",
                title = "Eat Small Frequent Meals",
                description = "Avoid Large Meals To Prevent Heartburn",
                icon = "🍽️",
                borderColor = Color(0xFF4CAF50),
                backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
            ),
            DietRecommendation(
                id = "fiber_rich",
                title = "High-Fiber Foods",
                description = "Include Whole Grains, Fruits And Vegetables",
                icon = "🌾",
                borderColor = Color(0xFF9C27B0),
                backgroundColor = Color(0xFF9C27B0).copy(alpha = 0.2f)
            ),
            DietRecommendation(
                id = "reduce_sodium",
                title = "Limit Sodium Intake",
                description = "Reduce Salt To Prevent Swelling And High Blood Pressure",
                icon = "🧂",
                borderColor = Color(0xFFFF9800),
                backgroundColor = Color(0xFFFF9800).copy(alpha = 0.2f)
            ),
            DietRecommendation(
                id = "light_exercise",
                title = "Light Exercise",
                description = "Gentle Walking And Breathing Exercises",
                icon = "🚶‍♀️",
                borderColor = Color(0xFF2196F3),
                backgroundColor = Color(0xFF2196F3).copy(alpha = 0.2f)
            )
        )
    }
}