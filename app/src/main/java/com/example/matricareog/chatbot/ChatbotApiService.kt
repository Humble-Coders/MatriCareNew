package com.example.matricareog.chatbot

import retrofit2.Response
import retrofit2.http.*

interface ChatbotApiService {

    @POST("ask")
    suspend fun askQuestion(@Body request: ChatbotRequest): Response<ChatbotResponse>

    @GET("health")
    suspend fun getHealth(): Response<HealthResponse>

    @GET("questions")
    suspend fun getAllQuestions(): Response<Map<String, Any>>
}

data class ChatbotRequest(
    val question: String,
    val threshold: Double = 0.3
)

data class ChatbotResponse(
    val success: Boolean,
    val answer: String?,
    val matched_question: String?,
    val similarity_score: Double?,
    val confidence: String?,
    val question_id: String?,
    val error: String? = null
)

data class HealthResponse(
    val status: String,
    val service: String,
    val questions_loaded: Int,
    val model_loaded: Boolean,
    val firebase_connected: Boolean
)