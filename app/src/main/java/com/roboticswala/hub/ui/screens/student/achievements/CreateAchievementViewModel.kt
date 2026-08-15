package com.roboticswala.hub.ui.screens.student.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.Achievement
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.data.repository.AchievementRepository
import com.roboticswala.hub.data.repository.FirestoreAchievementRepository
import com.roboticswala.hub.utils.BookingTimeUtils
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateAchievementUiState(
    val title: String = "",
    val category: String = Achievement.CATEGORY_COMPETITION,
    val description: String = "",
    val achievementDate: String = BookingTimeUtils.getTodayDateString(),
    val organizationName: String = "",
    val achievementLevel: String = Achievement.LEVEL_COLLEGE,
    val verificationLink: String = "",
    val certificateFileName: String = "",
    val certificateBytes: ByteArray? = null,
    val isUploading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class CreateAchievementViewModel(
    private val repository: AchievementRepository = FirestoreAchievementRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAchievementUiState())
    val uiState: StateFlow<CreateAchievementUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) = _uiState.update { it.copy(title = title, errorMessage = null) }
    fun onCategoryChange(category: String) = _uiState.update { it.copy(category = category) }
    fun onDescriptionChange(desc: String) = _uiState.update { it.copy(description = desc) }
    fun onDateChange(date: String) = _uiState.update { it.copy(achievementDate = date, errorMessage = null) }
    fun onOrganizationChange(org: String) = _uiState.update { it.copy(organizationName = org, errorMessage = null) }
    fun onLevelChange(level: String) = _uiState.update { it.copy(achievementLevel = level) }
    fun onLinkChange(link: String) = _uiState.update { it.copy(verificationLink = link, errorMessage = null) }

    fun setCertificateFile(fileName: String, bytes: ByteArray) {
        _uiState.update {
            it.copy(
                certificateFileName = fileName,
                certificateBytes = bytes,
                errorMessage = null
            )
        }
    }

    fun submitAchievement(userProfile: UserProfile, existingAchievement: Achievement? = null) {
        val state = _uiState.value

        if (state.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Achievement title is required.") }
            return
        }
        if (state.organizationName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Organization or platform name is required.") }
            return
        }
        if (BookingTimeUtils.isFutureDate(state.achievementDate)) {
            _uiState.update { it.copy(errorMessage = "Achievement date cannot be in the future.") }
            return
        }
        if (state.verificationLink.isNotBlank() &&
            !state.verificationLink.startsWith("http://") &&
            !state.verificationLink.startsWith("https://")
        ) {
            _uiState.update { it.copy(errorMessage = "Verification link must start with http:// or https://") }
            return
        }

        val studentUid = auth.currentUser?.uid ?: userProfile.uid
        val base = existingAchievement ?: Achievement()
        val achievement = base.copy(
            studentUid = studentUid,
            studentId = userProfile.studentId.ifBlank { "STU-${studentUid.take(4)}" },
            studentName = userProfile.fullName.ifBlank { "Student" },
            studentProfilePhotoUrl = userProfile.photoUrl,
            title = state.title.trim(),
            category = state.category,
            description = state.description.trim(),
            achievementDate = state.achievementDate,
            organizationName = state.organizationName.trim(),
            achievementLevel = state.achievementLevel,
            verificationLink = state.verificationLink.trim(),
            status = Achievement.STATUS_PENDING
        )

        viewModelScope.launch {
            if (existingAchievement != null) {
                repository.updateAchievement(
                    achievement = achievement,
                    certificateBytes = state.certificateBytes,
                    certificateFileName = state.certificateFileName.ifBlank { null }
                ).collect { res ->
                    handleResource(res)
                }
            } else {
                repository.submitAchievement(
                    achievement = achievement,
                    certificateBytes = state.certificateBytes,
                    certificateFileName = state.certificateFileName.ifBlank { null }
                ).collect { res ->
                    handleResource(res)
                }
            }
        }
    }

    private fun handleResource(res: Resource<*>) {
        when (res) {
            is Resource.Loading -> _uiState.update { it.copy(isUploading = true, errorMessage = null) }
            is Resource.Success -> _uiState.update { it.copy(isUploading = false, isSuccess = true) }
            is Resource.Error -> _uiState.update { it.copy(isUploading = false, errorMessage = res.message ?: "Submission failed.") }
        }
    }
}
