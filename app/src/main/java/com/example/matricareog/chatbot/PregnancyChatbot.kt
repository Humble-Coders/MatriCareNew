package com.example.matricareog.chatbot

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

data class ChatResponse(
    val answer: String,
    val matchedQuestion: String?,
    val similarityScore: Double,
    val confidence: String
)

// Comprehensive error handling for chatbot
sealed class ChatbotError : Exception() {
    object NetworkError : ChatbotError()
    object ApiError : ChatbotError()
    object InvalidInputError : ChatbotError()
    data class ServerError(val code: Int, override val message: String) : ChatbotError()
    data class UnknownError(val exception: Exception) : ChatbotError()
}

class PregnancyChatbot private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: PregnancyChatbot? = null
        private const val TAG = "PregnancyChatbot"

        fun getInstance(): PregnancyChatbot {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PregnancyChatbot().also { INSTANCE = it }
            }
        }
    }

    private val apiService = ApiClient.apiService
    private var isApiReady = false

    // Initialize the chatbot by checking API health
    suspend fun initialize(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🚀 Initializing Pregnancy Chatbot with API...")

                val isHealthy = checkApiHealth()
                isApiReady = isHealthy // Always update the ready state

                if (isHealthy) {
                    Log.d(TAG, "✅ Pregnancy Chatbot initialized successfully with API!")
                } else {
                    Log.e(TAG, "❌ API health check failed, but will retry on first message")
                }

                // Return true even if initial health check fails - we'll retry later
                true
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to initialize chatbot: ${e.message}", e)
                isApiReady = false
                // Still return true - initialization completed, just not healthy
                true
            }
        }
    }

    private suspend fun checkApiHealth(): Boolean {
        return try {
            Log.d(TAG, "📡 Checking API health...")
            val response = apiService.getHealth()

            if (response.isSuccessful) {
                val healthStatus = response.body()
                Log.d(TAG, "✅ API Health: ${healthStatus?.status}")
                Log.d(TAG, "📊 Questions loaded: ${healthStatus?.questions_loaded}")
                Log.d(TAG, "🤖 Model loaded: ${healthStatus?.model_loaded}")
                Log.d(TAG, "🔥 Firebase connected: ${healthStatus?.firebase_connected}")

                healthStatus?.status == "healthy" &&
                        healthStatus.model_loaded &&
                        healthStatus.firebase_connected
            } else {
                Log.e(TAG, "❌ API health check failed: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error checking API health: ${e.message}", e)
            false
        }
    }

    // Get response for user question using API with comprehensive error handling
    suspend fun getResponse(userQuestion: String): ChatResponse {
        return withContext(Dispatchers.Default) {
            if (userQuestion.isBlank()) {
                return@withContext ChatResponse(
                    answer = "Please ask me a question about pregnancy!",
                    matchedQuestion = null,
                    similarityScore = 0.0,
                    confidence = "Low"
                )
            }

            try {
                Log.d(TAG, "🤔 Processing question: '${userQuestion.trim()}'")

                // Check API health if not ready
                if (!isApiReady) {
                    Log.d(TAG, "API not ready, attempting health check...")
                    val healthCheckSuccess = checkApiHealth()
                    isApiReady = healthCheckSuccess

                    if (!healthCheckSuccess) {
                        return@withContext ChatResponse(
                            answer = "I'm having trouble connecting to the service. Please check your internet connection and try the refresh button.",
                            matchedQuestion = null,
                            similarityScore = 0.0,
                            confidence = "Error"
                        )
                    }
                }

                val request = ChatbotRequest(
                    question = userQuestion.trim(),
                    threshold = 0.3
                )

                val response = apiService.askQuestion(request)

                if (response.isSuccessful) {
                    val chatbotResponse = response.body()

                    if (chatbotResponse?.success == true && !chatbotResponse.answer.isNullOrBlank()) {
                        Log.d(TAG, "✅ API Response received successfully")
                        Log.d(TAG, "📊 Matched question: '${chatbotResponse.matched_question?.take(30)}...'")
                        Log.d(TAG, "📊 Similarity score: ${chatbotResponse.similarity_score}")
                        Log.d(TAG, "📊 Confidence: ${chatbotResponse.confidence}")

                        // Mark as ready since we got a successful response
                        isApiReady = true

                        ChatResponse(
                            answer = chatbotResponse.answer,
                            matchedQuestion = chatbotResponse.matched_question,
                            similarityScore = chatbotResponse.similarity_score ?: 0.0,
                            confidence = chatbotResponse.confidence ?: "Unknown"
                        )
                    } else {
                        Log.w(TAG, "⚠️ API returned unsuccessful response or empty answer")
                        ChatResponse(
                            answer = chatbotResponse?.error ?: "I'm sorry, I don't have specific information about that. Please try rephrasing your question or ask about pregnancy-related topics like nutrition, exercise, symptoms, or medical care.",
                            matchedQuestion = null,
                            similarityScore = 0.0,
                            confidence = "Low"
                        )
                    }
                } else {
                    Log.e(TAG, "❌ API request failed: ${response.code()} - ${response.message()}")
                    isApiReady = false
                    
                    val errorMessage = when (response.code()) {
                        401 -> "Authentication failed. Please try logging in again."
                        403 -> "Access denied. Please check your permissions."
                        404 -> "Service not found. Please try again later."
                        500 -> "Server error. Please try again in a few minutes."
                        503 -> "Service temporarily unavailable. Please try again later."
                        else -> "I'm sorry, I'm having trouble connecting to the service. Please try again in a moment or use the refresh button."
                    }
                    
                    ChatResponse(
                        answer = errorMessage,
                        matchedQuestion = null,
                        similarityScore = 0.0,
                        confidence = "Error"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error processing question: ${e.message}", e)
                isApiReady = false
                
                val errorMessage = when (e) {
                    is UnknownHostException -> "No internet connection. Please check your network settings."
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is ConnectException -> "Unable to connect to the service. Please try again later."
                    else -> "I'm sorry, I encountered an error while processing your question. Please check your internet connection and try again."
                }
                
                ChatResponse(
                    answer = errorMessage,
                    matchedQuestion = null,
                    similarityScore = 0.0,
                    confidence = "Error"
                )
            }
        }
    }

    // Check if chatbot is ready
    fun isReady(): Boolean = isApiReady

    // Get chatbot status
    fun getStatus(): String {
        return when {
            !isApiReady -> "Connecting..."
            else -> "Ready"
        }
    }

    // Refresh API connection
    suspend fun refreshData(): Boolean {
        return try {
            val isHealthy = checkApiHealth()
            isApiReady = isHealthy
            isHealthy
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error refreshing API connection: ${e.message}", e)
            isApiReady = false
            false
        }
    }

    // Get available topics (static list for UI hints)
    fun getAvailableTopics(): List<String> {
        return listOf(
            "What foods should I avoid during pregnancy?",
            "Can I exercise while pregnant?",
            "What are early signs of pregnancy?",
            "Is caffeine safe during pregnancy?",
            "How much weight should I gain?",
            "What vitamins should I take?",
            "When should I call my doctor?",
            "Is it safe to travel while pregnant?",
            "What are signs of labor?",
            "Can I have sex during pregnancy?",
            "How to deal with morning sickness?",
            "What are normal pregnancy symptoms?"
        )
    }

    // Test API connection
    suspend fun testConnection(): Boolean {
        return checkApiHealth()
    }

    // Enhanced error recovery with retry logic
    suspend fun retryConnection(maxRetries: Int = 3): Boolean {
        repeat(maxRetries) { attempt ->
            try {
                Log.d(TAG, "🔄 Attempting to reconnect (attempt ${attempt + 1}/$maxRetries)")
                val success = checkApiHealth()
                if (success) {
                    isApiReady = true
                    Log.d(TAG, "✅ Reconnection successful on attempt ${attempt + 1}")
                    return true
                }
            } catch (e: Exception) {
                Log.w(TAG, "⚠️ Reconnection attempt ${attempt + 1} failed: ${e.message}")
            }
            
            if (attempt < maxRetries - 1) {
                kotlinx.coroutines.delay(1000L * (attempt + 1)) // Exponential backoff
            }
        }
        
        Log.e(TAG, "❌ Failed to reconnect after $maxRetries attempts")
        isApiReady = false
        return false
    }

    // Get detailed error information for debugging
    fun getLastError(): String? {
        return when {
            !isApiReady -> "API not ready - connection issues"
            else -> null
        }
    }
}