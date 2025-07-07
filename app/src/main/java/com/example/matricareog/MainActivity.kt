package com.example.matricareog

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.matricare.MaternalGuideScreen
import com.example.matricareog.graphscreen.MatriCareScreen
import com.example.matricareog.loginsignupscreen.LoginScreen
import com.example.matricareog.loginsignupscreen.LoginSignupopt
import com.example.matricareog.loginsignupscreen.SignUpScreen
import com.example.matricareog.medicalhistoryscreens.MedicalHistoryScreenOne
import com.example.matricareog.medicalhistoryscreens.MedicalHistoryScreenTwo
import com.example.matricareog.reportscreen.ReportAnalysisScreen
import com.example.matricareog.repository.MatriCareRepository
import com.example.matricareog.repository.MedicalHistoryRepository
import com.example.matricareog.repository.ReportRepository
import com.example.matricareog.repository.UserRepository
import com.example.matricareog.screens.welcomescreen.GetStarted
import com.example.matricareog.screens.welcomescreen.WelcomeScreenThree
import com.example.matricareog.screens.welcomescreen.WelcomeScreenone
import com.example.matricareog.screens.welcomescreen.WelcomeScreentwo
import com.example.matricareog.ui.theme.MatricareogTheme
import com.example.matricareog.viewmodels.AuthViewModel
import com.example.matricareog.viewmodels.MatriCareViewModel
import com.example.matricareog.viewmodels.MedicalHistoryViewModel
import com.example.matricareog.viewmodels.ReportViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var userRepository: UserRepository
    private lateinit var matriCareRepository: MatriCareRepository
    private lateinit var medicalHistoryRepository: MedicalHistoryRepository
    private lateinit var reportRepository: ReportRepository

    private lateinit var authViewModel: AuthViewModel
    private lateinit var matriCareViewModel: MatriCareViewModel
    private lateinit var medicalHistoryViewModel: MedicalHistoryViewModel
    private lateinit var reportAnalysisViewModel: ReportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize Repositories
        userRepository = UserRepository(auth, firestore)
        matriCareRepository = MatriCareRepository(auth, firestore)
        medicalHistoryRepository = MedicalHistoryRepository(firestore)
        reportRepository = ReportRepository(firestore)

        // Initialize ViewModels
        authViewModel = AuthViewModel(userRepository)
        matriCareViewModel = MatriCareViewModel(matriCareRepository)
        medicalHistoryViewModel = MedicalHistoryViewModel(medicalHistoryRepository)
        reportAnalysisViewModel = ReportViewModel(reportRepository)

        setContent {
            MatricareogTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val currentUser by authViewModel.currentUser.collectAsState()

                    NavHost(
                        navController = navController,
                        startDestination = if (currentUser != null) Routes.Home else Routes.Welcome1
                    ) {
                        // ----------------------
                        // Welcome Flow
                        // ----------------------
                        composable(Routes.Welcome1) {
                            WelcomeScreenone(
                                onNextClicked = { navController.navigate(Routes.Welcome2) },
                                onSkipClicked = { navController.navigate(Routes.GetStarted) },
                                currentPageIndex = 0
                            )
                        }
                        composable(Routes.Welcome2) {
                            WelcomeScreentwo(
                                onNextClicked = { navController.navigate(Routes.Welcome3) },
                                onSkipClicked = { navController.navigate(Routes.GetStarted) },
                                currentPageIndex = 1
                            )
                        }
                        composable(Routes.Welcome3) {
                            WelcomeScreenThree(
                                onNextClicked = { navController.navigate(Routes.GetStarted) },
                                onSkipClicked = { navController.navigate(Routes.GetStarted) },
                                currentPageIndex = 2
                            )
                        }
                        composable(Routes.GetStarted) {
                            GetStarted(
                                onGetStartedClick = { navController.navigate(Routes.AuthChoice) },
                                currentPageIndex = 3
                            )
                        }

                        // ----------------------
                        // Authentication Flow
                        // ----------------------
                        composable(Routes.AuthChoice) {
                            LoginSignupopt(
                                onSignUpClick = { navController.navigate(Routes.Signup) },
                                onLogInClick = { navController.navigate(Routes.Login) }
                            )
                        }
                        composable(Routes.Login) {
                            LoginScreen(
                                onNavigateToSignUp = { navController.navigate(Routes.Signup) },
                                onNavigateToHome = {
                                    navController.navigate(Routes.Home) {
                                        popUpTo(Routes.Welcome1) { inclusive = true }
                                    }
                                },
                                authViewModel = authViewModel
                            )
                        }
                        composable(Routes.Signup) {
                            SignUpScreen(
                                onNavigateToLogin = { navController.popBackStack() },
                                onNavigateToHome = {
                                    navController.navigate(Routes.Home) {
                                        popUpTo(Routes.Welcome1) { inclusive = true }
                                    }
                                },
                                authViewModel = authViewModel
                            )
                        }

                        // ----------------------
                        // Home or Main App Screens
                        // ----------------------
                        composable(Routes.Home) {
                            val userId = currentUser?.uid ?: ""
                            HomeScreen(
                                matriCareViewModel = matriCareViewModel,
                                onTrackHealthClicked = {
                                    navController.navigate(Routes.medicalHistory1Route(userId))
                                },
                                onMaternalGuideClicked = {
                                    navController.navigate(Routes.MaternalGuide)
                                },
                                onReportHistoryClicked = {
                                    navController.navigate(Routes.MATRICARE)
                                }
                            )
                        }

                        // ----------------------
                        // Medical History Screens
                        // ----------------------
                        composable(
                            route = Routes.MedicalHistory1,
                            arguments = listOf(navArgument("userId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId") ?: ""
                            MedicalHistoryScreenOne(
                                userId = userId,
                                navigateBack = { navController.popBackStack() },
                                navigateToScreenTwo = {
                                    navController.navigate(Routes.medicalHistory2Route(userId))
                                },
                                viewModel = medicalHistoryViewModel
                            )
                        }

                        composable(
                            route = Routes.MedicalHistory2,
                            arguments = listOf(navArgument("userId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId") ?: ""
                            MedicalHistoryScreenTwo(
                                userId = userId,
                                onBackPressed = { navController.popBackStack()
                                                },
                                onContinuePressed = {
                                    navController.navigate(Routes.reportAnalysisRoute(userId))
                                },
                                viewModel = medicalHistoryViewModel
                            )
                        }

                        // ----------------------
                        // Report Analysis Screen
                        // ----------------------
                        composable(
                            route = Routes.ReportAnalysis,
                            arguments = listOf(navArgument("userId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId") ?: ""
                            ReportAnalysisScreen(
                                userId = userId,
                                onBackClick = { navController.popBackStack() },
                                onShareClick = { shareReport(userId) },
                                reportViewModel = reportAnalysisViewModel,
                                medicalHistoryViewModel = medicalHistoryViewModel
                            )
                        }

                        // ----------------------
                        // MatriCare Screen
                        // ----------------------
                        composable(route = Routes.MATRICARE) {
                            MatriCareScreen(
                                onBackClick = { navController.popBackStack() },
                                viewModel = matriCareViewModel
                            )
                        }

                        // ----------------------
                        // Maternal Guide Screen
                        // ----------------------
                        composable(Routes.MaternalGuide) {
                            MaternalGuideScreen(
                                onBackClick = { navController.popBackStack() },
                                onCardClick = { cardId ->
                                    // Handle individual card clicks
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Share functionality
    private fun shareReport(userId: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out my health report from MatriCare!")
            putExtra(Intent.EXTRA_SUBJECT, "Health Report - MatriCare")
        }

        try {
            startActivity(Intent.createChooser(shareIntent, "Share Report"))
        } catch (e: Exception) {
            // Handle error - could show a toast or snackbar
            android.util.Log.e("MainActivity", "Error sharing report", e)
        }
    }
}