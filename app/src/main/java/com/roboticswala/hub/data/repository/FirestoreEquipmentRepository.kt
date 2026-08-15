package com.roboticswala.hub.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.roboticswala.hub.data.models.Equipment
import com.roboticswala.hub.data.models.EquipmentRequest
import com.roboticswala.hub.data.models.InventoryTransaction
import com.roboticswala.hub.utils.BookingTimeUtils
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirestoreEquipmentRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : EquipmentRepository {

    private val equipmentCollection = firestore.collection("equipment")
    private val requestsCollection = firestore.collection("equipmentRequests")
    private val transactionsCollection = firestore.collection("inventoryTransactions")

    // ─────────────────────────────────────────────────────────────────────────
    // 1. Equipment Catalog Management
    // ─────────────────────────────────────────────────────────────────────────

    override fun addEquipment(
        equipment: Equipment,
        imageBytes: ByteArray?
    ): Flow<Resource<Equipment>> = flow {
        emit(Resource.Loading())
        try {
            if (equipment.name.isBlank()) {
                emit(Resource.Error("Equipment name is required."))
                return@flow
            }
            if (equipment.availableQuantity > equipment.totalQuantity) {
                emit(Resource.Error("Available quantity cannot exceed total quantity."))
                return@flow
            }
            if (equipment.totalQuantity < 0 || equipment.availableQuantity < 0) {
                emit(Resource.Error("Quantities cannot be negative."))
                return@flow
            }

            val docRef = equipmentCollection.document()
            var finalImageUrl = equipment.imageUrl

            if (imageBytes != null && imageBytes.isNotEmpty()) {
                val imgRef = storage.reference.child("equipment/${docRef.id}/img_${System.currentTimeMillis()}.jpg")
                imgRef.putBytes(imageBytes).await()
                finalImageUrl = imgRef.downloadUrl.await().toString()
            }

            val finalEquipment = equipment.copy(
                equipmentId = docRef.id,
                imageUrl = finalImageUrl,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            docRef.set(finalEquipment).await()

            // Record audit transaction
            val txDoc = transactionsCollection.document()
            val tx = InventoryTransaction(
                transactionId = txDoc.id,
                equipmentId = finalEquipment.equipmentId,
                equipmentName = finalEquipment.name,
                action = "Equipment Added",
                quantityChange = finalEquipment.totalQuantity,
                previousQuantity = 0,
                newQuantity = finalEquipment.totalQuantity,
                performedByUid = finalEquipment.createdByAdminUid,
                notes = "Initial stock added: ${finalEquipment.totalQuantity} units at ${finalEquipment.storageLocation}"
            )
            txDoc.set(tx).await()

            emit(Resource.Success(finalEquipment))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to add equipment."))
        }
    }

    override fun updateEquipment(
        equipment: Equipment,
        imageBytes: ByteArray?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            if (equipment.availableQuantity > equipment.totalQuantity) {
                emit(Resource.Error("Available quantity cannot exceed total quantity."))
                return@flow
            }

            var finalImageUrl = equipment.imageUrl
            if (imageBytes != null && imageBytes.isNotEmpty()) {
                val imgRef = storage.reference.child("equipment/${equipment.equipmentId}/img_${System.currentTimeMillis()}.jpg")
                imgRef.putBytes(imageBytes).await()
                finalImageUrl = imgRef.downloadUrl.await().toString()
            }

            val updated = equipment.copy(
                imageUrl = finalImageUrl,
                updatedAt = System.currentTimeMillis()
            )

            equipmentCollection.document(equipment.equipmentId).set(updated).await()

            // Record audit transaction
            val txDoc = transactionsCollection.document()
            val tx = InventoryTransaction(
                transactionId = txDoc.id,
                equipmentId = updated.equipmentId,
                equipmentName = updated.name,
                action = "Equipment Edited",
                newQuantity = updated.totalQuantity,
                performedByUid = updated.createdByAdminUid,
                notes = "Updated details, stock: ${updated.availableQuantity}/${updated.totalQuantity}"
            )
            txDoc.set(tx).await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to update equipment."))
        }
    }

    override fun deleteEquipment(equipmentId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            equipmentCollection.document(equipmentId).delete().await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to delete equipment."))
        }
    }

    override fun observeAllEquipment(): Flow<List<Equipment>> = callbackFlow {
        val listener = equipmentCollection
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(Equipment::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override fun observeLowStockEquipment(): Flow<List<Equipment>> = callbackFlow {
        val listener = equipmentCollection
            .orderBy("availableQuantity", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(Equipment::class.java) } ?: emptyList()
                val lowStock = list.filter { it.isLowStock || it.isOutOfStock }
                trySend(lowStock)
            }

        awaitClose { listener.remove() }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. Student Equipment Requests
    // ─────────────────────────────────────────────────────────────────────────

    override fun createEquipmentRequest(request: EquipmentRequest): Flow<Resource<EquipmentRequest>> = flow {
        emit(Resource.Loading())
        try {
            if (request.requestedQuantity <= 0) {
                emit(Resource.Error("Requested quantity must be at least 1."))
                return@flow
            }
            if (request.expectedReturnDate.isBlank()) {
                emit(Resource.Error("Expected return date is required."))
                return@flow
            }

            // Check live stock
            val eqDoc = equipmentCollection.document(request.equipmentId).get().await()
            val equipment = eqDoc.toObject(Equipment::class.java)
            if (equipment == null) {
                emit(Resource.Error("Equipment not found."))
                return@flow
            }
            if (request.requestedQuantity > equipment.availableQuantity) {
                emit(Resource.Error("Requested quantity (${request.requestedQuantity}) exceeds available stock (${equipment.availableQuantity})."))
                return@flow
            }

            val docRef = requestsCollection.document()
            val finalRequest = request.copy(
                requestId = docRef.id,
                equipmentName = equipment.name,
                status = EquipmentRequest.STATUS_PENDING,
                requestedAt = System.currentTimeMillis()
            )

            docRef.set(finalRequest).await()
            emit(Resource.Success(finalRequest))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to submit request."))
        }
    }

    override fun cancelEquipmentRequest(requestId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val doc = requestsCollection.document(requestId).get().await()
            val req = doc.toObject(EquipmentRequest::class.java)
            if (req == null || !req.isPending) {
                emit(Resource.Error("Only pending requests can be cancelled."))
                return@flow
            }
            requestsCollection.document(requestId).delete().await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to cancel request."))
        }
    }

    override fun observeStudentRequests(studentUid: String): Flow<List<EquipmentRequest>> = callbackFlow {
        val today = BookingTimeUtils.getTodayDateString()
        val listener = requestsCollection
            .whereEqualTo("studentUid", studentUid)
            .orderBy("requestedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    val r = doc.toObject(EquipmentRequest::class.java) ?: return@mapNotNull null
                    // Check for overdue dynamically
                    if (r.status == EquipmentRequest.STATUS_ISSUED && r.expectedReturnDate.isNotBlank() && r.expectedReturnDate < today) {
                        r.copy(status = EquipmentRequest.STATUS_OVERDUE)
                    } else {
                        r
                    }
                } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. Admin Request Management & Safe Atomic Transactions
    // ─────────────────────────────────────────────────────────────────────────

    override fun observeAllRequests(): Flow<List<EquipmentRequest>> = callbackFlow {
        val today = BookingTimeUtils.getTodayDateString()
        val listener = requestsCollection
            .orderBy("requestedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    val r = doc.toObject(EquipmentRequest::class.java) ?: return@mapNotNull null
                    if (r.status == EquipmentRequest.STATUS_ISSUED && r.expectedReturnDate.isNotBlank() && r.expectedReturnDate < today) {
                        r.copy(status = EquipmentRequest.STATUS_OVERDUE)
                    } else {
                        r
                    }
                } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override fun approveRequest(requestId: String, adminUid: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            requestsCollection.document(requestId).update(
                mapOf(
                    "status" to EquipmentRequest.STATUS_APPROVED,
                    "approvedAt" to System.currentTimeMillis(),
                    "approvedByAdminUid" to adminUid
                )
            ).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to approve request."))
        }
    }

    override fun rejectRequest(
        requestId: String,
        rejectionReason: String,
        adminUid: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            requestsCollection.document(requestId).update(
                mapOf(
                    "status" to EquipmentRequest.STATUS_REJECTED,
                    "rejectionReason" to rejectionReason.trim(),
                    "rejectedAt" to System.currentTimeMillis(),
                    "approvedByAdminUid" to adminUid
                )
            ).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to reject request."))
        }
    }

    override fun issueEquipment(
        requestId: String,
        actualQuantity: Int,
        adminUid: String,
        adminName: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            firestore.runTransaction { transaction ->
                val reqRef = requestsCollection.document(requestId)
                val reqSnapshot = transaction.get(reqRef)
                val req = reqSnapshot.toObject(EquipmentRequest::class.java)
                    ?: throw IllegalStateException("Request not found.")

                val eqRef = equipmentCollection.document(req.equipmentId)
                val eqSnapshot = transaction.get(eqRef)
                val eq = eqSnapshot.toObject(Equipment::class.java)
                    ?: throw IllegalStateException("Equipment not found.")

                val qtyToIssue = if (actualQuantity > 0) actualQuantity else req.requestedQuantity
                if (eq.availableQuantity < qtyToIssue) {
                    throw IllegalStateException("Insufficient stock! Available: ${eq.availableQuantity}, Trying to issue: $qtyToIssue")
                }

                val prevAvail = eq.availableQuantity
                val newAvail = prevAvail - qtyToIssue
                val newIssued = eq.issuedQuantity + qtyToIssue

                // 1. Update Equipment Quantities
                transaction.update(
                    eqRef,
                    mapOf(
                        "availableQuantity" to newAvail,
                        "issuedQuantity" to newIssued,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )

                // 2. Update Request Status
                transaction.update(
                    reqRef,
                    mapOf(
                        "status" to EquipmentRequest.STATUS_ISSUED,
                        "actualIssuedQuantity" to qtyToIssue,
                        "issuedAt" to System.currentTimeMillis(),
                        "issuedByAdminUid" to adminUid
                    )
                )

                // 3. Create Audit Transaction
                val txRef = transactionsCollection.document()
                val tx = InventoryTransaction(
                    transactionId = txRef.id,
                    equipmentId = eq.equipmentId,
                    equipmentName = eq.name,
                    action = "Equipment Issued",
                    quantityChange = -qtyToIssue,
                    previousQuantity = prevAvail,
                    newQuantity = newAvail,
                    relatedRequestId = requestId,
                    performedByUid = adminUid,
                    performedByName = adminName,
                    notes = "Issued $qtyToIssue units to ${req.studentName} (${req.studentId}) for ${req.purpose}"
                )
                transaction.set(txRef, tx)
            }.await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to issue equipment."))
        }
    }

    override fun returnEquipment(
        requestId: String,
        returnCondition: String,
        returnNotes: String,
        isStockRestored: Boolean,
        adminUid: String,
        adminName: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            firestore.runTransaction { transaction ->
                val reqRef = requestsCollection.document(requestId)
                val reqSnapshot = transaction.get(reqRef)
                val req = reqSnapshot.toObject(EquipmentRequest::class.java)
                    ?: throw IllegalStateException("Request not found.")

                val eqRef = equipmentCollection.document(req.equipmentId)
                val eqSnapshot = transaction.get(eqRef)
                val eq = eqSnapshot.toObject(Equipment::class.java)
                    ?: throw IllegalStateException("Equipment not found.")

                val qtyReturned = if (req.actualIssuedQuantity > 0) req.actualIssuedQuantity else req.requestedQuantity
                val prevAvail = eq.availableQuantity
                val newAvail = if (isStockRestored) prevAvail + qtyReturned else prevAvail
                val newIssued = (eq.issuedQuantity - qtyReturned).coerceAtLeast(0)
                val newTotal = if (!isStockRestored) (eq.totalQuantity - qtyReturned).coerceAtLeast(0) else eq.totalQuantity

                // 1. Update Equipment
                transaction.update(
                    eqRef,
                    mapOf(
                        "availableQuantity" to newAvail,
                        "issuedQuantity" to newIssued,
                        "totalQuantity" to newTotal,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )

                // 2. Update Request Status
                transaction.update(
                    reqRef,
                    mapOf(
                        "status" to EquipmentRequest.STATUS_RETURNED,
                        "returnedAt" to System.currentTimeMillis(),
                        "returnCondition" to returnCondition,
                        "returnNotes" to returnNotes.trim()
                    )
                )

                // 3. Create Audit Transaction
                val txRef = transactionsCollection.document()
                val actionName = if (isStockRestored) "Equipment Returned" else "Damaged / Missing Equipment"
                val tx = InventoryTransaction(
                    transactionId = txRef.id,
                    equipmentId = eq.equipmentId,
                    equipmentName = eq.name,
                    action = actionName,
                    quantityChange = if (isStockRestored) qtyReturned else 0,
                    previousQuantity = prevAvail,
                    newQuantity = newAvail,
                    relatedRequestId = requestId,
                    performedByUid = adminUid,
                    performedByName = adminName,
                    notes = "Returned in condition: $returnCondition. Notes: $returnNotes"
                )
                transaction.set(txRef, tx)
            }.await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to return equipment."))
        }
    }

    override fun observeInventoryTransactions(): Flow<List<InventoryTransaction>> = callbackFlow {
        val listener = transactionsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(InventoryTransaction::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }
}
