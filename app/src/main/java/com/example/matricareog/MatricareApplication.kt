package com.example.matricareog

import android.app.Application
import android.util.Log
import com.example.matricareog.chatbot.PregnancyChatbot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MatricareApplication : Application() {

    // Application-scoped coroutine for background initialization
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        Log.d("MatricareApp", "🚀 Application starting - initializing chatbot...")

        // Initialize chatbot in background
        initializeChatbot()
    }

    private fun initializeChatbot() {
        applicationScope.launch {
            try {
                val chatbot = PregnancyChatbot.getInstance()
                val success = chatbot.initialize(this@MatricareApplication)

                if (success) {
                    Log.d("MatricareApp", "✅ Pregnancy chatbot initialized successfully!")
                } else {
                    Log.e("MatricareApp", "❌ Failed to initialize pregnancy chatbot")
                }
            } catch (e: Exception) {
                Log.e("MatricareApp", "💥 Exception during chatbot initialization: ${e.message}", e)
            }
        }
    }
}