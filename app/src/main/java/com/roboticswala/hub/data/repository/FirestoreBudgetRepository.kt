package com.roboticswala.hub.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.roboticswala.hub.data.models.BudgetAuditLog
import com.roboticswala.hub.data.models.Project
import com.roboticswala.hub.data.models.ProjectExpense
import com.roboticswala.hub.utils.BookingTimeUtils
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirestoreBudgetRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : BudgetRepository {

    private val expensesCollection = firestore.collection("expenses")
    private val auditCollection = firestore.collection("budgetAuditLogs")
    private val projectsCollection = firestore.collection("projects")

    // ─────────────────────────────────────────────────────────────────────────
    // 1. Student Expense Submission & Management
    // ─────────────────────────────────────────────────────────────────────────

    override fun submitExpense(
        expense: ProjectExpense,
        receiptBytes: ByteArray?,
        receiptFileName: String?
    ): Flow<Resource<ProjectExpense>> = flow {
        emit(Resource.Loading())
        try {
            if (expense.projectId.isBlank()) {
                emit(Resource.Error("Please select a related project."))
                return@flow
            }
            if (expense.title.isBlank()) {
                emit(Resource.Error("Expense title is required."))
                return@flow
            }
            if (expense.amount <= 0.0) {
                emit(Resource.Error("Expense amount must be greater than ₹0."))
                return@flow
            }
            val today = BookingTimeUtils.getTodayDateString()
            if (expense.purchaseDate.isNotBlank() && expense.purchaseDate > today) {
                emit(Resource.Error("Purchase date cannot be in the future."))
                return@flow
            }

            val docRef = expensesCollection.document()
            var finalReceiptUrl = expense.receiptUrl
            var finalReceiptName = expense.receiptFileName

            if (receiptBytes != null && receiptBytes.isNotEmpty()) {
                val fName = receiptFileName ?: "receipt_${System.currentTimeMillis()}.pdf"
                val fileRef = storage.reference.child("expenses/${docRef.id}/receipts/${System.currentTimeMillis()}_$fName")
                fileRef.putBytes(receiptBytes).await()
                finalReceiptUrl = fileRef.downloadUrl.await().toString()
                finalReceiptName = fName
            }

            val finalExpense = expense.copy(
                expenseId = docRef.id,
                receiptUrl = finalReceiptUrl,
                receiptFileName = finalReceiptName,
                status = ProjectExpense.STATUS_PENDING,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            // 1. Save expense document
            docRef.set(finalExpense).await()

            // 2. Update project pending expenses
            projectsCollection.document(expense.projectId).update(
                mapOf(
                    "totalPendingExpenses" to FieldValue.increment(expense.amount),
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()

            // 3. Record Audit log
            val auditRef = auditCollection.document()
            val audit = BudgetAuditLog(
                auditId = auditRef.id,
                projectId = finalExpense.projectId,
                projectName = finalExpense.projectName,
                expenseId = finalExpense.expenseId,
                action = "Expense Submitted",
                newValue = finalExpense.amount,
                performedByUid = finalExpense.submittedByUid,
                performedByName = finalExpense.submittedByName,
                notes = "Submitted ₹${finalExpense.amount} for \"${finalExpense.title}\" (${finalExpense.category})"
            )
            auditRef.set(audit).await()

            emit(Resource.Success(finalExpense))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to submit expense."))
        }
    }

    override fun updatePendingExpense(
        expense: ProjectExpense,
        receiptBytes: ByteArray?,
        receiptFileName: String?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val doc = expensesCollection.document(expense.expenseId).get().await()
            val existing = doc.toObject(ProjectExpense::class.java)
            if (existing == null || !existing.isPending) {
                emit(Resource.Error("Only pending expenses can be edited."))
                return@flow
            }

            var finalReceiptUrl = existing.receiptUrl
            var finalReceiptName = existing.receiptFileName

            if (receiptBytes != null && receiptBytes.isNotEmpty()) {
                val fName = receiptFileName ?: "receipt_${System.currentTimeMillis()}.pdf"
                val fileRef = storage.reference.child("expenses/${expense.expenseId}/receipts/${System.currentTimeMillis()}_$fName")
                fileRef.putBytes(receiptBytes).await()
                finalReceiptUrl = fileRef.downloadUrl.await().toString()
                finalReceiptName = fName
            }

            val diff = expense.amount - existing.amount
            val updated = expense.copy(
                receiptUrl = finalReceiptUrl,
                receiptFileName = finalReceiptName,
                updatedAt = System.currentTimeMillis()
            )

            expensesCollection.document(expense.expenseId).set(updated).await()

            if (diff != 0.0) {
                projectsCollection.document(expense.projectId).update(
                    "totalPendingExpenses", FieldValue.increment(diff)
                ).await()
            }

            // Audit
            val auditRef = auditCollection.document()
            val audit = BudgetAuditLog(
                auditId = auditRef.id,
                projectId = updated.projectId,
                projectName = updated.projectName,
                expenseId = updated.expenseId,
                action = "Expense Edited",
                previousValue = existing.amount,
                newValue = updated.amount,
                performedByUid = updated.submittedByUid,
                performedByName = updated.submittedByName,
                notes = "Edited expense \"${updated.title}\""
            )
            auditRef.set(audit).await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to update expense."))
        }
    }

    override fun deletePendingExpense(
        expenseId: String,
        projectId: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val doc = expensesCollection.document(expenseId).get().await()
            val existing = doc.toObject(ProjectExpense::class.java)
            if (existing == null || !existing.isPending) {
                emit(Resource.Error("Only pending expenses can be deleted."))
                return@flow
            }

            expensesCollection.document(expenseId).delete().await()

            // Update project pending
            projectsCollection.document(projectId).update(
                "totalPendingExpenses", FieldValue.increment(-existing.amount)
            ).await()

            // Audit
            val auditRef = auditCollection.document()
            val audit = BudgetAuditLog(
                auditId = auditRef.id,
                projectId = projectId,
                projectName = existing.projectName,
                expenseId = expenseId,
                action = "Expense Deleted",
                previousValue = existing.amount,
                newValue = 0.0,
                performedByUid = existing.submittedByUid,
                performedByName = existing.submittedByName,
                notes = "Deleted pending expense \"${existing.title}\""
            )
            auditRef.set(audit).await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to delete expense."))
        }
    }

    override fun observeStudentExpenses(studentUid: String): Flow<List<ProjectExpense>> = callbackFlow {
        val listener = expensesCollection
            .whereEqualTo("submittedByUid", studentUid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(ProjectExpense::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override fun observeProjectExpenses(projectId: String): Flow<List<ProjectExpense>> = callbackFlow {
        val listener = expensesCollection
            .whereEqualTo("projectId", projectId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(ProjectExpense::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. Admin Budget Controls & Approvals
    // ─────────────────────────────────────────────────────────────────────────

    override fun setProjectApprovedBudget(
        projectId: String,
        newBudget: Double,
        adminUid: String,
        adminName: String,
        reason: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            if (newBudget < 0.0) {
                emit(Resource.Error("Approved budget cannot be negative."))
                return@flow
            }

            val pDoc = projectsCollection.document(projectId).get().await()
            val project = pDoc.toObject(Project::class.java)
            if (project == null) {
                emit(Resource.Error("Project not found."))
                return@flow
            }

            val prevBudget = project.approvedBudget
            projectsCollection.document(projectId).update(
                mapOf(
                    "approvedBudget" to newBudget,
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()

            // Audit
            val auditRef = auditCollection.document()
            val audit = BudgetAuditLog(
                auditId = auditRef.id,
                projectId = projectId,
                projectName = project.title,
                action = if (prevBudget > 0) "Budget Updated" else "Budget Created",
                previousValue = prevBudget,
                newValue = newBudget,
                performedByUid = adminUid,
                performedByName = adminName,
                notes = if (reason.isNotBlank()) reason else "Admin set approved budget to ₹$newBudget"
            )
            auditRef.set(audit).await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to set approved budget."))
        }
    }

    override fun approveExpense(
        expenseId: String,
        adminUid: String,
        adminName: String,
        allowOverBudget: Boolean,
        overBudgetReason: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val expDoc = expensesCollection.document(expenseId).get().await()
            val expense = expDoc.toObject(ProjectExpense::class.java)
            if (expense == null) {
                emit(Resource.Error("Expense record not found."))
                return@flow
            }

            val pDoc = projectsCollection.document(expense.projectId).get().await()
            val project = pDoc.toObject(Project::class.java)

            val effectiveBudget = if ((project?.approvedBudget ?: 0.0) > 0.0) project!!.approvedBudget else (project?.estimatedBudget ?: 0.0)
            val currentApproved = project?.totalApprovedExpenses ?: 0.0
            val newTotalApproved = currentApproved + expense.amount

            if (effectiveBudget > 0.0 && newTotalApproved > effectiveBudget && !allowOverBudget) {
                val remaining = (effectiveBudget - currentApproved).coerceAtLeast(0.0)
                emit(Resource.Error("OVER_BUDGET: Expense of ₹${expense.amount} exceeds remaining project budget (₹${remaining})."))
                return@flow
            }

            // Update Expense
            expensesCollection.document(expenseId).update(
                mapOf(
                    "status" to ProjectExpense.STATUS_APPROVED,
                    "approvedAt" to System.currentTimeMillis(),
                    "approvedByAdminUid" to adminUid,
                    "approvedByAdminName" to adminName,
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()

            // Update Project totals
            projectsCollection.document(expense.projectId).update(
                mapOf(
                    "totalApprovedExpenses" to FieldValue.increment(expense.amount),
                    "totalPendingExpenses" to FieldValue.increment(-expense.amount),
                    "actualExpense" to FieldValue.increment(expense.amount),
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()

            // Record Audit log
            val auditRef = auditCollection.document()
            val audit = BudgetAuditLog(
                auditId = auditRef.id,
                projectId = expense.projectId,
                projectName = expense.projectName,
                expenseId = expenseId,
                action = "Expense Approved",
                newValue = expense.amount,
                performedByUid = adminUid,
                performedByName = adminName,
                notes = if (allowOverBudget && overBudgetReason.isNotBlank()) {
                    "Approved over-budget exception: $overBudgetReason"
                } else {
                    "Approved expense ₹${expense.amount} for ${expense.title}"
                }
            )
            auditRef.set(audit).await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to approve expense."))
        }
    }

    override fun rejectExpense(
        expenseId: String,
        reason: String,
        adminUid: String,
        adminName: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val expDoc = expensesCollection.document(expenseId).get().await()
            val expense = expDoc.toObject(ProjectExpense::class.java)
            if (expense == null) {
                emit(Resource.Error("Expense record not found."))
                return@flow
            }

            expensesCollection.document(expenseId).update(
                mapOf(
                    "status" to ProjectExpense.STATUS_REJECTED,
                    "rejectionReason" to reason.trim(),
                    "rejectedAt" to System.currentTimeMillis(),
                    "rejectedByAdminUid" to adminUid,
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()

            projectsCollection.document(expense.projectId).update(
                mapOf(
                    "totalRejectedExpenses" to FieldValue.increment(expense.amount),
                    "totalPendingExpenses" to FieldValue.increment(-expense.amount),
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()

            val auditRef = auditCollection.document()
            val audit = BudgetAuditLog(
                auditId = auditRef.id,
                projectId = expense.projectId,
                projectName = expense.projectName,
                expenseId = expenseId,
                action = "Expense Rejected",
                newValue = expense.amount,
                performedByUid = adminUid,
                performedByName = adminName,
                notes = "Rejected expense ₹${expense.amount}. Reason: $reason"
            )
            auditRef.set(audit).await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to reject expense."))
        }
    }

    override fun observeAllExpenses(): Flow<List<ProjectExpense>> = callbackFlow {
        val listener = expensesCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(ProjectExpense::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override fun observeBudgetAuditLogs(): Flow<List<BudgetAuditLog>> = callbackFlow {
        val listener = auditCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(BudgetAuditLog::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override fun observeAllProjectsWithBudget(): Flow<List<Project>> = callbackFlow {
        val listener = projectsCollection
            .orderBy("title", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(Project::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }
}
