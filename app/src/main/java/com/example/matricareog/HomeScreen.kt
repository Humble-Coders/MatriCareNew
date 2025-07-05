package com.example.matricareog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.Icon as MaterialIcon
import androidx.compose.material.Text as MaterialText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable

@Composable
fun HomeScreen(
    onTrackHealthClicked: () -> Unit = {},
    onMaternalGuideClicked: () -> Unit = {},
    onReportHistoryClicked: () -> Unit = {}
) {
    // Get configuration for screen dimensions
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // Static user data for UI display
    val userName = "Sarah"

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp)
                .padding(bottom = 65.dp) // Increased padding for bottom navigation
                .statusBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp) // Increased spacing between items
        ) {
            item {
                TopBar()
                Spacer(modifier = Modifier.height(12.dp)) // Increased space after top bar
            }

            item {
                Text(
                    text = "Good Morning,",
                    fontSize = 24.sp, // Increased font size
                    color = Color.Black
                )
                Text(
                    text = userName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp, // Increased font size
                    color = Color(0xFFE91E63)
                )
                Spacer(modifier = Modifier.height(12.dp)) // Increased spacing
            }

            // Feature cards list
            items(featureCards) { card ->
                FeatureCard(
                    title = card.title,
                    subtitle = card.subtitle,
                    bgColor = card.bgColor,
                    icon = card.icon,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (card.title == "Track your health with AI") {
                            onTrackHealthClicked()
                        }
                        if (card.title == "Refer the maternal guide") {
                            onMaternalGuideClicked()
                        }
                        if (card.title == "Report History") {
                            onReportHistoryClicked()
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(horizontalFeatureCards) { card ->
                        // Make horizontal cards wider
                        val cardWidth = (screenWidth * 0.68f).coerceAtMost(240.dp)

                        FeatureCard(
                            title = card.title,
                            subtitle = card.subtitle,
                            bgColor = card.bgColor,
                            icon = card.icon,
                            extraActionText = card.extraActionText,
                            modifier = Modifier.width(cardWidth)
                        )
                    }
                }

                // Add a spacer at the bottom to ensure content isn't cut off
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        BottomNavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun FeatureCard(
    title: String,
    subtitle: String,
    bgColor: Color,
    icon: ImageVector,
    extraActionText: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .padding(vertical = 5.dp)
            .clickable(onClick = onClick),
        backgroundColor = bgColor,
        shape = RoundedCornerShape(20.dp),
        elevation = 2.dp // Added slight elevation for better visibility
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp) // Increased padding
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier.size(30.dp) // Larger icon
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp // Increased font size
            )
            Text(
                text = subtitle,
                fontSize = 14.5.sp, // Increased font size
                color = Color.Gray
            )

            extraActionText?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = it,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2E7D32),
                    fontSize = 15.sp // Increased font size
                )
            }
        }
    }
}

data class FeatureCardData(
    val title: String,
    val subtitle: String,
    val bgColor: Color,
    val icon: ImageVector,
    val extraActionText: String? = null
)

// Define feature cards as constants to avoid recreating them in the composable
private val featureCards = listOf(
    FeatureCardData("Track your health with AI", "Personalized health monitoring", Color(0xFFF0F4FF), Icons.Default.Done),
    FeatureCardData("Refer the maternal guide", "Expert guidance and tips", Color(0xFFE8FCE9), Icons.Default.Person),
    FeatureCardData("Ask MomBuddy anything!", "24/7 support assistant", Color(0xFFFFEBF0), Icons.Default.Call),
    FeatureCardData("Report History", "see previous records", Color(0xFFFFF4D9), Icons.Default.AccountBox)
)

private val horizontalFeatureCards = listOf(
    FeatureCardData("Diet Plan", "Healthy meals for you & baby", Color(0xFFEAF4EC), Icons.Default.Favorite, "View Plan"),
    FeatureCardData("Yoga", "Safe workouts", Color(0xFFE6F0FF), Icons.Default.Face, "Start Now"),
    FeatureCardData("Meditation", "Relax your mind", Color(0xFFFFF3E0), Icons.Default.Favorite, "Try Now")
)

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Default profile image
            Image(
                painter = painterResource(id = R.drawable.user),
                contentDescription = "Default Profile",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Matri",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = "Care",
                color = Color(0xFFE91E63),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = Color(0xFFE91E63),
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier
) {
    BottomNavigation(
        backgroundColor = Color.White,
        elevation = 12.dp, // Increased elevation
        modifier = modifier.height(60.dp) // Increased height for better visibility
    ) {
        BottomNavigationItem(
            selected = true,
            onClick = {},
            icon = {
                MaterialIcon(
                    Icons.Default.Home,
                    contentDescription = null,
                    tint = Color(0xFFE91E63),
                    modifier = Modifier.size(26.dp) // Larger icon
                )
            },
            label = {
                MaterialText(
                    "Home",
                    color = Color(0xFFE91E63),
                    fontSize = 12.sp // Increased text size
                )
            }
        )
        BottomNavigationItem(
            selected = false,
            onClick = {},
            icon = {
                MaterialIcon(
                    Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { MaterialText("Favorites", fontSize = 12.sp) }
        )
        BottomNavigationItem(
            selected = false,
            onClick = {},
            icon = {
                MaterialIcon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { MaterialText("Community", fontSize = 12.sp) }
        )
        BottomNavigationItem(
            selected = false,
            onClick = {},
            icon = {
                MaterialIcon(
                    Icons.Outlined.MailOutline,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { MaterialText("Guide", fontSize = 12.sp) }
        )
    }
}