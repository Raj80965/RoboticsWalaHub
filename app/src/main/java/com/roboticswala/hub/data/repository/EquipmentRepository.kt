package com.roboticswala.hub.data.repository

import com.roboticswala.hub.data.models.Equipment
import com.roboticswala.hub.data.models.EquipmentRequest
import com.roboticswala.hub.data.models.InventoryTransaction
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.Flow

interface EquipmentRepository {

    // ── Equipment Catalog ────────────────────────────────────────────────────
    fun addEquipment(
        equipment: Equipment,
        imageBytes: ByteArray? = null
    ): Flow<Resource<Equipment>>

    fun updateEquipment(
        equipment: Equipment,
        imageBytes: ByteArray? = null
    ): Flow<Resource<Unit>>

    fun deleteEquipment(equipmentId: String): Flow<Resource<Unit>>

    fun observeAllEquipment(): Flow<List<Equipment>>

    fun observeLowStockEquipment(): Flow<List<Equipment>>

    // ── Student Requests ─────────────────────────────────────────────────────
    fun createEquipmentRequest(request: EquipmentRequest): Flow<Resource<EquipmentRequest>>

    fun cancelEquipmentRequest(requestId: String): Flow<Resource<Unit>>

    fun observeStudentRequests(studentUid: String): Flow<List<EquipmentRequest>>

    // ── Admin Request Management & Issue/Return Transactions ─────────────────
    fun observeAllRequests(): Flow<List<EquipmentRequest>>

    fun approveRequest(
        requestId: String,
        adminUid: String
    ): Flow<Resource<Unit>>

    fun rejectRequest(
        requestId: String,
        rejectionReason: String,
        adminUid: String
    ): Flow<Resource<Unit>>

    fun issueEquipment(
        requestId: String,
        actualQuantity: Int,
        adminUid: String,
        adminName: String
    ): Flow<Resource<Unit>>

    fun returnEquipment(
        requestId: String,
        returnCondition: String,
        returnNotes: String,
        isStockRestored: Boolean,
        adminUid: String,
        adminName: String
    ): Flow<Resource<Unit>>

    // ── Audit History ────────────────────────────────────────────────────────
    fun observeInventoryTransactions(): Flow<List<InventoryTransaction>>
}
