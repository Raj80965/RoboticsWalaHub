package com.roboticswala.hub.ui.screens.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.roboticswala.hub.data.models.ChatMessage
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.data.repository.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val currentUserId: String = "",
    val currentUserName: String = "",
    val currentUserRole: String = "STUDENT",
    val currentUserPhotoUrl: String = "",
    val typingUsers: List<String> = emptyList(),
    val error: String? = null
)

class ChatViewModel(
    private val repository: ChatRepository = ChatRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var typingJob: Job? = null

    init {
        val uid = auth.currentUser?.uid.orEmpty()
        val email = auth.currentUser?.email.orEmpty()
        val displayName = auth.currentUser?.displayName.orEmpty()

        _uiState.update {
            it.copy(
                currentUserId = uid,
                currentUserName = displayName.ifBlank { email.substringBefore("@").ifBlank { "Member" } }
            )
        }

        loadUserProfile(uid)
        observeMessages()
        observeTypingUsers(uid)
    }

    private fun loadUserProfile(uid: String) {
        if (uid.isBlank()) return
        viewModelScope.launch {
            try {
                val doc = firestore.collection("users").document(uid).get().await()
                if (doc.exists()) {
                    val profile = doc.toObject(UserProfile::class.java)
                    val role = profile?.role.orEmpty().uppercase()
                    val actualRole = if (role == "ADMIN") "ADMIN" else "STUDENT"
                    val profileName = profile?.fullName?.takeIf { it.isNotBlank() }
                    val fbName = auth.currentUser?.displayName?.takeIf { it.isNotBlank() }
                    val name = profileName ?: fbName ?: _uiState.value.currentUserName

                    _uiState.update {
                        it.copy(
                            currentUserName = name,
                            currentUserRole = actualRole,
                            currentUserPhotoUrl = profile?.photoUrl.orEmpty()
                        )
                    }
                }
            } catch (_: Exception) {
                // Keep defaults if fetch fails
            }
        }
    }

    private fun observeMessages() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.getMessages()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
                }
                .collect { messageList ->
                    _uiState.update {
                        it.copy(
                            messages = messageList,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun observeTypingUsers(currentUid: String) {
        viewModelScope.launch {
            repository.getTypingUsersFlow(currentUid)
                .catch { /* ignore */ }
                .collect { users ->
                    _uiState.update { it.copy(typingUsers = users) }
                }
        }
    }

    fun markAsRead(context: Context) {
        val uid = _uiState.value.currentUserId
        if (uid.isNotBlank()) {
            ChatRepository.markChatAsRead(context, uid)
        }
    }

    fun onInputTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }

        val uid = _uiState.value.currentUserId
        val name = _uiState.value.currentUserName
        if (uid.isNotBlank() && name.isNotBlank()) {
            typingJob?.cancel()
            if (text.isNotBlank()) {
                repository.setTypingStatus(uid, name, true)
                typingJob = viewModelScope.launch {
                    delay(2500)
                    repository.setTypingStatus(uid, name, false)
                }
            } else {
                repository.setTypingStatus(uid, name, false)
            }
        }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank() || _uiState.value.isSending) return

        val state = _uiState.value
        typingJob?.cancel()
        repository.setTypingStatus(state.currentUserId, state.currentUserName, false)

        val newMsg = ChatMessage(
            senderId = state.currentUserId,
            senderName = state.currentUserName,
            senderRole = state.currentUserRole,
            senderPhotoUrl = state.currentUserPhotoUrl,
            message = text,
            clientTimeMillis = System.currentTimeMillis()
        )

        _uiState.update { it.copy(inputText = "", isSending = true) }

        viewModelScope.launch {
            val success = repository.sendMessage(newMsg)
            _uiState.update { it.copy(isSending = false) }
            if (!success) {
                _uiState.update { it.copy(error = "Failed to send message") }
            }
        }
    }

    fun deleteMessage(messageId: String) {
        if (messageId.isBlank()) return
        viewModelScope.launch {
            repository.deleteMessage(messageId)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        typingJob?.cancel()
        val uid = _uiState.value.currentUserId
        val name = _uiState.value.currentUserName
        if (uid.isNotBlank() && name.isNotBlank()) {
            repository.setTypingStatus(uid, name, false)
        }
    }
}
