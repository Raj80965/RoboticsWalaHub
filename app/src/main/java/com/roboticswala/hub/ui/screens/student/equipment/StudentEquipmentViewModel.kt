package com.roboticswala.hub.ui.screens.student.equipment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.Equipment
import com.roboticswala.hub.data.models.EquipmentRequest
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.data.repository.EquipmentRepository
import com.roboticswala.hub.data.repository.FirestoreEquipmentRepository
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentEquipmentUiState(
    val equipmentList: List<Equipment> = emptyList(),
    val filteredEquipment: List<Equipment> = emptyList(),
    val myRequests: List<EquipmentRequest> = emptyList(),
    val selectedTab: Int = 0, // 0 = Catalog, 1 = My Requests
    val categoryFilter: String = "All",
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val selectedEquipmentForRequest: Equipment? = null,
    val selectedEquipmentForDetails: Equipment? = null,
    val isActionLoading: Boolean = false,
    val snackbarMessage: String? = null,
    val errorMessage: String? = null
)

class StudentEquipmentViewModel(
    private val studentUid: String = FirebaseAuth.getInstance().currentUser?.uid ?: "",
    private val repository: EquipmentRepository = FirestoreEquipmentRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentEquipmentUiState())
    val uiState: StateFlow<StudentEquipmentUiState> = _uiState.asStateFlow()

    init {
        observeCatalog()
        observeRequests()
    }

    private fun observeCatalog() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.observeAllEquipment().collect { list ->
                _uiState.update { state ->
                    state.copy(
                        equipmentList = list,
                        filteredEquipment = filterList(list, state.categoryFilter, state.searchQuery),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun observeRequests() {
        viewModelScope.launch {
            repository.observeStudentRequests(studentUid).collect { reqs ->
                _uiState.update { it.copy(myRequests = reqs) }
            }
        }
    }

    fun selectTab(index: Int) = _uiState.update { it.copy(selectedTab = index) }

    fun setCategoryFilter(category: String) {
        _uiState.update { state ->
            state.copy(
                categoryFilter = category,
                filteredEquipment = filterList(state.equipmentList, category, state.searchQuery)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredEquipment = filterList(state.equipmentList, state.categoryFilter, query)
            )
        }
    }

    private fun filterList(list: List<Equipment>, category: String, query: String): List<Equipment> {
        return list.filter { item ->
            val matchesCat = category == "All" || item.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true) ||
                    item.storageLocation.contains(query, ignoreCase = true)

            matchesCat && matchesQuery
        }
    }

    fun openRequestDialog(equipment: Equipment) = _uiState.update { it.copy(selectedEquipmentForRequest = equipment, errorMessage = null) }
    fun closeRequestDialog() = _uiState.update { it.copy(selectedEquipmentForRequest = null, errorMessage = null) }

    fun openDetails(equipment: Equipment) = _uiState.update { it.copy(selectedEquipmentForDetails = equipment) }
    fun closeDetails() = _uiState.update { it.copy(selectedEquipmentForDetails = null) }

    fun submitRequest(
        userProfile: UserProfile,
        quantity: Int,
        purpose: String,
        returnDate: String,
        projectName: String
    ) {
        val eq = _uiState.value.selectedEquipmentForRequest ?: return
        val request = EquipmentRequest(
            studentUid = studentUid,
            studentId = userProfile.studentId.ifBlank { "STU-${studentUid.take(4)}" },
            studentName = userProfile.fullName.ifBlank { "Student" },
            equipmentId = eq.equipmentId,
            equipmentName = eq.name,
            requestedQuantity = quantity,
            purpose = purpose.trim(),
            expectedReturnDate = returnDate,
            relatedProjectName = projectName.trim()
        )

        viewModelScope.launch {
            repository.createEquipmentRequest(request).collect { res ->
                when (res) {
                    is Resource.Loading -> _uiState.update { it.copy(isActionLoading = true, errorMessage = null) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                selectedEquipmentForRequest = null,
                                snackbarMessage = "Request submitted for ${eq.name} ($quantity units)!"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                errorMessage = res.message ?: "Failed to submit request."
                            )
                        }
                    }
                }
            }
        }
    }

    fun cancelRequest(requestId: String) {
        viewModelScope.launch {
            repository.cancelEquipmentRequest(requestId).collect { res ->
                when (res) {
                    is Resource.Success -> _uiState.update { it.copy(snackbarMessage = "Request cancelled.") }
                    is Resource.Error -> _uiState.update { it.copy(errorMessage = res.message) }
                    else -> {}
                }
            }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(snackbarMessage = null, errorMessage = null) }
}

class StudentEquipmentViewModelFactory(
    private val studentUid: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentEquipmentViewModel::class.java)) {
            return StudentEquipmentViewModel(studentUid = studentUid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
