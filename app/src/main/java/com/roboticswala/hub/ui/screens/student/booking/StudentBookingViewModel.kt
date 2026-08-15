package com.roboticswala.hub.ui.screens.student.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.LabBooking
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.data.repository.FirestoreLabBookingRepository
import com.roboticswala.hub.data.repository.LabBookingRepository
import com.roboticswala.hub.utils.BookingTimeUtils
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentBookingUiState(
    val bookings: List<LabBooking> = emptyList(),
    val filteredBookings: List<LabBooking> = emptyList(),
    val selectedDateFilter: String = "All",
    val todayBooking: LabBooking? = null,
    val isSubmitting: Boolean = false,
    val isCancelling: Boolean = false,
    val selectedBookingForDetails: LabBooking? = null,
    val showCreateDialog: Boolean = false,
    val snackbarMessage: String? = null,
    val errorMessage: String? = null
)

class StudentBookingViewModel(
    private val studentUid: String = FirebaseAuth.getInstance().currentUser?.uid ?: "",
    private val bookingRepository: LabBookingRepository = FirestoreLabBookingRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentBookingUiState())
    val uiState: StateFlow<StudentBookingUiState> = _uiState.asStateFlow()

    init {
        if (studentUid.isNotBlank()) {
            observeStudentBookings()
            observeTodayBooking()
        }
    }

    private fun observeStudentBookings() {
        viewModelScope.launch {
            bookingRepository.observeStudentBookings(studentUid).collect { list ->
                _uiState.update { state ->
                    state.copy(
                        bookings = list,
                        filteredBookings = filterList(list, state.selectedDateFilter)
                    )
                }
            }
        }
    }

    private fun observeTodayBooking() {
        viewModelScope.launch {
            bookingRepository.observeTodayStudentBooking(studentUid).collect { booking ->
                _uiState.update { it.copy(todayBooking = booking) }
            }
        }
    }

    fun setDateFilter(date: String) {
        _uiState.update { state ->
            state.copy(
                selectedDateFilter = date,
                filteredBookings = filterList(state.bookings, date)
            )
        }
    }

    private fun filterList(list: List<LabBooking>, dateFilter: String): List<LabBooking> {
        return if (dateFilter == "All" || dateFilter.isBlank()) {
            list
        } else {
            list.filter { it.bookingDate == dateFilter }
        }
    }

    fun createBooking(
        date: String,
        startTime: String,
        endTime: String,
        projectName: String,
        workDescription: String,
        requiredEquipment: String,
        teamMembers: String,
        studentProfile: UserProfile
    ) {
        if (projectName.isBlank() || workDescription.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Project name and work description are required.") }
            return
        }

        val startMins = BookingTimeUtils.timeToMinutes(startTime)
        val endMins = BookingTimeUtils.timeToMinutes(endTime)

        if (startMins >= endMins) {
            _uiState.update { it.copy(errorMessage = "Start time must be before end time.") }
            return
        }

        if (BookingTimeUtils.isDateInPast(date)) {
            _uiState.update { it.copy(errorMessage = "Cannot book a lab slot in the past.") }
            return
        }

        val booking = LabBooking(
            studentUid = studentUid,
            studentId = studentProfile.studentId.ifBlank { "RW-STD-${studentUid.take(4)}" },
            studentName = studentProfile.fullName.ifBlank { "Student" },
            bookingDate = date,
            startTime = startTime,
            endTime = endTime,
            startMinutes = startMins,
            endMinutes = endMins,
            projectName = projectName,
            workDescription = workDescription,
            requiredEquipment = requiredEquipment,
            teamMembers = teamMembers,
            status = LabBooking.STATUS_PENDING
        )

        viewModelScope.launch {
            bookingRepository.createBookingRequest(booking).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                showCreateDialog = false,
                                snackbarMessage = "Lab booking request submitted successfully! (Status: Pending Review)"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                errorMessage = resource.message ?: "Failed to submit booking."
                            )
                        }
                    }
                }
            }
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            bookingRepository.cancelBooking(bookingId, studentUid).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isCancelling = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isCancelling = false,
                                selectedBookingForDetails = null,
                                snackbarMessage = "Booking cancelled successfully."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isCancelling = false,
                                snackbarMessage = resource.message ?: "Failed to cancel booking."
                            )
                        }
                    }
                }
            }
        }
    }

    fun openDetails(booking: LabBooking) {
        _uiState.update { it.copy(selectedBookingForDetails = booking) }
    }

    fun closeDetails() {
        _uiState.update { it.copy(selectedBookingForDetails = null) }
    }

    fun openCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = true, errorMessage = null) }
    }

    fun closeCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = false, errorMessage = null) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(snackbarMessage = null, errorMessage = null) }
    }
}

class StudentBookingViewModelFactory(
    private val studentUid: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentBookingViewModel::class.java)) {
            return StudentBookingViewModel(studentUid = studentUid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
