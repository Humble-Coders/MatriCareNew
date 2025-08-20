package com.example.matricareog.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matricareog.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToReports: (String) -> Unit = {},
    onTrackHealthClicked: () -> Unit = {},
    onMaternalGuideClicked: () -> Unit = {},
    onReportHistoryClicked: () -> Unit = {},
    authViewModel: AuthViewModel,
    onLogoutClicked: () -> Unit,
    onDietClicked: () -> Unit,
    onYogaClicked: () -> Unit,
    onDoClicked: () -> Unit,
    onChatbotClicked: () -> Unit
) {
    val currentUserState = authViewModel.currentUser.collectAsState()
    val currentUser = currentUserState.value
    val userName = currentUser?.fullName?.split(" ")?.firstOrNull() ?: "User"
    val userId = currentUser?.uid ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F9FA),
                        Color(0xFFFFFFFF)
                    )
                )
            )
    ) {
        // Header Section
        HeaderSection(
            authViewModel = authViewModel,
            onLogoutClicked = onLogoutClicked
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Welcome Section
            item {
                WelcomeSection(userName = userName)
            }

            // Health Tracking Card
            item {
                HealthTrackingCard(onClick = onTrackHealthClicked)
            }

            // Quick Actions Section
            item {
                QuickActionsSection(
                    onMaternalGuideClicked = onMaternalGuideClicked,
                    onReportHistoryClicked = onReportHistoryClicked,
                    onNavigateToReports = onNavigateToReports,
                    userId = userId,
                    onChatbotClicked = onChatbotClicked
                )
            }

            // Wellness Programs Section
            item {
                WellnessProgramsSection(
                    onDietClicked = onDietClicked,
                    onYogaClicked = onYogaClicked,
                    onDoClicked = onDoClicked
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun HeaderSection(
    authViewModel: AuthViewModel,
    onLogoutClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Card(
                shape = CircleShape,
                modifier = Modifier.size(48.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color(0xFFE91E63),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "MatriCare",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "Your wellness companion",
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
            }
        }

        IconButton(
            onClick = {
                authViewModel.logout()
                onLogoutClicked()
            },
            modifier = Modifier
                .background(
                    Color(0xFFFFF0F3),
                    RoundedCornerShape(12.dp)
                )
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Logout",
                tint = Color(0xFFE91E63),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun WelcomeSection(userName: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Good morning,",
            fontSize = 16.sp,
            color = Color(0xFF666666),
            fontWeight = FontWeight.Normal
        )
        Text(
            text = userName,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "How are you feeling today?",
            fontSize = 14.sp,
            color = Color(0xFF888888)
        )
    }
}

@Composable
private fun HealthTrackingCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE91E63)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFE91E63),
                            Color(0xFFAD1457)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Track Your Health",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "AI-powered health monitoring",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Start tracking",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.MonitorHeart,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickActionsSection(
    onMaternalGuideClicked: () -> Unit,
    onReportHistoryClicked: () -> Unit,
    onNavigateToReports: (String) -> Unit,
    onChatbotClicked: () -> Unit, // Add this parameter
    userId: String
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Quick Actions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "Access your essentials",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }

            Icon(
                imageVector = Icons.Default.GridView,
                contentDescription = null,
                tint = Color(0xFFE91E63),
                modifier = Modifier.size(24.dp)
            )
        }

        // Update to show 3 cards in a row
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EnhancedQuickActionCard(
                    title = "Maternal Guide",
                    subtitle = "Expert guidance & tips",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    bgGradient = listOf(Color(0xFFE8F5E8), Color(0xFFF1F8E9)),
                    iconBgColor = Color(0xFF2E7D32),
                    iconColor = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = onMaternalGuideClicked
                )

                EnhancedQuickActionCard(
                    title = "Report History",
                    subtitle = "View past records",
                    icon = Icons.Default.Assessment,
                    bgGradient = listOf(Color(0xFFF0F4FF), Color(0xFFE3F2FD)),
                    iconBgColor = Color(0xFF1976D2),
                    iconColor = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onReportHistoryClicked()
                        onNavigateToReports(userId)
                    }
                )
            }
        }
    }
}

@Composable
private fun EnhancedQuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    bgGradient: List<Color>,
    iconBgColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(bgGradient),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon container with circular background
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = iconBgColor,
                            shape = CircleShape
                        )
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = Color.White.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Open",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = iconBgColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = iconBgColor,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WellnessProgramsSection(
    onDietClicked: () -> Unit,
    onYogaClicked: () -> Unit,
    onDoClicked: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Wellness Programs",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "Your daily wellness routine",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }

            Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = null,
                tint = Color(0xFFE91E63),
                modifier = Modifier.size(24.dp)
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(
                listOf(
                    WellnessProgramData(
                        title = "Diet Plan",
                        subtitle = "Healthy meals for you & baby",
                        bgGradient = listOf(Color(0xFFF0F8F0), Color(0xFFE8F5E8)),
                        iconBgColor = Color(0xFF2E7D32),
                        icon = Icons.Default.Restaurant,
                        badge = "Daily",
                        onClick = onDietClicked
                    ),
                    WellnessProgramData(
                        title = "Yoga & Exercise",
                        subtitle = "Safe workouts for pregnancy",
                        bgGradient = listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7)),
                        iconBgColor = Color(0xFF7B1FA2),
                        icon = Icons.Default.SelfImprovement,
                        badge = "15 min",
                        onClick = onYogaClicked
                    ),
                    WellnessProgramData(
                        title = "Do's & Don'ts",
                        subtitle = "Essential pregnancy tips and advices",
                        bgGradient = listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3)),
                        iconBgColor = Color(0xFFE65100),
                        icon = Icons.Default.Lightbulb,
                        badge = "Tips",
                        onClick = onDoClicked
                    )
                )
            ) { program ->
                EnhancedWellnessProgramCard(
                    program = program,
                    modifier = Modifier.width(220.dp) // Fixed width for consistency
                )
            }
        }
    }
}

@Composable
private fun EnhancedWellnessProgramCard(
    program: WellnessProgramData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { program.onClick() }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(program.bgGradient),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            // Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = program.badge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = program.iconBgColor
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top section with icon
                Column {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = program.iconBgColor,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = program.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = program.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = program.subtitle,
                        fontSize = 13.sp,
                        color = Color(0xFF666666),
                        lineHeight = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Bottom section with action button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.White.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Start Now",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = program.iconBgColor
                    )

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = program.iconBgColor,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// Updated data class
data class WellnessProgramData(
    val title: String,
    val subtitle: String,
    val bgGradient: List<Color>,
    val iconBgColor: Color,
    val icon: ImageVector,
    val badge: String,
    val onClick: () -> Unit
)