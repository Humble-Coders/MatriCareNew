package com.example.matricareog.chatbot

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import org.apache.commons.math3.linear.ArrayRealVector

// Data classes
data class PregnancyQuestion(
    val id: String = "",
    val question: String = "",
    val answer: String = "",
    val embedding: List<Double> = emptyList()
)

data class PregnancyChatbotData(
    val metadata: ChatbotMetadata = ChatbotMetadata(),
    val questions: List<PregnancyQuestion> = emptyList()
)

data class ChatbotMetadata(
    val modelName: String = "",
    val embeddingDimensions: Int = 512,
    val totalQuestions: Int = 0
)

data class ChatResponse(
    val answer: String,
    val matchedQuestion: String?,
    val similarityScore: Double,
    val confidence: String
)

class PregnancyChatbot private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: PregnancyChatbot? = null
        private const val TAG = "PregnancyChatbot"
        private const val MODEL_FILE = "universal_sentence_encoder_lite.tflite"
        private const val COLLECTION_NAME = "pregnancy_chatbot"
        private const val DOCUMENT_NAME = "qa_data"
        private const val SIMILARITY_THRESHOLD = 0.3

        fun getInstance(): PregnancyChatbot {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PregnancyChatbot().also { INSTANCE = it }
            }
        }
    }

    private var interpreter: Interpreter? = null
    private var isModelLoaded = false
    private var chatbotData: PregnancyChatbotData? = null
    private var isDataLoaded = false

    // Initialize the chatbot (call this in Application onCreate or MainActivity onCreate)
    suspend fun initialize(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🚀 Initializing Pregnancy Chatbot...")

                // Load TensorFlow Lite model
                val success = loadTFLiteModel(context)
                if (!success) {
                    Log.e(TAG, "❌ Failed to load TFLite model")
                    return@withContext false
                }

                // Load chatbot data from Firebase
                val dataLoaded = loadChatbotDataFromFirebase()
                if (!dataLoaded) {
                    Log.e(TAG, "❌ Failed to load chatbot data from Firebase")
                    return@withContext false
                }

                Log.d(TAG, "✅ Pregnancy Chatbot initialized successfully!")
                true
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to initialize chatbot: ${e.message}", e)
                false
            }
        }
    }

    // Load TensorFlow Lite model
    private fun loadTFLiteModel(context: Context): Boolean {
        return try {
            val modelBuffer = loadModelFile(context, MODEL_FILE)
            interpreter = Interpreter(modelBuffer)
            isModelLoaded = true
            Log.d(TAG, "✅ TensorFlow Lite model loaded successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error loading TFLite model: ${e.message}", e)
            isModelLoaded = false
            false
        }
    }

    // Load model file from assets
    private fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    // Load chatbot data from Firebase
    private suspend fun loadChatbotDataFromFirebase(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "📡 Loading chatbot data from Firebase...")

                val firestore = FirebaseFirestore.getInstance()
                val document = firestore.collection(COLLECTION_NAME).document(DOCUMENT_NAME).get().await()

                if (document.exists()) {
                    chatbotData = document.toObject(PregnancyChatbotData::class.java)
                    isDataLoaded = chatbotData != null

                    if (isDataLoaded) {
                        Log.d(TAG, "✅ Loaded ${chatbotData?.questions?.size} questions from Firebase")
                        Log.d(TAG, "📊 Embedding dimensions: ${chatbotData?.metadata?.embeddingDimensions}")
                    }

                    isDataLoaded
                } else {
                    Log.e(TAG, "❌ Document does not exist in Firebase")
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error loading data from Firebase: ${e.message}", e)
                false
            }
        }
    }

    // Generate embedding for user question
    private suspend fun generateEmbedding(text: String): List<Double>? {
        return withContext(Dispatchers.Default) {
            if (!isModelLoaded || interpreter == null) {
                Log.e(TAG, "❌ Model not loaded")
                return@withContext null
            }

            try {
                // Prepare input (this is simplified - you might need text preprocessing)
                val input = Array(1) { text }
                val output = Array(1) { FloatArray(512) } // Universal Sentence Encoder outputs 512 dimensions

                // Run inference
                interpreter?.run(input, output)

                // Convert to List<Double>
                val embedding = output[0].map { it.toDouble() }
                Log.d(TAG, "✅ Generated embedding for: '${text.take(50)}...'")

                embedding
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error generating embedding: ${e.message}", e)
                null
            }
        }
    }

    // Calculate cosine similarity between two vectors
    private fun calculateCosineSimilarity(vector1: List<Double>, vector2: List<Double>): Double {
        if (vector1.size != vector2.size) {
            Log.e(TAG, "❌ Vector size mismatch: ${vector1.size} vs ${vector2.size}")
            return 0.0
        }

        return try {
            val vec1 = ArrayRealVector(vector1.toDoubleArray())
            val vec2 = ArrayRealVector(vector2.toDoubleArray())

            val similarity = vec1.dotProduct(vec2) / (vec1.norm * vec2.norm)
            similarity
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error calculating cosine similarity: ${e.message}", e)
            0.0
        }
    }

    // Get response for user question
    suspend fun getResponse(userQuestion: String): ChatResponse {
        return withContext(Dispatchers.Default) {
            if (!isModelLoaded || !isDataLoaded || chatbotData == null) {
                return@withContext ChatResponse(
                    answer = "I'm sorry, the chatbot is not ready yet. Please try again in a moment.",
                    matchedQuestion = null,
                    similarityScore = 0.0,
                    confidence = "Error"
                )
            }

            if (userQuestion.isBlank()) {
                return@withContext ChatResponse(
                    answer = "Please ask me a question about pregnancy!",
                    matchedQuestion = null,
                    similarityScore = 0.0,
                    confidence = "Low"
                )
            }

            try {
                Log.d(TAG, "🤔 Processing question: '$userQuestion'")

                // Generate embedding for user question
                val userEmbedding = generateEmbedding(userQuestion)
                if (userEmbedding == null) {
                    return@withContext ChatResponse(
                        answer = "I'm sorry, I'm having trouble understanding your question. Please try rephrasing it.",
                        matchedQuestion = null,
                        similarityScore = 0.0,
                        confidence = "Error"
                    )
                }

                // Find best match
                var bestMatch: PregnancyQuestion? = null
                var bestSimilarity = 0.0

                chatbotData?.questions?.forEach { question ->
                    if (question.embedding.isNotEmpty()) {
                        val similarity = calculateCosineSimilarity(userEmbedding, question.embedding)
                        Log.d(TAG, "📊 Similarity with '${question.question.take(30)}...': ${"%.3f".format(similarity)}")

                        if (similarity > bestSimilarity) {
                            bestSimilarity = similarity
                            bestMatch = question
                        }
                    }
                }

                // Determine confidence and response
                val confidence = when {
                    bestSimilarity > 0.7 -> "High"
                    bestSimilarity >= SIMILARITY_THRESHOLD -> "Medium"
                    else -> "Low"
                }

                val response = if (bestSimilarity >= SIMILARITY_THRESHOLD && bestMatch != null) {
                    ChatResponse(
                        answer = bestMatch!!.answer,
                        matchedQuestion = bestMatch!!.question,
                        similarityScore = bestSimilarity,
                        confidence = confidence
                    )
                } else {
                    ChatResponse(
                        answer = "I'm sorry, I don't have specific information about that. Please try rephrasing your question or ask about pregnancy-related topics like nutrition, exercise, symptoms, or medical care.",
                        matchedQuestion = null,
                        similarityScore = bestSimilarity,
                        confidence = "Low"
                    )
                }

                Log.d(TAG, "💬 Best match: '${response.matchedQuestion?.take(50) ?: "None"}' (${response.confidence}, ${"%.3f".format(response.similarityScore)})")

                response
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error processing question: ${e.message}", e)
                ChatResponse(
                    answer = "I'm sorry, I encountered an error while processing your question. Please try again.",
                    matchedQuestion = null,
                    similarityScore = 0.0,
                    confidence = "Error"
                )
            }
        }
    }

    // Check if chatbot is ready
    fun isReady(): Boolean = isModelLoaded && isDataLoaded

    // Get chatbot status
    fun getStatus(): String {
        return when {
            !isModelLoaded && !isDataLoaded -> "Not initialized"
            !isModelLoaded -> "Model not loaded"
            !isDataLoaded -> "Data not loaded"
            else -> "Ready (${chatbotData?.questions?.size ?: 0} questions loaded)"
        }
    }

    // Reload data from Firebase (useful for updates)
    suspend fun refreshData(): Boolean {
        return loadChatbotDataFromFirebase()
    }

    // Get available topics (for UI hints)
    fun getAvailableTopics(): List<String> {
        return listOf(
            "Foods to avoid during pregnancy",
            "Caffeine and pregnancy",
            "Exercise during pregnancy",
            "Safe medications",
            "Early pregnancy signs",
            "Fetal movement",
            "Weight gain recommendations",
            "Travel during pregnancy",
            "Labor signs",
            "Sexual activity during pregnancy",
            "Morning sickness remedies",
            "Normal pregnancy discomforts"
        )
    }
}
