package com.example.matricareog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.matricareog.screens.maternalGuide.MaternalGuideScreen
import com.example.matricareog.screens.GraphReportScreen
import com.example.matricareog.screens.authScreens.LoginScreen
import com.example.matricareog.screens.authScreens.LoginSignup
import com.example.matricareog.screens.authScreens.SignUpScreen
import com.example.matricareog.screens.inputScreens.InputScreenOne
import com.example.matricareog.screens.inputScreens.InputScreenTwo
import com.example.matricareog.screens.ReportAnalysisScreen
import com.example.matricareog.repository.MatriCareRepository
import com.example.matricareog.repository.MedicalHistoryRepository
import com.example.matricareog.repository.ReportRepository
import com.example.matricareog.repository.UserRepository
import com.example.matricareog.screens.maternalGuide.DietPlanScreen
import com.example.matricareog.screens.HomeScreen
import com.example.matricareog.screens.SplashScreen
import com.example.matricareog.screens.maternalGuide.DosAndDontsScreen
import com.example.matricareog.screens.maternalGuide.YogaExercisesScreen
import com.example.matricareog.screens.welcomeScreens.GetStarted
import com.example.matricareog.screens.welcomeScreens.WelcomeScreenThree
import com.example.matricareog.screens.welcomeScreens.WelcomeScreenOne
import com.example.matricareog.screens.welcomeScreens.WelcomeScreenTwo
import com.example.matricareog.ui.theme.MatricareogTheme
import com.example.matricareog.viewmodels.AuthViewModel
import com.example.matricareog.viewmodels.MatriCareViewModel
import com.example.matricareog.viewmodels.MedicalHistoryViewModel
import com.example.matricareog.viewmodels.ReportAnalysisViewModel
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
    private lateinit var reportAnalysisViewModel: ReportAnalysisViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize Repositories
        userRepository = UserRepository(
            auth, firestore,
            dataStoreManager = DataStoreManager(applicationContext)
        )
        matriCareRepository = MatriCareRepository(auth, firestore)
        medicalHistoryRepository = MedicalHistoryRepository(firestore)
        reportRepository = ReportRepository()

        // Initialize ViewModels
        authViewModel = AuthViewModel(userRepository)
        matriCareViewModel = MatriCareViewModel(matriCareRepository)
        medicalHistoryViewModel = MedicalHistoryViewModel(medicalHistoryRepository)
        reportAnalysisViewModel = ReportAnalysisViewModel(reportRepository)

        setContent {
            MatricareogTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val currentUser by authViewModel.currentUser.collectAsState()

                    NavHost(
                        navController = navController,
                        startDestination = Routes.Splash
                    ) {
                        // ----------------------
                        // Welcome Flow
                        // ----------------------
                        composable(Routes.Splash) {
                            SplashScreen(
                                onNavigateToHome = {
                                    navController.navigate(Routes.Home) {
                                        popUpTo(Routes.Splash) { inclusive = true }
                                    }
                                },
                                onNavigateToWelcome = {
                                    navController.navigate(Routes.Welcome1) {
                                        popUpTo(Routes.Splash) { inclusive = true }
                                    }
                                },
                                authViewModel = authViewModel
                            )
                        }

                        composable(Routes.Welcome1) {
                            WelcomeScreenOne(
                                onNextClicked = { navController.navigate(Routes.Welcome2) },
                                onSkipClicked = { navController.navigate(Routes.GetStarted) },
                                currentPageIndex = 0
                            )
                        }
                        composable(Routes.Welcome2) {
                            WelcomeScreenTwo(
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
                        composable(Routes.AuthChoice) {
                            LoginSignup(
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
                                authViewModel = authViewModel,
                                onTrackHealthClicked = {
                                    navController.navigate(Routes.medicalHistory1Route(userId))
                                },
                                onMaternalGuideClicked = {
                                    navController.navigate(Routes.MaternalGuide)
                                },
                                onReportHistoryClicked = {
                                    navController.navigate(Routes.MATRICARE)
                                },
                                onLogoutClicked = {
                                    navController.navigate(Routes.AuthChoice) {
                                        popUpTo(Routes.Home) { inclusive = true }
                                    }
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
                            InputScreenOne(
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
                            InputScreenTwo(
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
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onShareClick = { shareReport(userId) },
                                reportViewModel = reportAnalysisViewModel,
                                medicalHistoryViewModel = medicalHistoryViewModel,
                                authViewModel= authViewModel
                            )
                        }

                        // ----------------------
                        // MatriCare Screen
                        // ----------------------
                        composable(route = Routes.MATRICARE) {
                            GraphReportScreen(
                                onBackClick = { navController.popBackStack() },
                                viewModel = matriCareViewModel
                            )
                        }

                        composable(Routes.MaternalGuide) {
                            MaternalGuideScreen(
                                onBackClick = { navController.popBackStack() },
                                onCardClick = { cardId ->
                                    when (cardId) {
                                        "diet_plan" -> navController.navigate(Routes.DietPlan)
                                        "yoga_exercises" -> navController.navigate(Routes.YogaExercises)
                                        "dos_donts" -> navController.navigate(Routes.DosAndDonts)
                                    }
                                }
                            )
                        }

// Add the new Diet Plan composable in NavHost:

                        composable(Routes.DietPlan) {
                            DietPlanScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable(Routes.DosAndDonts) {
                            DosAndDontsScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }

// Add the new Yoga & Exercises composable in NavHost:

                        composable(Routes.YogaExercises) {
                            YogaExercisesScreen(
                                onBackClick = { navController.popBackStack() }
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
            Log.e("MainActivity", "Error sharing report", e)
        }
    }
}

object Routes {
    const val Splash = "SplashScreen" // Add this line
    const val Welcome1 = "WelcomeScreenOne"
    const val Welcome2 = "WelcomeScreenTwo"
    const val Welcome3 = "WelcomeScreenThree"
    const val GetStarted = "GetStarted"
    const val AuthChoice = "auth_choice"
    const val Login = "LoginScreen"
    const val Signup = "SignUpScreen"
    const val Home = "HomeScreen"
    const val MaternalGuide = "MaternalGuideScreen"

    // Add new routes for maternal guide sections
    const val DietPlan = "DietPlanScreen"
    const val YogaExercises = "YogaExercisesScreen"
    const val DosAndDonts = "DosAndDontsScreen"

    // Existing routes
    const val MedicalHistory1 = "WelcomeScreenOne/{userId}"
    const val MedicalHistory2 = "WelcomeScreenTwo/{userId}"
    const val ReportAnalysis = "ReportAnalysisScreen/{userId}"
    const val MATRICARE = "GraphReportScreen"

    fun medicalHistory1Route(userId: String) = "WelcomeScreenOne/$userId"
    fun medicalHistory2Route(userId: String) = "WelcomeScreenTwo/$userId"
    fun reportAnalysisRoute(userId: String) = "ReportAnalysisScreen/$userId"
}