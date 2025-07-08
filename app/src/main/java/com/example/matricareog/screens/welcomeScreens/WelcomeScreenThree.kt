package com.example.matricareog.screens.welcomeScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@Composable
fun WelcomeScreenThree(
    onSkipClicked: () -> Unit,
    onNextClicked: () -> Unit,
    currentPageIndex: Int
) {
    val pinkColor = Color(0xFFEF5DA8)
    val lightPinkColor = Color(0xFFFFD6E5)
    val backgroundCircleColor1 = Color(0xFFF0F4FF)
    val backgroundCircleColor2 = Color(0xFFFFF0F7)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        // Decorative background circles
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(backgroundCircleColor1)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = 350.dp)
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(backgroundCircleColor2)
                .align(Alignment.BottomStart)
                .offset(x = (-30).dp, y = 60.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // <-- replaces Spacer for vertical spacing
        ) {
            // Top Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 60.dp, bottom = 40.dp)
                ) {
                    Text(
                        text = "Matri",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "care",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = pinkColor
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.welcomescreen03),
                    contentDescription = "Pregnant woman",
                    modifier = Modifier
                        .size(280.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = "Welcome",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 32.dp)
                )

                Text(
                    text = "Welcome to Matricare, your companion through every step of your pregnancy journey.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                )
            }

            // Bottom Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == currentPageIndex) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (index == currentPageIndex) pinkColor else lightPinkColor)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onSkipClicked) {
                        Text(
                            text = "SKIP",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = onNextClicked,
                        modifier = Modifier
                            .width(120.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = pinkColor
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            text = "NEXT",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }


            }
        }
    }
}
