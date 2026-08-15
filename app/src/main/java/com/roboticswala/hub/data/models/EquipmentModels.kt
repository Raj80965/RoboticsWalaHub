package com.roboticswala.hub.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Equipment(
    @DocumentId
    val equipmentId: String = "",
    val name: String = "",
    val category: String = CATEGORY_SENSORS,
    val description: String = "",
    val imageUrl: String = "",
    val totalQuantity: Int = 0,
    val availableQuantity: Int = 0,
    val issuedQuantity: Int = 0,
    val minimumStockLevel: Int = 2,
    val storageLocation: String = "", // e.g. "Rack B, Bin 4"
    val condition: String = CONDITION_GOOD,
    val purchaseDate: String = "",     // Format: "yyyy-MM-dd"
    val unitPrice: Double = 0.0,
    val supplierName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdByAdminUid: String = ""
) {
    companion object {
        // Categories
        const val CATEGORY_MICROCONTROLLERS = "Microcontrollers"
        const val CATEGORY_SENSORS = "Sensors"
        const val CATEGORY_MOTORS = "Motors"
        const val CATEGORY_MOTOR_DRIVERS = "Motor Drivers"
        const val CATEGORY_BATTERIES = "Batteries"
        const val CATEGORY_POWER_SUPPLIES = "Power Supplies"
        const val CATEGORY_TOOLS = "Tools"
        const val CATEGORY_3D_PRINTING = "3D Printing"
        const val CATEGORY_COMMUNICATION = "Communication Modules"
        const val CATEGORY_DISPLAYS = "Displays"
        const val CATEGORY_MECHANICAL = "Mechanical Parts"
        const val CATEGORY_WIRES = "Wires and Connectors"
        const val CATEGORY_OTHER = "Other"

        val ALL_CATEGORIES = listOf(
            CATEGORY_MICROCONTROLLERS,
            CATEGORY_SENSORS,
            CATEGORY_MOTORS,
            CATEGORY_MOTOR_DRIVERS,
            CATEGORY_BATTERIES,
            CATEGORY_POWER_SUPPLIES,
            CATEGORY_TOOLS,
            CATEGORY_3D_PRINTING,
            CATEGORY_COMMUNICATION,
            CATEGORY_DISPLAYS,
            CATEGORY_MECHANICAL,
            CATEGORY_WIRES,
            CATEGORY_OTHER
        )

        // Conditions
        const val CONDITION_NEW = "New"
        const val CONDITION_GOOD = "Good"
        const val CONDITION_FAIR = "Fair"
        const val CONDITION_NEEDS_REPAIR = "Needs Repair"
        const val CONDITION_DAMAGED = "Damaged"

        val ALL_CONDITIONS = listOf(
            CONDITION_NEW,
            CONDITION_GOOD,
            CONDITION_FAIR,
            CONDITION_NEEDS_REPAIR,
            CONDITION_DAMAGED
        )
    }

    val isOutOfStock: Boolean get() = availableQuantity <= 0
    val isLowStock: Boolean get() = availableQuantity > 0 && availableQuantity <= minimumStockLevel
}

@IgnoreExtraProperties
data class EquipmentRequest(
    @DocumentId
    val requestId: String = "",
    val studentUid: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val equipmentId: String = "",
    val equipmentName: String = "",
    val requestedQuantity: Int = 1,
    val actualIssuedQuantity: Int = 0,
    val relatedProjectId: String = "",
    val relatedProjectName: String = "",
    val purpose: String = "",
    val expectedReturnDate: String = "", // Format: "yyyy-MM-dd"
    val status: String = STATUS_PENDING, // Pending, Approved, Rejected, Issued, Returned, Overdue
    val rejectionReason: String = "",
    val requestedAt: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null,
    val approvedByAdminUid: String = "",
    val rejectedAt: Long? = null,
    val issuedAt: Long? = null,
    val issuedByAdminUid: String = "",
    val returnedAt: Long? = null,
    val returnCondition: String = "",
    val returnNotes: String = ""
) {
    companion object {
        const val STATUS_PENDING = "Pending"
        const val STATUS_APPROVED = "Approved"
        const val STATUS_REJECTED = "Rejected"
        const val STATUS_ISSUED = "Issued"
        const val STATUS_RETURNED = "Returned"
        const val STATUS_OVERDUE = "Overdue"

        val ALL_STATUSES = listOf(
            STATUS_PENDING,
            STATUS_APPROVED,
            STATUS_ISSUED,
            STATUS_RETURNED,
            STATUS_OVERDUE,
            STATUS_REJECTED
        )
    }

    val isPending: Boolean get() = status.equals(STATUS_PENDING, ignoreCase = true)
    val isApproved: Boolean get() = status.equals(STATUS_APPROVED, ignoreCase = true)
    val isIssued: Boolean get() = status.equals(STATUS_ISSUED, ignoreCase = true)
    val isReturned: Boolean get() = status.equals(STATUS_RETURNED, ignoreCase = true)
    val isRejected: Boolean get() = status.equals(STATUS_REJECTED, ignoreCase = true)
    val isOverdue: Boolean get() = status.equals(STATUS_OVERDUE, ignoreCase = true)
}

@IgnoreExtraProperties
data class InventoryTransaction(
    @DocumentId
    val transactionId: String = "",
    val equipmentId: String = "",
    val equipmentName: String = "",
    val action: String = "", // "Equipment Added", "Equipment Edited", "Stock Adjusted", "Request Approved", "Request Rejected", "Equipment Issued", "Equipment Returned", "Damaged Equipment", "Missing Equipment"
    val quantityChange: Int = 0,
    val previousQuantity: Int = 0,
    val newQuantity: Int = 0,
    val relatedRequestId: String = "",
    val performedByUid: String = "",
    val performedByName: String = "Admin",
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
