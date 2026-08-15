package com.roboticswala.hub.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.LabBooking
import com.roboticswala.hub.data.repository.FirestoreLabBookingRepository
import com.roboticswala.hub.data.repository.LabBookingRepository
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminBookingUiState(
    val bookings: List<LabBooking> = emptyList(),
    val filteredBookings: List<LabBooking> = emptyList(),
    val statusFilter: String = "All", // "All", "Pending", "Approved", "Rejected", "Cancelled"
    val searchQuery: String = "",
    val dateFilter: String = "All",
    val selectedBookingForDetails: LabBooking? = null,
    val bookingToReject: LabBooking? = null,
    val isProcessing: Boolean = false,
    val conflictError: String? = null,
    val snackbarMessage: String? = null
)

class AdminBookingViewModel(
    private val bookingRepository: LabBookingRepository = FirestoreLabBookingRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminBookingUiState())
    val uiState: StateFlow<AdminBookingUiState> = _uiState.asStateFlow()

    init {
        observeAllBookings()
    }

    private fun observeAllBookings() {
        viewModelScope.launch {
            bookingRepository.observeAllAdminBookings().collect { list ->
                _uiState.update { state ->
                    state.copy(
                        bookings = list,
                        filteredBookings = filterList(list, state.searchQuery, state.statusFilter, state.dateFilter)
                    )
                }
            }
        }
    }

    fun setStatusFilter(status: String) {
        _uiState.update { state ->
            state.copy(
                statusFilter = status,
                filteredBookings = filterList(state.bookings, state.searchQuery, status, state.dateFilter)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredBookings = filterList(state.bookings, query, state.statusFilter, state.dateFilter)
            )
        }
    }

    fun setDateFilter(date: String) {
        _uiState.update { state ->
            state.copy(
                dateFilter = date,
                filteredBookings = filterList(state.bookings, state.searchQuery, state.statusFilter, date)
            )
        }
    }

    private fun filterList(
        list: List<LabBooking>,
        query: String,
        status: String,
        date: String
    ): List<LabBooking> {
        return list.filter { booking ->
            val matchesQuery = query.isBlank() ||
                    booking.studentName.contains(query, ignoreCase = true) ||
                    booking.studentId.contains(query, ignoreCase = true) ||
                    booking.projectName.contains(query, ignoreCase = true)

            val matchesStatus = status == "All" || booking.status.equals(status, ignoreCase = true)
            val matchesDate = date == "All" || date.isBlank() || booking.bookingDate == date

            matchesQuery && matchesStatus && matchesDate
        }
    }

    fun approveBooking(booking: LabBooking) {
        val adminUid = auth.currentUser?.uid ?: "ADMIN-LOCAL"
        viewModelScope.launch {
            bookingRepository.approveBooking(booking, adminUid).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isProcessing = true, conflictError = null) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                selectedBookingForDetails = null,
                                conflictError = null,
                                snackbarMessage = "Booking for '${booking.projectName}' Approved! Slot reserved."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                conflictError = resource.message ?: "Failed to approve booking."
                            )
                        }
                    }
                }
            }
        }
    }

    fun promptRejectBooking(booking: LabBooking) {
        _uiState.update { it.copy(bookingToReject = booking) }
    }

    fun confirmRejectBooking(reason: String) {
        val booking = _uiState.value.bookingToReject ?: return
        val adminUid = auth.currentUser?.uid ?: "ADMIN-LOCAL"

        viewModelScope.launch {
            bookingRepository.rejectBooking(booking.bookingId, reason, adminUid).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isProcessing = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                bookingToReject = null,
                                selectedBookingForDetails = null,
                                snackbarMessage = "Booking request rejected."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                snackbarMessage = resource.message ?: "Failed to reject booking."
                            )
                        }
                    }
                }
            }
        }
    }

    fun dismissRejectDialog() {
        _uiState.update { it.copy(bookingToReject = null) }
    }

    fun openDetails(booking: LabBooking) {
        _uiState.update { it.copy(selectedBookingForDetails = booking) }
    }

    fun closeDetails() {
        _uiState.update { it.copy(selectedBookingForDetails = null) }
    }

    fun clearConflictError() {
        _uiState.update { it.copy(conflictError = null) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
