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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import com.example.matricareog.R

// Data class for yoga poses
data class YogaPose(
    val id: String,
    val name: String,
    val sanskritName: String,
    val description: String,
    val imageRes: Int?, // You'll add images later
    val backgroundColor: Color,
    val borderColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YogaExercisesScreen(
    onBackClick: () -> Unit = {}
) {
    var selectedTrimester by remember { mutableStateOf(Trimester.FIRST) }
    var fullScreenImage by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            YogaExercisesTopBar(onBackClick = onBackClick)
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Yoga and Exercises",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE91E63),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Safe poses for each trimester",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trimester Tabs
            YogaTrimesterTabs(
                selectedTrimester = selectedTrimester,
                onTrimesterSelected = { selectedTrimester = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Recommended Yoga Poses Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Recommended Yoga Poses",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE91E63),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Yoga Poses List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                                    items(getYogaPoses(selectedTrimester)) { pose ->
                    YogaPoseCard(
                        pose = pose,
                        onImageClick = { imageRes -> fullScreenImage = imageRes }
                    )
                }

                    // Add bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
        
        // Full-screen image dialog
        if (fullScreenImage != null) {
            FullScreenImageDialog(
                imageRes = fullScreenImage!!,
                onDismiss = { fullScreenImage = null }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YogaExercisesTopBar(
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
fun YogaTrimesterTabs(
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
fun YogaPoseCard(
    pose: YogaPose,
    onImageClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Yoga pose image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                if (pose.imageRes != null) {
                    Image(
                        painter = painterResource(id = pose.imageRes),
                        contentDescription = pose.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onImageClick(pose.imageRes) },
                        contentScale = ContentScale.Crop
                    )
                    
                    // Clickable indicator overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.8f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔍",
                            fontSize = 16.sp
                        )
                    }
                } else {
                    // Fallback background with yoga emoji
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = Color.Gray.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🧘‍♀️",
                            fontSize = 48.sp
                        )
                    }
                }
            }

            // Content section with colored background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = pose.backgroundColor,
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Pose name and sanskrit name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pose.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Text(
                            text = "(${pose.sanskritName})",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    // Pink underline
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(3.dp)
                            .background(
                                color = pose.borderColor,
                                shape = RoundedCornerShape(2.dp)
                            )
                            .padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description
                    Text(
                        text = pose.description,
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.8f),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FullScreenImageDialog(
    imageRes: Int,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        // Close button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(48.dp)
                .background(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = CircleShape
                )
        ) {
            Text(
                text = "✕",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Full-screen image
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Full screen yoga pose",
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentScale = ContentScale.Fit
        )
        
        // Tap to close hint
        Text(
            text = "Tap anywhere to close",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

// Mock data for yoga poses
private fun getYogaPoses(trimester: Trimester): List<YogaPose> {
    return when (trimester) {
        Trimester.FIRST -> listOf(
            YogaPose(
                id = "cat_cow",
                name = "Cat-Cow Pose",
                sanskritName = "Marjaryasana-Bitilasana",
                description = "Gentle spinal movement that helps relieve back tension and improves flexibility. Perfect for early pregnancy.",
                imageRes = R.drawable.catcow,
                backgroundColor = Color(0xFFE3F2FD),
                borderColor = Color(0xFF2196F3)
            ),
            YogaPose(
                id = "child_pose",
                name = "Child's Pose",
                sanskritName = "Balasana",
                description = "A restorative pose that helps reduce stress and fatigue. Promotes relaxation and gentle stretching.",
                imageRes = R.drawable.childs,
                backgroundColor = Color(0xFFF3E5F5),
                borderColor = Color(0xFF9C27B0)
            ),
            YogaPose(
                id = "mountain_pose",
                name = "Mountain Pose",
                sanskritName = "Tadasana",
                description = "Improves posture and balance while strengthening the legs and core. A fundamental standing pose.",
                imageRes = R.drawable.mountain,
                backgroundColor = Color(0xFFE8F5E8),
                borderColor = Color(0xFF4CAF50)
            ),
            YogaPose(
                id = "seated_twist",
                name = "Seated Spinal Twist",
                sanskritName = "Ardha Matsyendrasana",
                description = "Gentle twisting motion helps with digestion and relieves lower back tension. Keep twists gentle.",
                imageRes = R.drawable.seatedspinal,
                backgroundColor = Color(0xFFFFF3E0),
                borderColor = Color(0xFFFF9800)
            )
        )

        Trimester.SECOND -> listOf(
            YogaPose(
                id = "warrior_pose",
                name = "Warrior II Pose",
                sanskritName = "Virabhadrasana II",
                description = "Strengthens the legs, opens the hips, and improves stability. Great for building endurance.",
                imageRes = R.drawable.warrier,
                backgroundColor = Color(0xFFE3F2FD),
                borderColor = Color(0xFF2196F3)
            ),
            YogaPose(
                id = "triangle_pose",
                name = "Triangle Pose",
                sanskritName = "Trikonasana",
                description = "Stretches the sides of the body, improves balance, and strengthens the legs. Use blocks if needed.",
                imageRes = R.drawable.triangle,
                backgroundColor = Color(0xFFF3E5F5),
                borderColor = Color(0xFF9C27B0)
            ),
            YogaPose(
                id = "tree_pose",
                name = "Tree Pose",
                sanskritName = "Vrikshasana",
                description = "Improves balance and focus while strengthening the standing leg. Use wall support if needed.",
                imageRes = R.drawable.tree,
                backgroundColor = Color(0xFFE8F5E8),
                borderColor = Color(0xFF4CAF50)
            ),
            YogaPose(
                id = "goddess_pose",
                name = "Goddess Pose",
                sanskritName = "Utkata Konasana",
                description = "Strengthens the legs and opens the hips. Helps prepare the body for childbirth.",
                imageRes = R.drawable.godess,
                backgroundColor = Color(0xFFE8F5E8),
                borderColor = Color(0xFF4CAF50)
            )
        )

        Trimester.THIRD -> listOf(
            YogaPose(
                id = "butterfly_pose",
                name = "Butterfly Pose",
                sanskritName = "Baddha Konasana",
                description = "Opens the hips and inner thighs, promotes relaxation, and helps prepare for delivery.",
                imageRes = R.drawable.butterfly,
                backgroundColor = Color(0xFFFFF8E1),
                borderColor = Color(0xFFFFEB3B)
            ),
            YogaPose(
                id = "supported_squat",
                name = "Supported Squat",
                sanskritName = "Malasana",
                description = "Helps open the pelvis and strengthen the legs. Use props for support and comfort.",
                imageRes = R.drawable.supportedsquat,
                backgroundColor = Color(0xFFF3E5F5),
                borderColor = Color(0xFF9C27B0)
            ),
            YogaPose(
                id = "legs_up_wall",
                name = "Legs Up the Wall",
                sanskritName = "Viparita Karani",
                description = "Relieves swelling in legs and feet, promotes circulation, and helps with relaxation.",
                imageRes = R.drawable.legs,
                backgroundColor = Color(0xFFE3F2FD),
                borderColor = Color(0xFF2196F3)
            ),
            YogaPose(
                id = "side_lying_savasana",
                name = "Side-Lying Savasana",
                sanskritName = "Parsva Savasana",
                description = "Modified relaxation pose that's comfortable for late pregnancy. Promotes deep rest and calm.",
                imageRes = R.drawable.sidelying,
                backgroundColor = Color(0xFFE8F5E8),
                borderColor = Color(0xFF4CAF50)
            )
        )
    }
}