package com.roboticswala.hub.ui.screens.admin.equipment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.Equipment
import com.roboticswala.hub.data.repository.EquipmentRepository
import com.roboticswala.hub.data.repository.FirestoreEquipmentRepository
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminEquipmentUiState(
    val equipmentList: List<Equipment> = emptyList(),
    val filteredEquipment: List<Equipment> = emptyList(),
    val lowStockCount: Int = 0,
    val outOfStockCount: Int = 0,
    val categoryFilter: String = "All",
    val conditionFilter: String = "All",
    val onlyLowStock: Boolean = false,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val equipmentToEdit: Equipment? = null,
    val equipmentToDelete: Equipment? = null,
    val selectedEquipmentDetails: Equipment? = null,
    val isActionLoading: Boolean = false,
    val snackbarMessage: String? = null,
    val errorMessage: String? = null
)

class AdminEquipmentViewModel(
    private val repository: EquipmentRepository = FirestoreEquipmentRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminEquipmentUiState())
    val uiState: StateFlow<AdminEquipmentUiState> = _uiState.asStateFlow()

    init {
        observeEquipment()
    }

    private fun observeEquipment() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.observeAllEquipment().collect { list ->
                val lowCount = list.count { it.isLowStock }
                val outCount = list.count { it.isOutOfStock }
                _uiState.update { state ->
                    state.copy(
                        equipmentList = list,
                        lowStockCount = lowCount,
                        outOfStockCount = outCount,
                        filteredEquipment = filterList(list, state.categoryFilter, state.conditionFilter, state.onlyLowStock, state.searchQuery),
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
                filteredEquipment = filterList(state.equipmentList, category, state.conditionFilter, state.onlyLowStock, state.searchQuery)
            )
        }
    }

    fun setConditionFilter(condition: String) {
        _uiState.update { state ->
            state.copy(
                conditionFilter = condition,
                filteredEquipment = filterList(state.equipmentList, state.categoryFilter, condition, state.onlyLowStock, state.searchQuery)
            )
        }
    }

    fun toggleLowStockOnly() {
        _uiState.update { state ->
            val next = !state.onlyLowStock
            state.copy(
                onlyLowStock = next,
                filteredEquipment = filterList(state.equipmentList, state.categoryFilter, state.conditionFilter, next, state.searchQuery)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredEquipment = filterList(state.equipmentList, state.categoryFilter, state.conditionFilter, state.onlyLowStock, query)
            )
        }
    }

    private fun filterList(
        list: List<Equipment>,
        category: String,
        condition: String,
        lowStockOnly: Boolean,
        query: String
    ): List<Equipment> {
        return list.filter { item ->
            val matchesCat = category == "All" || item.category.equals(category, ignoreCase = true)
            val matchesCond = condition == "All" || item.condition.equals(condition, ignoreCase = true)
            val matchesLowStock = !lowStockOnly || item.isLowStock || item.isOutOfStock
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.storageLocation.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true)

            matchesCat && matchesCond && matchesLowStock && matchesQuery
        }
    }

    fun openAddDialog() = _uiState.update { it.copy(showAddDialog = true, equipmentToEdit = null, errorMessage = null) }
    fun openEditDialog(equipment: Equipment) = _uiState.update { it.copy(showAddDialog = true, equipmentToEdit = equipment, errorMessage = null) }
    fun closeAddDialog() = _uiState.update { it.copy(showAddDialog = false, equipmentToEdit = null, errorMessage = null) }

    fun openDetails(equipment: Equipment) = _uiState.update { it.copy(selectedEquipmentDetails = equipment) }
    fun closeDetails() = _uiState.update { it.copy(selectedEquipmentDetails = null) }

    fun promptDelete(equipment: Equipment) = _uiState.update { it.copy(equipmentToDelete = equipment) }
    fun dismissDelete() = _uiState.update { it.copy(equipmentToDelete = null) }

    fun saveEquipment(
        name: String,
        category: String,
        description: String,
        totalQuantity: Int,
        availableQuantity: Int,
        minimumStock: Int,
        storageLocation: String,
        condition: String,
        purchaseDate: String,
        unitPrice: Double,
        supplierName: String
    ) {
        val adminUid = auth.currentUser?.uid ?: "Admin"
        val existing = _uiState.value.equipmentToEdit

        val eq = (existing ?: Equipment()).copy(
            name = name.trim(),
            category = category,
            description = description.trim(),
            totalQuantity = totalQuantity,
            availableQuantity = availableQuantity,
            minimumStockLevel = minimumStock,
            storageLocation = storageLocation.trim(),
            condition = condition,
            purchaseDate = purchaseDate,
            unitPrice = unitPrice,
            supplierName = supplierName.trim(),
            createdByAdminUid = adminUid
        )

        viewModelScope.launch {
            if (existing != null) {
                repository.updateEquipment(eq).collect { res ->
                    handleResource(res, "Equipment \"${eq.name}\" updated successfully!")
                }
            } else {
                repository.addEquipment(eq).collect { res ->
                    handleResource(res, "Equipment \"${eq.name}\" added to inventory!")
                }
            }
        }
    }

    fun confirmDelete() {
        val item = _uiState.value.equipmentToDelete ?: return
        viewModelScope.launch {
            repository.deleteEquipment(item.equipmentId).collect { res ->
                when (res) {
                    is Resource.Success -> _uiState.update { it.copy(equipmentToDelete = null, snackbarMessage = "Equipment deleted.") }
                    is Resource.Error -> _uiState.update { it.copy(equipmentToDelete = null, snackbarMessage = res.message) }
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
                        showAddDialog = false,
                        equipmentToEdit = null,
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
