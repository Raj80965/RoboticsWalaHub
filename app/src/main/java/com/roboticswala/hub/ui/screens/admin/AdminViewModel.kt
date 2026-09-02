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
    val selectedSubScreen: String? = null,
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
    private var bookingsListener: ListenerRegistration? = null
    private var projectsListener: ListenerRegistration? = null
    private var attendanceListener: ListenerRegistration? = null
    private var equipmentListener: ListenerRegistration? = null

    init {
        observeFirestoreStudents()
        observeCurrentAdminProfile()
        observeFirestoreBookings()
        observeFirestoreProjects()
        observeFirestoreAttendance()
        observeFirestoreEquipment()
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

    fun onTabSelected(route: String, subScreen: String? = null) {
        _uiState.update { it.copy(selectedTab = route, selectedSubScreen = subScreen) }
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

    private fun observeFirestoreBookings() {
        bookingsListener = firestore.collection("labBookings")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val pendingCount = snapshot.documents.count { doc ->
                        val status = doc.getString("status") ?: "Pending"
                        status.equals("Pending", ignoreCase = true)
                    }
                    _uiState.update { state ->
                        state.copy(
                            dashboardData = state.dashboardData.copy(
                                pendingBookings = pendingCount
                            )
                        )
                    }
                }
            }
    }

    private fun observeFirestoreProjects() {
        projectsListener = firestore.collection("projects")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val count = snapshot.documents.size
                    _uiState.update { state ->
                        state.copy(
                            dashboardData = state.dashboardData.copy(
                                activeProjects = count
                            )
                        )
                    }
                }
            }
    }

    private fun observeFirestoreAttendance() {
        attendanceListener = firestore.collection("attendanceRecords")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null && !snapshot.isEmpty) {
                    val totalRecords = snapshot.size()
                    val uniqueStudentUids = snapshot.documents
                        .mapNotNull { it.getString("studentUid") ?: it.getString("userId") }
                        .distinct().size

                    val approvedStudents = _uiState.value.dashboardData.activeStudents.coerceAtLeast(1)
                    val percent = if (totalRecords > 0) {
                        ((uniqueStudentUids.toDouble() / approvedStudents.toDouble()) * 100.0).coerceIn(10.0, 100.0)
                    } else 87.5

                    val rounded = String.format(java.util.Locale.US, "%.1f", percent).toDoubleOrNull() ?: 87.5

                    _uiState.update { state ->
                        state.copy(
                            dashboardData = state.dashboardData.copy(
                                todayAttendancePercentage = rounded
                            )
                        )
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            dashboardData = state.dashboardData.copy(
                                todayAttendancePercentage = 87.5
                            )
                        )
                    }
                }
            }
    }

    private fun observeFirestoreEquipment() {
        equipmentListener = firestore.collection("equipment")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null && !snapshot.isEmpty) {
                    val alerts = snapshot.documents.mapNotNull { doc ->
                        val name = doc.getString("name") ?: return@mapNotNull null
                        val available = doc.getLong("availableQuantity")?.toInt()
                            ?: doc.getLong("quantity")?.toInt() ?: 0
                        val total = doc.getLong("totalQuantity")?.toInt()
                            ?: doc.getLong("quantity")?.toInt() ?: 10
                        val alertLevel = when {
                            available <= 1 -> "Critical"
                            available <= 5 -> "Low Stock"
                            else -> null
                        }
                        if (alertLevel != null) {
                            com.roboticswala.hub.data.models.EquipmentItem(
                                name = name,
                                stockDetail = "$available units left (Total: $total)",
                                alertLevel = alertLevel,
                                quantity = available
                            )
                        } else null
                    }

                    if (alerts.isNotEmpty()) {
                        _uiState.update { state ->
                            state.copy(
                                dashboardData = state.dashboardData.copy(
                                    lowStockEquipment = alerts
                                )
                            )
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
        adminProfileListener?.remove()
        bookingsListener?.remove()
        projectsListener?.remove()
        attendanceListener?.remove()
        equipmentListener?.remove()
    }
}
