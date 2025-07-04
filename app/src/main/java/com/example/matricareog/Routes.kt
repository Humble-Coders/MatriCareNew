package com.example.matricareog

object Routes {
    const val Welcome1 = "WelcomeScreenone"
    const val Welcome2 = "WelcomeScreentwo"
    const val Welcome3 = "WelcomeScreenThree"
    const val GetStarted = "GetStarted"
    const val AuthChoice = "auth_choice"
    const val Login = "LoginScreen"
    const val Signup = "SignUpScreen"
    const val Home = "HomeScreen"
    const val MaternalGuide = "MaternalGuideScreen"

    // ✅ Use '/{userId}' to define placeholders correctly
    const val MedicalHistory1 = "MedicalHistoryScreenOne/{userId}"
    const val MedicalHistory2 = "MedicalHistoryScreenTwo/{userId}"
    const val ReportAnalysis = "ReportAnalysisScreen/{userId}"

    // ✅ Use these functions to generate complete route strings at runtime
    fun medicalHistory1Route(userId: String) = "MedicalHistoryScreenOne/$userId"
    fun medicalHistory2Route(userId: String) = "MedicalHistoryScreenTwo/$userId"
    fun reportAnalysisRoute(userId: String) = "ReportAnalysisScreen/$userId"
}
