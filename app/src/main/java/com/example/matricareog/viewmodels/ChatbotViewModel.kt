package com.example.matricareog.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matricareog.chatbot.PregnancyChatbot
import com.example.matricareog.screens.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatbotViewModel : ViewModel() {

    private val chatbot = PregnancyChatbot.getInstance()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _chatbotStatus = MutableStateFlow("Connecting to API...")
    val chatbotStatus: StateFlow<String> = _chatbotStatus.asStateFlow()

    private val _suggestedTopics = MutableStateFlow<List<String>>(emptyList())
    val suggestedTopics: StateFlow<List<String>> = _suggestedTopics.asStateFlow()

    init {
        // Load suggested topics immediately
       // loadSuggestedTopics()
        // Start with initial status check
       // checkInitialStatus()
    }

    private fun checkInitialStatus() {
        viewModelScope.launch {
            // Give the application a moment to initialize
            kotlinx.coroutines.delay(1000)
            updateChatbotStatus()

            // If not ready, try to initialize
            if (!chatbot.isReady()) {
                _chatbotStatus.value = "Connecting..."
                val refreshSuccess = chatbot.refreshData()
                if (refreshSuccess) {
                    _chatbotStatus.value = "Ready"
                } else {
                    _chatbotStatus.value = "Connection issues - tap refresh"
                }
            }
        }
    }

    private fun updateChatbotStatus() {
        _chatbotStatus.value = chatbot.getStatus()
        if (chatbot.isReady()) {
            loadSuggestedTopics()
        } else {
            _chatbotStatus.value = "Connecting to API..."
        }
    }

    private fun loadSuggestedTopics() {
        _suggestedTopics.value = chatbot.getAvailableTopics()
    }

    fun sendMessage(messageText: String) {
        if (messageText.isBlank()) return

        // Add user message immediately
        val userMessage = ChatMessage(
            content = messageText,
            isFromUser = true
        )
        addMessage(userMessage)

        // Get bot response
        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Always try to get response - the chatbot will handle connection internally
                val response = chatbot.getResponse(messageText)

                val botMessage = ChatMessage(
                    content = response.answer,
                    isFromUser = false,
                    confidence = response.confidence,
                    matchedQuestion = response.matchedQuestion,
                    similarityScore = response.similarityScore
                )

                addMessage(botMessage)

                // Update status based on response
                if (response.confidence != "Error") {
                    _chatbotStatus.value = "Ready"
                } else {
                    _chatbotStatus.value = "Connection issues"
                }
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    content = "I'm sorry, I encountered an error. Please check your internet connection and try again.",
                    isFromUser = false,
                    confidence = "Error"
                )
                addMessage(errorMessage)
                _chatbotStatus.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun addMessage(message: ChatMessage) {
        _chatMessages.value = _chatMessages.value + message
    }

    fun refreshChatbot() {
        viewModelScope.launch {
            _isLoading.value = true
            _chatbotStatus.value = "Reconnecting..."

            try {
                val refreshSuccess = chatbot.refreshData()
                if (refreshSuccess) {
                    updateChatbotStatus()
                    loadSuggestedTopics()

                    // Add system message about successful reconnection
                    val systemMessage = ChatMessage(
                        content = "✅ Connection restored! I'm ready to help with your pregnancy questions.",
                        isFromUser = false,
                        confidence = "High"
                    )
                    addMessage(systemMessage)
                } else {
                    _chatbotStatus.value = "Connection failed"
                    val errorMessage = ChatMessage(
                        content = "Unable to connect to the service. Please check your internet connection.",
                        isFromUser = false,
                        confidence = "Error"
                    )
                    addMessage(errorMessage)
                }
            } catch (e: Exception) {
                _chatbotStatus.value = "Error: ${e.message}"
                val errorMessage = ChatMessage(
                    content = "Connection failed: ${e.message}",
                    isFromUser = false,
                    confidence = "Error"
                )
                addMessage(errorMessage)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
    }

    fun testConnection() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val isConnected = chatbot.testConnection()
                val message = if (isConnected) {
                    _chatbotStatus.value = "Ready"
                    "✅ Connection test successful! I'm ready to answer your pregnancy questions."
                } else {
                    _chatbotStatus.value = "Connection failed"
                    "❌ Connection test failed. Please check your internet connection."
                }

                val testMessage = ChatMessage(
                    content = message,
                    isFromUser = false,
                    confidence = if (isConnected) "High" else "Error"
                )
                addMessage(testMessage)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
