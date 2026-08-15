package com.roboticswala.hub.ui.screens.student.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.AttendanceRecord
import com.roboticswala.hub.data.models.AttendanceScanResult
import com.roboticswala.hub.data.models.AttendanceSession
import com.roboticswala.hub.data.models.AttendanceStats
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.data.repository.AttendanceRepository
import com.roboticswala.hub.data.repository.FirestoreAttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentAttendanceUiState(
    val stats: AttendanceStats = AttendanceStats(),
    val historyRecords: List<AttendanceRecord> = emptyList(),
    val activeSession: AttendanceSession? = null,
    val isProcessingScan: Boolean = false,
    val scanResult: AttendanceScanResult? = null,
    val isLoadingHistory: Boolean = false,
    val errorMessage: String? = null
)

class StudentAttendanceViewModel(
    private val studentUid: String = FirebaseAuth.getInstance().currentUser?.uid ?: "",
    private val attendanceRepository: AttendanceRepository = FirestoreAttendanceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentAttendanceUiState())
    val uiState: StateFlow<StudentAttendanceUiState> = _uiState.asStateFlow()

    init {
        if (studentUid.isNotBlank()) {
            observeStats()
            observeHistory()
            observeActiveSession()
        }
    }

    private fun observeStats() {
        viewModelScope.launch {
            attendanceRepository.observeStudentAttendanceStats(studentUid).collect { stats ->
                _uiState.update { it.copy(stats = stats) }
            }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHistory = true) }
            attendanceRepository.observeStudentAttendanceHistory(studentUid).collect { records ->
                _uiState.update {
                    it.copy(
                        historyRecords = records,
                        isLoadingHistory = false
                    )
                }
            }
        }
    }

    private fun observeActiveSession() {
        viewModelScope.launch {
            attendanceRepository.observeActiveAttendanceSession().collect { session ->
                _uiState.update { it.copy(activeSession = session) }
            }
        }
    }

    fun onQRScanned(qrPayload: String, studentProfile: UserProfile) {
        if (_uiState.value.isProcessingScan) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingScan = true, scanResult = null) }
            attendanceRepository.processAttendanceQRScan(qrPayload, studentProfile).collect { result ->
                _uiState.update {
                    it.copy(
                        isProcessingScan = false,
                        scanResult = result
                    )
                }
            }
        }
    }

    fun clearScanResult() {
        _uiState.update { it.copy(scanResult = null, isProcessingScan = false) }
    }
}

class StudentAttendanceViewModelFactory(
    private val studentUid: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentAttendanceViewModel::class.java)) {
            return StudentAttendanceViewModel(studentUid = studentUid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
