package com.roboticswala.hub.ui.screens.student.notices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.Notice
import com.roboticswala.hub.data.repository.FirestoreNoticeAndEventRepository
import com.roboticswala.hub.data.repository.NoticeAndEventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentNoticesUiState(
    val notices: List<Notice> = emptyList(),
    val filteredNotices: List<Notice> = emptyList(),
    val categoryFilter: String = "All",
    val priorityFilter: String = "All",
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val selectedNoticeForDetails: Notice? = null
)

class StudentNoticesViewModel(
    private val studentUid: String = FirebaseAuth.getInstance().currentUser?.uid ?: "",
    private val repository: NoticeAndEventRepository = FirestoreNoticeAndEventRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentNoticesUiState())
    val uiState: StateFlow<StudentNoticesUiState> = _uiState.asStateFlow()

    init {
        observeNotices()
    }

    private fun observeNotices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.observeActiveNotices(studentUid).collect { list ->
                _uiState.update { state ->
                    state.copy(
                        notices = list,
                        filteredNotices = filterList(list, state.categoryFilter, state.priorityFilter, state.searchQuery),
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
                filteredNotices = filterList(state.notices, category, state.priorityFilter, state.searchQuery)
            )
        }
    }

    fun setPriorityFilter(priority: String) {
        _uiState.update { state ->
            state.copy(
                priorityFilter = priority,
                filteredNotices = filterList(state.notices, state.categoryFilter, priority, state.searchQuery)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredNotices = filterList(state.notices, state.categoryFilter, state.priorityFilter, query)
            )
        }
    }

    private fun filterList(
        list: List<Notice>,
        category: String,
        priority: String,
        query: String
    ): List<Notice> {
        return list.filter { item ->
            val matchesCat = category == "All" || item.category.equals(category, ignoreCase = true)
            val matchesPrio = priority == "All" || item.priority.equals(priority, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true)

            matchesCat && matchesPrio && matchesQuery
        }
    }

    fun openDetails(notice: Notice) = _uiState.update { it.copy(selectedNoticeForDetails = notice) }
    fun closeDetails() = _uiState.update { it.copy(selectedNoticeForDetails = null) }
}

class StudentNoticesViewModelFactory(
    private val studentUid: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentNoticesViewModel::class.java)) {
            return StudentNoticesViewModel(studentUid = studentUid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
