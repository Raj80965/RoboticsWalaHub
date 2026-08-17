package com.roboticswala.hub.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.roboticswala.hub.data.models.AdminDashboardData
import com.roboticswala.hub.data.models.BookingRequestItem
import com.roboticswala.hub.data.models.StudentDirectoryItem
import com.roboticswala.hub.data.models.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AdminUiState(
    val dashboardData: AdminDashboardData = AdminDashboardData(),
    val studentsList: List<StudentDirectoryItem> = emptyList(),
    val bookingsList: List<BookingRequestItem> = emptyList(),
    val selectedTab: String = "admin_dashboard",
    val isLoading: Boolean = false,
    val snackbarMessage: String? = null
)

class AdminViewModel(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    private var usersListener: ListenerRegistration? = null

    init {
        observeFirestoreStudents()
    }

    private fun observeFirestoreStudents() {
        usersListener = firestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val students = snapshot.documents.mapNotNull { doc ->
                        val data = doc.data ?: return@mapNotNull null
                        val profile = UserProfile.fromMap(data)
                        // Show all registered students or non-admins
                        if (!profile.isAdmin) {
                            StudentDirectoryItem(
                                id = if (profile.uid.isNotBlank()) profile.uid else doc.id,
                                name = profile.fullName.ifBlank { "Student (${profile.email})" },
                                email = profile.email,
                                rfidStatus = if (profile.isApproved) "Active" else "Pending Review",
                                currentProject = profile.branch.ifBlank { "Robotics Lab Member" },
                                attendance = 95.0,
                                status = profile.status.ifBlank { "Pending" },
                                phone = profile.phone,
                                parentName = profile.parentName,
                                parentPhone = profile.parentPhone,
                                photoUrl = profile.photoUrl,
                                initials = profile.initials
                            )
                        } else null
                    }

                    _uiState.update { state ->
                        state.copy(
                            studentsList = students,
                            dashboardData = state.dashboardData.copy(
                                totalStudents = students.size,
                                activeStudents = students.count { it.status.equals("Approved", ignoreCase = true) },
                                pendingApprovals = students.count { it.status.equals("Pending", ignoreCase = true) }
                            )
                        )
                    }
                }
            }
    }

    fun onTabSelected(route: String) {
        _uiState.update { it.copy(selectedTab = route) }
    }

    fun approveStudent(studentUid: String) {
        viewModelScope.launch {
            try {
                firestore.collection("users")
                    .document(studentUid)
                    .update(
                        mapOf(
                            "status" to "Approved",
                            "approvedAt" to System.currentTimeMillis()
                        )
                    )
                    .await()

                _uiState.update {
                    it.copy(snackbarMessage = "Student access Approved successfully!")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.localizedMessage ?: "Failed to approve student.")
                }
            }
        }
    }

    fun deleteStudent(studentUid: String) {
        viewModelScope.launch {
            try {
                firestore.collection("users")
                    .document(studentUid)
                    .delete()
                    .await()

                _uiState.update {
                    it.copy(snackbarMessage = "Student removed successfully!")
                }
            } catch (e: Exception) {
                try {
                    firestore.collection("users")
                        .document(studentUid)
                        .update("status", "Rejected")
                        .await()
                    _uiState.update {
                        it.copy(snackbarMessage = "Student access revoked (Rejected).")
                    }
                } catch (_: Exception) {
                    _uiState.update {
                        it.copy(snackbarMessage = e.localizedMessage ?: "Failed to remove student.")
                    }
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        usersListener?.remove()
    }
}
