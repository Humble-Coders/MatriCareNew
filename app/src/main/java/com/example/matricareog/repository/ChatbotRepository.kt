package com.example.matricareog.repository

import com.example.matricareog.chatbot.ApiClient
import com.example.matricareog.chatbot.ChatbotRequest

class ChatbotRepository {
    private val apiService = ApiClient.apiService

    suspend fun askQuestion(question: String): Result<com.example.matricareog.chatbot.ChatbotResponse> {
        return try {
            val response = apiService.askQuestion(ChatbotRequest(question))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkHealth(): Result<com.example.matricareog.chatbot.HealthResponse> {
        return try {
            val response = apiService.getHealth()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Health check failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}