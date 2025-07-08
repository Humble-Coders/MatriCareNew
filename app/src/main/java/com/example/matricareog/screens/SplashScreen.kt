package com.example.matricareog.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matricareog.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToWelcome: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val primaryPink = Color(0xFFE91E63)

    LaunchedEffect(Unit) {
        delay(2000) // Show splash for 2 seconds
        authViewModel.checkAuthState()
    }

    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(authState, currentUser) {
        when {
            currentUser != null -> onNavigateToHome()
            authState.toString().contains("Not authenticated") -> onNavigateToWelcome()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Text(
                text = "Matri",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "care",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = primaryPink
            )
        }

        // Loading indicator
        CircularProgressIndicator(
            color = primaryPink,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Loading...",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}