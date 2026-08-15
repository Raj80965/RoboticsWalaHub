package com.roboticswala.hub.ui.screens.admin.notices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.Notice
import com.roboticswala.hub.data.repository.FirestoreNoticeAndEventRepository
import com.roboticswala.hub.data.repository.NoticeAndEventRepository
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminNoticesUiState(
    val notices: List<Notice> = emptyList(),
    val filteredNotices: List<Notice> = emptyList(),
    val categoryFilter: String = "All",
    val statusFilter: String = "All",
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val showCreateDialog: Boolean = false,
    val noticeToEdit: Notice? = null,
    val noticeToDelete: Notice? = null,
    val isActionLoading: Boolean = false,
    val snackbarMessage: String? = null,
    val errorMessage: String? = null
)

class AdminNoticesViewModel(
    private val repository: NoticeAndEventRepository = FirestoreNoticeAndEventRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminNoticesUiState())
    val uiState: StateFlow<AdminNoticesUiState> = _uiState.asStateFlow()

    init {
        observeAllNotices()
    }

    private fun observeAllNotices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.observeAllAdminNotices().collect { list ->
                _uiState.update { state ->
                    state.copy(
                        notices = list,
                        filteredNotices = filterList(list, state.categoryFilter, state.statusFilter, state.searchQuery),
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
                filteredNotices = filterList(state.notices, category, state.statusFilter, state.searchQuery)
            )
        }
    }

    fun setStatusFilter(status: String) {
        _uiState.update { state ->
            state.copy(
                statusFilter = status,
                filteredNotices = filterList(state.notices, state.categoryFilter, status, state.searchQuery)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredNotices = filterList(state.notices, state.categoryFilter, state.statusFilter, query)
            )
        }
    }

    private fun filterList(
        list: List<Notice>,
        category: String,
        status: String,
        query: String
    ): List<Notice> {
        return list.filter { item ->
            val matchesCat = category == "All" || item.category.equals(category, ignoreCase = true)
            val matchesStat = status == "All" || item.status.equals(status, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true)

            matchesCat && matchesStat && matchesQuery
        }
    }

    fun openCreateDialog() = _uiState.update { it.copy(showCreateDialog = true, noticeToEdit = null, errorMessage = null) }
    fun openEditDialog(notice: Notice) = _uiState.update { it.copy(showCreateDialog = true, noticeToEdit = notice, errorMessage = null) }
    fun closeCreateDialog() = _uiState.update { it.copy(showCreateDialog = false, noticeToEdit = null, errorMessage = null) }

    fun promptDelete(notice: Notice) = _uiState.update { it.copy(noticeToDelete = notice) }
    fun dismissDelete() = _uiState.update { it.copy(noticeToDelete = null) }

    fun saveNotice(
        title: String,
        description: String,
        category: String,
        priority: String,
        targetAudience: String,
        publishDate: String,
        expiryDate: String,
        isPublished: Boolean,
        attachmentName: String?
    ) {
        val adminUid = auth.currentUser?.uid ?: "Admin"
        val existing = _uiState.value.noticeToEdit

        val notice = (existing ?: Notice()).copy(
            title = title.trim(),
            description = description.trim(),
            category = category,
            priority = priority,
            targetAudience = targetAudience,
            publishDate = publishDate,
            expiryDate = expiryDate,
            status = if (isPublished) Notice.STATUS_PUBLISHED else Notice.STATUS_DRAFT,
            attachmentFileName = attachmentName ?: (existing?.attachmentFileName ?: ""),
            createdByAdminUid = adminUid
        )

        viewModelScope.launch {
            if (existing != null) {
                repository.updateNotice(notice).collect { res ->
                    handleResource(res, "Notice updated successfully!")
                }
            } else {
                repository.createNotice(notice).collect { res ->
                    handleResource(res, "Notice published successfully!")
                }
            }
        }
    }

    fun confirmDelete() {
        val item = _uiState.value.noticeToDelete ?: return
        viewModelScope.launch {
            repository.deleteNotice(item.noticeId).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                noticeToDelete = null,
                                snackbarMessage = "Notice deleted."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                noticeToDelete = null,
                                snackbarMessage = res.message ?: "Failed to delete notice."
                            )
                        }
                    }
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
                        showCreateDialog = false,
                        noticeToEdit = null,
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
