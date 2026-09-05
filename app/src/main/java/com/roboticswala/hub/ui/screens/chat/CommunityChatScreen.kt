package com.roboticswala.hub.ui.screens.chat

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roboticswala.hub.data.models.ChatMessage
import com.roboticswala.hub.ui.components.RoboticsBackground
import com.roboticswala.hub.ui.theme.CircuitError
import com.roboticswala.hub.ui.theme.CircuitWarning
import com.roboticswala.hub.ui.theme.CyberCyan
import com.roboticswala.hub.ui.theme.CyberCyanGlow
import com.roboticswala.hub.ui.theme.DarkBackground
import com.roboticswala.hub.ui.theme.DarkSurface
import com.roboticswala.hub.ui.theme.DarkSurfaceBorder
import com.roboticswala.hub.ui.theme.DarkSurfaceElevated
import com.roboticswala.hub.ui.theme.ElectricBlue
import com.roboticswala.hub.ui.theme.LightBackground
import com.roboticswala.hub.ui.theme.LightSurface
import com.roboticswala.hub.ui.theme.LightSurfaceBorder
import com.roboticswala.hub.ui.theme.TextPrimaryDark
import com.roboticswala.hub.ui.theme.TextPrimaryLight
import com.roboticswala.hub.ui.theme.TextSecondaryDark
import com.roboticswala.hub.ui.theme.TextSecondaryLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityChatScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()
    val listState = rememberLazyListState()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Mark as read on entry and on new message
    LaunchedEffect(Unit) {
        viewModel.markAsRead(context)
    }

    // Auto-scroll to bottom on new message or typing
    val totalChatItems = uiState.messages.size + if (uiState.typingUsers.isNotEmpty()) 1 else 0
    LaunchedEffect(totalChatItems) {
        if (totalChatItems > 0) {
            viewModel.markAsRead(context)
            listState.animateScrollToItem(totalChatItems - 1)
        }
    }

    RoboticsBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = if (isDark) CyberCyan else ElectricBlue
                            )
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(CyberCyan.copy(alpha = 0.3f), ElectricBlue.copy(alpha = 0.3f))
                                        )
                                    )
                                    .border(1.5.dp, CyberCyan.copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Groups,
                                    contentDescription = "Group",
                                    tint = if (isDark) CyberCyan else ElectricBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "RW HUB Discussion",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.2).sp
                                    ),
                                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                                )
                                val typingText = when {
                                    uiState.typingUsers.isNotEmpty() -> {
                                        if (uiState.typingUsers.size == 1) "${uiState.typingUsers.first()} is typing..."
                                        else "${uiState.typingUsers.take(2).joinToString(" & ")} are typing..."
                                    }
                                    uiState.inputText.isNotBlank() -> "Typing..."
                                    else -> null
                                }
                                if (typingText != null) {
                                    Text(
                                        text = "✍️ $typingText",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = CyberCyanGlow
                                    )
                                } else {
                                    Text(
                                        text = "Lab Community Room • Real-time",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = if (isDark) CyberCyan else ElectricBlue
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isDark) DarkBackground.copy(alpha = 0.95f) else LightBackground.copy(alpha = 0.95f)
                    )
                )
            },
            bottomBar = {
                ChatInputBar(
                    text = uiState.inputText,
                    onTextChanged = viewModel::onInputTextChanged,
                    onSend = viewModel::sendMessage,
                    isSending = uiState.isSending,
                    isDark = isDark,
                    typingUsers = uiState.typingUsers
                )
            }
        ) { innerPadding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (uiState.isLoading && uiState.messages.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = if (isDark) CyberCyan else ElectricBlue
                        )
                    }
                } else if (uiState.messages.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "💬 Welcome to the Lab Discussion!",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) CyberCyan else ElectricBlue
                            )
                            Text(
                                text = "Ask hardware questions, discuss projects, or share robotics updates with students and instructors.",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.messages, key = { it.id.ifBlank { "${it.clientTimeMillis}_${it.senderId}" } }) { msg ->
                            val isOwn = msg.senderId == uiState.currentUserId
                            val canDelete = isOwn || uiState.currentUserRole == "ADMIN"
                            ChatBubble(
                                message = msg,
                                isOwn = isOwn,
                                canDelete = canDelete,
                                onDelete = { viewModel.deleteMessage(msg.id) },
                                isDark = isDark
                            )
                        }

                        if (uiState.typingUsers.isNotEmpty()) {
                            item(key = "typing_bubble") {
                                TypingBubble(typingUsers = uiState.typingUsers, isDark = isDark)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    isOwn: Boolean,
    canDelete: Boolean,
    onDelete: () -> Unit,
    isDark: Boolean
) {
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp, message.clientTimeMillis) {
        val date = message.timestamp ?: Date(message.clientTimeMillis)
        timeFormat.format(date)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 310.dp),
            horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        if (isOwn) {
                            RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                        } else {
                            RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                        }
                    )
                    .background(
                        if (isOwn) {
                            Brush.linearGradient(
                                if (isDark) listOf(ElectricBlue, CyberCyan.copy(alpha = 0.9f))
                                else listOf(ElectricBlue, CyberCyan)
                            )
                        } else {
                            Brush.linearGradient(
                                if (isDark) listOf(DarkSurfaceElevated, DarkSurface)
                                else listOf(LightSurface, LightSurface)
                            )
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (isOwn) CyberCyanGlow.copy(alpha = 0.4f)
                        else if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                        shape = if (isOwn) {
                            RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                        } else {
                            RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                        }
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column {
                    // Header: sender name + role tag (for others' messages)
                    if (!isOwn) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 3.dp)
                        ) {
                            Text(
                                text = message.senderName.ifBlank { "Robotics Member" },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = if (message.senderRole == "ADMIN") CircuitWarning
                                else if (isDark) CyberCyan else ElectricBlue
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (message.senderRole == "ADMIN") CircuitWarning.copy(alpha = 0.2f)
                                        else CyberCyan.copy(alpha = 0.15f)
                                    )
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (message.senderRole == "ADMIN") "ADMIN" else "STUDENT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    color = if (message.senderRole == "ADMIN") CircuitWarning
                                    else if (isDark) CyberCyan else ElectricBlue
                                )
                            }
                        }
                    }

                    // Message text
                    Text(
                        text = message.message,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                        color = if (isOwn) Color.White
                        else if (isDark) TextPrimaryDark else TextPrimaryLight
                    )

                    // Footer: time + optional delete button
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = if (isOwn) Color.White.copy(alpha = 0.75f)
                            else if (isDark) TextSecondaryDark else TextSecondaryLight
                        )
                        if (canDelete && message.id.isNotBlank()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.DeleteOutline,
                                contentDescription = "Delete message",
                                tint = if (isOwn) Color.White.copy(alpha = 0.75f) else CircuitError.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .size(13.dp)
                                    .clickable(onClick = onDelete)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TypingBubble(
    typingUsers: List<String>,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing_dots")
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    val label = if (typingUsers.size == 1) "${typingUsers.first()} is typing"
    else "${typingUsers.take(2).joinToString(" & ")} are typing"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp))
                .background(if (isDark) DarkSurfaceElevated else LightSurface)
                .border(
                    1.dp,
                    if (isDark) CyberCyan.copy(alpha = 0.4f) else ElectricBlue.copy(alpha = 0.3f),
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp)
                )
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = dot1Alpha))
                    )
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = dot2Alpha))
                    )
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = dot3Alpha))
                    )
                }
            }
        }
    }
}

@Composable
fun ChatInputBar(
    text: String,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit,
    isSending: Boolean,
    isDark: Boolean,
    typingUsers: List<String> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .background(if (isDark) DarkSurface.copy(alpha = 0.98f) else LightSurface.copy(alpha = 0.98f))
            .border(1.dp, if (isDark) DarkSurfaceBorder else LightSurfaceBorder)
    ) {
        val isTyping = typingUsers.isNotEmpty() || text.isNotBlank()
        if (isTyping) {
            val typingText = when {
                typingUsers.isNotEmpty() -> {
                    if (typingUsers.size == 1) "${typingUsers.first()} is typing..."
                    else "${typingUsers.take(2).joinToString(" & ")} are typing..."
                }
                else -> "You are typing..."
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✍️ $typingText",
                    fontSize = 11.sp,
                    color = CyberCyan,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChanged,
                placeholder = {
                    Text(
                        text = "Type a message...",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = if (isDark) DarkSurfaceElevated else LightSurfaceBorder.copy(alpha = 0.3f),
                    unfocusedContainerColor = if (isDark) DarkSurfaceElevated else LightSurfaceBorder.copy(alpha = 0.3f),
                    focusedBorderColor = if (isDark) CyberCyan else ElectricBlue,
                    unfocusedBorderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                    focusedTextColor = if (isDark) TextPrimaryDark else TextPrimaryLight,
                    unfocusedTextColor = if (isDark) TextPrimaryDark else TextPrimaryLight
                ),
                maxLines = 4,
                singleLine = false
            )

            Spacer(modifier = Modifier.width(8.dp))

            val canSend = text.isNotBlank() && !isSending
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (canSend) Brush.linearGradient(listOf(CyberCyan, ElectricBlue))
                        else Brush.linearGradient(listOf(Color.Gray.copy(alpha = 0.4f), Color.Gray.copy(alpha = 0.4f)))
                    )
                    .clickable(enabled = canSend, onClick = onSend),
                contentAlignment = Alignment.Center
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
