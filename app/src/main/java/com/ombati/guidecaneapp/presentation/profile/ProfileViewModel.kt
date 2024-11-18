package com.ombati.guidecaneapp.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ombati.guidecaneapp.common.Result
import com.ombati.guidecaneapp.data.repositories.storage.GuideCaneRepository
import com.ombati.guidecaneapp.presentation.side_effects.GuideCaneSideEffects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val guideCaneRepository: GuideCaneRepository
) : ViewModel() {

    private val _state: MutableStateFlow<ProfileScreenUiState> = MutableStateFlow(ProfileScreenUiState())
    val state: StateFlow<ProfileScreenUiState> = _state.asStateFlow()

    private val _effect: Channel<GuideCaneSideEffects> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        observeGuideCaneUsers()
    }

    private fun observeGuideCaneUsers() {
        val currentAppUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        if (currentAppUserId.isEmpty()) {
            setEffect { GuideCaneSideEffects.ShowSnackBarMessage("User is not authenticated") }
            return
        }


        guideCaneRepository.fetchGuideCaneUsersForCurrentUser(currentAppUserId) { result ->
            when (result) {
                is Result.Failure -> {
                    setEffect { GuideCaneSideEffects.ShowSnackBarMessage("Error fetching guide cane users") }
                }
                is Result.Success -> {
                    Log.d("ProfileViewModel", "Fetched guideCaneUsers: ${result.data}")
                    setState(_state.value.copy(guideCaneUsers = result.data, isLoading = false))
                }
            }
        }
    }

    fun sendEvent(event: ProfileScreenUiEvent) {
        reduce(oldState = _state.value, event = event)
    }

    private fun setEffect(builder: () -> GuideCaneSideEffects) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    private fun setState(newState: ProfileScreenUiState) {
        _state.value = newState
    }

    private fun reduce(oldState: ProfileScreenUiState, event: ProfileScreenUiEvent) {
        when (event) {
            is ProfileScreenUiEvent.GetGuideCaneUsers -> {
                observeGuideCaneUsers()
            }
            is ProfileScreenUiEvent.AddGuideCaneUser -> {
                addGuideCaneUser(oldState, event.appUserId, event.smartCaneId, event.securityKey)
            }
            is ProfileScreenUiEvent.UpdateGuideCaneUser -> {
                updateGuideCaneUser(oldState, event.smartCaneId, event.updates)
            }
            is ProfileScreenUiEvent.DeleteGuideCaneUser -> {
                deleteGuideCaneUser(oldState, event.smartCaneId)
            }
            is ProfileScreenUiEvent.GetUserHistory -> {
                getUserHistory(oldState, event.smartCaneId)
            }
            is ProfileScreenUiEvent.OnChangeAddUserDialogState -> {
                setState(oldState.copy(isShowAddUserDialog = event.show))
            }
            is ProfileScreenUiEvent.OnChangeUpdateUserDialogState -> {
                setState(oldState.copy(isShowUpdateUserDialog = event.show))
            }
            is ProfileScreenUiEvent.SetUserToBeUpdated -> {
                setState(oldState.copy(userToBeUpdated = event.guideCaneUser))
            }
            is ProfileScreenUiEvent.SetUserHistory -> {
                setState(oldState.copy(userHistory = event.historyList))
            }
            ProfileScreenUiEvent.NavigateToUpdateUser -> {
                setEffect { GuideCaneSideEffects.NavigateToUpdateUser }
            }
            ProfileScreenUiEvent.NavigateToViewHistory -> {
                setEffect { GuideCaneSideEffects.NavigateToViewHistory }
            }

            else -> {}
        }
    }

    private fun addGuideCaneUser(oldState: ProfileScreenUiState, appUserId: String, smartCaneId: String, securityKey: String) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))
            when (val result = guideCaneRepository.updateAppUser(appUserId, smartCaneId, securityKey, add = true)) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))
                    setEffect { GuideCaneSideEffects.ShowSnackBarMessage("Error adding guide cane user") }
                }
                is Result.Success -> {
                    setState(oldState.copy(isLoading = false))
                    setEffect { GuideCaneSideEffects.ShowSnackBarMessage("Guide cane user added successfully") }
                }
            }
        }
    }

    private fun updateGuideCaneUser(oldState: ProfileScreenUiState, smartCaneId: String, updates: Map<String, Any>) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))
            when (val result = guideCaneRepository.updateGuideCaneUser(smartCaneId, updates)) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))
                    setEffect { GuideCaneSideEffects.ShowSnackBarMessage("Error updating guide cane user") }
                }
                is Result.Success -> {
                    setState(oldState.copy(isLoading = false))
                    setEffect { GuideCaneSideEffects.ShowSnackBarMessage("Guide cane user updated successfully") }
                }
            }
        }
    }

    private fun deleteGuideCaneUser(oldState: ProfileScreenUiState, smartCaneId: String) {
        viewModelScope.launch {
            val appUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            if (appUserId.isEmpty()) {
                setState(oldState.copy(isLoading = false))
                setEffect { GuideCaneSideEffects.ShowSnackBarMessage("User is not authenticated") }
                return@launch
            }

            setState(oldState.copy(isLoading = true))

            when (val result = guideCaneRepository.removeSmartCaneIdFromAppUser(smartCaneId)) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))
                    setEffect { GuideCaneSideEffects.ShowSnackBarMessage("Error deleting guide cane user") }
                }
                is Result.Success -> {
                    setState(oldState.copy(isLoading = false))
                    setEffect { GuideCaneSideEffects.ShowSnackBarMessage("Guide cane user deleted successfully") }
                }
            }
        }
    }


    private fun getUserHistory(oldState: ProfileScreenUiState, smartCaneId: String) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))
            when (val result = guideCaneRepository.fetchUserHistory(smartCaneId)) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))
                    setEffect { GuideCaneSideEffects.ShowSnackBarMessage("Error fetching user history") }
                }
                is Result.Success -> {
                    setState(oldState.copy(isLoading = false, userHistory = result.data))
                }
            }
        }
    }
}
