package com.roboticswala.hub.ui.screens.admin.equipment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.EquipmentRequest
import com.roboticswala.hub.data.models.InventoryTransaction
import com.roboticswala.hub.data.repository.EquipmentRepository
import com.roboticswala.hub.data.repository.FirestoreEquipmentRepository
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminEquipmentRequestsUiState(
    val requests: List<EquipmentRequest> = emptyList(),
    val filteredRequests: List<EquipmentRequest> = emptyList(),
    val transactions: List<InventoryTransaction> = emptyList(),
    val statusFilter: String = "All",
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val selectedRequestForAction: EquipmentRequest? = null,
    val isIssuing: Boolean = false,
    val isReturning: Boolean = false,
    val isRejecting: Boolean = false,
    val isActionLoading: Boolean = false,
    val snackbarMessage: String? = null,
    val errorMessage: String? = null
)

class AdminEquipmentRequestsViewModel(
    private val repository: EquipmentRepository = FirestoreEquipmentRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminEquipmentRequestsUiState())
    val uiState: StateFlow<AdminEquipmentRequestsUiState> = _uiState.asStateFlow()

    init {
        observeRequests()
        observeTransactions()
    }

    private fun observeRequests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.observeAllRequests().collect { list ->
                _uiState.update { state ->
                    state.copy(
                        requests = list,
                        filteredRequests = filterList(list, state.statusFilter, state.searchQuery),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            repository.observeInventoryTransactions().collect { txs ->
                _uiState.update { it.copy(transactions = txs) }
            }
        }
    }

    fun setStatusFilter(status: String) {
        _uiState.update { state ->
            state.copy(
                statusFilter = status,
                filteredRequests = filterList(state.requests, status, state.searchQuery)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredRequests = filterList(state.requests, state.statusFilter, query)
            )
        }
    }

    private fun filterList(list: List<EquipmentRequest>, status: String, query: String): List<EquipmentRequest> {
        return list.filter { item ->
            val matchesStatus = status == "All" || item.status.equals(status, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    item.studentName.contains(query, ignoreCase = true) ||
                    item.studentId.contains(query, ignoreCase = true) ||
                    item.equipmentName.contains(query, ignoreCase = true)

            matchesStatus && matchesQuery
        }
    }

    fun openIssueDialog(request: EquipmentRequest) = _uiState.update { it.copy(selectedRequestForAction = request, isIssuing = true, errorMessage = null) }
    fun openReturnDialog(request: EquipmentRequest) = _uiState.update { it.copy(selectedRequestForAction = request, isReturning = true, errorMessage = null) }
    fun openRejectDialog(request: EquipmentRequest) = _uiState.update { it.copy(selectedRequestForAction = request, isRejecting = true, errorMessage = null) }
    fun closeActionDialog() = _uiState.update { it.copy(selectedRequestForAction = null, isIssuing = false, isReturning = false, isRejecting = false, errorMessage = null) }

    fun approveRequest(requestId: String) {
        val adminUid = auth.currentUser?.uid ?: "Admin"
        viewModelScope.launch {
            repository.approveRequest(requestId, adminUid).collect { res ->
                when (res) {
                    is Resource.Success -> _uiState.update { it.copy(snackbarMessage = "Request approved! Ready for equipment issue.") }
                    is Resource.Error -> _uiState.update { it.copy(errorMessage = res.message) }
                    else -> {}
                }
            }
        }
    }

    fun rejectRequest(requestId: String, reason: String) {
        val adminUid = auth.currentUser?.uid ?: "Admin"
        viewModelScope.launch {
            repository.rejectRequest(requestId, reason, adminUid).collect { res ->
                when (res) {
                    is Resource.Success -> _uiState.update { it.copy(selectedRequestForAction = null, isRejecting = false, snackbarMessage = "Request rejected.") }
                    is Resource.Error -> _uiState.update { it.copy(errorMessage = res.message) }
                    else -> {}
                }
            }
        }
    }

    fun confirmIssue(quantity: Int) {
        val req = _uiState.value.selectedRequestForAction ?: return
        val adminUid = auth.currentUser?.uid ?: "Admin"
        val adminName = auth.currentUser?.displayName ?: "Lab Admin"

        viewModelScope.launch {
            repository.issueEquipment(req.requestId, quantity, adminUid, adminName).collect { res ->
                when (res) {
                    is Resource.Loading -> _uiState.update { it.copy(isActionLoading = true, errorMessage = null) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                selectedRequestForAction = null,
                                isIssuing = false,
                                snackbarMessage = "Issued $quantity units of ${req.equipmentName} to ${req.studentName}!"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                errorMessage = res.message ?: "Failed to issue equipment."
                            )
                        }
                    }
                }
            }
        }
    }

    fun confirmReturn(condition: String, notes: String, isStockRestored: Boolean) {
        val req = _uiState.value.selectedRequestForAction ?: return
        val adminUid = auth.currentUser?.uid ?: "Admin"
        val adminName = auth.currentUser?.displayName ?: "Lab Admin"

        viewModelScope.launch {
            repository.returnEquipment(req.requestId, condition, notes, isStockRestored, adminUid, adminName).collect { res ->
                when (res) {
                    is Resource.Loading -> _uiState.update { it.copy(isActionLoading = true, errorMessage = null) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                selectedRequestForAction = null,
                                isReturning = false,
                                snackbarMessage = "Equipment return processed for ${req.equipmentName}."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                errorMessage = res.message ?: "Failed to process return."
                            )
                        }
                    }
                }
            }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(snackbarMessage = null, errorMessage = null) }
}
