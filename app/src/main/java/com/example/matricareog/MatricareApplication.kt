package com.example.matricareog

import android.app.Application
import android.util.Log
import com.example.matricareog.chatbot.PregnancyChatbot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

class MatricareApplication : Application() {

    // Use IO dispatcher for background operations and add proper lifecycle management
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        Log.d("MatricareApp", "🚀 Application starting - initializing API-based chatbot...")

        // Initialize API-based chatbot in background
      //  initializeChatbot()
    }

    private fun initializeChatbot() {
        applicationScope.launch {
            try {
                val chatbot = PregnancyChatbot.getInstance()
                val success = chatbot.initialize(this@MatricareApplication)

                if (success) {
                    Log.d("MatricareApp", "✅ API-based pregnancy chatbot initialized successfully!")
                } else {
                    Log.e("MatricareApp", "❌ Failed to initialize API-based pregnancy chatbot")
                }
            } catch (e: Exception) {
                Log.e("MatricareApp", "💥 Exception during API chatbot initialization: ${e.message}", e)
                    }
    }

    fun onTerminate() {
        super.onTerminate()
        // Cancel all coroutines when application terminates
        applicationScope.cancel()
        Log.d("MatricareApp", "🔄 Application terminating - cleaning up coroutines")
    }
}
}