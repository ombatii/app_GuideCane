package com.ombati.guidecaneapp.presentation.adduser

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
class AddUserViewModel @Inject constructor(private val guideCaneRepository: GuideCaneRepository) : ViewModel() {

    private val _state: MutableStateFlow<AddUserScreenUiState> = MutableStateFlow(AddUserScreenUiState())
    val state: StateFlow<AddUserScreenUiState> = _state.asStateFlow()

    private val _effect: Channel<GuideCaneSideEffects> = Channel()
    val effect = _effect.receiveAsFlow()

    init {

    }

    fun sendEvent(event: AddUserScreenUiEvent) {
        reduce(oldState = _state.value, event = event)
    }

    private fun setEffect(builder: () -> GuideCaneSideEffects) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    private fun setState(newState: AddUserScreenUiState) {
        _state.value = newState
    }

    private fun reduce(oldState: AddUserScreenUiState, event: AddUserScreenUiEvent) {
        when (event) {
            is AddUserScreenUiEvent.SmartCaneIdChanged -> {
                onSmartCaneIdChanged(oldState = oldState, smartCaneId = event.smartCaneId)
            }

            is AddUserScreenUiEvent.SecurityKeyChanged -> {
                onSecurityKeyChanged(oldState = oldState, securityKey = event.securityKey)
            }

            AddUserScreenUiEvent.AddUserScreenUi -> {
                addUser(oldState = oldState)
            }

            AddUserScreenUiEvent.Cancel -> {
                cancelAddUser()
            }
        }
    }

    private fun onSmartCaneIdChanged(oldState: AddUserScreenUiState, smartCaneId: String) {
        setState(oldState.copy(smartCaneId = smartCaneId))
    }

    private fun onSecurityKeyChanged(oldState: AddUserScreenUiState, securityKey: String) {
        setState(oldState.copy(securityKey = securityKey))
    }

    private fun addUser(oldState: AddUserScreenUiState) {
        viewModelScope.launch {
            if (oldState.smartCaneId.isEmpty() || oldState.securityKey.isEmpty()) {
                setState(oldState.copy(isLoading = false))
                setEffect { GuideCaneSideEffects.ShowSnackBarMessage(message = "Fields cannot be empty") }
                return@launch
            }

            setState(oldState.copy(isLoading = true))

            val smartCaneId = oldState.smartCaneId
            val securityKey = oldState.securityKey
            val appUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            if (appUserId.isEmpty()) {
                setState(oldState.copy(isLoading = false))
                setEffect { GuideCaneSideEffects.ShowSnackBarMessage(message = "User is not authenticated") }
                return@launch
            }


            when (val result = guideCaneRepository.updateAppUser(appUserId, smartCaneId, securityKey, add = true)) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))
                    val errorMessage = result.exception.message ?: "An error occurred when adding the user"
                    setEffect { GuideCaneSideEffects.ShowSnackBarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            smartCaneId = "",
                            securityKey = ""
                        )
                    )
                    setEffect { GuideCaneSideEffects.NavigateToProfile }
                }
            }
        }
    }








    private fun cancelAddUser() {
        // Handle cancel action (reset fields, navigate back, etc.)
        setState(AddUserScreenUiState()) // Resets the state
    }
}
