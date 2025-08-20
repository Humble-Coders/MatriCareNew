package com.example.matricareog.screens.maternalGuide

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matricareog.R

// Data class for guide items (unchanged)
data class GuideItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageRes: Int?,
    val borderColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaternalGuideScreen(
    onCardClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val guideItems = listOf(
        GuideItem(
            id = "diet_plan",
            title = "Diet Plan",
            subtitle = "Nutrition guide for you and your baby",
            imageRes = R.drawable.diet,
            borderColor = Color(0xFF4CAF50)
        ),
        GuideItem(
            id = "yoga_exercises",
            title = "Yoga & Exercises",
            subtitle = "Safe workouts for each trimester",
            imageRes = R.drawable.yoga,
            borderColor = Color(0xFF2196F3)
        ),
        GuideItem(
            id = "dos_donts",
            title = "Do's and Don'ts",
            subtitle = "Essential guidelines for a healthy pregnancy",
            imageRes = R.drawable.dosdonts,
            borderColor = Color(0xFF9C27B0)
        )
    )

    Scaffold(
        topBar = {
            MaternalGuideTopBar(onBackClick = onBackClick)
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
            // Title section with pink accent similar to ReportAnalysisScreen
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
                            text = "Maternal Guide",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE91E63),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }



            // Guide Cards
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(guideItems) { item ->
                    StyledGuideCard(
                        item = item,
                        onClick = { onCardClick(item.id) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Add bottom spacing
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaternalGuideTopBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title =  {
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
                        text = "Care    ",
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
fun StyledGuideCard(
    item: GuideItem,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Enhanced colored left border with rounded vertices
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(120.dp)
                    .background(
                        color = item.borderColor,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            bottomStart = 16.dp,
                            topEnd = 4.dp,
                            bottomEnd = 4.dp
                        )
                    )
            )

            // Card content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.imageRes != null) {
                        Image(
                            painter = painterResource(id = item.imageRes),
                            contentDescription = item.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Placeholder with emoji based on card type
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Color.Gray.copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            val emoji = when (item.id) {
                                "diet_plan" -> "🥗"
                                "yoga_exercises" -> "🧘‍♀️"
                                "dos_donts" -> "📋"
                                else -> "📱"
                            }
                            Text(
                                text = emoji,
                                fontSize = 32.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Text content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.subtitle,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Circular arrow background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = item.borderColor.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Navigate",
                        tint = item.borderColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}