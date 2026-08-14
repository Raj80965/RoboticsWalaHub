package com.roboticswala.hub.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roboticswala.hub.data.repository.AuthRepository
import com.roboticswala.hub.data.repository.FirebaseAuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed interface SplashNavigationEvent {
    object NavigateToLogin : SplashNavigationEvent
    object NavigateToStudent : SplashNavigationEvent
    object NavigateToAdmin : SplashNavigationEvent
    object NavigateToPending : SplashNavigationEvent
}

class SplashViewModel(
    private val authRepository: AuthRepository = FirebaseAuthRepository()
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<SplashNavigationEvent>()
    val navigationEvent: SharedFlow<SplashNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        checkSessionAndNavigate()
    }

    private fun checkSessionAndNavigate() {
        viewModelScope.launch {
            // Keep the animated splash screen visible for ~2 seconds
            delay(2000)

            if (authRepository.isUserLoggedIn) {
                authRepository.getCurrentUserProfile().onSuccess { profile ->
                    if (profile != null) {
                        when {
                            profile.isAdmin -> _navigationEvent.emit(SplashNavigationEvent.NavigateToAdmin)
                            profile.isStudent && profile.isApproved -> _navigationEvent.emit(SplashNavigationEvent.NavigateToStudent)
                            profile.isPending -> _navigationEvent.emit(SplashNavigationEvent.NavigateToPending)
                            else -> _navigationEvent.emit(SplashNavigationEvent.NavigateToPending)
                        }
                        return@launch
                    }
                }
            }

            _navigationEvent.emit(SplashNavigationEvent.NavigateToLogin)
        }
    }

    fun onSkipClicked() {
        viewModelScope.launch {
            if (authRepository.isUserLoggedIn) {
                authRepository.getCurrentUserProfile().onSuccess { profile ->
                    if (profile != null) {
                        when {
                            profile.isAdmin -> _navigationEvent.emit(SplashNavigationEvent.NavigateToAdmin)
                            profile.isStudent && profile.isApproved -> _navigationEvent.emit(SplashNavigationEvent.NavigateToStudent)
                            profile.isPending -> _navigationEvent.emit(SplashNavigationEvent.NavigateToPending)
                            else -> _navigationEvent.emit(SplashNavigationEvent.NavigateToPending)
                        }
                        return@launch
                    }
                }
            }
            _navigationEvent.emit(SplashNavigationEvent.NavigateToLogin)
        }
    }
}
