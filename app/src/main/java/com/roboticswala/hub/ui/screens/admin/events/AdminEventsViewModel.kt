package com.roboticswala.hub.ui.screens.admin.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.LabEvent
import com.roboticswala.hub.data.repository.FirestoreNoticeAndEventRepository
import com.roboticswala.hub.data.repository.NoticeAndEventRepository
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminEventsUiState(
    val events: List<LabEvent> = emptyList(),
    val filteredEvents: List<LabEvent> = emptyList(),
    val categoryFilter: String = "All",
    val statusFilter: String = "All",
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val showCreateDialog: Boolean = false,
    val eventToEdit: LabEvent? = null,
    val eventToDelete: LabEvent? = null,
    val isActionLoading: Boolean = false,
    val snackbarMessage: String? = null,
    val errorMessage: String? = null
)

class AdminEventsViewModel(
    private val repository: NoticeAndEventRepository = FirestoreNoticeAndEventRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminEventsUiState())
    val uiState: StateFlow<AdminEventsUiState> = _uiState.asStateFlow()

    init {
        observeAllEvents()
    }

    private fun observeAllEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.observeAllEvents().collect { list ->
                _uiState.update { state ->
                    state.copy(
                        events = list,
                        filteredEvents = filterList(list, state.categoryFilter, state.statusFilter, state.searchQuery),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun setCategoryFilter(category: String) {
        _uiState.update { state ->
            state.copy(
                categoryFilter = category,
                filteredEvents = filterList(state.events, category, state.statusFilter, state.searchQuery)
            )
        }
    }

    fun setStatusFilter(status: String) {
        _uiState.update { state ->
            state.copy(
                statusFilter = status,
                filteredEvents = filterList(state.events, state.categoryFilter, status, state.searchQuery)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredEvents = filterList(state.events, state.categoryFilter, state.statusFilter, query)
            )
        }
    }

    private fun filterList(
        list: List<LabEvent>,
        category: String,
        status: String,
        query: String
    ): List<LabEvent> {
        return list.filter { item ->
            val matchesCat = category == "All" || item.category.equals(category, ignoreCase = true)
            val matchesStat = status == "All" || item.eventStatus.equals(status, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.location.contains(query, ignoreCase = true) ||
                    item.organizerName.contains(query, ignoreCase = true)

            matchesCat && matchesStat && matchesQuery
        }
    }

    fun openCreateDialog() = _uiState.update { it.copy(showCreateDialog = true, eventToEdit = null, errorMessage = null) }
    fun openEditDialog(event: LabEvent) = _uiState.update { it.copy(showCreateDialog = true, eventToEdit = event, errorMessage = null) }
    fun closeCreateDialog() = _uiState.update { it.copy(showCreateDialog = false, eventToEdit = null, errorMessage = null) }

    fun promptDelete(event: LabEvent) = _uiState.update { it.copy(eventToDelete = event) }
    fun dismissDelete() = _uiState.update { it.copy(eventToDelete = null) }

    fun saveEvent(
        title: String,
        description: String,
        category: String,
        date: String,
        startTime: String,
        endTime: String,
        location: String,
        organizer: String,
        maxParticipants: Int,
        deadline: String,
        externalLink: String
    ) {
        val adminUid = auth.currentUser?.uid ?: "Admin"
        val existing = _uiState.value.eventToEdit

        val event = (existing ?: LabEvent()).copy(
            title = title.trim(),
            description = description.trim(),
            category = category,
            eventDate = date,
            startTime = startTime,
            endTime = endTime,
            location = location.trim(),
            organizerName = organizer.trim(),
            maximumParticipants = maxParticipants,
            registrationDeadline = deadline,
            externalRegistrationLink = externalLink.trim(),
            createdByAdminUid = adminUid
        )

        viewModelScope.launch {
            if (existing != null) {
                repository.updateEvent(event).collect { res ->
                    handleResource(res, "Event updated successfully!")
                }
            } else {
                repository.createEvent(event).collect { res ->
                    handleResource(res, "Event published successfully!")
                }
            }
        }
    }

    fun cancelEvent(eventId: String) {
        viewModelScope.launch {
            repository.cancelEvent(eventId).collect { res ->
                when (res) {
                    is Resource.Success -> _uiState.update { it.copy(snackbarMessage = "Event cancelled.") }
                    is Resource.Error -> _uiState.update { it.copy(errorMessage = res.message) }
                    else -> {}
                }
            }
        }
    }

    fun confirmDelete() {
        val item = _uiState.value.eventToDelete ?: return
        viewModelScope.launch {
            repository.deleteEvent(item.eventId).collect { res ->
                when (res) {
                    is Resource.Success -> _uiState.update { it.copy(eventToDelete = null, snackbarMessage = "Event deleted.") }
                    is Resource.Error -> _uiState.update { it.copy(eventToDelete = null, snackbarMessage = res.message) }
                    else -> {}
                }
            }
        }
    }

    private fun handleResource(res: Resource<*>, successMsg: String) {
        when (res) {
            is Resource.Loading -> _uiState.update { it.copy(isActionLoading = true, errorMessage = null) }
            is Resource.Success -> {
                _uiState.update {
                    it.copy(
                        isActionLoading = false,
                        showCreateDialog = false,
                        eventToEdit = null,
                        snackbarMessage = successMsg
                    )
                }
            }
            is Resource.Error -> {
                _uiState.update {
                    it.copy(
                        isActionLoading = false,
                        errorMessage = res.message ?: "Operation failed."
                    )
                }
            }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(snackbarMessage = null, errorMessage = null) }
}
