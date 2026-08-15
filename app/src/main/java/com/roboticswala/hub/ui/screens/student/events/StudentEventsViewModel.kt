package com.roboticswala.hub.ui.screens.student.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.LabEvent
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.data.repository.FirestoreNoticeAndEventRepository
import com.roboticswala.hub.data.repository.NoticeAndEventRepository
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentEventsUiState(
    val events: List<LabEvent> = emptyList(),
    val filteredEvents: List<LabEvent> = emptyList(),
    val selectedTab: Int = 0, // 0 = All Events, 1 = My Registered Events
    val statusFilter: String = "All",
    val categoryFilter: String = "All",
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val selectedEventForDetails: LabEvent? = null,
    val isRegistering: Boolean = false,
    val snackbarMessage: String? = null,
    val errorMessage: String? = null
)

class StudentEventsViewModel(
    private val studentUid: String = FirebaseAuth.getInstance().currentUser?.uid ?: "",
    private val repository: NoticeAndEventRepository = FirestoreNoticeAndEventRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentEventsUiState())
    val uiState: StateFlow<StudentEventsUiState> = _uiState.asStateFlow()

    init {
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.observeAllEvents().collect { list ->
                _uiState.update { state ->
                    state.copy(
                        events = list,
                        filteredEvents = filterList(list, state.statusFilter, state.categoryFilter, state.searchQuery),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectTab(tabIndex: Int) = _uiState.update { it.copy(selectedTab = tabIndex) }

    fun setStatusFilter(status: String) {
        _uiState.update { state ->
            state.copy(
                statusFilter = status,
                filteredEvents = filterList(state.events, status, state.categoryFilter, state.searchQuery)
            )
        }
    }

    fun setCategoryFilter(category: String) {
        _uiState.update { state ->
            state.copy(
                categoryFilter = category,
                filteredEvents = filterList(state.events, state.statusFilter, category, state.searchQuery)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredEvents = filterList(state.events, state.statusFilter, state.categoryFilter, query)
            )
        }
    }

    private fun filterList(
        list: List<LabEvent>,
        status: String,
        category: String,
        query: String
    ): List<LabEvent> {
        return list.filter { item ->
            val matchesStatus = status == "All" || item.eventStatus.equals(status, ignoreCase = true)
            val matchesCat = category == "All" || item.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.location.contains(query, ignoreCase = true) ||
                    item.organizerName.contains(query, ignoreCase = true)

            matchesStatus && matchesCat && matchesQuery
        }
    }

    fun openDetails(event: LabEvent) = _uiState.update { it.copy(selectedEventForDetails = event) }
    fun closeDetails() = _uiState.update { it.copy(selectedEventForDetails = null) }

    fun registerForEvent(event: LabEvent, profile: UserProfile) {
        viewModelScope.launch {
            repository.registerForEvent(
                eventId = event.eventId,
                studentUid = studentUid,
                studentId = profile.studentId.ifBlank { "STU-${studentUid.take(4)}" },
                studentName = profile.fullName.ifBlank { "Student" }
            ).collect { res ->
                when (res) {
                    is Resource.Loading -> _uiState.update { it.copy(isRegistering = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isRegistering = false,
                                selectedEventForDetails = null,
                                snackbarMessage = "Registration confirmed for ${event.title}!"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isRegistering = false,
                                errorMessage = res.message ?: "Failed to register."
                            )
                        }
                    }
                }
            }
        }
    }

    fun cancelRegistration(eventId: String) {
        viewModelScope.launch {
            repository.cancelEventRegistration(eventId, studentUid).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                selectedEventForDetails = null,
                                snackbarMessage = "Registration cancelled."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(errorMessage = res.message ?: "Failed to cancel.")
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(snackbarMessage = null, errorMessage = null) }
}

class StudentEventsViewModelFactory(
    private val studentUid: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentEventsViewModel::class.java)) {
            return StudentEventsViewModel(studentUid = studentUid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
