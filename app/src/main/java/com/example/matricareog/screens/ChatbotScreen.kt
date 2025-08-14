package com.example.matricareog.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matricareog.chatbot.ChatResponse
import com.example.matricareog.chatbot.PregnancyChatbot
import com.example.matricareog.viewmodels.ChatbotViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Message data class
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val confidence: String? = null,
    val matchedQuestion: String? = null,
    val similarityScore: Double? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(
    onBackClick: () -> Unit,
    viewModel: ChatbotViewModel
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val chatbotStatus by viewModel.chatbotStatus.collectAsState()
    val suggestedTopics by viewModel.suggestedTopics.collectAsState()

    var messageText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            ChatbotTopBar(
                onBackClick = onBackClick,
                chatbotStatus = chatbotStatus,
                onRefreshClick = { viewModel.refreshChatbot() }
            )
        },
        bottomBar = {
            MessageInputBar(
                messageText = messageText,
                onMessageTextChange = { messageText = it },
                onSendMessage = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(messageText)
                        messageText = ""
                        keyboardController?.hide()
                    }
                },
                isLoading = isLoading,
                isEnabled = chatbotStatus == "Ready"
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (chatMessages.isEmpty()) {
                // Welcome screen with suggested topics
                WelcomeSection(
                    suggestedTopics = suggestedTopics,
                    onTopicClick = { topic ->
                        viewModel.sendMessage(topic)
                    },
                    chatbotStatus = chatbotStatus
                )
            } else {
                // Chat messages
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(chatMessages) { message ->
                        ChatMessageItem(message = message)
                    }

                    // Typing indicator
                    if (isLoading) {
                        item {
                            TypingIndicator()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatbotTopBar(
    onBackClick: () -> Unit,
    chatbotStatus: String,
    onRefreshClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Pregnancy Assistant",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE91E63)
                )
                Text(
                    text = chatbotStatus,
                    fontSize = 12.sp,
                    color = if (chatbotStatus == "Ready") Color.Green else Color.Yellow
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFFE91E63)
                )
            }
        },
        actions = {
            IconButton(onClick = onRefreshClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Color(0xFFE91E63)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
private fun WelcomeSection(
    suggestedTopics: List<String>,
    onTopicClick: (String) -> Unit,
    chatbotStatus: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Chatbot avatar
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFE91E63).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🤰",
                fontSize = 40.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Hi! I'm your pregnancy assistant",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE91E63),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Ask me anything about pregnancy, nutrition, exercise, symptoms, and more!",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (chatbotStatus == "Ready" && suggestedTopics.isNotEmpty()) {
            Text(
                text = "Popular topics:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(suggestedTopics.take(6)) { topic ->
                    SuggestedTopicChip(
                        topic = topic,
                        onClick = { onTopicClick(topic) }
                    )
                }
            }
        } else if (chatbotStatus != "Ready") {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFFFF9800),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Setting up your assistant...",
                        color = Color(0xFFFF9800),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestedTopicChip(
    topic: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = topic,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            color = Color(0xFFE91E63),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ChatMessageItem(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isFromUser) {
            // Bot avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE91E63).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🤰", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (message.isFromUser)
                        Color(0xFFE91E63) else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                    bottomEnd = if (message.isFromUser) 4.dp else 16.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = message.content,
                        color = if (message.isFromUser) Color.White else Color.Black,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    // Show confidence for bot messages
                    if (!message.isFromUser && message.confidence != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val confidenceColor = when (message.confidence) {
                                "High" -> Color.Green
                                "Medium" -> Color.Yellow
                                else -> Color.Red
                            }

                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(confidenceColor)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "${message.confidence} confidence",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )

                            if (message.similarityScore != null) {
                                Text(
                                    text = " • ${(message.similarityScore * 100).toInt()}%",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Timestamp
            Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)),
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(
                    start = if (message.isFromUser) 0.dp else 4.dp,
                    end = if (message.isFromUser) 4.dp else 0.dp,
                    top = 4.dp
                )
            )
        }

        if (message.isFromUser) {
            Spacer(modifier = Modifier.width(8.dp))
            // User avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE91E63)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bot avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFE91E63).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🤰", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Thinking",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Animated dots
                repeat(3) { index ->
                    var alpha by remember { mutableStateOf(0.3f) }

                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(index * 200L)
                        while (true) {
                            alpha = 1f
                            kotlinx.coroutines.delay(600)
                            alpha = 0.3f
                            kotlinx.coroutines.delay(600)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = alpha))
                    )

                    if (index < 2) {
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageInputBar(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    isLoading: Boolean,
    isEnabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageTextChange,
                placeholder = {
                    Text(
                        text = if (isEnabled) "Ask me about pregnancy..." else "Chatbot is loading...",
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .weight(1f),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = { if (messageText.isNotBlank()) onSendMessage() }
                ),
                enabled = isEnabled && !isLoading,
                maxLines = 3,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE91E63),
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Color(0xFFE91E63)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Send button
            IconButton(
                onClick = onSendMessage,
                enabled = isEnabled && !isLoading && messageText.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isEnabled && !isLoading && messageText.isNotBlank())
                            Color(0xFFE91E63) else Color.Gray
                    )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}