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

    private val _chatbotStatus = MutableStateFlow("Initializing...")
    val chatbotStatus: StateFlow<String> = _chatbotStatus.asStateFlow()

    private val _suggestedTopics = MutableStateFlow<List<String>>(emptyList())
    val suggestedTopics: StateFlow<List<String>> = _suggestedTopics.asStateFlow()

    init {
        updateChatbotStatus()
        loadSuggestedTopics()
    }

    private fun updateChatbotStatus() {
        _chatbotStatus.value = chatbot.getStatus()
        if (chatbot.isReady()) {
            loadSuggestedTopics()
        }
    }

    private fun loadSuggestedTopics() {
        _suggestedTopics.value = chatbot.getAvailableTopics()
    }

    fun sendMessage(messageText: String) {
        if (!chatbot.isReady()) {
            addMessage(ChatMessage(
                content = "I'm still getting ready. Please wait a moment and try again.",
                isFromUser = false,
                confidence = "Error"
            ))
            return
        }

        // Add user message
        val userMessage = ChatMessage(
            content = messageText,
            isFromUser = true
        )
        addMessage(userMessage)

        // Get bot response
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = chatbot.getResponse(messageText)

                val botMessage = ChatMessage(
                    content = response.answer,
                    isFromUser = false,
                    confidence = response.confidence,
                    matchedQuestion = response.matchedQuestion,
                    similarityScore = response.similarityScore
                )

                addMessage(botMessage)
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    content = "I'm sorry, I encountered an error. Please try again.",
                    isFromUser = false,
                    confidence = "Error"
                )
                addMessage(errorMessage)
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
            try {
                chatbot.refreshData()
                updateChatbotStatus()
                loadSuggestedTopics()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
    }
}