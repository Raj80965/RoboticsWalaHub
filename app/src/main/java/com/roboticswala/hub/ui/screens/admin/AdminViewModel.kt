package com.roboticswala.hub.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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
    val adminProfile: UserProfile? = null,
    val selectedTab: String = "admin_dashboard",
    val isLoading: Boolean = false,
    val snackbarMessage: String? = null
)

class AdminViewModel(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    private var usersListener: ListenerRegistration? = null
    private var adminProfileListener: ListenerRegistration? = null

    init {
        observeFirestoreStudents()
        observeCurrentAdminProfile()
    }

    private fun observeCurrentAdminProfile() {
        val currentUid = auth.currentUser?.uid ?: return
        adminProfileListener = firestore.collection("users")
            .document(currentUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null && snapshot.exists()) {
                    val profile = snapshot.data?.let { UserProfile.fromMap(it) } ?: UserProfile(
                        uid = currentUid,
                        fullName = auth.currentUser?.displayName ?: "Administrator",
                        email = auth.currentUser?.email ?: "",
                        role = "Admin",
                        status = "Approved"
                    )
                    _uiState.update { it.copy(adminProfile = profile) }
                } else {
                    val fallback = UserProfile(
                        uid = currentUid,
                        fullName = auth.currentUser?.displayName ?: "Administrator",
                        email = auth.currentUser?.email ?: "",
                        role = "Admin",
                        status = "Approved"
                    )
                    _uiState.update { it.copy(adminProfile = fallback) }
                }
            }
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
                                studentId = profile.displayStudentId,
                                name = profile.fullName.ifBlank { "Student (${profile.email})" },
                                email = profile.email,
                                rfidStatus = if (profile.isApproved) "Active" else "Pending Review",
                                currentProject = profile.branch.ifBlank { "Robotics Lab Member" },
                                attendance = 95.0,
                                status = profile.status.ifBlank { "Pending" },
                                college = profile.college,
                                branch = profile.branch,
                                year = profile.year,
                                phone = profile.phone,
                                parentName = profile.parentName,
                                parentPhone = profile.parentPhone,
                                emergencyContact = profile.emergencyContact,
                                aadharNumber = profile.aadharNumber,
                                aadharCardUrl = profile.aadharCardUrl,
                                photoUrl = profile.photoUrl,
                                initials = profile.initials,
                                createdAt = profile.createdAt
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

    fun updateAdminProfile(
        fullName: String,
        phone: String,
        college: String,
        branch: String,
        year: String,
        adminId: String,
        emergencyContact: String
    ) {
        val currentUid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                firestore.collection("users")
                    .document(currentUid)
                    .update(
                        mapOf(
                            "fullName" to fullName,
                            "phone" to phone,
                            "college" to college,
                            "branch" to branch,
                            "year" to year,
                            "studentId" to adminId,
                            "emergencyContact" to emergencyContact,
                            "role" to "Admin",
                            "status" to "Approved"
                        )
                    )
                    .await()

                _uiState.update {
                    it.copy(snackbarMessage = "Admin Profile updated successfully!")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.localizedMessage ?: "Failed to update profile.")
                }
            }
        }
    }

    fun updateAdminPhoto(photoBase64: String) {
        val currentUid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                firestore.collection("users")
                    .document(currentUid)
                    .update("photoUrl", photoBase64)
                    .await()

                _uiState.update {
                    it.copy(snackbarMessage = "Admin Photo updated successfully!")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.localizedMessage ?: "Failed to upload photo.")
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
        adminProfileListener?.remove()
    }
}
