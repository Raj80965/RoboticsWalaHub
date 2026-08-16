package com.roboticswala.hub.ui.screens.admin

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.AttendanceRecord
import com.roboticswala.hub.data.models.AttendanceSession
import com.roboticswala.hub.data.models.SessionQRData
import com.roboticswala.hub.data.repository.AttendanceRepository
import com.roboticswala.hub.data.repository.FirestoreAttendanceRepository
import com.roboticswala.hub.utils.QRCodeGenerator
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class AdminAttendanceUiState(
    val activeSession: AttendanceSession? = null,
    val qrBitmap: Bitmap? = null,
    val records: List<AttendanceRecord> = emptyList(),
    val filteredRecords: List<AttendanceRecord> = emptyList(),
    val searchQuery: String = "",
    val statusFilter: String = "All", // "All", "Checked In", "Completed"
    val labFilter: String = "All",
    val selectedDurationMinutes: Int = 5,
    val labNameInput: String = "Main Robotics Lab Bay 1",
    val isCreatingSession: Boolean = false,
    val isStoppingSession: Boolean = false,
    val remainingSeconds: Long = 0L,
    val snackbarMessage: String? = null
)

class AdminAttendanceViewModel(
    private val attendanceRepository: AttendanceRepository = FirestoreAttendanceRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminAttendanceUiState())
    val uiState: StateFlow<AdminAttendanceUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        observeActiveSession()
        observeTodayRecords()
    }

    private fun observeActiveSession() {
        viewModelScope.launch {
            attendanceRepository.observeActiveAttendanceSession().collect { session ->
                if (session != null && session.remainingSeconds > 0) {
                    val qrPayload = QRCodeGenerator.encodeSessionPayload(
                        SessionQRData(
                            sessionId = session.sessionId,
                            sessionToken = session.sessionToken,
                            labName = session.labName,
                            expiresAt = session.expiresAt
                        )
                    )
                    val bitmap = QRCodeGenerator.generateQRCodeBitmap(qrPayload, size = 600)
                    _uiState.update {
                        it.copy(
                            activeSession = session,
                            qrBitmap = bitmap,
                            remainingSeconds = session.remainingSeconds
                        )
                    }
                    startCountdown(session)
                } else {
                    countdownJob?.cancel()
                    _uiState.update {
                        it.copy(
                            activeSession = null,
                            qrBitmap = null,
                            remainingSeconds = 0L
                        )
                    }
                }
            }
        }
    }

    private fun startCountdown(session: AttendanceSession) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (isActive) {
                val remaining = session.remainingSeconds
                if (remaining <= 0) {
                    _uiState.update { it.copy(remainingSeconds = 0L, activeSession = null, qrBitmap = null) }
                    break
                }
                _uiState.update { it.copy(remainingSeconds = remaining) }
                delay(1000)
            }
        }
    }

    private fun observeTodayRecords() {
        viewModelScope.launch {
            attendanceRepository.observeAdminAttendanceRecords().collect { records ->
                _uiState.update { state ->
                    state.copy(
                        records = records,
                        filteredRecords = filterRecords(records, state.searchQuery, state.statusFilter, state.labFilter)
                    )
                }
            }
        }
    }

    fun startSession(labName: String, durationMinutes: Int) {
        val adminUid = auth.currentUser?.uid ?: "ADMIN-LOCAL"
        viewModelScope.launch {
            attendanceRepository.createAttendanceSession(
                labName = labName,
                durationMinutes = durationMinutes,
                adminUid = adminUid
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isCreatingSession = true) }
                    is Resource.Success -> {
                        val session = resource.data
                        if (session != null) {
                            val qrPayload = QRCodeGenerator.encodeSessionPayload(
                                SessionQRData(
                                    sessionId = session.sessionId,
                                    sessionToken = session.sessionToken,
                                    labName = session.labName,
                                    expiresAt = session.expiresAt
                                )
                            )
                            val bitmap = QRCodeGenerator.generateQRCodeBitmap(qrPayload, size = 600)
                            _uiState.update {
                                it.copy(
                                    isCreatingSession = false,
                                    activeSession = session,
                                    qrBitmap = bitmap,
                                    remainingSeconds = session.remainingSeconds,
                                    snackbarMessage = "Attendance Session started for $labName (${durationMinutes}m)!"
                                )
                            }
                            startCountdown(session)
                        } else {
                            _uiState.update {
                                it.copy(
                                    isCreatingSession = false,
                                    snackbarMessage = "Attendance Session started for $labName (${durationMinutes}m)!"
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isCreatingSession = false,
                                snackbarMessage = resource.message ?: "Failed to start session."
                            )
                        }
                    }
                }
            }
        }
    }

    fun stopSession() {
        val sessionId = _uiState.value.activeSession?.sessionId ?: return
        viewModelScope.launch {
            attendanceRepository.stopAttendanceSession(sessionId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isStoppingSession = true) }
                    is Resource.Success -> {
                        countdownJob?.cancel()
                        _uiState.update {
                            it.copy(
                                isStoppingSession = false,
                                activeSession = null,
                                qrBitmap = null,
                                remainingSeconds = 0L,
                                snackbarMessage = "Attendance session stopped successfully."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isStoppingSession = false,
                                snackbarMessage = resource.message ?: "Failed to stop session."
                            )
                        }
                    }
                }
            }
        }
    }

    fun setLabNameInput(name: String) {
        _uiState.update { it.copy(labNameInput = name) }
    }

    fun setDuration(minutes: Int) {
        _uiState.update { it.copy(selectedDurationMinutes = minutes) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredRecords = filterRecords(state.records, query, state.statusFilter, state.labFilter)
            )
        }
    }

    fun setStatusFilter(status: String) {
        _uiState.update { state ->
            state.copy(
                statusFilter = status,
                filteredRecords = filterRecords(state.records, state.searchQuery, status, state.labFilter)
            )
        }
    }

    fun setLabFilter(lab: String) {
        _uiState.update { state ->
            state.copy(
                labFilter = lab,
                filteredRecords = filterRecords(state.records, state.searchQuery, state.statusFilter, lab)
            )
        }
    }

    private fun filterRecords(
        list: List<AttendanceRecord>,
        query: String,
        status: String,
        lab: String
    ): List<AttendanceRecord> {
        return list.filter { record ->
            val matchesQuery = query.isBlank() ||
                    record.fullName.contains(query, ignoreCase = true) ||
                    record.studentId.contains(query, ignoreCase = true)

            val matchesStatus = status == "All" || record.status.equals(status, ignoreCase = true)
            val matchesLab = lab == "All" || record.labName.equals(lab, ignoreCase = true)

            matchesQuery && matchesStatus && matchesLab
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
